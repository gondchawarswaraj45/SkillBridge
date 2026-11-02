import React, { useState } from 'react';
import {
  Sparkles,
  FileCheck,
  Briefcase,
  FileText,
  DollarSign,
  Compass,
  ShieldAlert,
  Video,
  ArrowRight,
  Zap,
} from 'lucide-react';
import { Navbar } from '@/components/common/Navbar';
import { Footer } from '@/components/common/Footer';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';

const aiModules = [
  {
    id: 'matching',
    title: 'AI Talent Matcher & Neural Compatibility',
    description:
      'Evaluates multidimensional compatibility between freelancer skill graphs and client project scopes with explainable matching breakdowns.',
    icon: <Sparkles className="w-6 h-6 text-brand-500" />,
    badge: 'Neural Engine',
    link: '/marketplace',
  },
  {
    id: 'resume',
    title: 'AI Resume & Portfolio Analyzer',
    description:
      'Parses PDF resumes, extracts verified tech stacks, analyzes code repositories, and generates actionable improvement recommendations.',
    icon: <FileCheck className="w-6 h-6 text-accent-500" />,
    badge: 'CV Intelligence',
    link: '/ai/resume-analyzer',
  },
  {
    id: 'scope',
    title: 'AI Project Description & Scope Generator',
    description:
      'Transforms rough client concepts into comprehensive project specifications with suggested deliverables and milestone schedules.',
    icon: <Briefcase className="w-6 h-6 text-emerald-500" />,
    badge: 'Scoping Engine',
    link: '/client/post-job',
  },
  {
    id: 'proposal',
    title: 'AI Proposal & Cover Letter Assistant',
    description:
      'Assists freelancers in tailoring winning, persuasive proposals emphasizing relevant portfolio projects and realistic estimates.',
    icon: <FileText className="w-6 h-6 text-blue-500" />,
    badge: 'Proposal Coach',
    link: '/ai/proposal-coach',
  },
  {
    id: 'estimator',
    title: 'AI Budget & Timeline Estimator',
    description:
      'Predicts realistic project costs, complexity scores, and milestone deadlines based on historical market trends and tech stack requirements.',
    icon: <DollarSign className="w-6 h-6 text-amber-500" />,
    badge: 'Cost Forecaster',
    link: '/ai/budget-estimator',
  },
  {
    id: 'coach',
    title: 'AI Career Coach & Skill Gap Roadmap',
    description:
      'Pinpoints high-demand market skills, identifies gaps in freelancer profiles, and constructs tailored career learning pathways.',
    icon: <Compass className="w-6 h-6 text-purple-500" />,
    badge: 'Career Growth',
    link: '/ai/career-coach',
  },
  {
    id: 'fraud',
    title: 'AI Scam & Fraud Detection Engine',
    description:
      'Autonomous heuristics scanning for off-platform payment evasion, fake reviews, copycat portfolios, and suspicious job postings.',
    icon: <ShieldAlert className="w-6 h-6 text-rose-500" />,
    badge: 'Security Guard',
    link: '/admin/fraud-detection',
  },
  {
    id: 'meetings',
    title: 'AI Meeting Minutes & Action Items',
    description:
      'Extracts key architectural decisions, action items, assigned deliverables, and deadlines automatically from meeting transcripts.',
    icon: <Video className="w-6 h-6 text-indigo-500" />,
    badge: 'Collaboration',
    link: '/meetings',
  },
];

