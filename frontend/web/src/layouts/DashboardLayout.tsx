import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import {
  LayoutDashboard,
  Briefcase,
  FolderGit2,
  FileText,
  DollarSign,
  MessageSquare,
  Sparkles,
  Settings,
  LogOut,
  Menu,
  Bell,
  Search,
  ShieldAlert,
  Users,
  Award,
  Video,
} from 'lucide-react';
import { ThemeToggle } from '@/components/common/ThemeToggle';
import { Avatar } from '@/components/ui/Avatar';
import { Badge } from '@/components/ui/Badge';
import { cn } from '@/utils/cn';

interface SidebarItem {
  label: string;
  href: string;
  icon: React.ReactNode;
  badge?: string;
}

interface DashboardLayoutProps {
  role: 'freelancer' | 'client' | 'admin';
  userName?: string;
  userEmail?: string;
  children: React.ReactNode;
}

export const DashboardLayout: React.FC<DashboardLayoutProps> = ({
  role,
  userName = 'Alex Rivers',
  userEmail = 'alex@example.com',
  children,
}) => {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const location = useLocation();

  const freelancerNav: SidebarItem[] = [
    { label: 'Overview', href: '/freelancer/dashboard', icon: <LayoutDashboard className="w-4 h-4" /> },
    { label: 'Find Work', href: '/marketplace', icon: <Search className="w-4 h-4" />, badge: 'New' },
    { label: 'My Proposals', href: '/freelancer/proposals', icon: <FileText className="w-4 h-4" /> },
    { label: 'Active Contracts', href: '/freelancer/contracts', icon: <Briefcase className="w-4 h-4" /> },
    { label: 'Project Workspace', href: '/workspace/1', icon: <FolderGit2 className="w-4 h-4" /> },
    { label: 'Messages', href: '/messages', icon: <MessageSquare className="w-4 h-4" />, badge: '3' },
    { label: 'Video Meetings', href: '/meetings', icon: <Video className="w-4 h-4" /> },
    { label: 'Portfolio & Profile', href: '/freelancer/profile', icon: <Award className="w-4 h-4" /> },
    { label: 'Earnings & Payouts', href: '/freelancer/earnings', icon: <DollarSign className="w-4 h-4" /> },
    { label: 'AI Career Coach', href: '/ai/career-coach', icon: <Sparkles className="w-4 h-4 text-accent-500" /> },
    { label: 'Settings', href: '/freelancer/settings', icon: <Settings className="w-4 h-4" /> },
  ];

  const clientNav: SidebarItem[] = [
    { label: 'Dashboard', href: '/client/dashboard', icon: <LayoutDashboard className="w-4 h-4" /> },
    { label: 'Post a Project', href: '/client/post-job', icon: <Sparkles className="w-4 h-4 text-brand-500" />, badge: 'AI' },
    { label: 'My Postings', href: '/client/projects', icon: <Briefcase className="w-4 h-4" /> },
    { label: 'Discover Talent', href: '/talent', icon: <Users className="w-4 h-4" /> },
    { label: 'Active Contracts', href: '/client/contracts', icon: <FileText className="w-4 h-4" /> },
    { label: 'Project Workspace', href: '/workspace/1', icon: <FolderGit2 className="w-4 h-4" /> },
    { label: 'Messages', href: '/messages', icon: <MessageSquare className="w-4 h-4" /> },
    { label: 'Financials & Escrow', href: '/client/billing', icon: <DollarSign className="w-4 h-4" /> },
    { label: 'Company Profile', href: '/client/profile', icon: <Award className="w-4 h-4" /> },
    { label: 'Settings', href: '/client/settings', icon: <Settings className="w-4 h-4" /> },
  ];

  const adminNav: SidebarItem[] = [
    { label: 'Console Overview', href: '/admin/dashboard', icon: <LayoutDashboard className="w-4 h-4" /> },
    { label: 'User Governance', href: '/admin/users', icon: <Users className="w-4 h-4" /> },
    { label: 'Project Moderation', href: '/admin/projects', icon: <Briefcase className="w-4 h-4" /> },
    { label: 'AI Fraud Detection', href: '/admin/fraud-detection', icon: <ShieldAlert className="w-4 h-4 text-rose-500" />, badge: 'Alert' },
    { label: 'Escrow & Transactions', href: '/admin/transactions', icon: <DollarSign className="w-4 h-4" /> },
    { label: 'Dispute Arbitration', href: '/admin/disputes', icon: <FileText className="w-4 h-4" /> },
    { label: 'Platform Analytics', href: '/admin/analytics', icon: <Sparkles className="w-4 h-4" /> },
    { label: 'System Settings', href: '/admin/settings', icon: <Settings className="w-4 h-4" /> },
  ];

  const currentNav = role === 'admin' ? adminNav : role === 'client' ? clientNav : freelancerNav;

  const roleTitle = role === 'admin' ? 'Administrator' : role === 'client' ? 'Client' : 'Freelancer';
  const roleBadgeColor = role === 'admin' ? 'danger' : role === 'client' ? 'secondary' : 'primary';

  return (
    <div className="flex h-screen overflow-hidden bg-surface-bg dark:bg-dark-bg text-surface-text dark:text-dark-text transition-colors">
      {/* Sidebar Desktop */}
      <aside className="hidden lg:flex flex-col w-64 border-r border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 transition-colors">
        {/* Brand */}
        <div className="h-16 px-6 flex items-center justify-between border-b border-slate-200 dark:border-slate-800">
          <Link to="/" className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-xl bg-brand-500 flex items-center justify-center text-white font-bold text-sm shadow-soft-sm">
              SB
            </div>
            <span className="font-extrabold text-lg tracking-tight text-slate-900 dark:text-white">
              Skill<span className="text-brand-500">Bridge</span>
            </span>
          </Link>
          <Badge variant={roleBadgeColor as any} size="sm">
            {roleTitle}
          </Badge>
        </div>

        {/* Navigation list */}
        <nav className="flex-1 overflow-y-auto px-4 py-5 space-y-1">
          {currentNav.map((item, idx) => {
            const active = location.pathname === item.href;
            return (
              <Link
                key={idx}
                to={item.href}
                className={cn(
                  'flex items-center justify-between px-3 py-2.5 rounded-xl text-sm font-semibold transition-all duration-150',
                  active
                    ? 'bg-brand-500 text-white shadow-soft-sm'
                    : 'text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 hover:text-slate-900 dark:hover:text-slate-100'
                )}
              >
                <div className="flex items-center gap-3">
                  <span className={active ? 'text-white' : ''}>{item.icon}</span>
                  {item.label}
                </div>
                {item.badge && (
                  <span
                    className={cn(
                      'text-[10px] font-bold px-1.5 py-0.5 rounded-full',
                      active
                        ? 'bg-white/20 text-white'
                        : 'bg-brand-100 dark:bg-brand-950 text-brand-700 dark:text-brand-300'
                    )}
                  >
                    {item.badge}
                  </span>
                )}
              </Link>
            );
          })}
        </nav>

        {/* User Card & Logout */}
        <div className="p-4 border-t border-slate-200 dark:border-slate-800">
          <div className="flex items-center gap-3 p-2 rounded-xl bg-slate-50 dark:bg-slate-800/60">
            <Avatar name={userName} size="sm" status="online" />
            <div className="flex-1 min-w-0">
              <p className="text-xs font-bold text-slate-900 dark:text-white truncate">{userName}</p>
              <p className="text-[11px] text-slate-400 dark:text-slate-500 truncate">{userEmail}</p>
            </div>
            <Link to="/login" title="Log Out" className="text-slate-400 hover:text-rose-600 p-1">
              <LogOut className="w-4 h-4" />
            </Link>
          </div>
        </div>
      </aside>

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        {/* Top Header */}
        <header className="h-16 border-b border-slate-200 dark:border-slate-800 bg-white/80 dark:bg-slate-900/80 backdrop-blur-md px-4 sm:px-8 flex items-center justify-between z-10 transition-colors">
          <div className="flex items-center gap-4">
            <button
              onClick={() => setSidebarOpen(!sidebarOpen)}
              className="lg:hidden p-2 rounded-xl text-slate-700 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800"
            >
              <Menu className="w-5 h-5" />
            </button>
            <div className="relative hidden sm:block w-72">
              <Search className="w-4 h-4 absolute left-3 top-3 text-slate-400" />
              <input
                type="text"
                placeholder="Search projects, talent, tasks..."
                className="w-full pl-9 pr-4 py-2 rounded-xl text-xs bg-slate-100 dark:bg-slate-800 border-none text-slate-900 dark:text-white placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-brand-500"
              />
            </div>
          </div>

          <div className="flex items-center gap-3">
            <ThemeToggle />
            <button className="p-2 rounded-xl text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 relative">
              <Bell className="w-4 h-4" />
              <span className="w-2 h-2 rounded-full bg-brand-500 absolute top-2 right-2 ring-2 ring-white dark:ring-slate-900" />
            </button>
            <div className="h-6 w-px bg-slate-200 dark:bg-slate-800" />
            <Link to={`/${role}/profile`} className="flex items-center gap-2">
              <Avatar name={userName} size="sm" />
            </Link>
          </div>
        </header>

        {/* Scrollable Dashboard Body */}
        <main className="flex-1 overflow-y-auto p-4 sm:p-8">
          <div className="max-w-7xl mx-auto space-y-8">{children}</div>
        </main>
      </div>
    </div>
  );
};
