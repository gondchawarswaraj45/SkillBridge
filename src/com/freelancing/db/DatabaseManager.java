package com.freelancing.db;

import com.freelancing.model.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String DATA_FILE = "freelancing_data.dat";
    private static DatabaseManager instance;

    private Map<String, User> users = new ConcurrentHashMap<>();
    private Map<String, FreelancerProfile> freelancerProfiles = new ConcurrentHashMap<>();
    private Map<String, ClientProfile> clientProfiles = new ConcurrentHashMap<>();
    private Map<String, Project> projects = new ConcurrentHashMap<>();
    private Map<String, Proposal> proposals = new ConcurrentHashMap<>();
    private Map<String, Milestone> milestones = new ConcurrentHashMap<>();
    private Map<String, ChatMessage> chatMessages = new ConcurrentHashMap<>();
    private Map<String, Dispute> disputes = new ConcurrentHashMap<>();
    private Map<String, SupportTicket> supportTickets = new ConcurrentHashMap<>();
    private Map<String, Notification> notifications = new ConcurrentHashMap<>();
    private Map<String, Rating> ratings = new ConcurrentHashMap<>();
    private Map<String, FeedPost> feedPosts = new ConcurrentHashMap<>();
    private List<String> activityLogs = Collections.synchronizedList(new ArrayList<>());

    private DatabaseManager() {}

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = loadData();
            if (instance.users.isEmpty()) {
                instance.seedInitialData();
                instance.saveData();
            }
        }
        return instance;
    }

    private static DatabaseManager loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                return (DatabaseManager) ois.readObject();
            } catch (Exception e) {
                System.err.println("Error loading data file, re-initializing: " + e.getMessage());
            }
        }
        return new DatabaseManager();
    }

    public synchronized void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(this);
        } catch (Exception e) {
            System.err.println("Error saving database file: " + e.getMessage());
        }
    }

    private void seedInitialData() {
        logActivity("System database initialized with default seed records.");

        // 1. Admin User
        User admin = new User("usr_admin", "admin", "admin@freelancing.sb", "+18005550100", "admin123", User.Role.ADMIN, User.Status.VERIFIED, "2026-01-01");
        users.put(admin.getId(), admin);

        // 2. Client Users & Profiles
        User client1 = new User("usr_client1", "techcorp", "client@example.com", "+18005550101", "client123", User.Role.CLIENT, User.Status.VERIFIED, "2026-01-10");
        users.put(client1.getId(), client1);
        ClientProfile cp1 = new ClientProfile(client1.getId(), "TechCorp Global Inc.", "Software & Cloud Services", "Leading enterprise cloud and software solutions provider.", "https://techcorp.example.com", 2, 7700.0, 4.9);
        clientProfiles.put(client1.getId(), cp1);

        User client2 = new User("usr_client2", "designstudio", "design@example.com", "+18005550102", "client123", User.Role.CLIENT, User.Status.VERIFIED, "2026-01-15");
        users.put(client2.getId(), client2);
        ClientProfile cp2 = new ClientProfile(client2.getId(), "Apex Creative Design Studio", "Digital Media & Branding", "Award-winning agency specializing in product design and UI/UX.", "https://apexdesign.example.com", 1, 2800.0, 4.8);
        clientProfiles.put(client2.getId(), cp2);

        // 3. Freelancer Users & Profiles
        User free1 = new User("usr_free1", "alex_dev", "freelancer@example.com", "+18005550201", "free123", User.Role.FREELANCER, User.Status.VERIFIED, "2026-01-12");
        users.put(free1.getId(), free1);
        FreelancerProfile fp1 = new FreelancerProfile(free1.getId(), "Senior Java & Full-Stack Architect",
                "Passionate Java engineer with 6+ years experience building scalable enterprise systems, JavaFX desktop apps, and Spring Boot microservices.",
                Arrays.asList("Java", "JavaFX", "Spring Boot", "SQL", "REST API", "Git"),
                "Senior (6+ Years)", "B.Tech Computer Science", "Oracle Certified Professional Java SE 17",
                "alex_johnson_resume.pdf", "https://linkedin.com/in/alex-dev", "https://github.com/alex-dev",
                "https://gitlab.com/alex-dev", "https://alexdev.portfolio.io", 4.95, 14);
        freelancerProfiles.put(free1.getId(), fp1);

        User free2 = new User("usr_free2", "sarah_ui", "sarah@example.com", "+18005550202", "free123", User.Role.FREELANCER, User.Status.VERIFIED, "2026-01-18");
        users.put(free2.getId(), free2);
        FreelancerProfile fp2 = new FreelancerProfile(free2.getId(), "UI/UX & Mobile Product Designer",
                "Creative visual designer crafting intuitive mobile application interfaces and modern desktop control panels.",
                Arrays.asList("Figma", "UI/UX", "Adobe XD", "CSS", "Wireframing", "Prototyping"),
                "Mid-Level (4 Years)", "M.Des Interaction Design", "Google UX Design Professional Certificate",
                "sarah_miller_portfolio.pdf", "https://linkedin.com/in/sarah-ui", "https://github.com/sarah-ui",
                "https://gitlab.com/sarah-ui", "https://sarahdesign.io", 4.90, 9);
        freelancerProfiles.put(free2.getId(), fp2);

        // 4. Sample Projects
        Project p1 = new Project("proj_101", client1.getId(), "TechCorp Global Inc.",
                "E-Commerce Mobile Application (Java & Flutter)", "Mobile Development",
                "We require an experienced mobile developer to build a high-performance shopping application integrated with payment gateway and push notifications.",
                Arrays.asList("Java", "Flutter", "REST API", "Firebase"), 4500.0, "2026-08-30",
                Project.Status.OPEN, null, null, "2026-02-01");
        projects.put(p1.getId(), p1);

        Project p2 = new Project("proj_102", client2.getId(), "Apex Creative Design Studio",
                "Corporate Analytics Dashboard & Design System", "UI/UX Design",
                "Design a clean, dark-mode analytics dashboard interface for our SaaS platform with high usability and responsive components.",
                Arrays.asList("Figma", "UI/UX", "CSS", "Prototyping"), 2800.0, "2026-08-20",
                Project.Status.IN_PROGRESS, free2.getId(), "sarah_ui", "2026-02-05");
        projects.put(p2.getId(), p2);

        Project p3 = new Project("proj_103", client1.getId(), "TechCorp Global Inc.",
                "AI-Powered Support ChatBot & Recommendation Engine", "AI & Machine Learning",
                "Develop an intelligent NLP chatbot module to automatically answer user queries and calculate candidate-project skill match scores.",
                Arrays.asList("Java", "Python", "AI", "NLP", "SQL"), 3200.0, "2026-09-15",
                Project.Status.OPEN, null, null, "2026-02-10");
        projects.put(p3.getId(), p3);

        // 5. Sample Proposals
        Proposal prop1 = new Proposal("prop_01", p1.getId(), p1.getTitle(), free1.getId(), "alex_dev",
                "Hello TechCorp! I have built multiple Java REST & mobile backend integrations. I can deliver this project within 25 days with clean code and full unit tests.",
                4200.0, 25, Proposal.Status.PENDING, "2026-02-02");
        proposals.put(prop1.getId(), prop1);

        Proposal prop2 = new Proposal("prop_02", p2.getId(), p2.getTitle(), free2.getId(), "sarah_ui",
                "Hi Apex Studio! I specialize in modern dark dashboard design systems in Figma. Let's create an elegant UI for your SaaS userbase.",
                2800.0, 15, Proposal.Status.ACCEPTED, "2026-02-06");
        proposals.put(prop2.getId(), prop2);

        // 6. Sample Milestones
        Milestone m1 = new Milestone("ms_01", p2.getId(), "Wireframes & Component Design System", "Initial wireframe concepts and dark color scheme guidelines", 1000.0, "2026-08-10", Milestone.Status.APPROVED);
        Milestone m2 = new Milestone("ms_02", p2.getId(), "High-Fidelity Dashboard Prototypes", "Interactive Figma prototype with chart widgets and sidebar navigation", 1800.0, "2026-08-20", Milestone.Status.IN_PROGRESS);
        milestones.put(m1.getId(), m1);
        milestones.put(m2.getId(), m2);

        // 7. Sample Chat Messages
        ChatMessage cm1 = new ChatMessage("msg_01", p2.getId(), free2.getId(), "sarah_ui", client2.getId(),
                "Hi! I uploaded the preliminary design system guidelines in Figma.", null, true, "10:30 AM");
        ChatMessage cm2 = new ChatMessage("msg_02", p2.getId(), client2.getId(), "designstudio", free2.getId(),
                "Looks super sleek Sarah! Love the dark palette contrast.", null, true, "10:35 AM");
        chatMessages.put(cm1.getId(), cm1);
        chatMessages.put(cm2.getId(), cm2);

        // 8. Sample Notifications
        Notification n1 = new Notification("notif_01", free1.getId(), "New Project Match", "An AI-recommended project 'E-Commerce Mobile Application' matches 95% of your skills!", false, "2026-02-12 09:00");
        Notification n2 = new Notification("notif_02", free2.getId(), "Proposal Accepted", "Your proposal for 'Corporate Analytics Dashboard' has been accepted by Apex Creative Studio!", false, "2026-02-06 14:20");
        notifications.put(n1.getId(), n1);
        notifications.put(n2.getId(), n2);

        // 9. Sample Support Ticket
        SupportTicket st1 = new SupportTicket("tkt_01", free1.getId(), "alex_dev", "OTP Verification Delay", "Received SMS OTP after 2 minutes on initial registration.", SupportTicket.Status.CLOSED, "2026-02-01");
        st1.setAdminReply("Resolved: Gateway latency cleared.");
        supportTickets.put(st1.getId(), st1);

        // 10. Sample Feed Posts
        FeedPost fp1Post = new FeedPost("feed_01", free1.getId(), "alex_dev", "FREELANCER",
                "Just completed a full-stack e-commerce platform!",
                "Excited to share my latest project — a complete e-commerce solution built with Java Spring Boot backend and React frontend. Features include real-time inventory management, Razorpay payment integration, and an AI-powered product recommendation engine. Looking for feedback from the community!",
                FeedPost.PostCategory.SHOWCASE, FeedPost.PostStatus.ACTIVE, "2026-02-15 10:30");
        fp1Post.setLikes(12);
        fp1Post.addComment(new FeedPost.FeedComment("cmt_01", client1.getId(), "techcorp", "Impressive work Alex! We might have a similar project coming up.", "2026-02-15 11:00"));
        fp1Post.addComment(new FeedPost.FeedComment("cmt_02", free2.getId(), "sarah_ui", "Love the clean UI design! Would love to collaborate on the frontend sometime.", "2026-02-15 11:45"));
        feedPosts.put(fp1Post.getId(), fp1Post);

        FeedPost fp2Post = new FeedPost("feed_02", client1.getId(), "techcorp", "CLIENT",
                "Hiring: Senior Mobile Developer for Healthcare App",
                "TechCorp Global is looking for an experienced mobile developer to build a HIPAA-compliant telemedicine application. Must have experience with Flutter/React Native, real-time video calls, and secure data handling. Competitive budget and long-term engagement possible. Apply through our posted project or DM!",
                FeedPost.PostCategory.HIRING, FeedPost.PostStatus.ACTIVE, "2026-02-18 09:15");
        fp2Post.setLikes(8);
        fp2Post.addComment(new FeedPost.FeedComment("cmt_03", free1.getId(), "alex_dev", "Very interested! I have healthcare app experience. Sending a proposal shortly.", "2026-02-18 10:00"));
        feedPosts.put(fp2Post.getId(), fp2Post);

        FeedPost fp3Post = new FeedPost("feed_03", free2.getId(), "sarah_ui", "FREELANCER",
                "Tips for creating stunning dark-mode dashboards",
                "After designing 15+ dashboard interfaces, here are my top 5 tips for dark-mode UI:\n1. Use contrast ratios of at least 4.5:1 for text\n2. Avoid pure black (#000) — use dark grays like #1E293B\n3. Use subtle gradients for depth\n4. Color-code data visualization elements consistently\n5. Test with real data, not lorem ipsum!\n\nWhat are your best practices? Share below!",
                FeedPost.PostCategory.DISCUSSION, FeedPost.PostStatus.ACTIVE, "2026-02-20 14:00");
        fp3Post.setLikes(23);
        fp3Post.addComment(new FeedPost.FeedComment("cmt_04", client2.getId(), "designstudio", "Great tips Sarah! Tip #2 is so important. We follow the same approach at Apex.", "2026-02-20 15:30"));
        feedPosts.put(fp3Post.getId(), fp3Post);
    }

    // Getters for Collections
    public Map<String, User> getUsers() { return users; }
    public Map<String, FreelancerProfile> getFreelancerProfiles() { return freelancerProfiles; }
    public Map<String, ClientProfile> getClientProfiles() { return clientProfiles; }
    public Map<String, Project> getProjects() { return projects; }
    public Map<String, Proposal> getProposals() { return proposals; }
    public Map<String, Milestone> getMilestones() { return milestones; }
    public Map<String, ChatMessage> getChatMessages() { return chatMessages; }
    public Map<String, Dispute> getDisputes() { return disputes; }
    public Map<String, SupportTicket> getSupportTickets() { return supportTickets; }
    public Map<String, Notification> getNotifications() { return notifications; }
    public Map<String, Rating> getRatings() { return ratings; }
    public Map<String, FeedPost> getFeedPosts() { return feedPosts; }
    public List<String> getActivityLogs() { return activityLogs; }

    public void logActivity(String entry) {
        String log = "[" + new java.util.Date().toString() + "] " + entry;
        activityLogs.add(0, log);
        saveData();
    }
}
