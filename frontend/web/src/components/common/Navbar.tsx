import React from 'react';
import { Link } from 'react-router-dom';
import { Sparkles, ArrowRight } from 'lucide-react';
import { Button } from '@/components/ui/Button';

export const Navbar: React.FC = () => {
  return (
    <header className="sticky top-0 z-50 bg-white/80 backdrop-blur-md border-b border-surface-border">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        <Link to="/" className="flex items-center gap-2.5">
          <div className="w-9 h-9 rounded-xl bg-brand-500 flex items-center justify-center text-white font-bold text-lg shadow-soft-sm">
            SB
          </div>
          <span className="font-bold text-xl tracking-tight text-surface-text">
            Skill<span className="text-brand-500">Bridge</span>
          </span>
        </Link>

        <nav className="hidden md:flex items-center gap-8 text-sm font-medium text-surface-muted">
          <Link to="/marketplace" className="hover:text-brand-500 transition-colors">
            Explore Jobs
          </Link>
          <Link to="/talent" className="hover:text-brand-500 transition-colors">
            Find Talent
          </Link>
          <Link to="/ai-match" className="flex items-center gap-1.5 hover:text-accent-500 transition-colors">
            <Sparkles className="w-4 h-4 text-accent-500" />
            AI Matching
          </Link>
        </nav>

        <div className="flex items-center gap-3">
          <Link to="/login">
            <Button variant="ghost" size="sm">
              Log In
            </Button>
          </Link>
          <Link to="/register">
            <Button variant="primary" size="sm" className="gap-1.5">
              Get Started <ArrowRight className="w-4 h-4" />
            </Button>
          </Link>
        </div>
      </div>
    </header>
  );
};

