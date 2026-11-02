import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Sparkles, ArrowRight, Menu, X, Briefcase, UserCheck, Shield } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { ThemeToggle } from '@/components/common/ThemeToggle';

export const Navbar: React.FC = () => {
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
        <nav className="hidden lg:flex items-center gap-7 text-sm font-semibold text-slate-600 dark:text-slate-300">
          <Link
            to="/marketplace"
            className={`transition-colors hover:text-brand-500 ${
              isActive('/marketplace') ? 'text-brand-500 font-bold' : ''
            }`}
          >
            Find Work
          </Link>
          <Link
            to="/talent"
            className={`transition-colors hover:text-brand-500 ${
              isActive('/talent') ? 'text-brand-500 font-bold' : ''
            }`}
          >
            Hire Talent
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
            to="/freelancer/dashboard"
            className="flex items-center gap-1.5 text-xs px-2.5 py-1 rounded-lg bg-slate-100 dark:bg-slate-800 hover:text-brand-500 transition-colors"
          >
            <UserCheck className="w-3.5 h-3.5 text-brand-500" />
            Freelancer Portal
          </Link>
          <Link
            to="/client/dashboard"
            className="flex items-center gap-1.5 text-xs px-2.5 py-1 rounded-lg bg-slate-100 dark:bg-slate-800 hover:text-brand-500 transition-colors"
          >
            <Briefcase className="w-3.5 h-3.5 text-accent-500" />
            Client Portal
          </Link>
          <Link
            to="/admin/dashboard"
            className="flex items-center gap-1.5 text-xs px-2.5 py-1 rounded-lg bg-slate-100 dark:bg-slate-800 hover:text-brand-500 transition-colors"
          >
            <Shield className="w-3.5 h-3.5 text-amber-500" />
            Admin
          </Link>
        </nav>

        {/* Right CTA & Theme Toggle */}
        <div className="flex items-center gap-3">
          <ThemeToggle />

          <Link to="/login" className="hidden sm:inline-flex">
            <Button variant="ghost" size="sm">
              Log In
            </Button>
          </Link>
          <Link to="/register">
            <Button variant="primary" size="sm" className="gap-1.5">
              Get Started <ArrowRight className="w-3.5 h-3.5" />
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
            to="/talent"
            onClick={() => setMobileMenuOpen(false)}
            className="block px-3 py-2 rounded-lg text-sm font-medium hover:bg-slate-50 dark:hover:bg-slate-800"
          >
            Hire Talent
          </Link>
          <Link
            to="/ai-suite"
            onClick={() => setMobileMenuOpen(false)}
            className="block px-3 py-2 rounded-lg text-sm font-medium text-accent-500 hover:bg-slate-50 dark:hover:bg-slate-800"
          >
            AI Suite & Tools
          </Link>
          <div className="pt-2 border-t border-slate-200 dark:border-slate-800 grid grid-cols-3 gap-2">
            <Link
              to="/freelancer/dashboard"
              onClick={() => setMobileMenuOpen(false)}
              className="text-center p-2 rounded-lg bg-slate-100 dark:bg-slate-800 text-xs font-semibold"
            >
              Freelancer
            </Link>
            <Link
              to="/client/dashboard"
              onClick={() => setMobileMenuOpen(false)}
              className="text-center p-2 rounded-lg bg-slate-100 dark:bg-slate-800 text-xs font-semibold"
            >
              Client
            </Link>
            <Link
              to="/admin/dashboard"
              onClick={() => setMobileMenuOpen(false)}
              className="text-center p-2 rounded-lg bg-slate-100 dark:bg-slate-800 text-xs font-semibold"
            >
              Admin
            </Link>
          </div>
        </div>
      )}
    </header>
  );
};
