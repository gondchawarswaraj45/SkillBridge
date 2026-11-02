import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Sparkles, ArrowRight, Menu, X } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { ThemeToggle } from '@/components/common/ThemeToggle';
import { useApp } from '@/context/AppContext';

export const Navbar: React.FC = () => {
  const { currentUser, switchRole } = useApp();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const location = useLocation();

  const isActive = (path: string) => location.pathname === path;

  return (
    <header className="sticky top-0 z-50 bg-white/80 dark:bg-slate-900/80 backdrop-blur-md border-b border-surface-border dark:border-slate-800 transition-colors">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        {/* Logo */}
        <Link to="/" className="flex items-center gap-2.5">
          <div className="w-9 h-9 rounded-xl bg-brand-500 flex items-center justify-center text-white font-extrabold text-lg shadow-soft-sm">
            SB
          </div>
          <span className="font-extrabold text-xl tracking-tight text-slate-900 dark:text-white">
            Skill<span className="text-brand-500">Bridge</span>
          </span>
        </Link>

        {/* Desktop Nav */}
        <nav className="hidden lg:flex items-center gap-6 text-sm font-semibold text-slate-600 dark:text-slate-300">
          <Link
            to="/marketplace"
            className={`transition-colors hover:text-brand-500 ${
              isActive('/marketplace') ? 'text-brand-500 font-bold' : ''
            }`}
          >
            Find Work
          </Link>
          <Link
            to="/client/post-job"
            className={`transition-colors hover:text-brand-500 ${
              isActive('/client/post-job') ? 'text-brand-500 font-bold' : ''
            }`}
          >
            Post a Project
          </Link>
          <Link
            to="/ai-suite"
            className={`flex items-center gap-1.5 transition-colors hover:text-accent-500 ${
              isActive('/ai-suite') ? 'text-accent-500 font-bold' : ''
            }`}
          >
            <Sparkles className="w-4 h-4 text-accent-500" />
            AI Suite
          </Link>
          <Link
            to="/workspace/1"
            className="flex items-center gap-1 text-xs px-2.5 py-1 rounded-lg bg-brand-50 dark:bg-brand-950/60 text-brand-600 dark:text-brand-400 font-bold hover:bg-brand-100"
          >
            Live Kanban
          </Link>
          <Link
            to="/messages"
            className="flex items-center gap-1 text-xs px-2.5 py-1 rounded-lg bg-slate-100 dark:bg-slate-800 hover:text-brand-500 transition-colors"
          >
            Chat
          </Link>
          <Link
            to="/meetings"
            className="flex items-center gap-1 text-xs px-2.5 py-1 rounded-lg bg-slate-100 dark:bg-slate-800 hover:text-brand-500 transition-colors"
          >
            Meet
          </Link>
        </nav>

        {/* Right CTA, Role Switcher & Theme Toggle */}
        <div className="flex items-center gap-2.5">
          {/* Quick Interactive Role Switcher */}
          <div className="hidden sm:flex items-center p-1 rounded-xl bg-slate-100 dark:bg-slate-800 text-[11px] font-bold">
            <button
              onClick={() => switchRole('FREELANCER')}
              className={`px-2 py-1 rounded-lg transition-all ${
                currentUser.role === 'FREELANCER'
                  ? 'bg-brand-500 text-white shadow-soft-sm'
                  : 'text-slate-500 hover:text-slate-900 dark:hover:text-white'
              }`}
            >
              Freelancer
            </button>
            <button
              onClick={() => switchRole('CLIENT')}
              className={`px-2 py-1 rounded-lg transition-all ${
                currentUser.role === 'CLIENT'
                  ? 'bg-accent-500 text-white shadow-soft-sm'
                  : 'text-slate-500 hover:text-slate-900 dark:hover:text-white'
              }`}
            >
              Client
            </button>
            <button
              onClick={() => switchRole('ADMIN')}
              className={`px-2 py-1 rounded-lg transition-all ${
                currentUser.role === 'ADMIN'
                  ? 'bg-amber-500 text-white shadow-soft-sm'
                  : 'text-slate-500 hover:text-slate-900 dark:hover:text-white'
              }`}
            >
              Admin
            </button>
          </div>

          <ThemeToggle />

          <Link to={`/${currentUser.role.toLowerCase()}/dashboard`}>
            <Button variant="primary" size="sm" className="gap-1.5 text-xs">
              Go to Console <ArrowRight className="w-3.5 h-3.5" />
            </Button>
          </Link>

          {/* Mobile menu button */}
          <button
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="lg:hidden p-2 rounded-xl text-slate-700 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800"
            aria-label="Toggle Menu"
          >
            {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
          </button>
        </div>
      </div>

      {/* Mobile Drawer */}
      {mobileMenuOpen && (
        <div className="lg:hidden px-4 pt-3 pb-6 border-b border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 space-y-3">
          <Link
            to="/marketplace"
            onClick={() => setMobileMenuOpen(false)}
            className="block px-3 py-2 rounded-lg text-sm font-medium hover:bg-slate-50 dark:hover:bg-slate-800"
          >
            Find Work
          </Link>
          <Link
            to="/client/post-job"
            onClick={() => setMobileMenuOpen(false)}
            className="block px-3 py-2 rounded-lg text-sm font-medium hover:bg-slate-50 dark:hover:bg-slate-800"
          >
            Post a Project (AI)
          </Link>
          <Link
            to="/workspace/1"
            onClick={() => setMobileMenuOpen(false)}
            className="block px-3 py-2 rounded-lg text-sm font-medium text-brand-500 hover:bg-slate-50 dark:hover:bg-slate-800"
          >
            Live Kanban Board
          </Link>
          <Link
            to="/messages"
            onClick={() => setMobileMenuOpen(false)}
            className="block px-3 py-2 rounded-lg text-sm font-medium hover:bg-slate-50 dark:hover:bg-slate-800"
          >
            Live Chat
          </Link>
          <Link
            to="/meetings"
            onClick={() => setMobileMenuOpen(false)}
            className="block px-3 py-2 rounded-lg text-sm font-medium hover:bg-slate-50 dark:hover:bg-slate-800"
          >
            Video Room
          </Link>

          <div className="pt-2 border-t border-slate-200 dark:border-slate-800 grid grid-cols-3 gap-2">
            <button
              onClick={() => {
                switchRole('FREELANCER');
                setMobileMenuOpen(false);
              }}
              className="text-center p-2 rounded-lg bg-slate-100 dark:bg-slate-800 text-xs font-semibold"
            >
              Freelancer
            </button>
            <button
              onClick={() => {
                switchRole('CLIENT');
                setMobileMenuOpen(false);
              }}
              className="text-center p-2 rounded-lg bg-slate-100 dark:bg-slate-800 text-xs font-semibold"
            >
              Client
            </button>
            <button
              onClick={() => {
                switchRole('ADMIN');
                setMobileMenuOpen(false);
              }}
              className="text-center p-2 rounded-lg bg-slate-100 dark:bg-slate-800 text-xs font-semibold"
            >
              Admin
            </button>
          </div>
        </div>
      )}
    </header>
  );
};
