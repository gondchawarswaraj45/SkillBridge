import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import {
  Search,
  Filter,
  Sparkles,
  ChevronDown,
  ShieldCheck,
  Star,
} from 'lucide-react';
import { Navbar } from '@/components/common/Navbar';
import { Footer } from '@/components/common/Footer';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';

const sampleJobs = [
  {
    id: 1,
    title: 'Senior Full-Stack Java (Spring Boot) & React Platform Architect',
    client: 'Apex Financial Labs',
    clientCountry: 'United States',
    clientSpent: '$120k+ spent',
    clientRating: 4.95,
    budget: '$4,500 - $6,000',
    type: 'Fixed Price (Escrow)',
    duration: '1-3 months',
    experience: 'Expert',
    matchScore: 98,
    postedTime: '1 hour ago',
    proposals: '5 to 10',
    description:
      'We are looking for an experienced Full-Stack Engineer to lead the architecture of our cloud-native financial analytics platform. Must have deep expertise in Java 17, Spring Boot, MySQL, and modern React with TypeScript.',
    skills: ['Java', 'Spring Boot', 'React', 'TypeScript', 'MySQL', 'Docker'],
  },
  {
    id: 2,
    title: 'Cross-Platform React Native Mobile App (Expo SDK 52) with Offline Sync',
    client: 'HealthSync Global',
    clientCountry: 'Canada',
    clientSpent: '$45k+ spent',
    clientRating: 5.0,
    budget: '$65 - $80 / hr',
    type: 'Hourly',
    duration: '3-6 months',
    experience: 'Intermediate',
    matchScore: 94,
    postedTime: '3 hours ago',
    proposals: 'Less than 5',
    description:
      'Looking for a mobile developer to build and polish our patient monitoring app using React Native, Expo Router, NativeWind, and SQLite local cache synchronization.',
    skills: ['React Native', 'Expo', 'TypeScript', 'NativeWind', 'Zod', 'REST API'],
  },
  {
    id: 3,
    title: 'AI Semantic Document Parsing Pipeline with Google Gemini & Groq APIs',
    client: 'Lexis Automations Inc.',
    clientCountry: 'United Kingdom',
    clientSpent: '$80k+ spent',
    clientRating: 4.88,
    budget: '$3,800',
    type: 'Fixed Price',
    duration: 'Less than 1 month',
    experience: 'Expert',
    matchScore: 91,
    postedTime: '5 hours ago',
    proposals: '10 to 15',
    description:
      'We require an autonomous AI engineer to build a high-throughput legal document parser utilizing Spring AI with configurable Groq LLaMA and Gemini 1.5 flash models.',
    skills: ['Python', 'Spring AI', 'Groq', 'Gemini API', 'Docker'],
  },
  {
    id: 4,
    title: 'Figma to Production Code: Design System & SaaS UI Component Library',
    client: 'Vanguard Studios',
    clientCountry: 'Germany',
    clientSpent: '$30k+ spent',
    clientRating: 4.92,
    budget: '$2,500',
    type: 'Fixed Price',
    duration: '2-4 weeks',
    experience: 'Intermediate',
    matchScore: 88,
    postedTime: '6 hours ago',
    proposals: '5 to 10',
    description:
      'Convert our completed Figma token library into clean, modular, accessible React components with Tailwind CSS and full light & dark theme capabilities.',
    skills: ['Tailwind CSS', 'React', 'TypeScript', 'UI/UX', 'shadcn/ui'],
  },
];

