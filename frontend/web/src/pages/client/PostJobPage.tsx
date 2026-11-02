import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Sparkles, ArrowRight, ArrowLeft, CheckCircle2, DollarSign } from 'lucide-react';
import { DashboardLayout } from '@/layouts/DashboardLayout';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Badge } from '@/components/ui/Badge';
import { useApp } from '@/context/AppContext';

export const PostJobPage: React.FC = () => {
  const { postJob } = useApp();
  const navigate = useNavigate();

  const [step, setStep] = useState(1);
  const [title, setTitle] = useState('');
  const [category, setCategory] = useState('Web Development');
  const [description, setDescription] = useState('');
  const [budgetMin, setBudgetMin] = useState('4000');
  const [budgetMax, setBudgetMax] = useState('6000');
  const [contractType, setContractType] = useState('Fixed Price (Escrow)');
  const [experience] = useState('Expert');
  const [duration, setDuration] = useState('1-3 months');
  const [skills, setSkills] = useState<string[]>(['React', 'TypeScript', 'Spring Boot']);
  const [isAiGenerating, setIsAiGenerating] = useState(false);

  const availableSkills = ['Java', 'Spring Boot', 'React', 'TypeScript', 'Node.js', 'Python', 'MySQL', 'Docker', 'AWS', 'Expo', 'React Native'];

  const handleAiGenerateDescription = () => {
    if (!title.trim()) return;
    setIsAiGenerating(true);
    setTimeout(() => {
      setIsAiGenerating(false);
      setDescription(
        `We are seeking a seasoned ${title} to spearhead our upcoming architecture. \n\nKey Responsibilities:\n• Design and implement cloud-native microservices with robust test coverage.\n• Collaborate closely with cross-functional product stakeholders.\n• Deliver milestone deliverables on time with automated CI/CD pipelines.\n\nQualifications:\n• 4+ years of hands-on professional production experience.\n• Strong mastery of ${skills.join(', ')}.\n• Proven track record of high code quality and reliable execution.`
      );
    }, 1000);
  };

  const addSkill = (skill: string) => {
    if (!skills.includes(skill)) {
      setSkills([...skills, skill]);
    }
  };

  const removeSkill = (skillToRemove: string) => {
    setSkills(skills.filter((s) => s !== skillToRemove));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    postJob({
      title,
      client: 'Apex Financial Labs',
      clientCountry: 'United States',
      clientSpent: '$120k+ spent',
      clientRating: 4.95,
      budget: `$${budgetMin} - $${budgetMax}`,
      type: contractType,
      duration,
      experience,
      matchScore: 96,
      category,
      description,
      skills,
    });
    navigate('/marketplace');
  };

  return (
    <DashboardLayout role="client" userName="Apex Financial Labs" userEmail="hiring@apexlabs.com">
      <div className="max-w-4xl mx-auto space-y-8">
        <div>
          <Badge variant="primary" size="md">
            Job Scoper Wizard
          </Badge>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white mt-1">
            Post a Project with AI Scoper
          </h1>
          <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">
            Step {step} of 3: {step === 1 ? 'Scope & Category' : step === 2 ? 'Description & AI Generator' : 'Budget & Milestone Settings'}
          </p>
        </div>

        {/* Step Progress Bar */}
        <div className="w-full bg-slate-200 dark:bg-slate-800 h-2 rounded-full overflow-hidden">
          <div
            className="bg-brand-500 h-full transition-all duration-300"
            style={{ width: `${(step / 3) * 100}%` }}
          />
        </div>

        <form onSubmit={handleSubmit}>
          {/* STEP 1: Basic Scope */}
          {step === 1 && (
            <Card className="p-6 sm:p-8 space-y-6">
              <h3 className="font-extrabold text-lg text-slate-900 dark:text-white">
                1. Project Title & Category
              </h3>

              <Input
                label="Project Title"
                placeholder="e.g. Lead Full-Stack Spring Boot & React Platform Architect"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                required
              />

              <div className="space-y-1.5">
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300">
                  Category
                </label>
                <select
                  value={category}
                  onChange={(e) => setCategory(e.target.value)}
                  className="w-full rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 px-3.5 py-2.5 text-sm text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-brand-500"
                >
                  <option>Web Development</option>
                  <option>Mobile Apps</option>
                  <option>AI & Machine Learning</option>
                  <option>UI/UX Design</option>
                  <option>DevOps & Cloud</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-2">
                  Select Required Skills
                </label>
                <div className="flex flex-wrap gap-2 mb-3">
                  {skills.map((skill) => (
                    <span
                      key={skill}
                      className="px-3 py-1 rounded-xl bg-brand-50 dark:bg-brand-950 text-brand-700 dark:text-brand-300 border border-brand-200 dark:border-brand-800 text-xs font-bold flex items-center gap-1.5"
                    >
                      {skill}
                      <button
                        type="button"
                        onClick={() => removeSkill(skill)}
                        className="hover:text-rose-500"
                      >
                        ×
                      </button>
                    </span>
                  ))}
                </div>

                <div className="flex flex-wrap gap-1.5">
                  {availableSkills
                    .filter((s) => !skills.includes(s))
                    .map((s) => (
                      <button
                        key={s}
                        type="button"
                        onClick={() => addSkill(s)}
                        className="px-2.5 py-1 rounded-lg border border-dashed border-slate-300 dark:border-slate-700 text-xs font-medium text-slate-600 dark:text-slate-300 hover:border-brand-500 hover:text-brand-500"
                      >
                        + {s}
                      </button>
                    ))}
                </div>
              </div>

              <div className="pt-4 flex justify-end">
                <Button
                  type="button"
                  variant="primary"
                  onClick={() => {
                    if (title.trim()) setStep(2);
                  }}
                  className="gap-2"
                >
                  Continue to Description <ArrowRight className="w-4 h-4" />
                </Button>
              </div>
            </Card>
          )}

          {/* STEP 2: Description & AI Scoper */}
          {step === 2 && (
            <Card className="p-6 sm:p-8 space-y-6">
              <div className="flex items-center justify-between">
                <h3 className="font-extrabold text-lg text-slate-900 dark:text-white">
                  2. Detailed Specification & Scope
                </h3>
                <Button
                  type="button"
                  variant="secondary"
                  size="sm"
                  onClick={handleAiGenerateDescription}
                  isLoading={isAiGenerating}
                  className="gap-1.5 text-xs font-bold"
                >
                  <Sparkles className="w-3.5 h-3.5" /> Auto-Generate with AI
                </Button>
              </div>

              <div className="space-y-1.5">
                <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300">
                  Project Description
                </label>
                <textarea
                  rows={8}
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  placeholder="Provide detailed project requirements or click 'Auto-Generate with AI' above..."
                  className="w-full rounded-2xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 p-4 text-sm text-slate-900 dark:text-white placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-brand-500 leading-relaxed font-sans"
                  required
                />
              </div>

              <div className="pt-4 flex items-center justify-between">
                <Button type="button" variant="outline" onClick={() => setStep(1)} className="gap-2">
                  <ArrowLeft className="w-4 h-4" /> Back
                </Button>
                <Button
                  type="button"
                  variant="primary"
                  onClick={() => {
                    if (description.trim()) setStep(3);
                  }}
                  className="gap-2"
                >
                  Set Budget & Timeline <ArrowRight className="w-4 h-4" />
                </Button>
              </div>
            </Card>
          )}

          {/* STEP 3: Budget & Publish */}
          {step === 3 && (
            <Card className="p-6 sm:p-8 space-y-6">
              <h3 className="font-extrabold text-lg text-slate-900 dark:text-white">
                3. Budget, Timeline & Escrow Protection
              </h3>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <Input
                  label="Minimum Budget ($ USD)"
                  type="number"
                  value={budgetMin}
                  onChange={(e) => setBudgetMin(e.target.value)}
                  leftIcon={<DollarSign className="w-4 h-4" />}
                  required
                />
                <Input
                  label="Maximum Budget ($ USD)"
                  type="number"
                  value={budgetMax}
                  onChange={(e) => setBudgetMax(e.target.value)}
                  leftIcon={<DollarSign className="w-4 h-4" />}
                  required
                />
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div className="space-y-1.5">
                  <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300">
                    Contract Type
                  </label>
                  <select
                    value={contractType}
                    onChange={(e) => setContractType(e.target.value)}
                    className="w-full rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 px-3.5 py-2.5 text-sm text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-brand-500"
                  >
                    <option>Fixed Price (Escrow)</option>
                    <option>Hourly Rate</option>
                  </select>
                </div>

                <div className="space-y-1.5">
                  <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300">
                    Estimated Duration
                  </label>
                  <select
                    value={duration}
                    onChange={(e) => setDuration(e.target.value)}
                    className="w-full rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 px-3.5 py-2.5 text-sm text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-brand-500"
                  >
                    <option>Less than 1 month</option>
                    <option>1-3 months</option>
                    <option>3-6 months</option>
                    <option>6+ months</option>
                  </select>
                </div>
              </div>

              <div className="p-4 rounded-2xl bg-emerald-50 dark:bg-emerald-950/60 border border-emerald-200 dark:border-emerald-800 text-xs text-emerald-900 dark:text-emerald-200 space-y-1">
                <p className="font-bold flex items-center gap-1.5">
                  <CheckCircle2 className="w-4 h-4 text-emerald-600" /> SkillBridge Milestone Escrow Guarantee
                </p>
                <p className="text-[11px] opacity-90">
                  Your funds are only deposited when contracts begin, and disbursed strictly upon milestone deliverable approval.
                </p>
              </div>

              <div className="pt-4 flex items-center justify-between">
                <Button type="button" variant="outline" onClick={() => setStep(2)} className="gap-2">
                  <ArrowLeft className="w-4 h-4" /> Back
                </Button>
                <Button type="submit" variant="primary" className="gap-2">
                  Publish to Marketplace Now <ArrowRight className="w-4 h-4" />
                </Button>
              </div>
            </Card>
          )}
        </form>
      </div>
    </DashboardLayout>
  );
};
