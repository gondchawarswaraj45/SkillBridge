# ⚡ SkillBridge

<div align="center">

### *Next-Gen Desktop Freelancing & Developer Skill Exchange*

A high-performance desktop platform engineered with **Java 17**, **JavaFX**, and embedded **SQLite (WAL Mode)**. Featuring single-stage SubScene windowing, deterministic AI talent matching, automated milestone proposals, atomic JDBC escrow payments, and peer-to-peer developer skill barter.

---

[![Java 17](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![JavaFX 17](https://img.shields.io/badge/JavaFX-17-FF4500?style=for-the-badge&logo=java&logoColor=white)](https://openjfx.io/)
[![SQLite](https://img.shields.io/badge/SQLite-WAL%20Mode-003B57?style=for-the-badge&logo=sqlite&logoColor=white)](https://www.sqlite.org/)
[![Tests](https://img.shields.io/badge/JUnit%205-82%20Passing-25A162?style=for-the-badge&logo=junit5&logoColor=white)](https://junit.org/junit5/)
[![Architecture](https://img.shields.io/badge/Architecture-Single--Stage%20SubScene-6366F1?style=for-the-badge)](#-architecture-at-a-glance)

</div>

---

## 🌟 What Makes SkillBridge Different?

```
 ┌──────────────────────┐  ┌──────────────────────┐  ┌──────────────────────┐
 │   🖥️ Single-Stage    │  │   🧠 Deterministic   │  │   🤝 Peer-to-Peer    │
 │       SubScene       │  │      AI Matching     │  │     Skill Barter     │
 ├──────────────────────┤  ├──────────────────────┤  ├──────────────────────┤
 │ Zero secondary popup │  │ 6-factor algorithmic │  │ Non-monetary dev     │
 │ windows. Smooth, in- │  │ compatibility score  │  │ skill exchange hub   │
 │ scene glassmorphic   │  │ + 1-click automated  │  │ alongside escrow     │
 │ modal overlays.      │  │ proposal drafting.   │  │ milestones.          │
 └──────────────────────┘  └──────────────────────┘  └──────────────────────┘
 ┌──────────────────────┐  ┌──────────────────────┐  ┌──────────────────────┐
 │   ⚡ Zero-Freeze     │  │   🔒 Local-First     │  │   🎨 Uniform Design  │
 │     Concurrency      │  │      ACID Engine     │  │        System        │
 ├──────────────────────┤  ├──────────────────────┤  ├──────────────────────┤
 │ Daemon worker pool   │  │ 100% local operation │  │ 96px metric cards,   │
 │ executes heavy tasks │  │ with SQLite WAL mode │  │ 230px category tiles,│
 │ off the UI thread    │  │ & atomic multi-table │  │ full Dark/Light dual │
 │ for 60 FPS motion.   │  │ JDBC transactions.   │  │ theme support.       │
 └──────────────────────┘  └──────────────────────┘  └──────────────────────┘
```

---

## 🏛️ Architecture at a Glance

### Single-Stage SubScene Lifecycle
Traditional JavaFX applications spawn disjointed OS window popups for dialogs and alerts. SkillBridge solves this by hosting everything inside **One Primary Stage & Scene**, using dynamic `SubScene` encapsulation and an in-scene modal overlay stack:

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Primary Stage (Single Window)                   │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                    Master Root StackPane                         │  │
│  │  ┌────────────────────────────────────────────────────────────┐  │  │
│  │  │        Active View SubScene (Bound width & height)        │  │  │
│  │  │   Landing Page • Client Portal • Freelancer Portal • ...  │  │  │
│  │  └────────────────────────────────────────────────────────────┘  │  │
│  │  ┌────────────────────────────────────────────────────────────┐  │  │
│  │  │            In-Scene Modal & Alert Overlay Layer           │  │  │
│  │  │   Glassmorphic Dialogs • OTP Verification • Schedulers    │  │  │
│  │  └────────────────────────────────────────────────────────────┘  │  │
│  └──────────────────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────────────────┘
```

- **Zero OS Dialog Windows**: Replaced all native `Alert` and `TextInputDialog` stages with in-scene glassmorphic cards (`UIComponents.showAlert`, `showPromptDialog`, `showConfirmDialog`).
- **Responsive SubScene Dimensions**: The active view's dimensions are bound to the window (`activeSubScene.widthProperty().bind(masterRoot.widthProperty())`), adapting fluidly to any screen resize.
- **Multithreaded Daemon Pool**: Database queries, AI scoring, and file I/O execute asynchronously via `AnimationUtil.runAsync`, safely updating the UI on JavaFX's thread via `Platform.runLater`.

---

## 🚀 Experience Portals

| Portal | Core Capabilities | Highlights |
|---|---|---|
| **🏢 Company Portal** | • Structured project wizard with custom skill tags<br>• AI scope & requirements generator<br>• Candidate shortlist & proposal comparison<br>• Milestone deliverable review & escrow release | Dynamic KPI dashboard with 96px uniform stat cards; real-time escrow disbursement. |
| **👤 Freelancer Portal** | • Multi-filter marketplace (budget, skills, category)<br>• Instant AI match score preview (`★ 95% Match`)<br>• 1-click AI proposal drafter with milestone breakdown<br>• Deliverable upload tracker & financial ledger | Demo withdrawal simulator; portfolio item gallery with GitHub & LinkedIn integration. |
| **🤝 Barter & Community** | • Peer-to-peer developer skill exchange offers<br>• Negotiation, acceptance & completion workflow<br>• Developer discussion forums with threaded comments | Non-monetary collaboration for devs trading complementary skillsets. |
| **🛡️ Admin Cockpit** | • Platform-wide metrics & aggregate analytics<br>• User moderation & AI fraud risk scanner<br>• 1-click dispute arbitration (Refund / Release)<br>• SQLite live backup & table CSV export | Full operational oversight with immutable audit logs. |
| **💬 Collaboration Hub** | • Direct messaging threads linked to projects<br>• Interactive monthly work calendar with dot filters<br>• Instant virtual meeting launcher (Google Meet links) | Real-time notification center for all contract lifecycle events. |

---

## 🧠 Algorithmic AI Matching Engine

SkillBridge computes a deterministic **6-factor compatibility score (100% total)** in pure Core Java:

```
  50% Skill Match        ├── Tokenized, collision-free skill overlap
  15% Experience Match   ├── Entry (1y) • Intermediate (3y) • Expert (6+y)
  10% Rating History     ├── Scaled 5.0-star client review average
  10% Budget Alignment   ├── Hourly rate vs. project budget bounds
  10% Availability       ├── Available • Part-time • Busy
   5% Profile Depth      ├── Bio completeness, avatar, resume & social links
```

---

## 🔑 Demo Access Accounts

Launch the app and use any of the pre-seeded accounts below, or click any **Quick Demo Login** button directly on the login screen:

| Role | Username / Identifier | Password | Persona & Focus |
|---|---|---|---|
| **Admin** | `admin` | `admin123` | System oversight, dispute arbitration & platform KPIs |
| **Company** | `sarah_client` | `client123` | NovaTech Financial (FinTech & Analytics projects) |
| **Company** | `techcorp` | `techcorp123` | TechCorp Cloud (Enterprise Cloud & Distributed Systems) |
| **Freelancer** | `alex_dev` | `free123` | Senior Full-Stack Java & Desktop Architect |
| **Freelancer** | `sarah_ui` | `free123` | Lead UI/UX & Design Systems Architect |
| **Freelancer** | `marcus_backend` | `free123` | Cloud Security & Distributed Systems Engineer |

---

## ⚡ Quick Start

### Prerequisites
- **JDK 17+** and **Apache Maven 3.8+**

```bash
# 1. Clone & enter project
git clone https://github.com/gondchawarswaraj45/SkillBridge.git
cd freelancing.sb

# 2. Run automated test suite (82 tests across 16 test suites)
mvn test

# 3. Launch the desktop application
mvn javafx:run
```

*(Windows shortcuts: double-click `build.bat` to compile or `run.bat` to launch).*

---

<div align="center">

**SkillBridge** • Crafted with ❤️ in Pure Java 17 & JavaFX

</div>