export const MarketplacePage: React.FC = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('All');

  const categories = [
    'All',
    'Web Development',
    'Mobile Apps',
    'AI & Machine Learning',
    'UI/UX Design',
    'DevOps & Cloud',
    'Data Engineering',
  ];

  return (
    <div className="flex flex-col min-h-screen bg-surface-bg dark:bg-dark-bg transition-colors">
      <Navbar />

      {/* Header Banner */}
      <div className="border-b border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 py-10 transition-colors">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-4">
          <Badge variant="primary" size="md">
            Live Marketplace
          </Badge>
          <h1 className="text-3xl sm:text-4xl font-extrabold text-slate-900 dark:text-white">
            Explore High-Impact Freelance Projects
          </h1>
          <p className="text-slate-500 dark:text-slate-400 text-sm max-w-2xl">
            Browse verified client job postings protected by milestone escrows, with real-time AI
            compatibility matching.
          </p>

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
            <Button size="md" variant="primary" className="w-full sm:w-auto px-6 py-3 shrink-0">
              Search Jobs
            </Button>
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
                  <Filter className="w-4 h-4 text-brand-500" /> Filters
                </span>
                <button className="text-xs text-brand-500 hover:underline">Reset</button>
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

              {/* Project Type */}
              <div className="pt-3 border-t border-slate-200 dark:border-slate-800">
                <p className="text-xs font-bold uppercase tracking-wider text-slate-400 mb-3">
                  Contract Type
                </p>
                <div className="space-y-2 text-xs font-semibold text-slate-700 dark:text-slate-300">
                  <label className="flex items-center gap-2 cursor-pointer">
                    <input type="checkbox" defaultChecked className="rounded text-brand-500" /> Fixed Price
                    Escrow
                  </label>
                  <label className="flex items-center gap-2 cursor-pointer">
                    <input type="checkbox" defaultChecked className="rounded text-brand-500" /> Hourly Rate
                  </label>
                </div>
              </div>

              {/* Experience Level */}
              <div className="pt-3 border-t border-slate-200 dark:border-slate-800">
                <p className="text-xs font-bold uppercase tracking-wider text-slate-400 mb-3">
                  Experience Level
                </p>
                <div className="space-y-2 text-xs font-semibold text-slate-700 dark:text-slate-300">
                  <label className="flex items-center gap-2 cursor-pointer">
                    <input type="checkbox" defaultChecked className="rounded text-brand-500" /> Entry Level
                  </label>
                  <label className="flex items-center gap-2 cursor-pointer">
                    <input type="checkbox" defaultChecked className="rounded text-brand-500" /> Intermediate
                  </label>
                  <label className="flex items-center gap-2 cursor-pointer">
                    <input type="checkbox" defaultChecked className="rounded text-brand-500" /> Expert / Lead
                  </label>
                </div>
              </div>
            </Card>
          </div>

          {/* Right Project Feed */}
          <div className="lg:col-span-9 space-y-4">
            <div className="flex items-center justify-between text-xs font-semibold text-slate-500 dark:text-slate-400 pb-2">
              <span>Showing 4 curated projects</span>
              <span className="flex items-center gap-1 cursor-pointer hover:text-brand-500">
                Sort by: <span className="font-bold text-slate-800 dark:text-slate-200">AI Compatibility</span>
                <ChevronDown className="w-3.5 h-3.5" />
              </span>
            </div>

            {sampleJobs.map((job) => (
              <Card key={job.id} hoverEffect className="p-6 space-y-4">
                <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-3">
                  <div className="space-y-1">
                    <div className="flex items-center gap-2">
                      <Badge variant="success" size="sm">
                        <Sparkles className="w-3 h-3 mr-1" /> {job.matchScore}% Match
                      </Badge>
                      <span className="text-xs font-medium text-slate-400">• {job.postedTime}</span>
                    </div>
                    <Link to={`/projects/${job.id}`}>
                      <h3 className="font-extrabold text-lg sm:text-xl text-slate-900 dark:text-white hover:text-brand-500 transition-colors pt-1">
                        {job.title}
                      </h3>
                    </Link>
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
                    <span>{job.clientCountry}</span>
                  </div>

                  <Link to={`/projects/${job.id}/apply`}>
                    <Button size="sm" variant="primary">
                      Submit Proposal
                    </Button>
                  </Link>
                </div>
              </Card>
            ))}
          </div>
        </div>
      </div>

      <Footer />
    </div>
  );
};
