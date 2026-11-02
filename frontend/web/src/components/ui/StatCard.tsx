import React from 'react';
import { Card } from '@/components/ui/Card';
import { cn } from '@/utils/cn';
import { TrendingUp, TrendingDown } from 'lucide-react';

export interface StatCardProps {
  title: string;
  value: string | number;
  change?: string;
  isPositive?: boolean;
  icon?: React.ReactNode;
  iconBgColor?: string;
  className?: string;
}

export const StatCard: React.FC<StatCardProps> = ({
  title,
  value,
  change,
  isPositive = true,
  icon,
  iconBgColor = 'bg-brand-50 text-brand-600 dark:bg-brand-950/70 dark:text-brand-400',
  className,
}) => {
  return (
    <Card hoverEffect className={cn('flex flex-col justify-between p-5', className)}>
      <div className="flex items-start justify-between">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wider text-slate-500 dark:text-slate-400">
            {title}
          </p>
          <h3 className="mt-2 text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white">
            {value}
          </h3>
        </div>
        {icon && (
          <div className={cn('p-3 rounded-2xl flex items-center justify-center', iconBgColor)}>
            {icon}
          </div>
        )}
      </div>

      {change && (
        <div className="mt-4 flex items-center gap-1.5 text-xs font-semibold">
          {isPositive ? (
            <span className="flex items-center text-emerald-600 dark:text-emerald-400">
              <TrendingUp className="w-3.5 h-3.5 mr-1" />
              {change}
            </span>
          ) : (
            <span className="flex items-center text-rose-600 dark:text-rose-400">
              <TrendingDown className="w-3.5 h-3.5 mr-1" />
              {change}
            </span>
          )}
          <span className="text-slate-400 dark:text-slate-500">vs last month</span>
        </div>
      )}
    </Card>
  );
};
