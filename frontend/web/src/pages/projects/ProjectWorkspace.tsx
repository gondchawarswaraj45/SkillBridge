import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import {
  FolderGit2,
  CheckSquare,
  Plus,
  MessageSquare,
  Video,
  FileText,
  DollarSign,
  ChevronRight,
  ShieldCheck,
  Calendar,
  ArrowRight,
  ArrowLeft,
  Trash2,
  CheckCircle2,
} from 'lucide-react';
import { DashboardLayout } from '@/layouts/DashboardLayout';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';
import { Avatar } from '@/components/ui/Avatar';
import { Modal } from '@/components/ui/Modal';
import { Input } from '@/components/ui/Input';
import { useApp, TaskItem } from '@/context/AppContext';
import { cn } from '@/utils/cn';

const columns = [
  { id: 'TODO', title: 'To Do', color: 'border-t-slate-400', next: 'IN_PROGRESS', prev: null },
  { id: 'IN_PROGRESS', title: 'In Progress', color: 'border-t-brand-500', next: 'IN_REVIEW', prev: 'TODO' },
  { id: 'IN_REVIEW', title: 'In Review', color: 'border-t-amber-500', next: 'COMPLETED', prev: 'IN_PROGRESS' },
  { id: 'COMPLETED', title: 'Completed', color: 'border-t-emerald-500', next: null, prev: 'IN_REVIEW' },
];

