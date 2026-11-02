import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ThemeProvider } from '@/context/ThemeContext';
import { LandingPage } from '@/pages/landing/LandingPage';
import { MarketplacePage } from '@/pages/marketplace/MarketplacePage';
import { AiSuitePage } from '@/pages/ai/AiSuitePage';
import { LoginPage } from '@/pages/auth/LoginPage';
import { RegisterPage } from '@/pages/auth/RegisterPage';
import { FreelancerDashboard } from '@/pages/freelancer/FreelancerDashboard';
import { ClientDashboard } from '@/pages/client/ClientDashboard';
import { AdminDashboard } from '@/pages/admin/AdminDashboard';
import { ProjectWorkspace } from '@/pages/projects/ProjectWorkspace';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
    },
  },
});

export const App: React.FC = () => {
  return (
    <ThemeProvider>
      <QueryClientProvider client={queryClient}>
        <BrowserRouter>
          <Routes>
            {/* Public Routes */}
            <Route path="/" element={<LandingPage />} />
            <Route path="/marketplace" element={<MarketplacePage />} />
            <Route path="/talent" element={<MarketplacePage />} />
            <Route path="/ai-suite" element={<AiSuitePage />} />

            {/* Authentication Routes */}
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            {/* Freelancer Portal Routes */}
            <Route path="/freelancer/dashboard" element={<FreelancerDashboard />} />
            <Route path="/freelancer/contracts" element={<FreelancerDashboard />} />
            <Route path="/freelancer/proposals" element={<FreelancerDashboard />} />
            <Route path="/freelancer/earnings" element={<FreelancerDashboard />} />
            <Route path="/freelancer/profile" element={<FreelancerDashboard />} />

            {/* Client Portal Routes */}
            <Route path="/client/dashboard" element={<ClientDashboard />} />
            <Route path="/client/projects" element={<ClientDashboard />} />
            <Route path="/client/post-job" element={<AiSuitePage />} />
            <Route path="/client/contracts" element={<ClientDashboard />} />

            {/* Admin Console Routes */}
            <Route path="/admin/dashboard" element={<AdminDashboard />} />
            <Route path="/admin/fraud-detection" element={<AdminDashboard />} />
            <Route path="/admin/analytics" element={<AdminDashboard />} />
            <Route path="/admin/users" element={<AdminDashboard />} />

            {/* Interactive Project Workspace Routes */}
            <Route path="/workspace/:id" element={<ProjectWorkspace />} />

            {/* Fallback */}
            <Route
              path="*"
              element={
                <div className="min-h-screen flex flex-col items-center justify-center p-6 text-center bg-surface-bg dark:bg-dark-bg text-surface-text dark:text-dark-text">
                  <h2 className="text-3xl font-extrabold text-brand-500">404</h2>
                  <p className="text-xl font-bold mt-2">Module Under Construction</p>
                  <p className="text-xs text-slate-500 dark:text-slate-400 mt-1 max-w-sm">
                    This detailed route is mapped into our 90-100 page roadmap and will be expanded in the next phase batch.
                  </p>
                  <a href="/" className="mt-6 px-5 py-2.5 rounded-xl bg-brand-500 text-white text-xs font-bold">
                    Return to Home
                  </a>
                </div>
              }
            />
          </Routes>
        </BrowserRouter>
      </QueryClientProvider>
    </ThemeProvider>
  );
};

export default App;
