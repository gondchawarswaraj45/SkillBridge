import React from 'react';
import { Link } from 'react-router-dom';
import { Github, Twitter, Linkedin, Heart } from 'lucide-react';

export const Footer: React.FC = () => {
  return (
    <footer className="border-t border-surface-border dark:border-slate-800 bg-white dark:bg-slate-900 mt-auto transition-colors">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-14">
        <div className="grid grid-cols-2 md:grid-cols-5 gap-8">
          {/* Brand Info */}
          <div className="col-span-2">
            <Link to="/" className="flex items-center gap-2.5">
              <div className="w-8 h-8 rounded-xl bg-brand-500 flex items-center justify-center text-white font-bold text-sm shadow-soft-sm">
                SB
              </div>
              <span className="font-extrabold text-xl tracking-tight text-slate-900 dark:text-white">
                Skill<span className="text-brand-500">Bridge</span>
              </span>
            </Link>
            <p className="mt-4 text-sm text-slate-500 dark:text-slate-400 max-w-sm leading-relaxed">
              Where Talent Meets Opportunity. The next-generation AI-powered freelancing marketplace with
              intelligent talent matching, milestone escrow protection, and real-time collaboration.
            </p>
            <div className="mt-6 flex items-center gap-3 text-slate-400 dark:text-slate-500">
              <a href="#" className="p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 hover:text-slate-700 dark:hover:text-slate-200 transition-colors">
                <Github className="w-4 h-4" />
              </a>
              <a href="#" className="p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 hover:text-slate-700 dark:hover:text-slate-200 transition-colors">
                <Twitter className="w-4 h-4" />
              </a>
              <a href="#" className="p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 hover:text-slate-700 dark:hover:text-slate-200 transition-colors">
                <Linkedin className="w-4 h-4" />
              </a>
            </div>
          </div>

          {/* Column: For Freelancers */}
          <div>
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-900 dark:text-white">
              Freelancers
            </h4>
            <ul className="mt-4 space-y-2.5 text-sm text-slate-500 dark:text-slate-400">
              <li><Link to="/marketplace" className="hover:text-brand-500 transition-colors">Browse Projects</Link></li>
              <li><Link to="/freelancer/profile" className="hover:text-brand-500 transition-colors">Freelancer Profile</Link></li>
              <li><Link to="/ai-suite" className="hover:text-brand-500 transition-colors">AI Resume Analyzer</Link></li>
              <li><Link to="/freelancer/earnings" className="hover:text-brand-500 transition-colors">Earnings & Invoices</Link></li>
            </ul>
          </div>

          {/* Column: For Clients */}
          <div>
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-900 dark:text-white">
              Clients
            </h4>
            <ul className="mt-4 space-y-2.5 text-sm text-slate-500 dark:text-slate-400">
              <li><Link to="/talent" className="hover:text-brand-500 transition-colors">Find Top Talent</Link></li>
              <li><Link to="/client/post-job" className="hover:text-brand-500 transition-colors">Post a Job</Link></li>
              <li><Link to="/ai-suite" className="hover:text-brand-500 transition-colors">AI Project Scoper</Link></li>
              <li><Link to="/client/contracts" className="hover:text-brand-500 transition-colors">Escrow Management</Link></li>
            </ul>
          </div>

          {/* Column: Platform & Legal */}
          <div>
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-900 dark:text-white">
              Platform
            </h4>
            <ul className="mt-4 space-y-2.5 text-sm text-slate-500 dark:text-slate-400">
              <li><Link to="/about" className="hover:text-brand-500 transition-colors">About SkillBridge</Link></li>
              <li><Link to="/security" className="hover:text-brand-500 transition-colors">Trust & Security</Link></li>
              <li><Link to="/privacy" className="hover:text-brand-500 transition-colors">Privacy Policy</Link></li>
              <li><Link to="/terms" className="hover:text-brand-500 transition-colors">Terms of Service</Link></li>
            </ul>
          </div>
        </div>

        <div className="mt-12 pt-8 border-t border-slate-200 dark:border-slate-800 flex flex-col sm:flex-row items-center justify-between gap-4 text-xs text-slate-500 dark:text-slate-400">
          <p>&copy; {new Date().getFullYear()} SkillBridge Platform Inc. All rights reserved.</p>
          <p className="flex items-center gap-1">
            Engineered with <Heart className="w-3.5 h-3.5 text-rose-500 fill-rose-500" /> for global talent.
          </p>
        </div>
      </div>
    </footer>
  );
};
