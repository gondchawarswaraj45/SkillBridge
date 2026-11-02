import React from 'react';
import { ArrowRight, Sparkles, ShieldCheck, Zap } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';

export const LandingPage: React.FC = () => {
  return (
    <div className="flex flex-col min-h-screen">
      {/* Hero Section */}
      <section className="relative overflow-hidden py-20 lg:py-28 bg-gradient-to-b from-brand-50/50 via-white to-surface-bg">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center relative z-10">
          <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-brand-100 text-brand-700 text-xs font-semibold mb-6">
            <Sparkles className="w-3.5 h-3.5 text-brand-600" />
            Next-Gen AI Talent Marketplace
          </div>
          <h1 className="text-4xl sm:text-6xl font-extrabold tracking-tight text-surface-text max-w-4xl mx-auto leading-tight">
            Where Top Talent Meets{' '}
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-brand-500 to-accent-500">
              High-Impact Opportunity
            </span>
          </h1>
          <p className="mt-6 text-lg sm:text-xl text-surface-muted max-w-2xl mx-auto">
            SkillBridge revolutionizes freelancing with AI matching, verified portfolio assessments,
            milestone-based escrow contracts, and smart collaboration workspaces.
          </p>
          <div className="mt-10 flex flex-col sm:flex-row items-center justify-center gap-4">
            <Button size="lg" className="w-full sm:w-auto gap-2">
              Find Freelancers <ArrowRight className="w-4 h-4" />
            </Button>
            <Button variant="outline" size="lg" className="w-full sm:w-auto">
              Apply as a Freelancer
            </Button>
          </div>
        </div>
      </section>

      {/* Feature Highlights */}
      <section className="py-16 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <Card hoverEffect className="flex flex-col gap-4">
            <div className="w-12 h-12 rounded-xl bg-brand-100 text-brand-600 flex items-center justify-center">
              <Sparkles className="w-6 h-6" />
            </div>
            <h3 className="font-bold text-lg text-surface-text">AI Talent Matching</h3>
            <p className="text-sm text-surface-muted leading-relaxed">
              Proprietary semantic matching evaluates skills, experience depth, and project compatibility
              scores with explainable breakdowns.
            </p>
          </Card>

          <Card hoverEffect className="flex flex-col gap-4">
            <div className="w-12 h-12 rounded-xl bg-accent-100 text-accent-600 flex items-center justify-center">
              <ShieldCheck className="w-6 h-6" />
            </div>
            <h3 className="font-bold text-lg text-surface-text">Milestone Escrow Protection</h3>
            <p className="text-sm text-surface-muted leading-relaxed">
              Secure client funds in milestone escrows, released only upon deliverable review and verified
              satisfaction.
            </p>
          </Card>

          <Card hoverEffect className="flex flex-col gap-4">
            <div className="w-12 h-12 rounded-xl bg-emerald-100 text-emerald-600 flex items-center justify-center">
              <Zap className="w-6 h-6" />
            </div>
            <h3 className="font-bold text-lg text-surface-text">Integrated Kanban Workspace</h3>
            <p className="text-sm text-surface-muted leading-relaxed">
              Every contract includes a dedicated project workspace with real-time chat, Kanban boards,
              and video meetings.
            </p>
          </Card>
        </div>
      </section>
    </div>
  );
};
