import React from 'react';
import { cn } from '@/utils/cn';

export interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
  hoverEffect?: boolean;
}

export const Card = React.forwardRef<HTMLDivElement, CardProps>(
  ({ className, hoverEffect = false, children, ...props }, ref) => {
    return (
      <div
        ref={ref}
        className={cn(
          'bg-white dark:bg-slate-900 rounded-2xl border border-surface-border dark:border-slate-800 shadow-soft-sm dark:shadow-dark-sm p-6 transition-colors',
          hoverEffect &&
            'transition-all duration-200 hover:shadow-soft-lg dark:hover:shadow-dark-lg hover:-translate-y-0.5 hover:border-brand-200 dark:hover:border-slate-700',
          className
        )}
        {...props}
      >
        {children}
      </div>
    );
  }
);

Card.displayName = 'Card';
