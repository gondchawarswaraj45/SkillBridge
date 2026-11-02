import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Mail, Lock, User, Briefcase, CheckCircle2 } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { ThemeToggle } from '@/components/common/ThemeToggle';
import heroImage from '@/assets/hero-banner.jpg';

export const RegisterPage: React.FC = () => {
  const [role, setRole] = useState<'FREELANCER' | 'CLIENT'>('FREELANCER');
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleRegister = (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setTimeout(() => {
      setIsLoading(false);
      if (role === 'FREELANCER') {
        navigate('/freelancer/dashboard');
      } else {
        navigate('/client/dashboard');
      }
    }, 600);
  };

  return (
    <div className="min-h-screen flex bg-surface-bg dark:bg-dark-bg transition-colors">
      {/* Left Form Area */}
      <div className="flex-1 flex flex-col justify-between p-6 sm:p-12 lg:p-16 max-w-xl mx-auto w-full">
        {/* Top Header */}
        <div className="flex items-center justify-between">
          <Link to="/" className="flex items-center gap-2.5">
            <div className="w-9 h-9 rounded-xl bg-brand-500 flex items-center justify-center text-white font-extrabold text-base shadow-soft-sm">
              SB
            </div>
            <span className="font-extrabold text-xl tracking-tight text-slate-900 dark:text-white">
              Skill<span className="text-brand-500">Bridge</span>
            </span>
          </Link>
          <ThemeToggle />
        </div>

        {/* Center Form */}
        <div className="py-6 space-y-6">
          <div>
            <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white tracking-tight">
              Create your account
            </h1>
            <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-1">
              Join SkillBridge as a client looking for talent or a skilled freelancer
            </p>
          </div>

          {/* Role Choice Cards */}
          <div className="grid grid-cols-2 gap-3">
            <div
              onClick={() => setRole('FREELANCER')}
              className={`p-4 rounded-2xl border-2 cursor-pointer transition-all text-left ${
                role === 'FREELANCER'
                  ? 'border-brand-500 bg-brand-50/50 dark:bg-brand-950/40 shadow-soft-sm'
                  : 'border-slate-200 dark:border-slate-800 hover:border-slate-300 dark:hover:border-slate-700'
              }`}
            >
              <User className={`w-5 h-5 mb-2 ${role === 'FREELANCER' ? 'text-brand-500' : 'text-slate-400'}`} />
              <p className="text-xs font-bold text-slate-900 dark:text-white">I'm a Freelancer</p>
              <p className="text-[11px] text-slate-400 mt-0.5">Looking for projects & clients</p>
            </div>

            <div
              onClick={() => setRole('CLIENT')}
              className={`p-4 rounded-2xl border-2 cursor-pointer transition-all text-left ${
                role === 'CLIENT'
                  ? 'border-accent-500 bg-accent-50/50 dark:bg-accent-950/40 shadow-soft-sm'
                  : 'border-slate-200 dark:border-slate-800 hover:border-slate-300 dark:hover:border-slate-700'
              }`}
            >
              <Briefcase className={`w-5 h-5 mb-2 ${role === 'CLIENT' ? 'text-accent-500' : 'text-slate-400'}`} />
              <p className="text-xs font-bold text-slate-900 dark:text-white">I'm a Client</p>
              <p className="text-[11px] text-slate-400 mt-0.5">Hiring talent for projects</p>
            </div>
          </div>

          <form onSubmit={handleRegister} className="space-y-4">
            <Input
              label="Full Name"
              type="text"
              placeholder="e.g. Swaraj Bhosale"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              required
              leftIcon={<User className="w-4 h-4" />}
            />

            <Input
              label="Work Email"
              type="email"
              placeholder="you@company.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              leftIcon={<Mail className="w-4 h-4" />}
            />

            <Input
              label="Password (min. 8 characters)"
              type="password"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              leftIcon={<Lock className="w-4 h-4" />}
            />

            <p className="text-[11px] text-slate-400 leading-relaxed">
              By creating an account, you agree to SkillBridge's{' '}
              <Link to="/terms" className="text-brand-500 underline">Terms of Service</Link> and{' '}
              <Link to="/privacy" className="text-brand-500 underline">Privacy Policy</Link>.
            </p>

            <Button type="submit" variant="primary" className="w-full py-3" isLoading={isLoading}>
              Create {role === 'FREELANCER' ? 'Freelancer' : 'Client'} Account
            </Button>
          </form>

          <div className="text-center text-xs text-slate-500 dark:text-slate-400">
            Already have an account?{' '}
            <Link to="/login" className="font-bold text-brand-500 hover:underline">
              Log in
            </Link>
          </div>
        </div>

        {/* Footer */}
        <p className="text-[11px] text-slate-400 text-center">
          &copy; {new Date().getFullYear()} SkillBridge Platform Inc.
        </p>
      </div>

      {/* Right Marketing Banner */}
      <div className="hidden lg:flex flex-1 relative bg-gradient-to-br from-indigo-700 via-brand-600 to-accent-700 text-white p-16 flex-col justify-between overflow-hidden">
        <div className="absolute inset-0 opacity-20 bg-cover bg-center" style={{ backgroundImage: `url(${heroImage})` }} />
        <div className="relative z-10 space-y-4">
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-white/20 text-xs font-semibold backdrop-blur-sm">
            ✨ Free Forever for Talents
          </div>
          <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight max-w-md">
            Join the Premier Network of Engineers & Designers
          </h2>
        </div>

        <div className="relative z-10 space-y-3 bg-white/10 backdrop-blur-md p-6 rounded-2xl border border-white/20 max-w-md">
          <p className="text-xs font-semibold uppercase tracking-wider text-accent-300">
            Why Professionals Choose SkillBridge
          </p>
          <ul className="space-y-2 text-xs font-medium text-brand-100">
            <li className="flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 text-emerald-300" /> Milestone payments guaranteed before you start
            </li>
            <li className="flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 text-emerald-300" /> Explainable AI match scores matching your expertise
            </li>
            <li className="flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 text-emerald-300" /> Zero bidding wars with verified talent pools
            </li>
          </ul>
        </div>
      </div>
    </div>
  );
};