export const AiSuitePage: React.FC = () => {
  const [testInput, setTestInput] = useState('');
  const [isGenerating, setIsGenerating] = useState(false);
  const [generatedOutput, setGeneratedOutput] = useState<string | null>(null);

  const handleDemoGenerate = () => {
    if (!testInput.trim()) return;
    setIsGenerating(true);
    setTimeout(() => {
      setIsGenerating(false);
      setGeneratedOutput(
        `### AI Generated Project Scope\n\n**Title:** Enterprise ${testInput} Solution\n**Suggested Budget:** $4,500 - $6,500\n**Estimated Duration:** 4-6 Weeks\n\n**Recommended Milestones:**\n1. Architecture Blueprint & DB Schemas (25%)\n2. Core APIs & JWT Auth Implementation (35%)\n3. Frontend UI/UX & WebSocket Sync (25%)\n4. Testing, Auditing & Cloud Deployment (15%)\n\n**Recommended Skills:** Spring Boot, React, TypeScript, MySQL, Docker`
      );
    }, 1200);
  };

  return (
    <div className="flex flex-col min-h-screen bg-surface-bg dark:bg-dark-bg transition-colors">
      <Navbar />

      {/* Header */}
      <section className="py-16 bg-gradient-to-b from-brand-50/70 via-white to-surface-bg dark:from-slate-900/60 dark:via-dark-bg dark:to-dark-bg border-b border-slate-200 dark:border-slate-800 transition-colors">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center space-y-4">
          <Badge variant="secondary" size="md">
            <Sparkles className="w-3.5 h-3.5 mr-1" /> Autonomous AI Ecosystem
          </Badge>
          <h1 className="text-4xl sm:text-5xl font-extrabold text-slate-900 dark:text-white tracking-tight max-w-3xl mx-auto">
            Intelligent Automation Across the Full Freelance Lifecycle
          </h1>
          <p className="text-slate-600 dark:text-slate-300 max-w-2xl mx-auto text-base sm:text-lg">
            SkillBridge integrates deep AI models (Google Gemini & Groq LLMs) behind a unified backend
            abstraction to empower clients, freelancers, and platform moderators.
          </p>
        </div>
      </section>

      {/* Interactive AI Sandbox Demo */}
      <section className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 -mt-8 w-full z-10 relative">
        <Card className="p-6 sm:p-8 shadow-soft-lg border-2 border-brand-200 dark:border-brand-900/60 space-y-5">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Zap className="w-5 h-5 text-amber-500" />
              <h3 className="font-extrabold text-base sm:text-lg text-slate-900 dark:text-white">
                Live AI Scoping Sandbox (Interactive Preview)
              </h3>
            </div>
            <Badge variant="primary" size="sm">Groq / Gemini 1.5</Badge>
          </div>

          <p className="text-xs text-slate-500 dark:text-slate-400">
            Type any brief project concept below (e.g. "Fintech payment gateway", "Mobile fitness tracker")
            to generate a structured breakdown:
          </p>

          <div className="flex flex-col sm:flex-row gap-3">
            <input
              type="text"
              placeholder="e.g. AI-driven medical record transcription app..."
              value={testInput}
              onChange={(e) => setTestInput(e.target.value)}
              className="flex-1 px-4 py-3 rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-sm text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-brand-500"
            />
            <Button
              variant="primary"
              onClick={handleDemoGenerate}
              isLoading={isGenerating}
              className="px-6 py-3 shrink-0"
            >
              Generate Scope
            </Button>
          </div>

          {generatedOutput && (
            <div className="p-5 rounded-2xl bg-slate-900 text-slate-100 font-mono text-xs whitespace-pre-line border border-slate-800 animate-fadeIn">
              {generatedOutput}
            </div>
          )}
        </Card>
      </section>

      {/* 8 AI Modules Grid */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20 w-full">
        <div className="text-center mb-12">
          <h2 className="text-3xl font-extrabold text-slate-900 dark:text-white">
            Comprehensive AI Capability Suite
          </h2>
          <p className="text-xs text-slate-500 dark:text-slate-400 mt-2">
            Eight native machine learning engines engineered directly into the SkillBridge platform
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {aiModules.map((module) => (
            <Card key={module.id} hoverEffect className="p-6 flex flex-col justify-between space-y-4">
              <div className="space-y-3">
                <div className="flex items-center justify-between">
                  <div className="p-3 rounded-2xl bg-slate-100 dark:bg-slate-800">{module.icon}</div>
                  <Badge variant="neutral" size="sm">{module.badge}</Badge>
                </div>
                <h4 className="font-extrabold text-base text-slate-900 dark:text-white leading-snug">
                  {module.title}
                </h4>
                <p className="text-xs text-slate-500 dark:text-slate-400 leading-relaxed">
                  {module.description}
                </p>
              </div>

              <div className="pt-4 border-t border-slate-100 dark:border-slate-800">
                <Button size="sm" variant="outline" className="w-full gap-1.5 text-xs font-bold">
                  Open Engine <ArrowRight className="w-3.5 h-3.5" />
                </Button>
              </div>
            </Card>
          ))}
        </div>
      </section>

      <Footer />
    </div>
  );
};
