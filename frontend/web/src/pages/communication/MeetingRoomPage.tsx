import React, { useState } from 'react';
import {
  Video,
  VideoOff,
  Mic,
  MicOff,
  Monitor,
  PhoneOff,
  Sparkles,
  Copy,
} from 'lucide-react';
import { DashboardLayout } from '@/layouts/DashboardLayout';
import { Card } from '@/components/ui/Card';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';
import { Avatar } from '@/components/ui/Avatar';
import { useToast } from '@/context/ToastContext';

export const MeetingRoomPage: React.FC = () => {
  const toast = useToast();
  const [videoOn, setVideoOn] = useState(true);
  const [micOn, setMicOn] = useState(true);
  const [screenSharing, setScreenSharing] = useState(false);
  const [isGeneratingMinutes, setIsGeneratingMinutes] = useState(false);
  const [meetingMinutes, setMeetingMinutes] = useState<string | null>(null);

  const handleGenerateMinutes = () => {
    setIsGeneratingMinutes(true);
    setTimeout(() => {
      setIsGeneratingMinutes(false);
      setMeetingMinutes(`### 📋 AI Autonomous Meeting Minutes\n**Meeting:** Enterprise Fintech Architecture Sync\n**Participants:** Swaraj Bhosale (Freelancer), Marcus Vance (Apex Financial Labs)\n\n**Key Decisions:**\n1. Approved architecture design for multi-profile Spring Boot database configuration.\n2. Milestone 2 deliverable will include JWT token authentication & Flyway migrations.\n3. Escrow release of $1,500 will be triggered immediately upon Milestone 2 approval.\n\n**Action Items Assigned:**\n• Swaraj: Finalize JwtAuthenticationFilter & integration tests (Due: Nov 06)\n• Marcus: Provide Aiven Cloud MySQL staging credentials (Due: Nov 05)`);
      toast.success('Meeting Minutes Generated!', 'AI extracted key decisions and action items.');
    }, 1200);
  };

  const copyMeetingLink = () => {
    navigator.clipboard.writeText(window.location.href);
    toast.info('Meeting Link Copied', 'Share this link with stakeholders to join.');
  };

  return (
    <DashboardLayout role="freelancer" userName="Swaraj Bhosale">
      <div className="space-y-6">
        {/* Header */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-2 mb-1">
              <Badge variant="success" size="sm" className="gap-1 animate-pulse">
                <span className="w-2 h-2 rounded-full bg-emerald-500" /> Live Meeting
              </Badge>
              <span className="text-xs text-slate-400 font-mono">Room: apex-sync-4029</span>
            </div>
            <h1 className="text-2xl font-extrabold text-slate-900 dark:text-white">
              Enterprise Fintech Architecture Sync
            </h1>
          </div>

          <div className="flex items-center gap-2.5">
            <Button size="sm" variant="outline" onClick={copyMeetingLink} className="gap-1.5 text-xs">
              <Copy className="w-3.5 h-3.5" /> Copy Link
            </Button>
            <Button
              size="sm"
              variant="secondary"
              onClick={handleGenerateMinutes}
              isLoading={isGeneratingMinutes}
              className="gap-1.5 text-xs font-bold"
            >
              <Sparkles className="w-3.5 h-3.5" /> Generate AI Minutes
            </Button>
          </div>
        </div>

        {/* Meeting Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
          {/* Main Video Stage */}
          <div className="lg:col-span-8 space-y-4">
            <div className="aspect-video w-full rounded-3xl bg-slate-950 relative overflow-hidden shadow-2xl border border-slate-800 flex items-center justify-center">
              {/* Remote Participant Placeholder */}
              <div className="text-center text-white space-y-3">
                <Avatar name="Marcus Vance" size="xl" className="mx-auto" />
                <div>
                  <p className="font-extrabold text-base">Marcus Vance (Speaking)</p>
                  <p className="text-xs text-brand-300">Client • Apex Financial Labs</p>
                </div>
              </div>

              {/* Local Video Inset (PIP) */}
              <div className="absolute bottom-4 right-4 w-36 sm:w-48 aspect-video rounded-2xl bg-slate-800 border-2 border-brand-500 overflow-hidden shadow-lg flex items-center justify-center">
                {videoOn ? (
                  <div className="text-center text-white p-2">
                    <p className="text-[10px] font-bold">Swaraj Bhosale (You)</p>
                    <span className="text-[9px] text-emerald-400">1080p HD</span>
                  </div>
                ) : (
                  <div className="text-center text-slate-400">
                    <VideoOff className="w-5 h-5 mx-auto mb-1" />
                    <p className="text-[10px]">Camera Off</p>
                  </div>
                )}
              </div>
            </div>

            {/* Video Control Bar */}
            <div className="p-4 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 flex items-center justify-center gap-3">
              <Button
                variant={micOn ? 'outline' : 'danger'}
                onClick={() => setMicOn(!micOn)}
                className="gap-2"
              >
                {micOn ? <Mic className="w-4 h-4" /> : <MicOff className="w-4 h-4" />}
                {micOn ? 'Mute' : 'Unmuted'}
              </Button>

              <Button
                variant={videoOn ? 'outline' : 'danger'}
                onClick={() => setVideoOn(!videoOn)}
                className="gap-2"
              >
                {videoOn ? <Video className="w-4 h-4" /> : <VideoOff className="w-4 h-4" />}
                {videoOn ? 'Stop Camera' : 'Start Camera'}
              </Button>

              <Button
                variant={screenSharing ? 'secondary' : 'outline'}
                onClick={() => setScreenSharing(!screenSharing)}
                className="gap-2"
              >
                <Monitor className="w-4 h-4" />
                {screenSharing ? 'Sharing Screen' : 'Share Screen'}
              </Button>

              <Button
                variant="danger"
                onClick={() => toast.info('Meeting Ended', 'Call disconnected successfully.')}
                className="gap-2 bg-rose-600 hover:bg-rose-700"
              >
                <PhoneOff className="w-4 h-4" /> Leave Call
              </Button>
            </div>
          </div>

          {/* Right Live Transcript & Minutes Panel */}
          <div className="lg:col-span-4 space-y-4">
            <Card className="p-5 h-[480px] flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between pb-3 border-b border-slate-200 dark:border-slate-800">
                  <span className="font-extrabold text-xs uppercase tracking-wider text-slate-400">
                    Live Meeting Intel
                  </span>
                  <Badge variant="primary" size="sm">
                    <Sparkles className="w-3 h-3 mr-1" /> AI Active
                  </Badge>
                </div>

                <div className="mt-4 space-y-3 overflow-y-auto max-h-80 pr-1 text-xs">
                  {meetingMinutes ? (
                    <div className="p-4 rounded-xl bg-slate-900 text-slate-100 font-mono whitespace-pre-line text-[11px] leading-relaxed border border-slate-800">
                      {meetingMinutes}
                    </div>
                  ) : (
                    <>
                      <div className="p-3 rounded-xl bg-slate-50 dark:bg-slate-800/60 space-y-1">
                        <p className="font-bold text-slate-700 dark:text-slate-200">Marcus Vance</p>
                        <p className="text-slate-500 dark:text-slate-400">
                          "I reviewed the Phase 0 PR. Let's make sure the Flyway migration scripts are ready for Milestone 2."
                        </p>
                      </div>

                      <div className="p-3 rounded-xl bg-slate-50 dark:bg-slate-800/60 space-y-1">
                        <p className="font-bold text-brand-600 dark:text-brand-400">Swaraj Bhosale</p>
                        <p className="text-slate-500 dark:text-slate-400">
                          "Yes, I will submit the deliverable as soon as the test suite completes."
                        </p>
                      </div>

                      <p className="text-[11px] text-center text-slate-400 pt-4 italic">
                        Click 'Generate AI Minutes' above to extract decisions and assigned tasks instantly.
                      </p>
                    </>
                  )}
                </div>
              </div>

              <Button
                variant="outline"
                size="sm"
                onClick={handleGenerateMinutes}
                className="w-full gap-1.5 font-bold"
              >
                <Sparkles className="w-3.5 h-3.5 text-accent-500" /> Refresh AI Minutes
              </Button>
            </Card>
          </div>
        </div>
      </div>
    </DashboardLayout>
  );
};
