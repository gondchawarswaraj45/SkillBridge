// ==========================================
// SkillBridge Shared Types
// ==========================================

export type UserRole = 'CLIENT' | 'FREELANCER' | 'ADMIN';

export type AccountStatus = 'ACTIVE' | 'SUSPENDED' | 'PENDING_VERIFICATION';

export type AvailabilityStatus = 'AVAILABLE' | 'BUSY' | 'NOT_LOOKING';

export type ProjectStatus = 'DRAFT' | 'OPEN' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';

export type ProposalStatus = 'SUBMITTED' | 'SHORTLISTED' | 'ACCEPTED' | 'REJECTED' | 'WITHDRAWN';

export type ContractStatus = 'ACTIVE' | 'PAUSED' | 'COMPLETED' | 'TERMINATED';

export type MilestoneStatus = 'PENDING' | 'IN_PROGRESS' | 'SUBMITTED' | 'APPROVED' | 'DISPUTED';

export type TaskStatus = 'TODO' | 'IN_PROGRESS' | 'IN_REVIEW' | 'COMPLETED';

export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export interface UserSummary {
  id: number;
  fullName: string;
  email: string;
  phone?: string;
  role: UserRole;
  accountStatus: AccountStatus;
  emailVerified: boolean;
  avatarUrl?: string;
  createdAt: string;
}

export interface FreelancerProfile {
  id: number;
  userId: number;
  professionalTitle: string;
  description: string;
  hourlyRate: number;
  experienceYears: number;
  availability: AvailabilityStatus;
  location?: string;
  skills: string[];
  resumeUrl?: string;
  githubUrl?: string;
  linkedinUrl?: string;
  completedProjectsCount: number;
  ratingAverage: number;
}

export interface ClientProfile {
  id: number;
  userId: number;
  companyName: string;
  companyDescription?: string;
  industry?: string;
  website?: string;
  companySize?: string;
  logoUrl?: string;
  totalSpent: number;
}

export interface ProjectSummary {
  id: number;
  clientId: number;
  clientName: string;
  title: string;
  description: string;
  category: string;
  requiredSkills: string[];
  budgetMin: number;
  budgetMax: number;
  duration: string;
  status: ProjectStatus;
  proposalsCount: number;
  aiMatchScore?: number;
  createdAt: string;
  deadline?: string;
}

export interface ProposalSummary {
  id: number;
  projectId: number;
  freelancerId: number;
  freelancerName: string;
  freelancerAvatar?: string;
  coverLetter: string;
  proposedBudget: number;
  estimatedDuration: string;
  status: ProposalStatus;
  createdAt: string;
}

export interface ApiResponse<T = unknown> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

