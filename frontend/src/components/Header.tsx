import React, { useEffect, useState } from 'react';
import { Timer, CheckCircle, Copy, Check } from 'lucide-react';
import { AssessmentSession } from '../types/assessment';

interface HeaderProps {
  session: AssessmentSession;
  onSubmitClick: () => void;
  isSubmitting: boolean;
}

export const Header: React.FC<HeaderProps> = ({ session, onSubmitClick, isSubmitting }) => {
  const [secondsLeft, setSecondsLeft] = useState<number>(session.remainingSeconds || 3600);
  const [copied, setCopied] = useState(false);

  useEffect(() => {
    setSecondsLeft(session.remainingSeconds);
  }, [session.remainingSeconds]);

  useEffect(() => {
    if (secondsLeft <= 0) return;
    const interval = setInterval(() => {
      setSecondsLeft((prev) => (prev > 0 ? prev - 1 : 0));
    }, 1000);
    return () => clearInterval(interval);
  }, [secondsLeft]);

  const formatTime = (totalSec: number) => {
    const mins = Math.floor(totalSec / 60);
    const secs = totalSec % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const copySessionCode = () => {
    navigator.clipboard.writeText(session.sessionCode);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const isLowTime = secondsLeft < 300; // < 5 mins
  const isCriticalTime = secondsLeft < 60; // < 1 min

  return (
    <header className="bg-slate-800 border-b border-slate-700 px-6 py-3 flex items-center justify-between shadow-sm">
      <div className="flex items-center space-x-6">
        <div className="flex items-center space-x-2">
          <div className="w-8 h-8 rounded-lg bg-emerald-500 flex items-center justify-center font-bold text-slate-950 text-lg shadow">
            CR
          </div>
          <span className="text-xl font-bold tracking-tight text-white">
            Code<span className="text-emerald-400">Round</span>
          </span>
        </div>

        <div className="flex items-center bg-slate-900 border border-slate-700 rounded-md px-3 py-1 space-x-2">
          <span className="text-xs text-slate-400 uppercase tracking-wider font-semibold">Session:</span>
          <code className="text-sm font-mono font-semibold text-emerald-400">{session.sessionCode}</code>
          <button
            onClick={copySessionCode}
            title="Copy Session Code"
            className="text-slate-400 hover:text-white transition-colors"
          >
            {copied ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
          </button>
        </div>
      </div>

      <div className="flex items-center space-x-4">
        {/* Timer */}
        <div
          className={`flex items-center space-x-2 px-3.5 py-1.5 rounded-md font-mono text-sm font-semibold border ${
            isCriticalTime
              ? 'bg-rose-950/60 border-rose-600 text-rose-300 animate-pulse'
              : isLowTime
              ? 'bg-amber-950/50 border-amber-500 text-amber-300'
              : 'bg-slate-900 border-slate-700 text-slate-200'
          }`}
        >
          <Timer className="w-4 h-4" />
          <span>{formatTime(secondsLeft)}</span>
        </div>

        {/* Final Submit Button */}
        <button
          onClick={onSubmitClick}
          disabled={isSubmitting || secondsLeft <= 0}
          className="flex items-center space-x-2 bg-emerald-600 hover:bg-emerald-500 disabled:bg-slate-700 disabled:text-slate-500 text-white font-medium px-4 py-1.5 rounded-md transition-colors shadow"
        >
          <CheckCircle className="w-4 h-4" />
          <span>{isSubmitting ? 'Submitting...' : 'Submit Assessment'}</span>
        </button>
      </div>
    </header>
  );
};
