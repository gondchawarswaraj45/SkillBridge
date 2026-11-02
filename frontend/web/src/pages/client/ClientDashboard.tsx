import React from 'react';
import { Link } from 'react-router-dom';
import {
  DollarSign,
  Briefcase,
  Users,
  Sparkles,
  ArrowRight,
  Plus,
  ShieldCheck,
  Star,
  Clock,
} from 'lucide-react';
import { DashboardLayout } from '@/layouts/DashboardLayout';
import { StatCard } from '@/components/ui/StatCard';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';
import { Avatar } from '@/components/ui/Avatar';

const ongoingContracts = [
  {
    id: 1,
    title: 'Enterprise Fintech Dashboard & Analytics Platform',
    freelancer: 'Swaraj Bhosale',
    freelancerRole: 'Full Stack Java & React Engineer',
    milestoneCurrent: 'Milestone 2: Spring Boot REST API & Security',
    amount: '$4,500',
    escrowLocked: '$1,500',
    progress: 75,
    dueDate: 'Nov 12, 2026',
  },
  {
    id: 2,
    title: 'Cross-Platform React Native Healthcare App',
    freelancer: 'Elena Rostova',
    freelancerRole: 'Mobile Architecture Specialist',
    milestoneCurrent: 'Milestone 1: Expo Router Setup & Auth Flow',
    amount: '$3,800',
    escrowLocked: '$1,200',
    progress: 40,
    dueDate: 'Nov 20, 2026',
  },
];

const recommendedTalent = [
  {
    id: 101,
    name: 'Marcus Vance',
    title: 'Senior AI Engineer & LLM Integration Expert',
    rate: '$85/hr',
    earned: '$90k+ earned',
    rating: 4.98,
    matchScore: 97,
    skills: ['Python', 'Spring AI', 'Groq', 'Vector DB', 'PyTorch'],
    available: true,
  },
  {
    id: 102,
    name: 'Aisha Al-Mansoor',
    title: 'Lead UI/UX Product Designer & Design Systems',
    rate: '$75/hr',
    earned: '$65k+ earned',
    rating: 5.0,
    matchScore: 95,
    skills: ['Figma', 'Design Systems', 'Tailwind CSS', 'SaaS UX'],
    available: true,
  },
  {
    id: 103,
    name: 'David Kim',
    title: 'Cloud DevOps & Kubernetes Architect',
    rate: '$90/hr',
    earned: '$120k+ earned',
    rating: 4.95,
    matchScore: 93,
    skills: ['AWS', 'Docker', 'Kubernetes', 'CI/CD', 'Terraform'],
    available: true,
  },
];

