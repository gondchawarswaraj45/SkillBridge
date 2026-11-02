import React, { createContext, useContext, useState } from 'react';
import { useToast } from '@/context/ToastContext';

export interface User {
  id: string;
  name: string;
  email: string;
  role: 'FREELANCER' | 'CLIENT' | 'ADMIN';
  balance: number;
  escrowLocked: number;
  avatar?: string;
  verified: boolean;
}

export interface Job {
  id: number;
  title: string;
  client: string;
  clientCountry: string;
  clientSpent: string;
  clientRating: number;
  budget: string;
  type: string;
  duration: string;
  experience: string;
  matchScore: number;
  postedTime: string;
  proposals: string;
  description: string;
  skills: string[];
  category: string;
}

export interface TaskItem {
  id: string;
  title: string;
  description: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  assignee: string;
  dueDate: string;
  milestone: string;
  commentsCount: number;
  filesCount: number;
}

export interface Milestone {
  num: number;
  title: string;
  amount: number;
  status: 'COMPLETED' | 'IN_PROGRESS' | 'IN_REVIEW' | 'UPCOMING';
  date: string;
  deliverableNotes?: string;
  deliverableUrl?: string;
}

export interface ChatMessage {
  id: string;
  senderId: string;
  senderName: string;
  senderRole: string;
  content: string;
  timestamp: string;
  isMe: boolean;
}

interface AppContextType {
  currentUser: User;
  switchRole: (role: 'FREELANCER' | 'CLIENT' | 'ADMIN') => void;
  // Jobs
  jobs: Job[];
  postJob: (job: Omit<Job, 'id' | 'postedTime' | 'proposals'>) => void;
  // Proposals
  submitProposal: (jobId: number, coverLetter: string, proposedBudget: number, duration: string) => void;
  // Tasks (Kanban)
  tasks: Record<string, TaskItem[]>;
  moveTask: (taskId: string, targetCol: string) => void;
  addTask: (col: string, task: Omit<TaskItem, 'id' | 'commentsCount' | 'filesCount'>) => void;
  deleteTask: (col: string, taskId: string) => void;
  // Milestones & Escrow
  milestones: Milestone[];
  submitDeliverable: (num: number, notes: string, url: string) => void;
  approveMilestone: (num: number) => void;
  // Admin Fraud & Verifications
  flaggedAccounts: any[];
  suspendAccount: (accountId: string) => void;
  pendingVerifications: any[];
  approveVerification: (id: number) => void;
  rejectVerification: (id: number) => void;
  // Chat
  messages: ChatMessage[];
  sendMessage: (content: string) => void;
}

const defaultJobs: Job[] = [
  {
    id: 1,
    title: 'Senior Full-Stack Java (Spring Boot) & React Platform Architect',
    client: 'Apex Financial Labs',
    clientCountry: 'United States',
    clientSpent: '$120k+ spent',
    clientRating: 4.95,
    budget: '$4,500 - $6,000',
    type: 'Fixed Price (Escrow)',
    duration: '1-3 months',
    experience: 'Expert',
    matchScore: 98,
    postedTime: '1 hour ago',
    proposals: '7 proposals',
    category: 'Web Development',
    description:
      'Lead the architecture of our cloud-native financial analytics platform. Must have deep expertise in Java 17, Spring Boot, MySQL, and modern React with TypeScript.',
    skills: ['Java', 'Spring Boot', 'React', 'TypeScript', 'MySQL', 'Docker'],
  },
  {
    id: 2,
    title: 'Cross-Platform React Native Mobile App (Expo SDK 52) with Offline Sync',
    client: 'HealthSync Global',
    clientCountry: 'Canada',
    clientSpent: '$45k+ spent',
    clientRating: 5.0,
    budget: '$65 - $80 / hr',
    type: 'Hourly',
    duration: '3-6 months',
    experience: 'Intermediate',
    matchScore: 94,
    postedTime: '3 hours ago',
    proposals: '4 proposals',
    category: 'Mobile Apps',
    description:
      'Looking for a mobile developer to build and polish our patient monitoring app using React Native, Expo Router, NativeWind, and SQLite local cache synchronization.',
    skills: ['React Native', 'Expo', 'TypeScript', 'NativeWind', 'Zod', 'REST API'],
  },
  {
    id: 3,
    title: 'AI Semantic Document Parsing Pipeline with Google Gemini & Groq APIs',
    client: 'Lexis Automations Inc.',
    clientCountry: 'United Kingdom',
    clientSpent: '$80k+ spent',
    clientRating: 4.88,
    budget: '$3,800',
    type: 'Fixed Price (Escrow)',
    duration: 'Less than 1 month',
    experience: 'Expert',
    matchScore: 91,
    postedTime: '5 hours ago',
    proposals: '12 proposals',
    category: 'AI & Machine Learning',
    description:
      'Build a high-throughput legal document parser utilizing Spring AI with configurable Groq LLaMA and Gemini 1.5 flash models.',
    skills: ['Python', 'Spring AI', 'Groq', 'Gemini API', 'Docker'],
  },
];

