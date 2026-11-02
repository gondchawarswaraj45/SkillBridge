import React from 'react';

export const Footer: React.FC = () => {
  return (
    <footer className="border-t border-surface-border bg-white mt-auto py-12">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col md:flex-row items-center justify-between gap-6">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-lg bg-brand-500 flex items-center justify-center text-white font-bold text-sm">
              SB
            </div>
            <div>
              <p className="font-semibold text-surface-text">SkillBridge</p>
              <p className="text-xs text-surface-muted">Where Talent Meets Opportunity.</p>
            </div>
          </div>
          <p className="text-xs text-surface-muted">
            &copy; {new Date().getFullYear()} SkillBridge Inc. All rights reserved.
          </p>
        </div>
      </div>
    </footer>
  );
};

