import React, { useState } from 'react';
import { Play, LogIn, Clock, Code2, ShieldAlert, CheckCircle2 } from 'lucide-react';

interface StartScreenProps {
  onStartNew: () => void;
  onResume: (code: string) => void;
  isLoading: boolean;
  error: string | null;
}

export const StartScreen: React.FC<StartScreenProps> = ({
  onStartNew,
  onResume,
  isLoading,
  error,
}) => {
  const [sessionCodeInput, setSessionCodeInput] = useState('');

  const handleResume = (e: React.FormEvent) => {
    e.preventDefault();
    if (sessionCodeInput.trim()) {
      onResume(sessionCodeInput.trim());
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 flex flex-col justify-center items-center p-6 text-slate-100">
      <div className="max-w-xl w-full space-y-8 animate-in fade-in duration-200">
        {/* Brand Header */}
        <div className="text-center space-y-3">
          <div className="inline-flex w-16 h-16 rounded-2xl bg-emerald-500 items-center justify-center font-black text-slate-950 text-3xl shadow-xl shadow-emerald-500/20 mb-2">
            CR
          </div>
          <h1 className="text-4xl font-extrabold tracking-tight text-white">
            Code<span className="text-emerald-400">Round</span>
          </h1>
          <p className="text-slate-400 text-sm max-w-sm mx-auto">
            Full-Stack DSA Coding Assessment Platform with real-time test case execution.
          </p>
        </div>

        {/* Error Alert */}
        {error && (
          <div className="bg-rose-950/60 border border-rose-800 text-rose-300 p-4 rounded-xl text-sm flex items-center space-x-2">
            <ShieldAlert className="w-5 h-5 flex-shrink-0" />
            <span>{error}</span>
          </div>
        )}

        {/* Action Card */}
        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-8 shadow-2xl space-y-6">
          <div className="space-y-4">
            <button
              onClick={onStartNew}
              disabled={isLoading}
              className="w-full flex items-center justify-center space-x-2.5 py-3.5 px-6 rounded-xl font-bold text-base bg-emerald-600 hover:bg-emerald-500 text-white shadow-lg shadow-emerald-600/25 transition-all disabled:opacity-50"
            >
              <Play className="w-5 h-5 fill-white" />
              <span>{isLoading ? 'Creating Session...' : 'Start New Assessment Round'}</span>
            </button>

            <div className="relative flex py-2 items-center">
              <div className="flex-grow border-t border-slate-800"></div>
              <span className="flex-shrink mx-4 text-xs font-semibold text-slate-500 uppercase tracking-wider">
                or resume session
              </span>
              <div className="flex-grow border-t border-slate-800"></div>
            </div>

            <form onSubmit={handleResume} className="flex gap-2">
              <input
                type="text"
                placeholder="Enter Session Code (e.g. CR-A1B2C3D4)"
                value={sessionCodeInput}
                onChange={(e) => setSessionCodeInput(e.target.value.toUpperCase())}
                disabled={isLoading}
                className="flex-1 bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-sm font-mono text-white placeholder-slate-500 focus:outline-none focus:border-emerald-500 transition-colors uppercase"
              />
              <button
                type="submit"
                disabled={isLoading || !sessionCodeInput.trim()}
                className="flex items-center space-x-2 px-5 py-2.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-white text-sm font-semibold border border-slate-700 transition-colors disabled:opacity-50"
              >
                <LogIn className="w-4 h-4" />
                <span>Resume</span>
              </button>
            </form>
          </div>

          {/* Assessment Guidelines */}
          <div className="border-t border-slate-800/80 pt-6 space-y-3 text-xs text-slate-400">
            <div className="font-semibold text-slate-300 uppercase tracking-wider text-[11px]">
              Round Guidelines:
            </div>
            <ul className="space-y-2">
              <li className="flex items-center space-x-2">
                <Code2 className="w-4 h-4 text-emerald-400" />
                <span>Exactly 4 distinct Data Structure & Algorithm problems assigned.</span>
              </li>
              <li className="flex items-center space-x-2">
                <Clock className="w-4 h-4 text-emerald-400" />
                <span>60 minutes countdown timer enforced on server.</span>
              </li>
              <li className="flex items-center space-x-2">
                <CheckCircle2 className="w-4 h-4 text-emerald-400" />
                <span>Run code against sample test cases; final submission tests hidden suites.</span>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
};
