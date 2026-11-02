import React, { useState, useRef, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  Send,
  Paperclip,
  Video,
  Phone,
  Search,
  CheckCheck,
} from 'lucide-react';
import { DashboardLayout } from '@/layouts/DashboardLayout';
import { Button } from '@/components/ui/Button';
import { Avatar } from '@/components/ui/Avatar';
import { Badge } from '@/components/ui/Badge';
import { useApp } from '@/context/AppContext';

export const ChatPage: React.FC = () => {
  const { messages, sendMessage, currentUser } = useApp();
  const [inputText, setInputText] = useState('');
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSend = (e: React.FormEvent) => {
    e.preventDefault();
    if (!inputText.trim()) return;
    sendMessage(inputText);
    setInputText('');
  };

  return (
    <DashboardLayout role={currentUser.role.toLowerCase() as any} userName={currentUser.name}>
      <div className="h-[calc(100vh-140px)] flex flex-col md:flex-row rounded-3xl overflow-hidden border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 shadow-soft-lg transition-colors">
        {/* Left Thread List */}
        <div className="w-full md:w-80 border-r border-slate-200 dark:border-slate-800 flex flex-col shrink-0">
          <div className="p-4 border-b border-slate-200 dark:border-slate-800">
            <h2 className="font-extrabold text-base text-slate-900 dark:text-white mb-3">Messages</h2>
            <div className="relative">
              <Search className="w-4 h-4 absolute left-3 top-3 text-slate-400" />
              <input
                type="text"
                placeholder="Search conversations..."
                className="w-full pl-9 pr-3 py-2 text-xs rounded-xl bg-slate-100 dark:bg-slate-800 border-none text-slate-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-brand-500"
              />
            </div>
          </div>

          <div className="flex-1 overflow-y-auto divide-y divide-slate-100 dark:divide-slate-800">
            {/* Active Thread */}
            <div className="p-4 flex items-start gap-3 bg-brand-50/60 dark:bg-brand-950/40 cursor-pointer border-l-4 border-brand-500">
              <Avatar name="Marcus Vance" size="md" status="online" />
              <div className="flex-1 min-w-0">
                <div className="flex items-center justify-between">
                  <p className="text-xs font-bold text-slate-900 dark:text-white truncate">Marcus Vance</p>
                  <span className="text-[10px] text-slate-400">10:20 AM</span>
                </div>
                <p className="text-[11px] text-brand-600 dark:text-brand-400 font-semibold truncate">
                  Apex Financial Labs
                </p>
                <p className="text-xs text-slate-500 dark:text-slate-400 truncate mt-1">
                  {messages[messages.length - 1]?.content || 'Checking workspace...'}
                </p>
              </div>
            </div>

            {/* Other Mock Threads */}
            <div className="p-4 flex items-start gap-3 hover:bg-slate-50 dark:hover:bg-slate-800/50 cursor-pointer transition-colors">
              <Avatar name="Elena Rostova" size="md" status="busy" />
              <div className="flex-1 min-w-0">
                <div className="flex items-center justify-between">
                  <p className="text-xs font-bold text-slate-900 dark:text-white truncate">Elena Rostova</p>
                  <span className="text-[10px] text-slate-400">Yesterday</span>
                </div>
                <p className="text-[11px] text-slate-400 truncate">HealthSync Innovations</p>
                <p className="text-xs text-slate-500 dark:text-slate-400 truncate mt-1">
                  Thanks for sending over the Expo layout demo!
                </p>
              </div>
            </div>

            <div className="p-4 flex items-start gap-3 hover:bg-slate-50 dark:hover:bg-slate-800/50 cursor-pointer transition-colors">
              <Avatar name="Lexis Automations" size="md" status="offline" />
              <div className="flex-1 min-w-0">
                <div className="flex items-center justify-between">
                  <p className="text-xs font-bold text-slate-900 dark:text-white truncate">Lexis Automations</p>
                  <span className="text-[10px] text-slate-400">Nov 01</span>
                </div>
                <p className="text-[11px] text-slate-400 truncate">Legal Tech</p>
                <p className="text-xs text-slate-500 dark:text-slate-400 truncate mt-1">
                  Contract proposal approved for review.
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* Right Active Chat Pane */}
        <div className="flex-1 flex flex-col min-w-0">
          {/* Chat Header */}
          <div className="h-16 px-6 border-b border-slate-200 dark:border-slate-800 flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Avatar name="Marcus Vance" size="sm" status="online" />
              <div>
                <div className="flex items-center gap-2">
                  <h3 className="text-sm font-extrabold text-slate-900 dark:text-white">Marcus Vance</h3>
                  <Badge variant="primary" size="sm">Client</Badge>
                </div>
                <p className="text-[11px] text-slate-400">Apex Financial Labs • Enterprise Contract #4029</p>
              </div>
            </div>

            <div className="flex items-center gap-2">
              <Link to="/meetings">
                <Button size="sm" variant="outline" className="gap-1.5 text-xs">
                  <Video className="w-4 h-4 text-accent-500" /> Start Meeting
                </Button>
              </Link>
              <Button size="icon" variant="ghost">
                <Phone className="w-4 h-4 text-slate-500" />
              </Button>
            </div>
          </div>

          {/* Messages Stream */}
          <div className="flex-1 overflow-y-auto p-6 space-y-4">
            <div className="text-center">
              <span className="px-3 py-1 rounded-full bg-slate-100 dark:bg-slate-800 text-[10px] font-semibold text-slate-500">
                Today, October 28
              </span>
            </div>

            {messages.map((msg) => (
              <div
                key={msg.id}
                className={`flex flex-col ${msg.isMe ? 'items-end' : 'items-start'}`}
              >
                <div className="flex items-center gap-1.5 mb-1 text-[11px] text-slate-400">
                  <span className="font-semibold text-slate-600 dark:text-slate-300">{msg.senderName}</span>
                  <span>• {msg.timestamp}</span>
                </div>

                <div
                  className={`max-w-md p-4 rounded-2xl text-xs sm:text-sm leading-relaxed shadow-soft-sm ${
                    msg.isMe
                      ? 'bg-brand-500 text-white rounded-br-xs'
                      : 'bg-slate-100 dark:bg-slate-800 text-slate-900 dark:text-white rounded-bl-xs'
                  }`}
                >
                  {msg.content}
                </div>

                {msg.isMe && (
                  <span className="flex items-center gap-1 text-[10px] text-slate-400 mt-1">
                    Delivered <CheckCheck className="w-3 h-3 text-brand-500" />
                  </span>
                )}
              </div>
            ))}
            <div ref={messagesEndRef} />
          </div>

          {/* Chat Input Bar */}
          <form onSubmit={handleSend} className="p-4 border-t border-slate-200 dark:border-slate-800 flex items-center gap-2">
            <button
              type="button"
              className="p-2.5 rounded-xl text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
            >
              <Paperclip className="w-4 h-4" />
            </button>
            <input
              type="text"
              placeholder="Type your message to Marcus (Apex Labs)..."
              value={inputText}
              onChange={(e) => setInputText(e.target.value)}
              className="flex-1 px-4 py-2.5 text-xs sm:text-sm rounded-xl border border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800 text-slate-900 dark:text-white placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-brand-500"
            />
            <Button type="submit" variant="primary" size="sm" className="gap-1.5 shrink-0 px-4 py-2.5">
              Send <Send className="w-3.5 h-3.5" />
            </Button>
          </form>
        </div>
      </div>
    </DashboardLayout>
  );
};
