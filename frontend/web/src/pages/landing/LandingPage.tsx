import React from 'react';
import { Link } from 'react-router-dom';
import {
  ArrowRight,
  Sparkles,
  ShieldCheck,
  CheckCircle2,
  Users,
  Search,
  Lock,
  ChevronRight,
} from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';
import { Badge } from '@/components/ui/Badge';
import heroImage from '@/assets/hero-banner.jpg';
import aiMatchImage from '@/assets/ai-matching-illustration.jpg';
import escrowImage from '@/assets/escrow-workspace-illustration.jpg';

export const LandingPage: React.FC = () => {
  return (
    <div className="flex flex-col min-h-screen">
      {/* Hero Section */}
      <section className="relative overflow-hidden pt-12 pb-20 lg:pt-20 lg:pb-32 bg-gradient-to-b from-brand-50/60 via-surface-bg to-surface-bg dark:from-slate-900/40 dark:via-dark-bg dark:to-dark-bg transition-colors">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 items-center">
            {/* Left Content */}
            <div className="lg:col-span-7 space-y-6 text-center lg:text-left">
              <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-brand-100/80 dark:bg-brand-950/80 border border-brand-200 dark:border-brand-800 text-brand-700 dark:text-brand-300 text-xs font-semibold">
                <Sparkles className="w-3.5 h-3.5 text-brand-600 dark:text-brand-400" />
                Next-Gen AI Talent Marketplace
              </div>

              <h1 className="text-4xl sm:text-6xl font-extrabold tracking-tight text-slate-900 dark:text-white leading-[1.12]">
                Where Top Talent Meets{' '}
                <span className="text-transparent bg-clip-text bg-gradient-to-r from-brand-500 via-indigo-500 to-accent-500">
                  High-Impact Opportunity
                </span>
              </h1>

              <p className="text-lg text-slate-600 dark:text-slate-300 max-w-2xl mx-auto lg:mx-0 leading-relaxed">
                SkillBridge combines deep neural talent matching, verified portfolio intelligence,
                milestone-based escrow contracts, and built-in Kanban workspaces into a single high-performance
                platform.
              </p>

              {/* CTAs */}
              <div className="pt-2 flex flex-col sm:flex-row items-center justify-center lg:justify-start gap-3.5">
                <Link to="/marketplace" className="w-full sm:w-auto">
                  <Button size="lg" className="w-full sm:w-auto gap-2 shadow-soft-md">
                    Explore Opportunities <ArrowRight className="w-4 h-4" />
                  </Button>
                </Link>
                <Link to="/client/post-job" className="w-full sm:w-auto">
                  <Button variant="outline" size="lg" className="w-full sm:w-auto">
                    Post a Project
                  </Button>
                </Link>
              </div>

              {/* Social Proof Stats */}
              <div className="pt-8 border-t border-slate-200 dark:border-slate-800 grid grid-cols-3 gap-6 text-center lg:text-left">
                <div>
                  <p className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white">98.4%</p>
                  <p className="text-xs font-medium text-slate-500 dark:text-slate-400 mt-1">AI Match Accuracy</p>
                </div>
                <div>
                  <p className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white">$0</p>
                  <p className="text-xs font-medium text-slate-500 dark:text-slate-400 mt-1">Escrow Dispute Loss</p>
                </div>
                <div>
                  <p className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white">15k+</p>
                  <p className="text-xs font-medium text-slate-500 dark:text-slate-400 mt-1">Vetted Freelancers</p>
                </div>
              </div>
            </div>

            {/* Right Generated Hero Art */}
            <div className="lg:col-span-5 relative">
              <div className="relative mx-auto max-w-md lg:max-w-none rounded-3xl overflow-hidden shadow-2xl border border-slate-200/80 dark:border-slate-700/80 group">
                <img
                  src={heroImage}
                  alt="SkillBridge AI Talent Marketplace Banner"
                  className="w-full h-auto object-cover transform transition-transform duration-500 group-hover:scale-105"
                />
                <div className="absolute inset-0 bg-gradient-to-t from-slate-950/80 via-transparent to-transparent flex items-end p-6">
                  <div className="text-white">
                    <Badge variant="success" size="sm" className="mb-2">
                      <CheckCircle2 className="w-3 h-3 mr-1" /> Verified Talent Pool
                    </Badge>
                    <p className="text-sm font-semibold">Real-Time Autonomous Matching Active</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Feature Section 1: AI Talent Matching */}
      <section className="py-20 bg-white dark:bg-slate-900 transition-colors">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 items-center">
            <div className="lg:col-span-6 rounded-3xl overflow-hidden shadow-xl border border-slate-200 dark:border-slate-800">
              <img
                src={aiMatchImage}
                alt="AI Neural Compatibility Scoring"
                className="w-full h-auto object-cover"
              />
            </div>
            <div className="lg:col-span-6 space-y-5">
              <Badge variant="primary" size="md">
                Explainable AI Matching
              </Badge>
              <h2 className="text-3xl sm:text-4xl font-extrabold text-slate-900 dark:text-white tracking-tight">
                Stop Sifting Through Resumes.{' '}
                <span className="text-brand-500">Let AI Find the Perfect Fit.</span>
              </h2>
              <p className="text-slate-600 dark:text-slate-300 leading-relaxed">
                Traditional keyword searches miss critical context. SkillBridge evaluates semantic code
                repositories, project complexity, timeline availability, and historical client feedback to
                produce explainable compatibility ratings.
              </p>
              <ul className="space-y-3 pt-2">
                {[
                  'Automated GitHub & Portfolio Code Quality Inspection',
                  'Multidimensional Skill Compatibility Scores',
                  'Budget and Timeline Alignment Projections',
                  'AI Proposal Writing Assistant for Freelancers',
                ].map((item, idx) => (
                  <li key={idx} className="flex items-center gap-3 text-sm font-semibold text-slate-700 dark:text-slate-200">
                    <CheckCircle2 className="w-5 h-5 text-emerald-500 shrink-0" />
                    {item}
                  </li>
                ))}
              </ul>
              <div className="pt-4">
                <Link to="/ai-suite">
                  <Button variant="secondary" className="gap-2">
                    Explore AI Capabilities <ChevronRight className="w-4 h-4" />
                  </Button>
                </Link>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Feature Section 2: Milestone Escrow & Workspace */}
      <section className="py-20 bg-surface-bg dark:bg-dark-bg transition-colors">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 items-center">
            <div className="lg:col-span-6 space-y-5 lg:order-1">
              <Badge variant="secondary" size="md">
                Zero-Risk Delivery
              </Badge>
              <h2 className="text-3xl sm:text-4xl font-extrabold text-slate-900 dark:text-white tracking-tight">
                Milestone Escrow Contracts &{' '}
                <span className="text-accent-500">Unified Kanban Workspaces.</span>
              </h2>
              <p className="text-slate-600 dark:text-slate-300 leading-relaxed">
                Every contract on SkillBridge is backed by an automated escrow system. Funds are deposited
                upfront and disbursed only after deliverables are inspected and approved. Built-in task boards
                keep everyone aligned.
              </p>
              <ul className="space-y-3 pt-2">
                {[
                  'Automated Escrow Lock & Release System',
                  'Interactive Project Kanban Boards with Deadline Tracking',
                  'Real-time WebSocket Chat with File Attachments',
                  'Integrated Video Meeting Scheduling & AI Minutes',
                ].map((item, idx) => (
                  <li key={idx} className="flex items-center gap-3 text-sm font-semibold text-slate-700 dark:text-slate-200">
                    <Lock className="w-5 h-5 text-brand-500 shrink-0" />
                    {item}
                  </li>
                ))}
              </ul>
              <div className="pt-4">
                <Link to="/marketplace">
                  <Button variant="primary" className="gap-2">
                    Browse Projects <ArrowRight className="w-4 h-4" />
                  </Button>
                </Link>
              </div>
            </div>

            <div className="lg:col-span-6 lg:order-2 rounded-3xl overflow-hidden shadow-xl border border-slate-200 dark:border-slate-800">
              <img
                src={escrowImage}
                alt="Secure Milestone Escrow & Kanban Workspace"
                className="w-full h-auto object-cover"
              />
            </div>
          </div>
        </div>
      </section>

      {/* Role Pathways Section */}
      <section className="py-20 bg-white dark:bg-slate-900 transition-colors">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <Badge variant="neutral" size="md" className="mb-4">
            Custom Portals
          </Badge>
          <h2 className="text-3xl sm:text-4xl font-extrabold text-slate-900 dark:text-white tracking-tight">
            Tailored Experiences for Every Role
          </h2>
          <p className="text-slate-500 dark:text-slate-400 mt-3 max-w-2xl mx-auto">
            Switch effortlessly between client and freelancer modes or supervise system integrity as an
            administrator.
          </p>

          <div className="mt-12 grid grid-cols-1 md:grid-cols-3 gap-8 text-left">
            {/* Freelancer Card */}
            <Card hoverEffect className="flex flex-col justify-between p-8 border-t-4 border-t-brand-500">
              <div>
                <div className="w-12 h-12 rounded-2xl bg-brand-50 dark:bg-brand-950/60 text-brand-600 dark:text-brand-400 flex items-center justify-center mb-6">
                  <Users className="w-6 h-6" />
                </div>
                <h3 className="text-xl font-bold text-slate-900 dark:text-white">For Freelancers</h3>
                <p className="mt-3 text-sm text-slate-500 dark:text-slate-400 leading-relaxed">
                  Showcase your portfolio, receive AI-matched project alerts, submit high-converting
                  proposals, and get paid guaranteed on time.
                </p>
                <ul className="mt-6 space-y-2 text-xs font-semibold text-slate-600 dark:text-slate-300">
                  <li className="flex items-center gap-2">✓ Verified Skill Badges</li>
                  <li className="flex items-center gap-2">✓ Automated Invoice Generation</li>
                  <li className="flex items-center gap-2">✓ Career AI Coach & Skill Roadmap</li>
                </ul>
              </div>
              <div className="pt-8">
                <Link to="/freelancer/dashboard">
                  <Button variant="outline" className="w-full">
                    Launch Freelancer Portal
                  </Button>
                </Link>
              </div>
            </Card>

            {/* Client Card */}
            <Card hoverEffect className="flex flex-col justify-between p-8 border-t-4 border-t-accent-500">
              <div>
                <div className="w-12 h-12 rounded-2xl bg-accent-50 dark:bg-accent-950/60 text-accent-600 dark:text-accent-400 flex items-center justify-center mb-6">
                  <Search className="w-6 h-6" />
                </div>
                <h3 className="text-xl font-bold text-slate-900 dark:text-white">For Clients</h3>
                <p className="mt-3 text-sm text-slate-500 dark:text-slate-400 leading-relaxed">
                  Publish project scopes in minutes with AI generator, review top 1% candidates, manage
                  milestone tasks, and scale engineering capacity.
                </p>
                <ul className="mt-6 space-y-2 text-xs font-semibold text-slate-600 dark:text-slate-300">
                  <li className="flex items-center gap-2">✓ AI Job Description Generator</li>
                  <li className="flex items-center gap-2">✓ Milestone Escrow Protection</li>
                  <li className="flex items-center gap-2">✓ Team & Workspace Supervision</li>
                </ul>
              </div>
              <div className="pt-8">
                <Link to="/client/dashboard">
                  <Button variant="primary" className="w-full">
                    Launch Client Portal
                  </Button>
                </Link>
              </div>
            </Card>

            {/* Admin Card */}
            <Card hoverEffect className="flex flex-col justify-between p-8 border-t-4 border-t-amber-500">
              <div>
                <div className="w-12 h-12 rounded-2xl bg-amber-50 dark:bg-amber-950/60 text-amber-600 dark:text-amber-400 flex items-center justify-center mb-6">
                  <ShieldCheck className="w-6 h-6" />
                </div>
                <h3 className="text-xl font-bold text-slate-900 dark:text-white">For Administrators</h3>
                <p className="mt-3 text-sm text-slate-500 dark:text-slate-400 leading-relaxed">
                  Platform oversight, real-time transaction analytics, dispute mediation, and automated
                  AI scam and fraud detection engines.
                </p>
                <ul className="mt-6 space-y-2 text-xs font-semibold text-slate-600 dark:text-slate-300">
                  <li className="flex items-center gap-2">✓ AI Risk Indicators & Fraud Scan</li>
                  <li className="flex items-center gap-2">✓ Platform Revenue & Escrow Metrics</li>
                  <li className="flex items-center gap-2">✓ User Account Verification Queue</li>
                </ul>
              </div>
              <div className="pt-8">
                <Link to="/admin/dashboard">
                  <Button variant="outline" className="w-full">
                    Launch Admin Console
                  </Button>
                </Link>
              </div>
            </Card>
          </div>
        </div>
      </section>

      {/* Ready to Build Call To Action */}
      <section className="py-20 bg-gradient-to-r from-brand-600 via-indigo-600 to-accent-600 text-white">
        <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 text-center space-y-6">
          <h2 className="text-3xl sm:text-5xl font-extrabold tracking-tight">
            Ready to Connect with World-Class Talent?
          </h2>
          <p className="text-brand-100 max-w-2xl mx-auto text-base sm:text-lg">
            Join thousands of modern enterprises, startups, and vetted professionals building the future on
            SkillBridge.
          </p>
          <div className="pt-4 flex flex-col sm:flex-row items-center justify-center gap-4">
            <Link to="/register" className="w-full sm:w-auto">
              <Button size="lg" className="w-full sm:w-auto bg-white text-brand-600 hover:bg-slate-100 border-none font-bold">
                Create Free Account
              </Button>
            </Link>
            <Link to="/marketplace" className="w-full sm:w-auto">
              <Button size="lg" variant="ghost" className="w-full sm:w-auto text-white border border-white/30 hover:bg-white/10">
                Explore Job Board
              </Button>
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
};
