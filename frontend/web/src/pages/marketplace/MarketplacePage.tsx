import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import {
  Search,
  Filter,
  Sparkles,
  ChevronDown,
  ShieldCheck,
  Star,
  DollarSign,
  Plus,
} from 'lucide-react';
import { Navbar } from '@/components/common/Navbar';
import { Footer } from '@/components/common/Footer';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';
import { Modal } from '@/components/ui/Modal';
import { Input } from '@/components/ui/Input';
import { useApp, Job } from '@/context/AppContext';

export const MarketplacePage: React.FC = () => {
  const { jobs, submitProposal } = useApp();

  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('All');
  const [selectedType, setSelectedType] = useState('All');

  // Proposal Modal State
  const [activeJobForProposal, setActiveJobForProposal] = useState<Job | null>(null);
  const [coverLetter, setCoverLetter] = useState('');
  const [proposedBudget, setProposedBudget] = useState('5000');
  const [duration, setDuration] = useState('1-3 months');

  const categories = [
    'All',
    'Web Development',
    'Mobile Apps',
    'AI & Machine Learning',
    'UI/UX Design',
    'DevOps & Cloud',
  ];

  const filteredJobs = jobs.filter((job) => {
    const matchesSearch =
      job.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      job.skills.some((s) => s.toLowerCase().includes(searchQuery.toLowerCase())) ||
      job.client.toLowerCase().includes(searchQuery.toLowerCase());

    const matchesCategory = selectedCategory === 'All' || job.category === selectedCategory;
    const matchesType = selectedType === 'All' || job.type.includes(selectedType);

    return matchesSearch && matchesCategory && matchesType;
  });

  const handleOpenProposal = (job: Job) => {
    setActiveJobForProposal(job);
    setCoverLetter(
      `Hello ${job.client} team,\n\nI have extensive experience working with ${job.skills.slice(0, 3).join(', ')}. I recently completed a similar milestone-based contract and can deliver clean, modular code with full test coverage.`
    );
  };

  const handleProposalSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!activeJobForProposal) return;

    submitProposal(
      activeJobForProposal.id,
      coverLetter,
      Number(proposedBudget) || 5000,
      duration
    );
    setActiveJobForProposal(null);
  };

  return (
    <div className="flex flex-col min-h-screen bg-surface-bg dark:bg-dark-bg transition-colors">
      <Navbar />

      {/* Header Banner */}
      <div className="border-b border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 py-10 transition-colors">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div>
              <Badge variant="primary" size="md">
                Live Dynamic Marketplace
              </Badge>
              <h1 className="text-3xl sm:text-4xl font-extrabold text-slate-900 dark:text-white mt-1">
                Explore Verified Freelance Projects
              </h1>
              <p className="text-slate-500 dark:text-slate-400 text-sm max-w-2xl mt-1">
                Browse verified client job postings protected by milestone escrows, with real-time AI
                compatibility matching.
              </p>
            </div>
            <Link to="/client/post-job">
              <Button size="md" variant="primary" className="gap-2">
                <Plus className="w-4 h-4" /> Post a Project (AI)
              </Button>
            </Link>
          </div>

          {/* Search bar */}
          <div className="pt-2 flex flex-col sm:flex-row items-center gap-3 max-w-3xl">
            <div className="w-full relative">
              <Search className="w-5 h-5 absolute left-3.5 top-3.5 text-slate-400" />
              <input
                type="text"
                placeholder="Search by title, technology (e.g. React, Spring Boot, AI)..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full pl-11 pr-4 py-3 rounded-2xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-sm text-slate-900 dark:text-white placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-brand-500"
              />
            </div>
            {searchQuery && (
              <Button
                size="md"
                variant="outline"
                onClick={() => setSearchQuery('')}
                className="shrink-0"
              >
                Clear
              </Button>
            )}
          </div>
        </div>
      </div>

      {/* Main Marketplace Area */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 flex-1 w-full">
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
          {/* Left Filter Sidebar */}
          <div className="lg:col-span-3 space-y-6">
            <Card className="p-5 space-y-5">
              <div className="flex items-center justify-between pb-3 border-b border-slate-200 dark:border-slate-800">
                <span className="font-bold text-sm text-slate-900 dark:text-white flex items-center gap-2">
                  <Filter className="w-4 h-4 text-brand-500" /> Dynamic Filters
                </span>
                <button
                  onClick={() => {
                    setSelectedCategory('All');
                    setSelectedType('All');
                    setSearchQuery('');
                  }}
                  className="text-xs text-brand-500 hover:underline"
                >
                  Reset
                </button>
              </div>

              {/* Categories */}
              <div>
                <p className="text-xs font-bold uppercase tracking-wider text-slate-400 mb-3">
                  Categories
                </p>
                <div className="space-y-1.5">
                  {categories.map((cat) => (
                    <button
                      key={cat}
                      onClick={() => setSelectedCategory(cat)}
                      className={`w-full text-left px-3 py-1.5 rounded-lg text-xs font-semibold transition-colors ${
                        selectedCategory === cat
                          ? 'bg-brand-500 text-white'
                          : 'text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800'
                      }`}
                    >
                      {cat}
                    </button>
                  ))}
                </div>
              </div>

              {/* Contract Type */}
              <div className="pt-3 border-t border-slate-200 dark:border-slate-800">
                <p className="text-xs font-bold uppercase tracking-wider text-slate-400 mb-3">
                  Contract Type
                </p>
                <div className="space-y-1.5">
                  {['All', 'Fixed Price', 'Hourly'].map((type) => (
                    <button
                      key={type}
                      onClick={() => setSelectedType(type)}
                      className={`w-full text-left px-3 py-1.5 rounded-lg text-xs font-semibold transition-colors ${
                        selectedType === type
                          ? 'bg-brand-500 text-white'
                          : 'text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800'
                      }`}
                    >
                      {type}
                    </button>
                  ))}
                </div>
              </div>
            </Card>
          </div>

          {/* Right Project Feed */}
          <div className="lg:col-span-9 space-y-4">
            <div className="flex items-center justify-between text-xs font-semibold text-slate-500 dark:text-slate-400 pb-2">
              <span>Showing {filteredJobs.length} active opportunities</span>
              <span className="flex items-center gap-1">
                Sort by: <span className="font-bold text-slate-800 dark:text-slate-200">AI Compatibility</span>
                <ChevronDown className="w-3.5 h-3.5" />
              </span>
            </div>

            {filteredJobs.length === 0 ? (
              <Card className="p-12 text-center space-y-3">
                <p className="font-extrabold text-base text-slate-700 dark:text-slate-300">
                  No projects match your filter criteria
                </p>
                <p className="text-xs text-slate-400">
                  Try clearing your search query or selecting a different category.
                </p>
                <Button
                  size="sm"
                  variant="outline"
                  onClick={() => {
                    setSelectedCategory('All');
                    setSelectedType('All');
                    setSearchQuery('');
                  }}
                >
                  Reset All Filters
                </Button>
              </Card>
            ) : (
              filteredJobs.map((job) => (
                <Card key={job.id} hoverEffect className="p-6 space-y-4">
                  <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-3">
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <Badge variant="success" size="sm">
                          <Sparkles className="w-3 h-3 mr-1" /> {job.matchScore}% Match
                        </Badge>
                        <span className="text-xs font-medium text-slate-400">• {job.postedTime}</span>
                        <Badge variant="neutral" size="sm">{job.category}</Badge>
                      </div>
                      <h3 className="font-extrabold text-lg sm:text-xl text-slate-900 dark:text-white pt-1">
                        {job.title}
                      </h3>
                    </div>
                    <div className="text-left sm:text-right">
                      <p className="text-lg font-extrabold text-brand-600 dark:text-brand-400">
                        {job.budget}
                      </p>
                      <p className="text-xs text-slate-400 font-medium">{job.type}</p>
                    </div>
                  </div>

                  <p className="text-xs text-slate-600 dark:text-slate-300 leading-relaxed">
                    {job.description}
                  </p>

                  <div className="flex flex-wrap gap-1.5 pt-1">
                    {job.skills.map((skill, sIdx) => (
                      <span
                        key={sIdx}
                        className="px-2.5 py-1 rounded-lg bg-slate-100 dark:bg-slate-800 text-xs font-medium text-slate-700 dark:text-slate-300"
                      >
                        {skill}
                      </span>
                    ))}
                  </div>

                  <div className="pt-4 border-t border-slate-100 dark:border-slate-800 flex flex-col sm:flex-row sm:items-center justify-between gap-3 text-xs text-slate-500 dark:text-slate-400">
                    <div className="flex items-center gap-4">
                      <span className="flex items-center gap-1 font-semibold text-slate-700 dark:text-slate-300">
                        <ShieldCheck className="w-4 h-4 text-emerald-500" /> Payment Verified
                      </span>
                      <span className="flex items-center gap-1">
                        <Star className="w-3.5 h-3.5 fill-amber-500 text-amber-500" /> {job.clientRating}
                      </span>
                      <span>{job.clientSpent}</span>
                      <span>{job.proposals}</span>
                    </div>

                    <Button size="sm" variant="primary" onClick={() => handleOpenProposal(job)}>
                      Submit Proposal
                    </Button>
                  </div>
                </Card>
              ))
            )}
          </div>
        </div>
      </div>

      {/* PROPOSAL SUBMISSION MODAL */}
      {activeJobForProposal && (
        <Modal
          isOpen={!!activeJobForProposal}
          onClose={() => setActiveJobForProposal(null)}
          title={`Submit Proposal for ${activeJobForProposal.title}`}
          description={`Client: ${activeJobForProposal.client} • Budget: ${activeJobForProposal.budget}`}
          maxWidth="xl"
        >
          <form onSubmit={handleProposalSubmit} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <Input
                label="Your Proposed Budget ($ USD)"
                type="number"
                value={proposedBudget}
                onChange={(e) => setProposedBudget(e.target.value)}
                leftIcon={<DollarSign className="w-4 h-4" />}
                required
              />
              <div className="space-y-1.5">
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300">
                  Estimated Delivery Duration
                </label>
                <select
                  value={duration}
                  onChange={(e) => setDuration(e.target.value)}
                  className="w-full rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 px-3 py-2 text-xs text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-brand-500"
                >
                  <option>Less than 1 month</option>
                  <option>1-3 months</option>
                  <option>3-6 months</option>
                </select>
              </div>
            </div>

            <div className="space-y-1.5">
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300">
                Cover Letter & Value Proposition
              </label>
              <textarea
                rows={6}
                value={coverLetter}
                onChange={(e) => setCoverLetter(e.target.value)}
                placeholder="Highlight your previous relevant projects, architectural decisions, and why you are the ideal fit..."
                className="w-full rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 p-3 text-xs text-slate-900 dark:text-white placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-brand-500 leading-relaxed"
                required
              />
            </div>

            <div className="p-4 rounded-xl bg-slate-50 dark:bg-slate-800 text-[11px] text-slate-500 dark:text-slate-400 space-y-1">
              <p className="font-bold text-slate-700 dark:text-slate-200">
                Milestone Escrow Terms:
              </p>
              <p>
                100% of the proposed budget will be securely held in client escrow prior to project kickoff, and released as you complete each deliverable.
              </p>
            </div>

            <div className="pt-4 flex items-center justify-end gap-3">
              <Button type="button" variant="outline" onClick={() => setActiveJobForProposal(null)}>
                Cancel
              </Button>
              <Button type="submit" variant="primary">
                Confirm & Submit Proposal
              </Button>
            </div>
          </form>
        </Modal>
      )}

      <Footer />
    </div>
  );
};
