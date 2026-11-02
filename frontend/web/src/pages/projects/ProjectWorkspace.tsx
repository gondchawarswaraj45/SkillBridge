import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import {
  FolderGit2,
  CheckSquare,
  Plus,
  MessageSquare,
  Video,
  FileText,
  DollarSign,
  ChevronRight,
  ShieldCheck,
  Calendar,
  Paperclip,
} from 'lucide-react';
import { DashboardLayout } from '@/layouts/DashboardLayout';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';
import { Avatar } from '@/components/ui/Avatar';
import { cn } from '@/utils/cn';

interface Task {
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

const initialTasks: Record<string, Task[]> = {
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

const columns = [
  { id: 'TODO', title: 'To Do', color: 'border-t-slate-400' },
  { id: 'IN_PROGRESS', title: 'In Progress', color: 'border-t-brand-500' },
  { id: 'IN_REVIEW', title: 'In Review', color: 'border-t-amber-500' },
  { id: 'COMPLETED', title: 'Completed', color: 'border-t-emerald-500' },
];

export const ProjectWorkspace: React.FC = () => {
  const [tasks] = useState(initialTasks);
  const [activeTab, setActiveTab] = useState<'kanban' | 'milestones' | 'files' | 'meetings'>('kanban');

  const priorityBadge = (priority: Task['priority']) => {
    switch (priority) {
      case 'CRITICAL':
        return <Badge variant="danger" size="sm">Critical</Badge>;
      case 'HIGH':
        return <Badge variant="warning" size="sm">High</Badge>;
      case 'MEDIUM':
        return <Badge variant="primary" size="sm">Medium</Badge>;
      default:
        return <Badge variant="neutral" size="sm">Low</Badge>;
    }
  };

  return (
    <DashboardLayout role="freelancer" userName="Swaraj Bhosale">
      {/* Workspace Header */}
      <div className="space-y-4">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 text-xs font-semibold text-slate-400 mb-1">
              <span>Contracts</span>
              <ChevronRight className="w-3.5 h-3.5" />
              <span>CTR-4029</span>
              <ChevronRight className="w-3.5 h-3.5" />
              <span className="text-brand-500">Live Workspace</span>
            </div>
            <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white">
              Enterprise Fintech Dashboard & Analytics Platform
            </h1>
            <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">
              Client: <span className="font-semibold text-slate-700 dark:text-slate-300">Apex Financial Labs</span> • Escrow Protected • Started Oct 28, 2026
            </p>
          </div>

          <div className="flex items-center gap-2.5">
            <Link to="/messages">
              <Button size="sm" variant="outline" className="gap-1.5">
                <MessageSquare className="w-4 h-4 text-brand-500" /> Chat
              </Button>
            </Link>
            <Link to="/meetings">
              <Button size="sm" variant="outline" className="gap-1.5">
                <Video className="w-4 h-4 text-accent-500" /> Meet
              </Button>
            </Link>
            <Button size="sm" variant="primary" className="gap-1.5">
              <Plus className="w-4 h-4" /> Add Task
            </Button>
          </div>
        </div>

        {/* Milestone Escrow Status Banner */}
        <div className="p-4 rounded-2xl bg-brand-50 dark:bg-brand-950/60 border border-brand-200 dark:border-brand-800/80 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="p-2.5 rounded-xl bg-brand-500 text-white">
              <ShieldCheck className="w-5 h-5" />
            </div>
            <div>
              <p className="text-xs font-bold text-slate-900 dark:text-white">
                Milestone 2 of 4: Spring Boot Backend & Security Architecture
              </p>
              <p className="text-xs text-brand-700 dark:text-brand-300 mt-0.5">
                $1,500.00 USD securely held in Escrow • Deadline: Nov 12, 2026
              </p>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <Button size="sm" variant="success" className="text-xs font-bold">
              Submit Deliverable for Approval
            </Button>
          </div>
        </div>

        {/* Workspace Navigation Tabs */}
        <div className="flex items-center gap-2 border-b border-slate-200 dark:border-slate-800 pt-2">
          {[
            { id: 'kanban', label: 'Kanban Board', icon: <CheckSquare className="w-4 h-4" /> },
            { id: 'milestones', label: 'Milestones & Payments', icon: <DollarSign className="w-4 h-4" /> },
            { id: 'files', label: 'Project Vault & Files', icon: <FileText className="w-4 h-4" /> },
            { id: 'meetings', label: 'Video Meetings & Minutes', icon: <Video className="w-4 h-4" /> },
          ].map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id as any)}
              className={cn(
                'flex items-center gap-2 px-4 py-3 text-xs font-bold border-b-2 transition-all duration-150',
                activeTab === tab.id
                  ? 'border-brand-500 text-brand-600 dark:text-brand-400'
                  : 'border-transparent text-slate-500 hover:text-slate-800 dark:hover:text-slate-200'
              )}
            >
              {tab.icon}
              {tab.label}
            </button>
          ))}
        </div>
      </div>

      {/* Tab 1: Kanban Board */}
      {activeTab === 'kanban' && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 items-start">
          {columns.map((col) => (
            <div
              key={col.id}
              className={cn(
                'rounded-2xl bg-slate-100/70 dark:bg-slate-900/60 p-4 border-t-4 border border-slate-200 dark:border-slate-800 shadow-soft-sm space-y-3.5',
                col.color
              )}
            >
              {/* Column Header */}
              <div className="flex items-center justify-between pb-2 border-b border-slate-200/80 dark:border-slate-800">
                <div className="flex items-center gap-2">
                  <h3 className="font-extrabold text-sm text-slate-800 dark:text-slate-100">
                    {col.title}
                  </h3>
                  <span className="px-2 py-0.5 rounded-full text-[11px] font-bold bg-white dark:bg-slate-800 text-slate-600 dark:text-slate-300">
                    {tasks[col.id]?.length || 0}
                  </span>
                </div>
                <button className="text-slate-400 hover:text-slate-600 dark:hover:text-slate-200">
                  <Plus className="w-4 h-4" />
                </button>
              </div>

              {/* Task Cards */}
              <div className="space-y-3">
                {tasks[col.id]?.map((task) => (
                  <Card
                    key={task.id}
                    hoverEffect
                    className="p-4 bg-white dark:bg-slate-800/90 border border-slate-200/80 dark:border-slate-700/80 space-y-3 cursor-grab"
                  >
                    <div className="flex items-center justify-between">
                      <span className="text-[10px] font-mono font-bold text-slate-400">
                        {task.id}
                      </span>
                      {priorityBadge(task.priority)}
                    </div>

                    <h4 className="text-xs font-bold text-slate-900 dark:text-white leading-snug">
                      {task.title}
                    </h4>

                    <p className="text-[11px] text-slate-500 dark:text-slate-400 line-clamp-2 leading-relaxed">
                      {task.description}
                    </p>

                    <div className="pt-2 border-t border-slate-100 dark:border-slate-700 flex items-center justify-between text-[11px] text-slate-400">
                      <div className="flex items-center gap-2">
                        <span className="flex items-center gap-1 font-semibold text-slate-600 dark:text-slate-300">
                          <Calendar className="w-3 h-3 text-slate-400" /> {task.dueDate}
                        </span>
                        {task.filesCount > 0 && (
                          <span className="flex items-center gap-0.5">
                            <Paperclip className="w-3 h-3" /> {task.filesCount}
                          </span>
                        )}
                        {task.commentsCount > 0 && (
                          <span className="flex items-center gap-0.5">
                            <MessageSquare className="w-3 h-3" /> {task.commentsCount}
                          </span>
                        )}
                      </div>
                      <Avatar name={task.assignee} size="sm" />
                    </div>
                  </Card>
                ))}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Tab 2: Milestones & Payments */}
      {activeTab === 'milestones' && (
        <Card className="p-6 space-y-6">
          <div>
            <h3 className="font-extrabold text-lg text-slate-900 dark:text-white">
              Contract Milestones & Escrow Ledger
            </h3>
            <p className="text-xs text-slate-500 dark:text-slate-400">
              Contract Total: $6,000.00 USD • $1,500.00 Released • $1,500.00 In Escrow • $3,000.00 Remaining
            </p>
          </div>

          <div className="space-y-4">
            {[
              {
                num: 1,
                title: 'System Architecture Blueprint & Monorepo Setup',
                amount: '$1,500.00',
                status: 'APPROVED & RELEASED',
                date: 'Completed Nov 01, 2026',
              },
              {
                num: 2,
                title: 'Spring Boot REST APIs, JWT Auth & Database Schema',
                amount: '$1,500.00',
                status: 'FUNDS IN ESCROW',
                date: 'Due Nov 12, 2026',
              },
              {
                num: 3,
                title: 'React Frontend Implementation & Real-Time WebSockets',
                amount: '$1,500.00',
                status: 'UPCOMING',
                date: 'Due Nov 22, 2026',
              },
              {
                num: 4,
                title: 'AI Talent Matching Engine & Production Deployment',
                amount: '$1,500.00',
                status: 'UPCOMING',
                date: 'Due Dec 05, 2026',
              },
            ].map((m) => (
              <div
                key={m.num}
                className="p-5 rounded-2xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-800/50 flex flex-col sm:flex-row sm:items-center justify-between gap-4"
              >
                <div>
                  <div className="flex items-center gap-2">
                    <span className="font-bold text-sm text-slate-900 dark:text-white">
                      Milestone {m.num}: {m.title}
                    </span>
                    <Badge
                      variant={
                        m.status.includes('APPROVED')
                          ? 'success'
                          : m.status.includes('ESCROW')
                          ? 'primary'
                          : 'neutral'
                      }
                      size="sm"
                    >
                      {m.status}
                    </Badge>
                  </div>
                  <p className="text-xs text-slate-400 mt-1">{m.date}</p>
                </div>
                <div className="flex items-center gap-4">
                  <span className="text-lg font-extrabold text-slate-900 dark:text-white">
                    {m.amount}
                  </span>
                  {m.status.includes('ESCROW') && (
                    <Button size="sm" variant="primary">
                      Submit Work
                    </Button>
                  )}
                </div>
              </div>
            ))}
          </div>
        </Card>
      )}

      {/* Tab 3 & 4: Quick Preview */}
      {(activeTab === 'files' || activeTab === 'meetings') && (
        <Card className="p-12 text-center space-y-3">
          <FolderGit2 className="w-10 h-10 text-brand-500 mx-auto" />
          <h3 className="font-extrabold text-lg text-slate-900 dark:text-white">
            {activeTab === 'files' ? 'Project Asset Vault' : 'Live Video Meeting Room'}
          </h3>
          <p className="text-xs text-slate-500 dark:text-slate-400 max-w-sm mx-auto">
            {activeTab === 'files'
              ? 'Shared deliverables, design specifications, and code attachments are encrypted and preserved here.'
              : 'Launch end-to-end video meetings with AI automated transcript minutes generation.'}
          </p>
          <Button size="sm" variant="primary">
            {activeTab === 'files' ? 'Upload Asset' : 'Schedule Meeting'}
          </Button>
        </Card>
      )}
    </DashboardLayout>
  );
};
