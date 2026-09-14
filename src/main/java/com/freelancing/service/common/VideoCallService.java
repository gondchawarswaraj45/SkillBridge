package com.freelancing.service.common;

import com.freelancing.db.DatabaseManager;
import com.freelancing.model.common.VideoCall;
import com.freelancing.model.common.VideoCall.Status;
import com.freelancing.util.LoggingUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing video call sessions (simulated WebRTC layer).
 */
public class VideoCallService {
    private static final String TAG = "VideoCallService";
    private final NotificationService notifService = new NotificationService();

    /** Initiate a video call */
    public VideoCall initiateCall(String conversationId, String initiatorId, String initiatorName,
                                  String receiverId, String receiverName) {
        String callId = "vc_" + UUID.randomUUID().toString().substring(0, 8);
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        VideoCall call = new VideoCall();
        call.setId(callId);
        call.setConversationId(conversationId);
        call.setInitiatorId(initiatorId);
        call.setInitiatorName(initiatorName);
        call.setReceiverId(receiverId);
        call.setReceiverName(receiverName);
        call.setStartTime(now);

        DatabaseManager.getInstance().getVideoCalls().put(callId, call);

        notifService.sendNotification(receiverId, "📹 Incoming Video Call",
                initiatorName + " is calling you...");

        LoggingUtil.info(TAG, "Video call initiated: " + callId);
        return call;
    }

    /** Accept a call — transition to ACTIVE */
    public void acceptCall(String callId) {
        VideoCall call = DatabaseManager.getInstance().getVideoCalls().get(callId);
        if (call != null) {
            call.setStatus(Status.ACTIVE);
            call.setStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        }
    }

    /** End a call */
    public void endCall(String callId) {
        VideoCall call = DatabaseManager.getInstance().getVideoCalls().get(callId);
        if (call != null) {
            call.setStatus(Status.ENDED);
            call.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            call.setDurationSeconds(120); // Simulated duration
            LoggingUtil.info(TAG, "Video call ended: " + callId);
        }
    }

    /** Reject a call */
    public void rejectCall(String callId) {
        VideoCall call = DatabaseManager.getInstance().getVideoCalls().get(callId);
        if (call != null) {
            call.setStatus(Status.REJECTED);
            call.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        }
    }

    /** Toggle camera state */
    public boolean toggleCamera(String callId) {
        VideoCall call = DatabaseManager.getInstance().getVideoCalls().get(callId);
        if (call != null) {
            call.setCameraEnabled(!call.isCameraEnabled());
            return call.isCameraEnabled();
        }
        return false;
    }

    /** Toggle mic state */
    public boolean toggleMic(String callId) {
        VideoCall call = DatabaseManager.getInstance().getVideoCalls().get(callId);
        if (call != null) {
            call.setMicEnabled(!call.isMicEnabled());
            return call.isMicEnabled();
        }
        return false;
    }

    /** Toggle screen share */
    public boolean toggleScreenShare(String callId) {
        VideoCall call = DatabaseManager.getInstance().getVideoCalls().get(callId);
        if (call != null) {
            call.setScreenShareEnabled(!call.isScreenShareEnabled());
            return call.isScreenShareEnabled();
        }
        return false;
    }

    public VideoCall getCall(String callId) {
        return DatabaseManager.getInstance().getVideoCalls().get(callId);
    }

    public List<VideoCall> getCallHistory(String userId) {
        return DatabaseManager.getInstance().getVideoCalls().values().stream()
                .filter(c -> userId.equals(c.getInitiatorId()) || userId.equals(c.getReceiverId()))
                .sorted((a, b) -> Objects.toString(b.getStartTime(), "").compareTo(Objects.toString(a.getStartTime(), "")))
                .collect(Collectors.toList());
    }
}
