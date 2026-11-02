import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Mail, Lock, Sparkles } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { ThemeToggle } from '@/components/common/ThemeToggle';
import heroImage from '@/assets/hero-banner.jpg';

export const LoginPage: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState<'freelancer' | 'client'>('freelancer');
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setTimeout(() => {
      setIsLoading(false);
      if (role === 'freelancer') {
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
        <div className="py-8 space-y-6">
          <div>
            <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white tracking-tight">
              Welcome back
            </h1>
            <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400 mt-1.5">
              Enter your credentials to access your SkillBridge workspace
            </p>
          </div>

          {/* Role selector tab */}
          <div className="grid grid-cols-2 p-1 rounded-xl bg-slate-100 dark:bg-slate-800 text-xs font-bold text-slate-600 dark:text-slate-300">
            <button
              type="button"
              onClick={() => setRole('freelancer')}
              className={`py-2 rounded-lg transition-all ${
                role === 'freelancer'
                  ? 'bg-white dark:bg-slate-700 text-brand-600 dark:text-brand-300 shadow-soft-sm'
                  : 'hover:text-slate-900 dark:hover:text-white'
              }`}
            >
              Freelancer Portal
            </button>
            <button
              type="button"
              onClick={() => setRole('client')}
              className={`py-2 rounded-lg transition-all ${
                role === 'client'
                  ? 'bg-white dark:bg-slate-700 text-accent-600 dark:text-accent-300 shadow-soft-sm'
                  : 'hover:text-slate-900 dark:hover:text-white'
              }`}
            >
              Client Portal
            </button>
          </div>

          <form onSubmit={handleLogin} className="space-y-4">
            <Input
              label="Email Address"
              type="email"
              placeholder="you@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              leftIcon={<Mail className="w-4 h-4" />}
            />

            <div>
              <div className="flex items-center justify-between mb-1">
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300">
                  Password
                </label>
                <Link to="/forgot-password" className="text-xs font-semibold text-brand-500 hover:underline">
                  Forgot password?
                </Link>
              </div>
              <Input
                type="password"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                leftIcon={<Lock className="w-4 h-4" />}
              />
            </div>

            <Button type="submit" variant="primary" className="w-full py-3" isLoading={isLoading}>
              Sign In to {role === 'freelancer' ? 'Freelancer Portal' : 'Client Console'}
            </Button>
          </form>

          <div className="text-center text-xs text-slate-500 dark:text-slate-400">
            Don't have an account yet?{' '}
            <Link to="/register" className="font-bold text-brand-500 hover:underline">
              Create an account
            </Link>
          </div>
        </div>

        {/* Footer info */}
        <p className="text-[11px] text-slate-400 text-center">
          Protected by SkillBridge AI Security & 256-bit SSL Encryption
        </p>
      </div>

      {/* Right Marketing Banner (Desktop only) */}
      <div className="hidden lg:flex flex-1 relative bg-gradient-to-br from-brand-600 via-indigo-700 to-accent-700 text-white p-16 flex-col justify-between overflow-hidden">
        <div className="absolute inset-0 opacity-20 bg-cover bg-center" style={{ backgroundImage: `url(${heroImage})` }} />
        <div className="relative z-10 space-y-4">
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-white/20 text-xs font-semibold backdrop-blur-sm">
            <Sparkles className="w-3.5 h-3.5" /> Next-Gen Freelancing
          </div>
          <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight max-w-md">
            Unlock High-Impact Global Opportunities with AI
          </h2>
        </div>

        <div className="relative z-10 space-y-3 bg-white/10 backdrop-blur-md p-6 rounded-2xl border border-white/20 max-w-md">
          <p className="text-sm italic font-medium text-brand-50">
            "SkillBridge transformed our tech recruitment. The AI matching nailed our tech stack on the first
            shortlist, and milestone escrows gave us 100% peace of mind."
          </p>
          <div className="flex items-center gap-3 pt-2">
            <div className="w-8 h-8 rounded-full bg-white/30 font-bold flex items-center justify-center text-xs">
              MR
            </div>
            <div>
              <p className="text-xs font-bold">Marcus Ross</p>
              <p className="text-[11px] text-brand-200">VP Engineering, CloudScale Labs</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
