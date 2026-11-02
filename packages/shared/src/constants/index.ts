// ==========================================
// SkillBridge Design System & Shared Constants
// ==========================================

export const APP_CONFIG = {
  name: 'SkillBridge',
  tagline: 'Where Talent Meets Opportunity.',
  defaultLanguage: 'en',
  supportEmail: 'support@skillbridge.dev',
} as const;

export const BRAND_COLORS = {
  primary: '#5146E5', // Deep Indigo
  primaryHover: '#4338CA',
  secondary: '#8067F5', // Purple
  secondaryHover: '#6D28D9',
  background: '#F8F9FC',
  card: '#FFFFFF',
  text: '#172033',
  textMuted: '#64748B',
  border: '#E2E8F0',
  success: '#10B981',
  warning: '#F59E0B',
  danger: '#EF4444',
  info: '#3B82F6',
} as const;

export const PROJECT_CATEGORIES = [
  'Web Development',
  'Mobile Development',
  'AI & Machine Learning',
  'UI/UX Design',
  'DevOps & Cloud',
  'Data Science & Analytics',
  'Cybersecurity',
  'Blockchain & Web3',
  'Content Writing',
  'Digital Marketing',
] as const;

export const POPULAR_SKILLS = [
  'React',
  'TypeScript',
  'Node.js',
  'Java',
  'Spring Boot',
  'Python',
  'React Native',
  'Flutter',
  'Docker',
  'Kubernetes',
  'AWS',
  'GCP',
  'PostgreSQL',
  'MySQL',
  'Tailwind CSS',
  'GraphQL',
  'Next.js',
  'FastAPI',
  'TensorFlow',
  'PyTorch',
] as const;

export const KANBAN_COLUMNS = [
  { id: 'TODO', label: 'To Do', color: '#64748B' },
  { id: 'IN_PROGRESS', label: 'In Progress', color: '#3B82F6' },
  { id: 'IN_REVIEW', label: 'In Review', color: '#F59E0B' },
  { id: 'COMPLETED', label: 'Completed', color: '#10B981' },
] as const;

