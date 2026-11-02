import React from 'react';
import { Link } from 'react-router-dom';
import {
  ShieldAlert,
  Users,
  Briefcase,
  DollarSign,
  TrendingUp,
  CheckCircle2,
  ArrowRight,
  Eye,
} from 'lucide-react';
import { DashboardLayout } from '@/layouts/DashboardLayout';
import { StatCard } from '@/components/ui/StatCard';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';
import { Avatar } from '@/components/ui/Avatar';

const flaggedAccounts = [
  {
    id: 'USR-8941',
    user: 'CrypticSolutions Ltd',
    role: 'Client',
    riskLevel: 'HIGH',
    reason: 'Suspicious Off-Platform Payment Solicitation in Messages',
    aiConfidence: '96% confidence',
    date: '10 mins ago',
  },
  {
    id: 'USR-8930',
    user: 'DevMaster99',
    role: 'Freelancer',
    riskLevel: 'MEDIUM',
    reason: 'Duplicate Portfolio Code Repositories Across Multiple Accounts',
    aiConfidence: '88% confidence',
    date: '35 mins ago',
  },
  {
    id: 'USR-8912',
    user: 'UltraVentures Inc',
    role: 'Client',
    riskLevel: 'LOW',
    reason: 'Abnormally High Budget Discrepancy ($250k for basic HTML script)',
    aiConfidence: '82% confidence',
    date: '2 hours ago',
  },
];

const pendingVerifications = [
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
];