const defaultTasks: Record<string, TaskItem[]> = {
  TODO: [
    {
      id: 'TASK-104',
      title: 'Design Dark Mode Elevation & Shadow Tokens',
      description: 'Implement dark mode shadow utilities in Tailwind configuration for rich depth.',
      priority: 'MEDIUM',
      assignee: 'Aisha Al-Mansoor',
      dueDate: 'Nov 08',
      milestone: 'Milestone 2',
      commentsCount: 2,
      filesCount: 1,
    },
    {
      id: 'TASK-105',
      title: 'Setup WebRTC STUN/TURN Peer Connection Server',
      description: 'Configure real-time peer connection signaling channel for low-latency video calls.',
      priority: 'HIGH',
      assignee: 'Swaraj Bhosale',
      dueDate: 'Nov 12',
      milestone: 'Milestone 3',
      commentsCount: 4,
      filesCount: 0,
    },
  ],
  IN_PROGRESS: [
    {
      id: 'TASK-102',
      title: 'Spring Security 6 Stateless JWT Filter Implementation',
      description: 'Implement JwtAuthenticationFilter with access and rotating refresh tokens.',
      priority: 'CRITICAL',
      assignee: 'Swaraj Bhosale',
      dueDate: 'Nov 06',
      milestone: 'Milestone 2',
      commentsCount: 7,
      filesCount: 3,
    },
  ],
  IN_REVIEW: [
    {
      id: 'TASK-101',
      title: 'Design System Typography & Primitive Component Set',
      description: 'Built Button, Card, Badge, Input, and StatCard components with full theme switching.',
      priority: 'HIGH',
      assignee: 'Swaraj Bhosale',
      dueDate: 'Nov 04',
      milestone: 'Milestone 1',
      commentsCount: 5,
      filesCount: 2,
    },
  ],
  COMPLETED: [
    {
      id: 'TASK-100',
      title: 'Monorepo Architecture & Multi-Profile Database Setup',
      description: 'Scaffolded Spring Boot backend with H2 dev fallback, Flyway, and shared TypeScript models.',
      priority: 'HIGH',
      assignee: 'Swaraj Bhosale',
      dueDate: 'Nov 03',
      milestone: 'Milestone 1',
      commentsCount: 3,
      filesCount: 4,
    },
  ],
};

const defaultMilestones: Milestone[] = [
  {
    num: 1,
    title: 'System Architecture Blueprint & Monorepo Setup',
    amount: 1500,
    status: 'COMPLETED',
    date: 'Completed Nov 01, 2026',
    deliverableNotes: 'Repository initialized with Gradle, H2 fallback, and React/Expo frontends.',
  },
  {
    num: 2,
    title: 'Spring Boot REST APIs, JWT Auth & Database Schema',
    amount: 1500,
    status: 'IN_PROGRESS',
    date: 'Due Nov 12, 2026',
  },
  {
    num: 3,
    title: 'React Frontend Implementation & Real-Time WebSockets',
    amount: 1500,
    status: 'UPCOMING',
    date: 'Due Nov 22, 2026',
  },
  {
    num: 4,
    title: 'AI Talent Matching Engine & Production Deployment',
    amount: 1500,
    status: 'UPCOMING',
    date: 'Due Dec 05, 2026',
  },
];