export const ClientDashboard: React.FC = () => {
  return (
    <DashboardLayout role="client" userName="Nexus Ventures" userEmail="hiring@nexusventures.com">
      {/* Client Welcome Banner */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 p-6 sm:p-8 rounded-3xl bg-gradient-to-r from-accent-600 via-indigo-600 to-brand-600 text-white shadow-soft-lg">
        <div>
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-white/20 text-xs font-semibold backdrop-blur-sm mb-3">
            <ShieldCheck className="w-3.5 h-3.5 text-emerald-300" /> Verified Enterprise Client
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight">
            Client Supervision Console
          </h1>
          <p className="text-accent-100 text-sm mt-1 max-w-xl">
            You have <span className="font-bold text-white">2 active contracts</span> with{' '}
            <span className="font-bold text-white">$2,700 safely held in escrow</span>, and{' '}
            <span className="font-bold text-white">18 new proposals</span> pending review.
          </p>
        </div>
        <div className="flex items-center gap-3">
          <Link to="/client/post-job">
            <Button size="md" className="bg-white text-accent-700 hover:bg-slate-100 border-none font-bold gap-2">
              <Plus className="w-4 h-4" /> Post a Project (AI)
            </Button>
          </Link>
          <Link to="/talent">
            <Button size="md" variant="outline" className="border-white/30 text-white hover:bg-white/10">
              Browse Talent
            </Button>
          </Link>
        </div>
      </div>

      {/* KPI Stats */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <StatCard
          title="Total Capital Deployed"
          value="$54,200"
          change="+12.5%"
          isPositive={true}
          icon={<DollarSign className="w-5 h-5 text-accent-600 dark:text-accent-400" />}
          iconBgColor="bg-accent-50 text-accent-600 dark:bg-accent-950/70"
        />
        <StatCard
          title="Active Contracts"
          value="2"
          change="100% on schedule"
          isPositive={true}
          icon={<Briefcase className="w-5 h-5" />}
        />
        <StatCard
          title="Talent Hired"
          value="8"
          change="Avg 4.95★ rating"
          isPositive={true}
          icon={<Users className="w-5 h-5 text-emerald-600 dark:text-emerald-400" />}
          iconBgColor="bg-emerald-50 text-emerald-600 dark:bg-emerald-950/70"
        />
        <StatCard
          title="Escrow Balance"
          value="$2,700"
          change="Locked & Protected"
          isPositive={true}
          icon={<ShieldCheck className="w-5 h-5 text-brand-600 dark:text-brand-400" />}
        />
      </div>

      {/* Ongoing Contracts Progress */}
      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="font-extrabold text-xl text-slate-900 dark:text-white">Active Contract Workspaces</h3>
            <p className="text-xs text-slate-500 dark:text-slate-400">
              Live milestone monitoring, deliverable reviews, and escrow releases
            </p>
          </div>
          <Link to="/client/contracts">
            <Button variant="ghost" size="sm" className="font-bold text-accent-500">
              View All Contracts <ArrowRight className="w-4 h-4 ml-1" />
            </Button>
          </Link>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {ongoingContracts.map((contract) => (
            <Card key={contract.id} className="p-6 flex flex-col justify-between space-y-5">
              <div>
                <div className="flex items-start justify-between">
                  <div>
                    <h4 className="font-extrabold text-lg text-slate-900 dark:text-white">
                      {contract.title}
                    </h4>
                    <div className="flex items-center gap-2 mt-2">
                      <Avatar name={contract.freelancer} size="sm" status="online" />
                      <div>
                        <p className="text-xs font-bold text-slate-800 dark:text-slate-200">
                          {contract.freelancer}
                        </p>
                        <p className="text-[11px] text-slate-400">{contract.freelancerRole}</p>
                      </div>
                    </div>
                  </div>
                  <Badge variant="primary" size="sm">
                    Active
                  </Badge>
                </div>

                <div className="mt-6 p-4 rounded-2xl bg-slate-50 dark:bg-slate-800/60 border border-slate-200/80 dark:border-slate-700/80 space-y-3">
                  <div className="flex items-center justify-between text-xs">
                    <span className="font-semibold text-slate-500 dark:text-slate-400">
                      Current Milestone:
                    </span>
                    <span className="font-bold text-slate-800 dark:text-slate-200">
                      {contract.milestoneCurrent}
                    </span>
                  </div>
                  <div className="flex items-center justify-between text-xs">
                    <span className="text-slate-500">Milestone Escrow:</span>
                    <span className="font-bold text-emerald-600 dark:text-emerald-400">
                      {contract.escrowLocked} Protected
                    </span>
                  </div>
                  <div className="space-y-1">
                    <div className="flex justify-between text-[11px] font-bold">
                      <span className="text-slate-400">Completion</span>
                      <span className="text-brand-500">{contract.progress}%</span>
                    </div>
                    <div className="w-full bg-slate-200 dark:bg-slate-700 h-2 rounded-full overflow-hidden">
                      <div
                        className="bg-brand-500 h-full rounded-full transition-all"
                        style={{ width: `${contract.progress}%` }}
                      />
                    </div>
                  </div>
                </div>
              </div>

              <div className="flex items-center justify-between pt-2">
                <span className="text-xs font-medium text-slate-400 flex items-center gap-1">
                  <Clock className="w-3.5 h-3.5" /> Due: {contract.dueDate}
                </span>
                <Link to="/workspace/1">
                  <Button size="sm" variant="primary">
                    Open Kanban Workspace
                  </Button>
                </Link>
              </div>
            </Card>
          ))}
        </div>
      </div>

      {/* AI-Matched Candidates for Your Postings */}
      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="font-extrabold text-xl text-slate-900 dark:text-white">AI-Recommended Talent</h3>
            <p className="text-xs text-slate-500 dark:text-slate-400">
              Top engineers matching your posted technology stack and requirements
            </p>
          </div>
          <Link to="/talent">
            <Button variant="ghost" size="sm" className="font-bold text-brand-500">
              Explore 15,000+ Profiles <ArrowRight className="w-4 h-4 ml-1" />
            </Button>
          </Link>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {recommendedTalent.map((talent) => (
            <Card key={talent.id} hoverEffect className="p-6 flex flex-col justify-between">
              <div>
                <div className="flex items-start justify-between">
                  <Avatar name={talent.name} size="lg" status="online" />
                  <Badge variant="success" size="sm">
                    <Sparkles className="w-3 h-3 mr-1" /> {talent.matchScore}% Match
                  </Badge>
                </div>

                <div className="mt-4">
                  <h4 className="font-extrabold text-base text-slate-900 dark:text-white">
                    {talent.name}
                  </h4>
                  <p className="text-xs text-brand-600 dark:text-brand-400 font-semibold mt-0.5">
                    {talent.title}
                  </p>
                </div>

                <div className="mt-3 flex items-center gap-3 text-xs font-bold text-slate-600 dark:text-slate-300">
                  <span className="flex items-center gap-1 text-amber-500">
                    <Star className="w-3.5 h-3.5 fill-amber-500" /> {talent.rating}
                  </span>
                  <span>•</span>
                  <span>{talent.rate}</span>
                  <span>•</span>
                  <span className="text-slate-400">{talent.earned}</span>
                </div>

                <div className="mt-4 flex flex-wrap gap-1.5">
                  {talent.skills.map((skill, idx) => (
                    <span
                      key={idx}
                      className="px-2 py-0.5 rounded-lg bg-slate-100 dark:bg-slate-800 text-[11px] font-medium text-slate-600 dark:text-slate-300"
                    >
                      {skill}
                    </span>
                  ))}
                </div>
              </div>

              <div className="mt-6 pt-4 border-t border-slate-100 dark:border-slate-800 flex items-center gap-2">
                <Link to={`/freelancers/${talent.id}`} className="flex-1">
                  <Button variant="outline" size="sm" className="w-full">
                    Profile
                  </Button>
                </Link>
                <Link to={`/client/hire/${talent.id}`} className="flex-1">
                  <Button variant="primary" size="sm" className="w-full">
                    Invite to Job
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
