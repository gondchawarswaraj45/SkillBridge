import React from 'react';
import { Link } from 'react-router-dom';
import {
  DollarSign,
  Briefcase,
  FileText,
  CheckCircle,
  Sparkles,
  ArrowUpRight,
  Clock,
  ChevronRight,
  TrendingUp,
} from 'lucide-react';
import { DashboardLayout } from '@/layouts/DashboardLayout';
import { StatCard } from '@/components/ui/StatCard';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';
import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from 'recharts';

const earningsData = [
  { month: 'May', earnings: 2400 },
  { month: 'Jun', earnings: 3200 },
  { month: 'Jul', earnings: 2800 },
  { month: 'Aug', earnings: 4500 },
  { month: 'Sep', earnings: 5100 },
  { month: 'Oct', earnings: 6850 },
];

const recommendedJobs = [
  {
    id: 1,
    title: 'Full-Stack React & Spring Boot Fintech Dashboard',
    client: 'Apex Financial Labs',
    budget: '$4,500 - $6,000',
    type: 'Fixed Price',
    skills: ['React', 'Spring Boot', 'TypeScript', 'MySQL'],
    matchScore: 98,
    postedTime: '2 hours ago',
  },
  {
    id: 2,
    title: 'Cross-Platform React Native Mobile App (Expo SDK 52)',
    client: 'HealthSync Innovations',
    budget: '$65 / hr',
    type: 'Hourly',
    skills: ['React Native', 'Expo', 'Zod', 'Tailwind CSS'],
    matchScore: 94,
    postedTime: '4 hours ago',
  },
  {
    id: 3,
    title: 'AI Semantic Document Parsing Pipeline with Groq/Gemini',
    client: 'DataNexus Global',
    budget: '$3,800',
    type: 'Milestone Escrow',
    skills: ['Python', 'Spring AI', 'REST API', 'Docker'],
    matchScore: 91,
    postedTime: '6 hours ago',
  },
];