export const AdminDashboard: React.FC = () => {
  return (
    <DashboardLayout role="admin" userName="Super Admin" userEmail="security@skillbridge.dev">
      {/* Admin Alert Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 p-6 sm:p-8 rounded-3xl bg-slate-900 text-white border border-slate-800 shadow-soft-lg">
        <div>
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-rose-500/20 text-rose-400 text-xs font-semibold mb-3 border border-rose-500/30">
            <ShieldAlert className="w-3.5 h-3.5" /> AI Fraud Guard Active (Real-Time Heuristics)
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight">
            SkillBridge Platform Governance Console
          </h1>
          <p className="text-slate-400 text-sm mt-1 max-w-xl">
            System status is healthy. <span className="text-white font-bold">3 flagged anomalies</span>{' '}
            require manual moderator review today.
          </p>
        </div>
        <div className="flex items-center gap-3">
          <Link to="/admin/fraud-detection">
            <Button size="md" variant="danger" className="gap-2 font-bold">
              Review AI Alerts (3)
            </Button>
          </Link>
          <Link to="/admin/analytics">
            <Button size="md" variant="outline" className="border-slate-700 text-slate-200 hover:bg-slate-800">
              Platform Metrics
            </Button>
          </Link>
        </div>
      </div>

      {/* High-Level Platform KPIs */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <StatCard
          title="Platform GMV (Volume)"
          value="$1.42M"
          change="+28.3%"
          isPositive={true}
          icon={<DollarSign className="w-5 h-5 text-emerald-600 dark:text-emerald-400" />}
          iconBgColor="bg-emerald-50 text-emerald-600 dark:bg-emerald-950/70"
        />
        <StatCard
          title="Platform Fee Revenue (10%)"
          value="$142,000"
          change="+24.1%"
          isPositive={true}
          icon={<TrendingUp className="w-5 h-5 text-brand-600 dark:text-brand-400" />}
        />
        <StatCard
          title="Total Registered Users"
          value="24,180"
          change="+1,250 this week"
          isPositive={true}
          icon={<Users className="w-5 h-5 text-accent-600 dark:text-accent-400" />}
          iconBgColor="bg-accent-50 text-accent-600 dark:bg-accent-950/70"
        />
        <StatCard
          title="Escrow Vault Locked"
          value="$384,500"
          change="100% Solvency"
          isPositive={true}
          icon={<Briefcase className="w-5 h-5 text-amber-600 dark:text-amber-400" />}
          iconBgColor="bg-amber-50 text-amber-600 dark:bg-amber-950/70"
        />
      </div>

      {/* AI Fraud & Security Anomalies */}
      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="font-extrabold text-xl text-slate-900 dark:text-white flex items-center gap-2">
              <ShieldAlert className="w-5 h-5 text-rose-500" /> AI Fraud & Scam Detection Queue
            </h3>
            <p className="text-xs text-slate-500 dark:text-slate-400">
              Autonomous risk heuristics scanning messaging patterns, repeated portfolios, and off-platform
              solicitations
            </p>
          </div>
          <Link to="/admin/fraud-detection">
            <Button variant="ghost" size="sm" className="font-bold text-rose-500">
              Audit Center <ArrowRight className="w-4 h-4 ml-1" />
            </Button>
          </Link>
        </div>

        <Card className="p-0 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-50 dark:bg-slate-800/80 text-xs font-bold uppercase text-slate-500 dark:text-slate-400 border-b border-slate-200 dark:border-slate-800">
                <tr>
                  <th className="px-6 py-4">Account ID</th>
                  <th className="px-6 py-4">Entity</th>
                  <th className="px-6 py-4">Risk Severity</th>
                  <th className="px-6 py-4">Detected Anomaly</th>
                  <th className="px-6 py-4">Confidence</th>
                  <th className="px-6 py-4 text-right">Moderator Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800 font-medium">
                {flaggedAccounts.map((account) => (
                  <tr key={account.id} className="hover:bg-slate-50/50 dark:hover:bg-slate-800/50 transition-colors">
                    <td className="px-6 py-4 text-xs font-mono font-bold text-slate-500">
                      {account.id}
                    </td>
                    <td className="px-6 py-4">
                      <p className="font-bold text-slate-900 dark:text-white">{account.user}</p>
                      <span className="text-[11px] text-slate-400">Role: {account.role}</span>
                    </td>
                    <td className="px-6 py-4">
                      <Badge
                        variant={account.riskLevel === 'HIGH' ? 'danger' : 'warning'}
                        size="sm"
                      >
                        {account.riskLevel}
                      </Badge>
                    </td>
                    <td className="px-6 py-4 text-xs text-slate-600 dark:text-slate-300 max-w-xs">
                      {account.reason}
                    </td>
                    <td className="px-6 py-4 text-xs font-bold text-brand-600 dark:text-brand-400">
                      {account.aiConfidence}
                    </td>
                    <td className="px-6 py-4 text-right space-x-2">
                      <Button size="sm" variant="outline" className="text-xs">
                        <Eye className="w-3.5 h-3.5 mr-1" /> Inspect
                      </Button>
                      <Button size="sm" variant="danger" className="text-xs">
                        Suspend
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </Card>
      </div>

      {/* User Verification Queue */}
      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="font-extrabold text-xl text-slate-900 dark:text-white">
              Pending Identity & Enterprise Verifications
            </h3>
            <p className="text-xs text-slate-500 dark:text-slate-400">
              Users awaiting verified checkmark badges for high-volume transactions
            </p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {pendingVerifications.map((item) => (
            <Card key={item.id} className="p-6 flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <Avatar name={item.name} size="md" />
                    <div>
                      <h4 className="font-extrabold text-slate-900 dark:text-white">{item.name}</h4>
                      <Badge variant="primary" size="sm" className="mt-1">
                        {item.role}
                      </Badge>
                    </div>
                  </div>
                  <span className="text-xs text-slate-400">{item.date}</span>
                </div>
                <div className="mt-4 p-3 rounded-xl bg-slate-50 dark:bg-slate-800 text-xs space-y-1">
                  <p className="font-semibold text-slate-700 dark:text-slate-200">
                    Category: {item.specialty}
                  </p>
                  <p className="text-slate-500 dark:text-slate-400">Docs: {item.idStatus}</p>
                </div>
              </div>

              <div className="mt-6 pt-4 border-t border-slate-100 dark:border-slate-800 flex items-center justify-end gap-3">
                <Button size="sm" variant="outline">
                  Reject
                </Button>
                <Button size="sm" variant="success" className="gap-1.5">
                  <CheckCircle2 className="w-4 h-4" /> Approve Badge
                </Button>
              </div>
            </Card>
          ))}
        </div>
      </div>
    </DashboardLayout>
  );
};
