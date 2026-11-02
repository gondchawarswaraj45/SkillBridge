import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  ArrowRight,
  Sparkles,
  ShieldCheck,
  CheckCircle2,
  Users,
  Search,
  Lock,
  ChevronRight,
  Zap,
  Code,
  Smartphone,
  Cpu,
  Palette,
  Cloud,
} from 'lucide-react';
import { Navbar } from '@/components/common/Navbar';
import { Footer } from '@/components/common/Footer';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';
import { Badge } from '@/components/ui/Badge';
import heroImage from '@/assets/hero-banner.jpg';
import aiMatchImage from '@/assets/ai-matching-illustration.jpg';
import escrowImage from '@/assets/escrow-workspace-illustration.jpg';

export const LandingPage: React.FC = () => {
  const navigate = useNavigate();
  const [demoPrompt, setDemoPrompt] = useState('');
  const [isAiGenerating, setIsAiGenerating] = useState(false);
  const [demoOutput, setDemoOutput] = useState<string | null>(null);

  const categories = [
    { name: 'Web Development', icon: <Code className="w-3.5 h-3.5" />, query: 'Web Development' },
    { name: 'Mobile Apps', icon: <Smartphone className="w-3.5 h-3.5" />, query: 'Mobile Apps' },
    { name: 'AI & Machine Learning', icon: <Cpu className="w-3.5 h-3.5" />, query: 'AI & Machine Learning' },
    { name: 'UI/UX Design', icon: <Palette className="w-3.5 h-3.5" />, query: 'UI/UX Design' },
    { name: 'Cloud & DevOps', icon: <Cloud className="w-3.5 h-3.5" />, query: 'DevOps & Cloud' },
  ];

  const handleDemoGenerate = (e: React.FormEvent) => {
    e.preventDefault();
    if (!demoPrompt.trim()) return;

    setIsAiGenerating(true);
    setTimeout(() => {
      setIsAiGenerating(false);
      setDemoOutput(
        `### ✨ AI Project Scope Breakdown\n**Project Concept:** ${demoPrompt}\n**Predicted Budget Range:** $3,500 – $5,800 USD\n**Estimated Delivery Duration:** 3 to 5 Weeks\n\n**Suggested Milestone Schedule:**\n1. Technical Architecture Blueprint & Database Schema (20% Escrow)\n2. Core Microservices, Auth & REST API Implementation (40% Escrow)\n3. Responsive Frontend Integration & Real-Time Sync (25% Escrow)\n4. Security Audit, Automated Testing & Cloud Deployment (15% Escrow)\n\n**Top AI Recommended Skills:** Java, Spring Boot, React, TypeScript, Docker`
      );
    }, 900);
  };

  return (
    <div className="flex flex-col min-h-screen bg-surface-bg dark:bg-dark-bg text-surface-text dark:text-dark-text transition-colors">
      <Navbar />

      {/* Hero Section */}
      <section className="relative overflow-hidden pt-12 pb-20 lg:pt-20 lg:pb-32 bg-gradient-to-b from-brand-50/70 via-surface-bg to-surface-bg dark:from-slate-900/40 dark:via-dark-bg dark:to-dark-bg transition-colors">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 items-center">
            {/* Left Hero Content */}
            <div className="lg:col-span-7 space-y-6 text-center lg:text-left">
              <Link to="/ai-suite" className="inline-block group">
                <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-brand-100/90 dark:bg-brand-950/90 border border-brand-200 dark:border-brand-800 text-brand-700 dark:text-brand-300 text-xs font-semibold hover:scale-105 transition-transform shadow-soft-sm">
                  <Sparkles className="w-3.5 h-3.5 text-brand-600 dark:text-brand-400 group-hover:rotate-12 transition-transform" />
                  Next-Gen AI Talent Marketplace
                  <ChevronRight className="w-3 h-3 text-brand-500" />
                </div>
              </Link>

              <h1 className="text-4xl sm:text-6xl font-extrabold tracking-tight text-slate-900 dark:text-white leading-[1.12]">
                Where Top Talent Meets{' '}
                <span className="text-transparent bg-clip-text bg-gradient-to-r from-brand-500 via-indigo-500 to-accent-500">
                  High-Impact Opportunity
                </span>
              </h1>

              <p className="text-lg text-slate-600 dark:text-slate-300 max-w-2xl mx-auto lg:mx-0 leading-relaxed">
                SkillBridge revolutionizes freelancing with AI matching, verified portfolio assessments,
                milestone-based escrow contracts, and smart collaboration workspaces.
              </p>

              {/* Primary Interactive CTAs */}
              <div className="pt-2 flex flex-col sm:flex-row items-center justify-center lg:justify-start gap-4">
                <Link to="/marketplace" className="w-full sm:w-auto">
                  <Button
                    size="lg"
                    className="w-full sm:w-auto gap-2 shadow-soft-md hover:scale-105 active:scale-95 transition-all text-base font-bold px-8 py-3.5"
                  >
                    Find Freelancers <ArrowRight className="w-4 h-4" />
                  </Button>
                </Link>

                <Link to="/client/post-job" className="w-full sm:w-auto">
                  <Button
                    variant="outline"
                    size="lg"
                    className="w-full sm:w-auto hover:scale-105 active:scale-95 transition-all text-base font-bold px-8 py-3.5"
                  >
                    Post a Project
                  </Button>
                </Link>
              </div>

              {/* Popular Category Quick Pills */}
              <div className="pt-4 flex flex-wrap items-center justify-center lg:justify-start gap-2">
                <span className="text-xs font-bold text-slate-400 mr-1">Trending:</span>
                {categories.map((cat, idx) => (
                  <button
                    key={idx}
                    onClick={() => navigate(`/marketplace`)}
                    className="px-3 py-1.5 rounded-xl bg-white dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700/80 text-xs font-semibold text-slate-700 dark:text-slate-200 flex items-center gap-1.5 hover:border-brand-500 hover:text-brand-500 dark:hover:border-brand-400 dark:hover:text-brand-300 hover:scale-105 active:scale-95 transition-all shadow-soft-sm"
                  >
                    {cat.icon}
                    {cat.name}
                  </button>
                ))}
              </div>

              {/* Social Proof Stats */}
              <div className="pt-8 border-t border-slate-200 dark:border-slate-800 grid grid-cols-3 gap-6 text-center lg:text-left">
                <Link to="/ai-suite" className="group">
                  <p className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white group-hover:text-brand-500 transition-colors">
                    98.4%
                  </p>
                  <p className="text-xs font-medium text-slate-500 dark:text-slate-400 mt-1">
                    AI Match Accuracy
                  </p>
                </Link>
                <Link to="/workspace/1" className="group">
                  <p className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white group-hover:text-emerald-500 transition-colors">
                    $0
                  </p>
                  <p className="text-xs font-medium text-slate-500 dark:text-slate-400 mt-1">
                    Escrow Dispute Loss
                  </p>
                </Link>
                <Link to="/talent" className="group">
                  <p className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white group-hover:text-accent-500 transition-colors">
                    15k+
                  </p>
                  <p className="text-xs font-medium text-slate-500 dark:text-slate-400 mt-1">
                    Vetted Freelancers
                  </p>
                </Link>
              </div>
            </div>

            {/* Right Generated Hero Art with Floating Pulse Card */}
            <div className="lg:col-span-5 relative">
              <div className="relative mx-auto max-w-md lg:max-w-none rounded-3xl overflow-hidden shadow-2xl border border-slate-200/80 dark:border-slate-700/80 group">
                <img
                  src={heroImage}
                  alt="SkillBridge AI Talent Marketplace Banner"
                  className="w-full h-auto object-cover transform transition-transform duration-700 group-hover:scale-105"
                />
                <div className="absolute inset-0 bg-gradient-to-t from-slate-950/80 via-transparent to-transparent flex items-end p-6">
                  <div className="text-white space-y-2">
                    <Badge variant="success" size="sm" className="mb-1">
                      <CheckCircle2 className="w-3.5 h-3.5 mr-1" /> Verified Talent Pool
                    </Badge>
                    <p className="text-sm font-bold">Autonomous Matching Active</p>
                    <p className="text-xs text-slate-300">
                      Real-time semantic skill graph alignment matching candidates in &lt;15ms.
                    </p>
                  </div>
                </div>
              </div>

              {/* Floating Interactive Badge */}
              <div className="absolute -top-4 -left-4 sm:top-6 sm:-left-6 p-4 rounded-2xl bg-white/90 dark:bg-slate-900/90 backdrop-blur-md border border-slate-200 dark:border-slate-700 shadow-xl hidden sm:flex items-center gap-3 animate-float">
                <div className="w-10 h-10 rounded-xl bg-brand-500 text-white flex items-center justify-center font-bold">
                  <Sparkles className="w-5 h-5" />
                </div>
                <div>
                  <p className="text-xs font-bold text-slate-900 dark:text-white">Neural Match</p>
                  <p className="text-[11px] text-emerald-600 dark:text-emerald-400 font-semibold">98% Compatibility</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Interactive Live AI Project Scoper Sandbox */}
      <section className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 -mt-10 mb-16 w-full z-10 relative">
        <Card className="p-6 sm:p-8 shadow-2xl border-2 border-brand-300 dark:border-brand-800/80 bg-white/95 dark:bg-slate-900/95 backdrop-blur-md space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
            <div className="flex items-center gap-2">
              <Zap className="w-5 h-5 text-amber-500 animate-pulse" />
              <h3 className="font-extrabold text-base sm:text-lg text-slate-900 dark:text-white">
                Live AI Project Scoper (Try It Right Here)
              </h3>
            </div>
            <Badge variant="primary" size="sm">
              <Sparkles className="w-3 h-3 mr-1" /> Powered by Gemini / Groq
            </Badge>
          </div>

          <p className="text-xs text-slate-500 dark:text-slate-400">
            Type any brief project idea below and test the autonomous AI scoping engine instantly:
          </p>

          <form onSubmit={handleDemoGenerate} className="flex flex-col sm:flex-row gap-3">
            <input
              type="text"
              placeholder="e.g. Real-time cryptocurrency trading mobile app with biometrics..."
              value={demoPrompt}
              onChange={(e) => setDemoPrompt(e.target.value)}
              className="flex-1 px-4 py-3 rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-sm text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-brand-500"
            />
            <Button
              type="submit"
              variant="primary"
              isLoading={isAiGenerating}
              className="px-6 py-3 font-bold shrink-0 hover:scale-105 active:scale-95 transition-transform"
            >
              Generate Scope
            </Button>
          </form>

          {demoOutput && (
            <div className="p-5 rounded-2xl bg-slate-950 text-slate-100 font-mono text-xs whitespace-pre-line border border-slate-800 animate-fadeIn shadow-inner">
              {demoOutput}
            </div>
          )}
        </Card>
      </section>

      {/* Feature Section 1: AI Talent Matching */}
      <section className="py-20 bg-white dark:bg-slate-900 transition-colors">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 items-center">
            <div className="lg:col-span-6 rounded-3xl overflow-hidden shadow-xl border border-slate-200 dark:border-slate-800 group">
              <img
                src={aiMatchImage}
                alt="AI Neural Compatibility Scoring"
                className="w-full h-auto object-cover transform transition-transform duration-700 group-hover:scale-105"
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
                  <Button variant="secondary" className="gap-2 hover:scale-105 active:scale-95 transition-transform">
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
              <div className="pt-4 flex items-center gap-3">
                <Link to="/marketplace">
                  <Button variant="primary" className="gap-2 hover:scale-105 active:scale-95 transition-transform">
                    Browse Projects <ArrowRight className="w-4 h-4" />
                  </Button>
                </Link>
                <Link to="/workspace/1">
                  <Button variant="outline" className="gap-2 hover:scale-105 active:scale-95 transition-transform">
                    Open Live Kanban Demo
                  </Button>
                </Link>
              </div>
            </div>

            <div className="lg:col-span-6 lg:order-2 rounded-3xl overflow-hidden shadow-xl border border-slate-200 dark:border-slate-800 group">
              <img
                src={escrowImage}
                alt="Secure Milestone Escrow & Kanban Workspace"
                className="w-full h-auto object-cover transform transition-transform duration-700 group-hover:scale-105"
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
            <Card hoverEffect className="flex flex-col justify-between p-8 border-t-4 border-t-brand-500 hover:scale-105 transition-all">
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
                  <Button variant="outline" className="w-full hover:scale-105 active:scale-95 transition-transform">
                    Launch Freelancer Portal
                  </Button>
                </Link>
              </div>
            </Card>

            {/* Client Card */}
            <Card hoverEffect className="flex flex-col justify-between p-8 border-t-4 border-t-accent-500 hover:scale-105 transition-all">
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
                  <Button variant="primary" className="w-full hover:scale-105 active:scale-95 transition-transform">
                    Launch Client Portal
                  </Button>
                </Link>
              </div>
            </Card>

            {/* Admin Card */}
            <Card hoverEffect className="flex flex-col justify-between p-8 border-t-4 border-t-amber-500 hover:scale-105 transition-all">
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
                  <Button variant="outline" className="w-full hover:scale-105 active:scale-95 transition-transform">
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
              <Button
                size="lg"
                className="w-full sm:w-auto bg-white text-brand-600 hover:bg-slate-100 border-none font-bold hover:scale-105 active:scale-95 transition-transform"
              >
                Create Free Account
              </Button>
            </Link>
            <Link to="/marketplace" className="w-full sm:w-auto">
              <Button
                size="lg"
                variant="ghost"
                className="w-full sm:w-auto text-white border border-white/30 hover:bg-white/10 hover:scale-105 active:scale-95 transition-transform"
              >
                Explore Job Board
              </Button>
            </Link>
          </div>
        </div>
      </section>

      <Footer />
    </div>
  );
};