export const FreelancerDashboard: React.FC = () => {
  return (
    <DashboardLayout role="freelancer" userName="Swaraj Bhosale" userEmail="swaraj@example.com">
      {/* Welcome Banner */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 p-6 sm:p-8 rounded-3xl bg-gradient-to-r from-brand-600 via-indigo-600 to-accent-600 text-white shadow-soft-lg">
        <div>
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-white/20 text-xs font-semibold backdrop-blur-sm mb-3">
            <Sparkles className="w-3.5 h-3.5 text-accent-300" /> Top Rated Plus Freelancer
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight">
            Welcome back, Swaraj! 👋
          </h1>
          <p className="text-brand-100 text-sm mt-1 max-w-xl">
            You have <span className="font-bold text-white">3 active contracts</span> and{' '}
            <span className="font-bold text-white">4 new project invitations</span> matching your verified
            skills.
          </p>
        </div>
        <div className="flex items-center gap-3">
          <Link to="/marketplace">
            <Button size="md" className="bg-white text-brand-600 hover:bg-slate-100 border-none font-bold">
              Find New Work
            </Button>
          </Link>
          <Link to="/freelancer/profile">
            <Button size="md" variant="outline" className="border-white/30 text-white hover:bg-white/10">
              Edit Profile
            </Button>
          </Link>
        </div>
      </div>

      {/* KPI Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <StatCard
          title="Total Earnings"
          value="$24,850"
          change="+18.4%"
          isPositive={true}
          icon={<DollarSign className="w-5 h-5" />}
        />
        <StatCard
          title="Active Contracts"
          value="3"
          change="On Schedule"
          isPositive={true}
          icon={<Briefcase className="w-5 h-5 text-accent-600 dark:text-accent-400" />}
          iconBgColor="bg-accent-50 text-accent-600 dark:bg-accent-950/70"
        />
        <StatCard
          title="Proposals Sent"
          value="12"
          change="4 Shortlisted"
          isPositive={true}
          icon={<FileText className="w-5 h-5 text-amber-600 dark:text-amber-400" />}
          iconBgColor="bg-amber-50 text-amber-600 dark:bg-amber-950/70"
        />
        <StatCard
          title="Profile Health"
          value="95%"
          change="All Badges Active"
          isPositive={true}
          icon={<CheckCircle className="w-5 h-5 text-emerald-600 dark:text-emerald-400" />}
          iconBgColor="bg-emerald-50 text-emerald-600 dark:bg-emerald-950/70"
        />
      </div>

      {/* Chart & Active Workspace Section */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
        {/* Earnings Trend Chart */}
        <Card className="lg:col-span-8 p-6">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h3 className="font-extrabold text-lg text-slate-900 dark:text-white">Earnings Analytics</h3>
              <p className="text-xs text-slate-500 dark:text-slate-400">Monthly revenue breakdown over the last 6 months</p>
            </div>
            <Badge variant="success" size="sm">
              <TrendingUp className="w-3 h-3 mr-1" /> +24% YoY Growth
            </Badge>
          </div>
          <div className="h-64 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={earningsData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <defs>
                  <linearGradient id="earningsGradient" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#5146E5" stopOpacity={0.4} />
                    <stop offset="95%" stopColor="#5146E5" stopOpacity={0.0} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#E2E8F0" opacity={0.5} />
                <XAxis dataKey="month" stroke="#94A3B8" fontSize={12} tickLine={false} />
                <YAxis stroke="#94A3B8" fontSize={12} tickLine={false} />
                <Tooltip
                  contentStyle={{
                    backgroundColor: '#1E293B',
                    borderRadius: '12px',
                    color: '#fff',
                    border: 'none',
                    fontSize: '12px',
                  }}
                  formatter={(val: any) => [`$${val}`, 'Earnings']}
                />
                <Area
                  type="monotone"
                  dataKey="earnings"
                  stroke="#5146E5"
                  strokeWidth={3}
                  fillOpacity={1}
                  fill="url(#earningsGradient)"
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </Card>

        {/* Current Active Milestones */}
        <Card className="lg:col-span-4 p-6 flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-extrabold text-lg text-slate-900 dark:text-white">Active Milestone</h3>
              <Badge variant="primary" size="sm">Milestone 2 / 4</Badge>
            </div>
            <p className="text-sm font-bold text-slate-800 dark:text-slate-100">
              Enterprise Dashboard Integration
            </p>
            <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">Client: CloudMatrix Systems</p>

            <div className="mt-5 space-y-2">
              <div className="flex justify-between text-xs font-semibold">
                <span className="text-slate-500">Progress</span>
                <span className="text-brand-500">75%</span>
              </div>
              <div className="w-full bg-slate-100 dark:bg-slate-800 h-2 rounded-full overflow-hidden">
                <div className="bg-brand-500 h-full rounded-full transition-all duration-300" style={{ width: '75%' }} />
              </div>
            </div>

            <div className="mt-6 p-4 rounded-xl bg-slate-50 dark:bg-slate-800/60 border border-slate-200 dark:border-slate-700/60 text-xs space-y-2">
              <div className="flex items-center justify-between">
                <span className="text-slate-500">Milestone Escrow:</span>
                <span className="font-bold text-emerald-600 dark:text-emerald-400">$1,500 Locked</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-slate-500">Deadline:</span>
                <span className="font-semibold text-slate-700 dark:text-slate-300 flex items-center gap-1">
                  <Clock className="w-3 h-3 text-amber-500" /> Nov 10, 2026
                </span>
              </div>
            </div>
          </div>

          <div className="pt-6">
            <Link to="/workspace/1">
              <Button variant="primary" className="w-full gap-2">
                Open Project Workspace <ArrowUpRight className="w-4 h-4" />
              </Button>
            </Link>
          </div>
        </Card>
      </div>

      {/* AI Recommended Projects */}
      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="font-extrabold text-xl text-slate-900 dark:text-white">AI-Recommended Projects</h3>
            <p className="text-xs text-slate-500 dark:text-slate-400">
              Curated based on your verified skills, rating, and rate preferences
            </p>
          </div>
          <Link to="/marketplace">
            <Button variant="ghost" size="sm" className="gap-1 text-brand-500 font-bold">
              View All 180+ Jobs <ChevronRight className="w-4 h-4" />
            </Button>
          </Link>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {recommendedJobs.map((job) => (
            <Card key={job.id} hoverEffect className="flex flex-col justify-between p-6">
              <div>
                <div className="flex items-start justify-between gap-2 mb-3">
                  <Badge variant="success" size="sm" className="font-bold">
                    <Sparkles className="w-3 h-3 mr-1" /> {job.matchScore}% Match
                  </Badge>
                  <span className="text-[11px] font-medium text-slate-400">{job.postedTime}</span>
                </div>

                <h4 className="font-bold text-base text-slate-900 dark:text-white line-clamp-2 hover:text-brand-500 transition-colors">
                  {job.title}
                </h4>

                <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">Client: {job.client}</p>

                <p className="mt-3 text-base font-extrabold text-brand-600 dark:text-brand-400">
                  {job.budget}{' '}
                  <span className="text-xs font-normal text-slate-400">({job.type})</span>
                </p>

                <div className="mt-4 flex flex-wrap gap-1.5">
                  {job.skills.map((skill, sIdx) => (
                    <span
                      key={sIdx}
                      className="px-2 py-0.5 rounded-lg bg-slate-100 dark:bg-slate-800 text-[11px] font-medium text-slate-600 dark:text-slate-300"
                    >
                      {skill}
                    </span>
                  ))}
                </div>
              </div>

              <div className="mt-6 pt-4 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between">
                <Link to={`/projects/${job.id}`} className="text-xs font-bold text-brand-500 hover:underline">
                  View Scope
                </Link>
                <Link to={`/projects/${job.id}/apply`}>
                  <Button size="sm" variant="primary">
                    Submit Proposal
                  </Button>
                </Link>
              </div>
            </Card>
          ))}
        </div>
      </div>
    </DashboardLayout>
  );
};

