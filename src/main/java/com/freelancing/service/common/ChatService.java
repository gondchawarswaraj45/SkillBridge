package com.freelancing.service.common;

import com.freelancing.dao.common.ConversationDAO;
import com.freelancing.dao.common.MessageDAO;
import com.freelancing.dao.common.UserDAO;
import com.freelancing.dao.company.ProjectDAO;
import com.freelancing.model.common.ChatMessage;
import com.freelancing.model.common.Conversation;
import com.freelancing.model.common.User;

import com.freelancing.util.LoggingUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Service managing direct messaging between clients and freelancers with project context.
 */
public class ChatService {
    private static final String TAG = "ChatService";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("hh:mm a");

    private final ConversationDAO conversationDAO;
    private final MessageDAO messageDAO;
    private final NotificationService notificationService;
    private final UserDAO userDAO;
    private final ProjectDAO projectDAO;

    public ChatService() {
        this.conversationDAO = new ConversationDAO();
        this.messageDAO = new MessageDAO();
        this.notificationService = new NotificationService();
        this.userDAO = new UserDAO();
        this.projectDAO = new ProjectDAO();
    }

    public ChatService(ConversationDAO conversationDAO, MessageDAO messageDAO,
                       NotificationService notificationService, UserDAO userDAO, ProjectDAO projectDAO) {
        this.conversationDAO = conversationDAO;
        this.messageDAO = messageDAO;
        this.notificationService = notificationService;
        this.userDAO = userDAO;
        this.projectDAO = projectDAO;
    }

    public ProjectDAO getProjectDAO() {
        return projectDAO;
    }

    /**
     * Finds an existing conversation thread between two users, or creates a new one.
     */
    public Conversation getOrCreateConversation(String user1Id, String user2Id, String projectId) {
        if (user1Id == null || user2Id == null) {
            throw new IllegalArgumentException("User IDs must not be null.");
        }
        if (user1Id.equals(user2Id)) {
            throw new IllegalArgumentException("Cannot create conversation with oneself.");
        }

        Conversation existing = conversationDAO.findBetweenUsers(user1Id, user2Id, projectId);
        if (existing != null) {
            return existing;
        }

        String convId = "conv_" + UUID.randomUUID().toString().substring(0, 8);
        String now = LocalDateTime.now().format(FORMATTER);

        Conversation conv = new Conversation();
        conv.setId(convId);
        conv.setUser1Id(user1Id);
        conv.setUser2Id(user2Id);
        conv.setProjectId(projectId);
        conv.setLastMessage("Conversation started");
        conv.setLastMessageTime(now);
        conv.setCreatedAt(now);

        conversationDAO.create(conv);
        LoggingUtil.info(TAG, "Created conversation " + convId + " between " + user1Id + " and " + user2Id);

        return conversationDAO.findById(convId);
    }

    /**
     * Gets all conversation threads for a given user.
     */
    public List<Conversation> getUserConversations(String userId) {
        return conversationDAO.findByUserId(userId);
    }

    /**
     * Gets a conversation by ID.
     */
    public Conversation getConversation(String conversationId) {
        return conversationDAO.findById(conversationId);
    }

    /**
     * Sends a message within a conversation and notifies the recipient.
     */
    public ChatMessage sendMessage(String conversationId, String senderId, String content, String attachmentPath) {
        if (conversationId == null || conversationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Conversation ID is required.");
        }
        if (senderId == null || senderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Sender ID is required.");
        }
        if ((content == null || content.trim().isEmpty()) && (attachmentPath == null || attachmentPath.trim().isEmpty())) {
            throw new IllegalArgumentException("Message must have text content or an attachment.");
        }

        Conversation conv = conversationDAO.findById(conversationId);
        if (conv == null) {
            throw new IllegalArgumentException("Conversation not found: " + conversationId);
        }

        String msgId = "msg_" + UUID.randomUUID().toString().substring(0, 8);
        String nowFull = LocalDateTime.now().format(FORMATTER);
        String nowTime = LocalDateTime.now().format(TIME_FMT);

        ChatMessage msg = new ChatMessage();
        msg.setId(msgId);
        msg.setConversationId(conversationId);
        msg.setSenderId(senderId);
        msg.setContent(content != null ? content.trim() : "");
        msg.setAttachmentPath(attachmentPath);
        msg.setRead(false);
        msg.setSentAt(nowFull);
        msg.setTimestamp(nowTime);

        messageDAO.create(msg);

        // Update conversation summary
        String snippet = msg.getContent();
        if (snippet.length() > 50) {
            snippet = snippet.substring(0, 47) + "...";
        }
        if (snippet.isEmpty() && attachmentPath != null) {
            snippet = "📎 Attachment";
        }
        conversationDAO.updateLastMessage(conversationId, snippet, nowFull);

        // Notify the receiver
        String receiverId = senderId.equals(conv.getUser1Id()) ? conv.getUser2Id() : conv.getUser1Id();
        User sender = userDAO.findById(senderId);
        String senderName = sender != null ? sender.getUsername() : "User";

        notificationService.sendNotification(
            receiverId,
            "💬 New message from " + senderName,
            snippet,
            "MESSAGE",
            conversationId
        );

        LoggingUtil.info(TAG, "Message " + msgId + " sent in conversation " + conversationId);
        return msg;
    }

    /**
     * Gets conversation messages and automatically marks unread messages as read for the current user.
     */
    public List<ChatMessage> getConversationMessages(String conversationId, String currentUserId) {
        if (conversationId == null) return List.of();

        List<ChatMessage> msgs = messageDAO.findByConversationId(conversationId);
        if (currentUserId != null) {
            messageDAO.markAsRead(conversationId, currentUserId);
        }
        return msgs;
    }

    /**
     * Gets total unread messages count for a user.
     */
    public int getTotalUnreadCount(String userId) {
        return messageDAO.getTotalUnreadCountForUser(userId);
    }
}
