# ⚡ SkillBridge — Freelancing & Skill Exchange Platform

> A modern JavaFX desktop application connecting freelancers with clients. Built with Java 17 and JavaFX 17, featuring AI-powered talent matching, community feed, encrypted messaging, Razorpay payment simulation, and a full admin control panel.

---

## 📋 Table of Contents
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [Default Login Credentials](#-default-login-credentials)
- [Modules Overview](#-modules-overview)
- [Screenshots](#-screenshots)

---

## ✨ Features

### 👤 Freelancer Portal
- **Dashboard Analytics** — Earnings trends, completed projects, AI skill scores with interactive JavaFX charts
- **📰 Community Feed** — Create posts (Showcase, Hiring, Discussion, Feedback), like, comment, and give feedback to other users
- **Profile & Portfolio** — Full profile management with LinkedIn, GitHub, GitLab links, resume upload, skills, certifications
- **AI-Powered Job Matching** — Cosine skill similarity algorithm recommends best-fit projects with match percentages
- **Proposal Submission** — AI auto-generates winning cover letters tailored to each project
- **Active Project Tracking** — Milestone management with deliverable uploads
- **Calendar & Schedule** — Visual deadline and meeting tracker
- **Encrypted Chat & Google Meet** — AES-256 encrypted messaging with project-based conversations
- **AI Career Coach** — Interactive chatbot for proposal tips, pricing advice, and trending skill guidance
- **Notifications** — Real-time system alerts and announcements

### 🏢 Client Portal
- **Dashboard** — Project stats, capital allocation charts, active contracts overview
- **📰 Community Feed** — Same full feed experience as freelancers — post, like, comment, give feedback
- **Post Projects** — AI-assisted project description generator with skill tagging
- **Proposal Review & Hiring** — Review bids, accept/reject proposals, auto-create milestone contracts
- **Find Freelancers** — Browse and invite freelancers with AI-recommended talent matching
- **Milestone & Razorpay Payments** — Release escrow payments with auto-generated invoice receipts
- **Chat & Messaging** — End-to-end encrypted communication with hired freelancers
- **Company Profile** — Manage company details, industry, and website

### 🛡️ Admin Portal
- **System Overview** — User demographics pie charts, revenue bar charts, activity logs
- **User & AI Fraud Scanner** — Account verification, suspension, AI-powered fraud risk scoring
- **📰 Feed Moderation** — View all posts (Active/Flagged/Removed), flag inappropriate content, remove or restore posts, permanently delete
- **Project Management** — Remove spam/illegal projects
- **Dispute Resolution** — Resolve disputes in favor of client or freelancer
- **AI Monitoring** — Track recommendation engine performance
- **Announcements & Support** — Broadcast system announcements, manage support tickets
- **Database Backup** — One-click database snapshot and system logs

---

## 🛠️ Tech Stack

| Component        | Technology                      |
|------------------|---------------------------------|
| Language         | Java 17                         |
| UI Framework     | JavaFX 17.0.10                  |
| Database         | Java Serialization (`.dat` file) |
| AI Engine        | Custom cosine similarity + NLP  |
| Payment Sim      | Razorpay simulation module      |
| Build Tool       | `javac` via batch script        |
| Architecture     | MVC (Model-View-Controller)     |

---

## 📁 Project Structure

```
SkillBridge/
├── src/
│   ├── com/freelancing/
│   │   ├── app/
│   │   │   └── Main.java                  # Application entry point
│   │   ├── config/
│   │   │   └── AppTheme.java              # Theme constants
│   │   ├── db/
│   │   │   └── DatabaseManager.java       # Singleton DB with serialization
│   │   ├── model/
│   │   │   ├── User.java                  # User model (Freelancer/Client/Admin)
│   │   │   ├── FreelancerProfile.java     # Freelancer profile with skills & links
│   │   │   ├── ClientProfile.java         # Client/company profile
│   │   │   ├── Project.java               # Project listings
│   │   │   ├── Proposal.java              # Bid proposals
│   │   │   ├── Milestone.java             # Project milestones
│   │   │   ├── FeedPost.java              # Community feed posts & comments
│   │   │   ├── ChatMessage.java           # Encrypted chat messages
│   │   │   ├── Rating.java                # User ratings & feedback
│   │   │   ├── Notification.java          # System notifications
│   │   │   ├── Dispute.java               # Dispute records
│   │   │   └── SupportTicket.java         # Support tickets
│   │   ├── service/
│   │   │   ├── AiService.java             # AI matching & NLP engine
│   │   │   ├── AuthService.java           # Authentication & OTP verification
│   │   │   ├── PaymentService.java        # Razorpay payment simulation
│   │   │   └── NotificationService.java   # Notification management
│   │   └── ui/
│   │       ├── LoginView.java             # Login screen with demo credentials
│   │       ├── RegisterView.java          # Registration with OTP verification
│   │       ├── FreelancerMainView.java    # Full freelancer dashboard & feed
│   │       ├── ClientMainView.java        # Full client dashboard & feed
│   │       ├── AdminMainView.java         # Admin panel with feed moderation
│   │       └── UIComponents.java          # Reusable UI component factory
│   └── style.css                          # Dark glassmorphic theme
├── lib/
│   └── javafx-sdk-17.0.10/               # JavaFX SDK libraries
├── bin/                                   # Compiled class files
├── build.bat                              # Windows build script
├── run.bat                                # Windows run script
└── README.md                              # This file
```

---

## 🚀 Getting Started

### Prerequisites
- **Java JDK 17** installed at `C:\Program Files\Java\jdk-17\`
- **JavaFX SDK 17.0.10** (included in `lib/` directory)

### Build
```batch
build.bat
```
This compiles all Java source files to the `bin/` directory.

### Run
```batch
run.bat
```
This launches the SkillBridge desktop application.

### ⚠️ Fresh Start
If you encounter serialization errors after code changes, delete the `freelancing_data.dat` file to re-seed the database:
```batch
del freelancing_data.dat
build.bat
run.bat
```

---

## 🔑 Default Login Credentials

| Role       | Username / Email            | Password    |
|------------|-----------------------------|-------------|
| Admin      | `admin`                     | `admin123`  |
| Client     | `client@example.com`        | `client123` |
| Client 2   | `design@example.com`        | `client123` |
| Freelancer | `freelancer@example.com`    | `free123`   |
| Freelancer 2 | `sarah@example.com`       | `free123`   |

---

## 📦 Modules Overview

### Community Feed System
Both **Freelancers** and **Clients** can:
- 📝 Create posts with categories: Showcase, Hiring, Discussion, Feedback
- 👍 Like / unlike posts
- 💬 Add comments on any post
- ⭐ Give feedback/ratings to post authors
- View a unified timeline of all active community posts

**Admins** can:
- View all posts across the platform (Active, Flagged, Removed)
- ⚠️ Flag suspicious or inappropriate content
- ❌ Remove posts from the public feed
- ✅ Restore flagged/removed posts
- 🗑️ Permanently delete posts

### AI-Powered Features
- **Skill Match Scoring** — Calculates percentage match between freelancer skills and project requirements
- **Fraud Risk Detection** — AI assigns risk scores to user accounts
- **Auto-Generated Proposals** — Creates professional cover letters based on project context
- **Career Coach Chatbot** — Answers career questions with contextual AI responses

### Payment & Escrow
- Razorpay payment simulation with transaction IDs
- Auto-generated invoice receipts exported as `.txt` files
- Client spending tracking and commission calculations

---

## 📸 Screenshots

> Screenshots will be added after the application is built and running.

---

## 👥 Contributors

- **Swaraj** — Full-stack Developer & Project Lead

---

## 📄 License

This project is developed for educational and demonstration purposes.

---

*Built with ❤️ using Java 17 & JavaFX — SkillBridge 2026*
