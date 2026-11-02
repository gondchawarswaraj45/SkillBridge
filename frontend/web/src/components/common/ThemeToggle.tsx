import React from 'react';
import { Sun, Moon } from 'lucide-react';
import { useTheme } from '@/context/ThemeContext';
import { cn } from '@/utils/cn';

interface ThemeToggleProps {
  className?: string;
  variant?: 'button' | 'switch';
}

export const ThemeToggle: React.FC<ThemeToggleProps> = ({ className }) => {
  const { isDark, toggleTheme } = useTheme();

  return (
    <button
      onClick={toggleTheme}
      type="button"
      className={cn(
        'p-2 rounded-xl transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-brand-500/50',
        'bg-slate-100 hover:bg-slate-200 text-slate-700',
        'dark:bg-slate-800 dark:hover:bg-slate-700 dark:text-slate-200',
        className
      )}
      title={isDark ? 'Switch to Light Mode' : 'Switch to Dark Mode'}
      aria-label="Toggle theme"
    >
      {isDark ? (
        <Sun className="w-4 h-4 text-amber-400 transition-transform duration-300 rotate-0 hover:rotate-90" />
      ) : (
        <Moon className="w-4 h-4 text-brand-600 transition-transform duration-300 rotate-0 hover:-rotate-12" />
      )}
    </button>
  );
};