export const ProjectWorkspace: React.FC = () => {
  const {
    tasks,
    moveTask,
    addTask,
    deleteTask,
    milestones,
    submitDeliverable,
    approveMilestone,
    currentUser,
  } = useApp();

  const [activeTab, setActiveTab] = useState<'kanban' | 'milestones' | 'files' | 'meetings'>('kanban');

  // Task creation modal state
  const [isTaskModalOpen, setIsTaskModalOpen] = useState(false);
  const [taskTitle, setTaskTitle] = useState('');
  const [taskDescription, setTaskDescription] = useState('');
  const [taskPriority, setTaskPriority] = useState<'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'>('MEDIUM');
  const [taskColumn, setTaskColumn] = useState('TODO');
  const [taskAssignee, setTaskAssignee] = useState('Swaraj Bhosale');
  const [taskDueDate, setTaskDueDate] = useState('Nov 10');

  // Milestone deliverable modal state
  const [isDeliverableModalOpen, setIsDeliverableModalOpen] = useState(false);
  const [deliverableNotes, setDeliverableNotes] = useState('');
  const [deliverableUrl, setDeliverableUrl] = useState('');

  const priorityBadge = (priority: TaskItem['priority']) => {
    switch (priority) {
      case 'CRITICAL':
        return <Badge variant="danger" size="sm">Critical</Badge>;
      case 'HIGH':
        return <Badge variant="warning" size="sm">High</Badge>;
      case 'MEDIUM':
        return <Badge variant="primary" size="sm">Medium</Badge>;
      default:
        return <Badge variant="neutral" size="sm">Low</Badge>;
    }
  };

  const handleCreateTask = (e: React.FormEvent) => {
    e.preventDefault();
    if (!taskTitle.trim()) return;

    addTask(taskColumn, {
      title: taskTitle,
      description: taskDescription,
      priority: taskPriority,
      assignee: taskAssignee,
      dueDate: taskDueDate,
      milestone: 'Milestone 2',
    });

    setTaskTitle('');
    setTaskDescription('');
    setIsTaskModalOpen(false);
  };

  const handleSubmitDeliverable = (e: React.FormEvent) => {
    e.preventDefault();
    submitDeliverable(2, deliverableNotes, deliverableUrl);
    setDeliverableNotes('');
    setDeliverableUrl('');
    setIsDeliverableModalOpen(false);
  };

  const activeMilestone = milestones.find((m) => m.num === 2);

  return (
    <DashboardLayout role={currentUser.role.toLowerCase() as any} userName={currentUser.name}>
      {/* Workspace Header */}
      <div className="space-y-4">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 text-xs font-semibold text-slate-400 mb-1">
              <span>Contracts</span>
              <ChevronRight className="w-3.5 h-3.5" />
              <span>CTR-4029</span>
              <ChevronRight className="w-3.5 h-3.5" />
              <span className="text-brand-500 font-bold">Interactive Workspace</span>
            </div>
            <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 dark:text-white">
              Enterprise Fintech Dashboard & Analytics Platform
            </h1>
            <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">
              Client: <span className="font-semibold text-slate-700 dark:text-slate-300">Apex Financial Labs</span> • Escrow Protected • Started Oct 28, 2026
            </p>
          </div>

          <div className="flex items-center gap-2.5">
            <Link to="/messages">
              <Button size="sm" variant="outline" className="gap-1.5">
                <MessageSquare className="w-4 h-4 text-brand-500" /> Chat
              </Button>
            </Link>
            <Link to="/meetings">
              <Button size="sm" variant="outline" className="gap-1.5">
                <Video className="w-4 h-4 text-accent-500" /> Meet
              </Button>
            </Link>
            <Button
              size="sm"
              variant="primary"
              onClick={() => setIsTaskModalOpen(true)}
              className="gap-1.5"
            >
              <Plus className="w-4 h-4" /> Add Task
            </Button>
          </div>
        </div>

        {/* Milestone Escrow Status Banner */}
        <div className="p-4 rounded-2xl bg-brand-50 dark:bg-brand-950/60 border border-brand-200 dark:border-brand-800/80 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="p-2.5 rounded-xl bg-brand-500 text-white shrink-0">
              <ShieldCheck className="w-5 h-5" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <p className="text-xs font-bold text-slate-900 dark:text-white">
                  Milestone 2 of 4: Spring Boot Backend & Security Architecture
                </p>
                <Badge
                  variant={
                    activeMilestone?.status === 'COMPLETED'
                      ? 'success'
                      : activeMilestone?.status === 'IN_REVIEW'
                      ? 'warning'
                      : 'primary'
                  }
                  size="sm"
                >
                  {activeMilestone?.status}
                </Badge>
              </div>
              <p className="text-xs text-brand-700 dark:text-brand-300 mt-0.5">
                $1,500.00 USD securely held in Escrow • Deadline: Nov 12, 2026
              </p>
            </div>
          </div>

          <div className="flex items-center gap-2">
            {activeMilestone?.status === 'IN_PROGRESS' && (
              <Button
                size="sm"
                variant="primary"
                onClick={() => setIsDeliverableModalOpen(true)}
                className="text-xs font-bold"
              >
                Submit Deliverable for Approval
              </Button>
            )}

            {activeMilestone?.status === 'IN_REVIEW' && (
              <div className="flex items-center gap-2">
                <Badge variant="warning" size="md">Under Review by Client</Badge>
                <Button
                  size="sm"
                  variant="success"
                  onClick={() => approveMilestone(2)}
                  className="text-xs font-bold gap-1"
                >
                  <CheckCircle2 className="w-3.5 h-3.5" /> Approve & Release $1,500
                </Button>
              </div>
            )}

            {activeMilestone?.status === 'COMPLETED' && (
              <Badge variant="success" size="md" className="gap-1 font-bold">
                <CheckCircle2 className="w-4 h-4" /> Escrow Funds Released to Freelancer
              </Badge>
            )}
          </div>
        </div>

        {/* Workspace Navigation Tabs */}
        <div className="flex items-center gap-2 border-b border-slate-200 dark:border-slate-800 pt-2">
          {[
            { id: 'kanban', label: 'Kanban Board', icon: <CheckSquare className="w-4 h-4" /> },
            { id: 'milestones', label: 'Milestones & Payments', icon: <DollarSign className="w-4 h-4" /> },
            { id: 'files', label: 'Project Vault & Files', icon: <FileText className="w-4 h-4" /> },
            { id: 'meetings', label: 'Video Meetings & Minutes', icon: <Video className="w-4 h-4" /> },
          ].map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id as any)}
              className={cn(
                'flex items-center gap-2 px-4 py-3 text-xs font-bold border-b-2 transition-all duration-150',
                activeTab === tab.id
                  ? 'border-brand-500 text-brand-600 dark:text-brand-400'
                  : 'border-transparent text-slate-500 hover:text-slate-800 dark:hover:text-slate-200'
              )}
            >
              {tab.icon}
              {tab.label}
            </button>
          ))}
        </div>
      </div>

      {/* Tab 1: Dynamic Kanban Board */}
      {activeTab === 'kanban' && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 items-start">
          {columns.map((col) => (
            <div
              key={col.id}
              className={cn(
                'rounded-2xl bg-slate-100/70 dark:bg-slate-900/60 p-4 border-t-4 border border-slate-200 dark:border-slate-800 shadow-soft-sm space-y-3.5',
                col.color
              )}
            >
              {/* Column Header */}
              <div className="flex items-center justify-between pb-2 border-b border-slate-200/80 dark:border-slate-800">
                <div className="flex items-center gap-2">
                  <h3 className="font-extrabold text-sm text-slate-800 dark:text-slate-100">
                    {col.title}
                  </h3>
                  <span className="px-2 py-0.5 rounded-full text-[11px] font-bold bg-white dark:bg-slate-800 text-slate-600 dark:text-slate-300">
                    {tasks[col.id]?.length || 0}
                  </span>
                </div>
                <button
                  onClick={() => {
                    setTaskColumn(col.id);
                    setIsTaskModalOpen(true);
                  }}
                  className="p-1 rounded-lg hover:bg-slate-200 dark:hover:bg-slate-700 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200"
                >
                  <Plus className="w-4 h-4" />
                </button>
              </div>

              {/* Task Cards */}
              <div className="space-y-3">
                {tasks[col.id]?.map((task) => (
                  <Card
                    key={task.id}
                    hoverEffect
                    className="p-4 bg-white dark:bg-slate-800/90 border border-slate-200/80 dark:border-slate-700/80 space-y-3"
                  >
                    <div className="flex items-center justify-between">
                      <span className="text-[10px] font-mono font-bold text-slate-400">
                        {task.id}
                      </span>
                      {priorityBadge(task.priority)}
                    </div>

                    <h4 className="text-xs font-bold text-slate-900 dark:text-white leading-snug">
                      {task.title}
                    </h4>

                    <p className="text-[11px] text-slate-500 dark:text-slate-400 line-clamp-2 leading-relaxed">
                      {task.description}
                    </p>

                    <div className="pt-2 border-t border-slate-100 dark:border-slate-700 flex items-center justify-between text-[11px] text-slate-400">
                      <span className="flex items-center gap-1 font-semibold text-slate-600 dark:text-slate-300">
                        <Calendar className="w-3 h-3 text-slate-400" /> {task.dueDate}
                      </span>
                      <Avatar name={task.assignee} size="sm" />
                    </div>

                    {/* Interactive Task Action Buttons */}
                    <div className="pt-2 border-t border-slate-100 dark:border-slate-700/60 flex items-center justify-between text-xs">
                      {col.prev ? (
                        <button
                          onClick={() => moveTask(task.id, col.prev!)}
                          className="p-1 text-[11px] font-semibold text-slate-500 hover:text-brand-500 flex items-center gap-0.5"
                          title="Move left"
                        >
                          <ArrowLeft className="w-3 h-3" /> Back
                        </button>
                      ) : (
                        <div />
                      )}

                      <button
                        onClick={() => deleteTask(col.id, task.id)}
                        className="p-1 text-slate-400 hover:text-rose-500"
                        title="Delete task"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>

                      {col.next && (
                        <button
                          onClick={() => moveTask(task.id, col.next!)}
                          className="p-1 text-[11px] font-bold text-brand-600 dark:text-brand-400 hover:underline flex items-center gap-0.5"
                          title="Move right"
                        >
                          Next <ArrowRight className="w-3 h-3" />
                        </button>
                      )}
                    </div>
                  </Card>
                ))}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Tab 2: Dynamic Milestones */}
      {activeTab === 'milestones' && (
        <Card className="p-6 space-y-6">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div>
              <h3 className="font-extrabold text-lg text-slate-900 dark:text-white">
                Contract Milestones & Escrow Ledger
              </h3>
              <p className="text-xs text-slate-500 dark:text-slate-400">
                Contract Total: $6,000.00 USD • Available Escrow Protection Active
              </p>
            </div>
            {activeMilestone?.status === 'IN_REVIEW' && (
              <Button
                variant="success"
                onClick={() => approveMilestone(2)}
                className="gap-2 text-xs font-bold"
              >
                <CheckCircle2 className="w-4 h-4" /> Approve Milestone 2 ($1,500)
              </Button>
            )}
          </div>

          <div className="space-y-4">
            {milestones.map((m) => (
              <div
                key={m.num}
                className="p-5 rounded-2xl border border-slate-200 dark:border-slate-800 bg-slate-50 dark:bg-slate-800/50 flex flex-col sm:flex-row sm:items-center justify-between gap-4"
              >
                <div>
                  <div className="flex items-center gap-2">
                    <span className="font-bold text-sm text-slate-900 dark:text-white">
                      Milestone {m.num}: {m.title}
                    </span>
                    <Badge
                      variant={
                        m.status === 'COMPLETED'
                          ? 'success'
                          : m.status === 'IN_REVIEW'
                          ? 'warning'
                          : m.status === 'IN_PROGRESS'
                          ? 'primary'
                          : 'neutral'
                      }
                      size="sm"
                    >
                      {m.status}
                    </Badge>
                  </div>
                  <p className="text-xs text-slate-400 mt-1">{m.date}</p>
                  {m.deliverableNotes && (
                    <p className="text-xs font-mono text-emerald-600 dark:text-emerald-400 mt-2 bg-emerald-50 dark:bg-emerald-950/60 p-2 rounded-lg">
                      Notes: {m.deliverableNotes}
                    </p>
                  )}
                </div>
                <div className="flex items-center gap-4">
                  <span className="text-lg font-extrabold text-slate-900 dark:text-white">
                    ${m.amount.toLocaleString()}.00
                  </span>
                  {m.status === 'IN_PROGRESS' && (
                    <Button
                      size="sm"
                      variant="primary"
                      onClick={() => setIsDeliverableModalOpen(true)}
                    >
                      Submit Work
                    </Button>
                  )}
                </div>
              </div>
            ))}
          </div>
        </Card>
      )}

      {/* Tab 3 & 4 */}
      {(activeTab === 'files' || activeTab === 'meetings') && (
        <Card className="p-12 text-center space-y-3">
          <FolderGit2 className="w-10 h-10 text-brand-500 mx-auto" />
          <h3 className="font-extrabold text-lg text-slate-900 dark:text-white">
            {activeTab === 'files' ? 'Project Asset Vault' : 'Live Video Meeting Room'}
          </h3>
          <p className="text-xs text-slate-500 dark:text-slate-400 max-w-sm mx-auto">
            {activeTab === 'files'
              ? 'Shared deliverables, design specifications, and code attachments are encrypted and preserved here.'
              : 'Launch end-to-end video meetings with AI automated transcript minutes generation.'}
          </p>
          <Link to={activeTab === 'files' ? '#' : '/meetings'}>
            <Button size="sm" variant="primary">
              {activeTab === 'files' ? 'Upload Asset' : 'Join Video Room Now'}
            </Button>
          </Link>
        </Card>
      )}

      {/* MODAL 1: Add Task Modal */}
      <Modal
        isOpen={isTaskModalOpen}
        onClose={() => setIsTaskModalOpen(false)}
        title="Create New Project Task"
        description="Add a task card to your live Kanban workspace board"
      >
        <form onSubmit={handleCreateTask} className="space-y-4">
          <Input
            label="Task Title"
            placeholder="e.g. Implement WebSocket Reconnection Logic"
            value={taskTitle}
            onChange={(e) => setTaskTitle(e.target.value)}
            required
          />

          <div className="space-y-1.5">
            <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300">
              Description
            </label>
            <textarea
              rows={3}
              placeholder="Describe acceptance criteria and deliverables..."
              value={taskDescription}
              onChange={(e) => setTaskDescription(e.target.value)}
              className="w-full rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 p-3 text-xs text-slate-900 dark:text-white placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-brand-500"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-1.5">
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300">
                Column
              </label>
              <select
                value={taskColumn}
                onChange={(e) => setTaskColumn(e.target.value)}
                className="w-full rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 px-3 py-2 text-xs text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-brand-500"
              >
                <option value="TODO">To Do</option>
                <option value="IN_PROGRESS">In Progress</option>
                <option value="IN_REVIEW">In Review</option>
                <option value="COMPLETED">Completed</option>
              </select>
            </div>

            <div className="space-y-1.5">
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300">
                Priority
              </label>
              <select
                value={taskPriority}
                onChange={(e) => setTaskPriority(e.target.value as any)}
                className="w-full rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 px-3 py-2 text-xs text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-brand-500"
              >
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
                <option value="CRITICAL">Critical</option>
              </select>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Assignee"
              value={taskAssignee}
              onChange={(e) => setTaskAssignee(e.target.value)}
            />
            <Input
              label="Due Date"
              value={taskDueDate}
              onChange={(e) => setTaskDueDate(e.target.value)}
            />
          </div>

          <div className="pt-4 flex items-center justify-end gap-3">
            <Button type="button" variant="outline" onClick={() => setIsTaskModalOpen(false)}>
              Cancel
            </Button>
            <Button type="submit" variant="primary">
              Create Task
            </Button>
          </div>
        </form>
      </Modal>

      {/* MODAL 2: Submit Deliverable Modal */}
      <Modal
        isOpen={isDeliverableModalOpen}
        onClose={() => setIsDeliverableModalOpen(false)}
        title="Submit Milestone 2 Deliverable"
        description="Notify the client for review to trigger escrow release"
      >
        <form onSubmit={handleSubmitDeliverable} className="space-y-4">
          <Input
            label="Live Demo / GitHub PR URL"
            placeholder="https://github.com/organization/repo/pull/12"
            value={deliverableUrl}
            onChange={(e) => setDeliverableUrl(e.target.value)}
            required
          />

          <div className="space-y-1.5">
            <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300">
              Deliverable Notes & Summary
            </label>
            <textarea
              rows={4}
              placeholder="Detail what was built, testing verification instructions, and API endpoints..."
              value={deliverableNotes}
              onChange={(e) => setDeliverableNotes(e.target.value)}
              className="w-full rounded-xl border border-slate-200 dark:border-slate-700 bg-white dark:bg-slate-900 p-3 text-xs text-slate-900 dark:text-white placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-brand-500"
              required
            />
          </div>

          <div className="pt-4 flex items-center justify-end gap-3">
            <Button type="button" variant="outline" onClick={() => setIsDeliverableModalOpen(false)}>
              Cancel
            </Button>
            <Button type="submit" variant="success">
              Submit for $1,500 Escrow Review
            </Button>
          </div>
        </form>
      </Modal>
    </DashboardLayout>
  );
};