const defaultMessages: ChatMessage[] = [
  {
    id: 'MSG-1',
    senderId: 'client-1',
    senderName: 'Marcus Vance (Apex Labs)',
    senderRole: 'Client',
    content: 'Hi Swaraj, we reviewed your Milestone 1 deliverables. The architecture docs look fantastic!',
    timestamp: '10:14 AM',
    isMe: false,
  },
  {
    id: 'MSG-2',
    senderId: 'user-1',
    senderName: 'Swaraj Bhosale',
    senderRole: 'Freelancer',
    content: 'Thank you Marcus! I am currently working on Milestone 2 (JWT security filters and Flyway schema migrations).',
    timestamp: '10:18 AM',
    isMe: true,
  },
  {
    id: 'MSG-3',
    senderId: 'client-1',
    senderName: 'Marcus Vance (Apex Labs)',
    senderRole: 'Client',
    content: 'Perfect. Let me know when you push the deliverable so I can review and release the $1,500 escrow.',
    timestamp: '10:20 AM',
    isMe: false,
  },
];

const AppContext = createContext<AppContextType | undefined>(undefined);

export const AppProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const toast = useToast();

  const [currentUser, setCurrentUser] = useState<User>({
    id: 'user-swaraj',
    name: 'Swaraj Bhosale',
    email: 'swaraj@example.com',
    role: 'FREELANCER',
    balance: 24850,
    escrowLocked: 2700,
    verified: true,
  });

  const [jobs, setJobs] = useState<Job[]>(defaultJobs);
  const [tasks, setTasks] = useState<Record<string, TaskItem[]>>(defaultTasks);
  const [milestones, setMilestones] = useState<Milestone[]>(defaultMilestones);
  const [messages, setMessages] = useState<ChatMessage[]>(defaultMessages);

  const [flaggedAccounts, setFlaggedAccounts] = useState([
    {
      id: 'USR-8941',
      user: 'CrypticSolutions Ltd',
      role: 'Client',
      riskLevel: 'HIGH',
      reason: 'Suspicious Off-Platform Payment Solicitation in Messages',
      aiConfidence: '96% confidence',
      status: 'ACTIVE',
      date: '10 mins ago',
    },
    {
      id: 'USR-8930',
      user: 'DevMaster99',
      role: 'Freelancer',
      riskLevel: 'MEDIUM',
      reason: 'Duplicate Portfolio Code Repositories Across Multiple Accounts',
      aiConfidence: '88% confidence',
      status: 'ACTIVE',
      date: '35 mins ago',
    },
  ]);

  const [pendingVerifications, setPendingVerifications] = useState([
    {
      id: 1,
      name: 'Rohit Kulkarni',
      role: 'Freelancer',
      specialty: 'Senior Backend Architect',
      idStatus: 'Government ID & LinkedIn Uploaded',
      date: 'Nov 02, 2026',
    },
    {
      id: 2,
      name: 'Symphony Software Group',
      role: 'Client (Enterprise)',
      specialty: 'Fintech Products',
      idStatus: 'Corporate Tax ID & Certificate of Incorporation',
      date: 'Nov 02, 2026',
    },
  ]);

  const switchRole = (newRole: 'FREELANCER' | 'CLIENT' | 'ADMIN') => {
    setCurrentUser((prev) => ({
      ...prev,
      role: newRole,
      name:
        newRole === 'FREELANCER'
          ? 'Swaraj Bhosale'
          : newRole === 'CLIENT'
          ? 'Apex Financial Labs'
          : 'Super Admin',
      email:
        newRole === 'FREELANCER'
          ? 'swaraj@example.com'
          : newRole === 'CLIENT'
          ? 'hiring@apexlabs.com'
          : 'security@skillbridge.dev',
    }));
    toast.info('Role Switched', `Now viewing platform as ${newRole}`);
  };

  const postJob = (jobData: Omit<Job, 'id' | 'postedTime' | 'proposals'>) => {
    const newJob: Job = {
      ...jobData,
      id: Date.now(),
      postedTime: 'Just now',
      proposals: '0 proposals',
    };
    setJobs((prev) => [newJob, ...prev]);
    toast.success('Project Posted!', 'Your project is now live on the SkillBridge Marketplace.');
  };

  const submitProposal = (jobId: number, _coverLetter: string, proposedBudget: number, duration: string) => {
    setJobs((prev) =>
      prev.map((j) => (j.id === jobId ? { ...j, proposals: 'Proposal Submitted ✓' } : j))
    );
    toast.success('Proposal Submitted!', `Budget: $${proposedBudget} • Estimated: ${duration}`);
  };

  const moveTask = (taskId: string, targetCol: string) => {
    let movedTask: TaskItem | null = null;
    const newTasks = { ...tasks };

    for (const col of Object.keys(newTasks)) {
      const idx = newTasks[col].findIndex((t) => t.id === taskId);
      if (idx !== -1) {
        movedTask = newTasks[col][idx];
        newTasks[col] = newTasks[col].filter((t) => t.id !== taskId);
        break;
      }
    }

    if (movedTask && newTasks[targetCol]) {
      newTasks[targetCol] = [movedTask, ...newTasks[targetCol]];
      setTasks(newTasks);
      toast.info('Task Moved', `"${movedTask.title}" shifted to ${targetCol.replace('_', ' ')}`);
    }
  };

  const addTask = (col: string, taskData: Omit<TaskItem, 'id' | 'commentsCount' | 'filesCount'>) => {
    const id = `TASK-${Math.floor(100 + Math.random() * 900)}`;
    const newTask: TaskItem = {
      ...taskData,
      id,
      commentsCount: 0,
      filesCount: 0,
    };
    setTasks((prev) => ({
      ...prev,
      [col]: [newTask, ...(prev[col] || [])],
    }));
    toast.success('Task Created!', `Added ${id} to ${col.replace('_', ' ')}`);
  };

  const deleteTask = (col: string, taskId: string) => {
    setTasks((prev) => ({
      ...prev,
      [col]: prev[col].filter((t) => t.id !== taskId),
    }));
    toast.info('Task Deleted', `Removed ${taskId}`);
  };

  const submitDeliverable = (num: number, notes: string, url: string) => {
    setMilestones((prev) =>
      prev.map((m) =>
        m.num === num
          ? { ...m, status: 'IN_REVIEW', deliverableNotes: notes, deliverableUrl: url }
          : m
      )
    );
    toast.success('Deliverable Submitted!', `Milestone ${num} submitted for client review.`);
  };

  const approveMilestone = (num: number) => {
    const target = milestones.find((m) => m.num === num);
    if (!target) return;

    setMilestones((prev) =>
      prev.map((m) => (m.num === num ? { ...m, status: 'COMPLETED' } : m))
    );
    setCurrentUser((prev) => ({
      ...prev,
      balance: prev.balance + target.amount,
      escrowLocked: Math.max(0, prev.escrowLocked - target.amount),
    }));
    toast.success('Milestone Approved!', `$${target.amount} escrow released to freelancer.`);
  };

  const suspendAccount = (accountId: string) => {
    setFlaggedAccounts((prev) =>
      prev.map((acc) => (acc.id === accountId ? { ...acc, status: 'SUSPENDED' } : acc))
    );
    toast.error('Account Suspended', `Account ${accountId} is now blocked from transactions.`);
  };

  const approveVerification = (id: number) => {
    setPendingVerifications((prev) => prev.filter((item) => item.id !== id));
    toast.success('Verification Approved', 'User received verified blue checkmark badge.');
  };

  const rejectVerification = (id: number) => {
    setPendingVerifications((prev) => prev.filter((item) => item.id !== id));
    toast.info('Verification Rejected', 'User notified to upload valid government documents.');
  };

  const sendMessage = (content: string) => {
    if (!content.trim()) return;
    const myMsg: ChatMessage = {
      id: `MSG-${Date.now()}`,
      senderId: currentUser.id,
      senderName: currentUser.name,
      senderRole: currentUser.role,
      content,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      isMe: true,
    };
    setMessages((prev) => [...prev, myMsg]);

    // Simulated automated intelligent reply
    setTimeout(() => {
      const replyMsg: ChatMessage = {
        id: `MSG-${Date.now() + 1}`,
        senderId: 'client-1',
        senderName: 'Marcus Vance (Apex Labs)',
        senderRole: 'Client',
        content: `Got your update! I am checking the workspace right now.`,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        isMe: false,
      };
      setMessages((prev) => [...prev, replyMsg]);
    }, 1200);
  };

  return (
    <AppContext.Provider
      value={{
        currentUser,
        switchRole,
        jobs,
        postJob,
        submitProposal,
        tasks,
        moveTask,
        addTask,
        deleteTask,
        milestones,
        submitDeliverable,
        approveMilestone,
        flaggedAccounts,
        suspendAccount,
        pendingVerifications,
        approveVerification,
        rejectVerification,
        messages,
        sendMessage,
      }}
    >
      {children}
    </AppContext.Provider>
  );
};

export const useApp = () => {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useApp must be used within an AppProvider');
  }
  return context;
};
