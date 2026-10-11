import React from 'react';
import { SubmissionResponse } from '../types/assessment';
import { CheckCircle2, XCircle, Award, RotateCcw, Clock, HardDrive, ShieldCheck } from 'lucide-react';

interface ResultsDashboardProps {
  submission: SubmissionResponse;
  onRestart: () => void;
}

export const ResultsDashboard: React.FC<ResultsDashboardProps> = ({ submission, onRestart }) => {
  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'ACCEPTED':
        return 'text-emerald-400 bg-emerald-500/10 border-emerald-500/30';
      case 'WRONG_ANSWER':
        return 'text-rose-400 bg-rose-500/10 border-rose-500/30';
      case 'COMPILATION_ERROR':
        return 'text-amber-400 bg-amber-500/10 border-amber-500/30';
      case 'RUNTIME_ERROR':
        return 'text-purple-400 bg-purple-500/10 border-purple-500/30';
      case 'TIME_LIMIT_EXCEEDED':
        return 'text-orange-400 bg-orange-500/10 border-orange-500/30';
      default:
        return 'text-slate-400 bg-slate-800 border-slate-700';
    }
  };

  const getDifficultyBadge = (diff: string) => {
    switch (diff) {
      case 'Easy':
        return 'text-emerald-400 border-emerald-800/80 bg-emerald-950/40';
      case 'Medium':
        return 'text-amber-400 border-amber-800/80 bg-amber-950/40';
      case 'Hard':
        return 'text-rose-400 border-rose-800/80 bg-rose-950/40';
      default:
        return 'text-slate-400 border-slate-700 bg-slate-800';
    }
  };

  const scorePercentage = Math.round((submission.fullySolvedQuestions / submission.totalQuestions) * 100);

  return (
    <div className="max-w-4xl mx-auto p-8 space-y-8 animate-in fade-in duration-200">
      {/* Header Banner */}
      <div className="bg-slate-850 border border-slate-750 rounded-2xl p-8 shadow-xl relative overflow-hidden">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-6 relative z-10">
          <div className="space-y-2">
            <div className="flex items-center space-x-2 text-emerald-400 text-sm font-semibold tracking-wide uppercase">
              <ShieldCheck className="w-4 h-4" />
              <span>Assessment Completed</span>
            </div>
            <h1 className="text-3xl font-extrabold text-white tracking-tight">Round Evaluation Results</h1>
            <p className="text-sm text-slate-400">
              Session Code: <code className="font-mono text-emerald-400 font-semibold">{submission.sessionCode}</code>
            </p>
          </div>

          <div className="flex items-center bg-slate-900 border border-slate-800 rounded-xl px-6 py-4 space-x-4 shadow-inner">
            <div className="w-12 h-12 rounded-full bg-emerald-500/20 border border-emerald-500/30 flex items-center justify-center text-emerald-400">
              <Award className="w-6 h-6" />
            </div>
            <div>
              <div className="text-2xl font-black text-white font-mono">
                {submission.fullySolvedQuestions} / {submission.totalQuestions}
              </div>
              <div className="text-xs text-slate-400 font-medium">Fully Solved ({scorePercentage}%)</div>
            </div>
          </div>
        </div>
      </div>

      {/* Question Results Breakdown */}
      <div className="space-y-4">
        <h2 className="text-lg font-bold text-slate-200 tracking-tight flex items-center space-x-2">
          <span>Question-by-Question Breakdown</span>
        </h2>

        <div className="space-y-3">
          {submission.results.map((res, idx) => {
            const isAccepted = res.status === 'ACCEPTED';

            return (
              <div
                key={res.id || idx}
                className="bg-slate-850 border border-slate-800 rounded-xl p-5 hover:border-slate-700 transition-all space-y-4"
              >
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                  <div className="flex items-center space-x-3">
                    <span className="w-7 h-7 rounded-lg bg-slate-800 border border-slate-700 flex items-center justify-center text-sm font-bold text-slate-300">
                      {idx + 1}
                    </span>
                    <h3 className="text-base font-bold text-white">{res.questionTitle}</h3>
                    <span className={`text-[11px] px-2 py-0.5 rounded border font-mono ${getDifficultyBadge(res.difficulty)}`}>
                      {res.difficulty}
                    </span>
                  </div>

                  <div className="flex items-center space-x-3">
                    <span className="text-xs font-mono text-slate-400">{res.language}</span>
                    <span
                      className={`flex items-center space-x-1.5 px-3 py-1 rounded-full border text-xs font-bold tracking-wide ${getStatusBadge(
                        res.status
                      )}`}
                    >
                      {isAccepted ? <CheckCircle2 className="w-3.5 h-3.5" /> : <XCircle className="w-3.5 h-3.5" />}
                      <span>{res.status.replace(/_/g, ' ')}</span>
                    </span>
                  </div>
                </div>

                {/* Metrics */}
                <div className="grid grid-cols-3 gap-2 bg-slate-900/60 p-3 rounded-lg border border-slate-800/80 text-xs text-slate-300">
                  <div>
                    <span className="text-slate-400 block text-[11px]">Test Cases Passed:</span>
                    <span className="font-mono font-bold text-white">
                      {res.passedTestCases} / {res.totalTestCases}
                    </span>
                  </div>

                  <div>
                    <span className="text-slate-400 block text-[11px]">Execution Time:</span>
                    <span className="font-mono flex items-center space-x-1 text-slate-200">
                      <Clock className="w-3 h-3 text-slate-500" />
                      <span>{res.executionTimeMs} ms</span>
                    </span>
                  </div>

                  <div>
                    <span className="text-slate-400 block text-[11px]">Memory Used:</span>
                    <span className="font-mono flex items-center space-x-1 text-slate-200">
                      <HardDrive className="w-3 h-3 text-slate-500" />
                      <span>{res.memoryUsedKb} KB</span>
                    </span>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Restart / Home Action */}
      <div className="flex justify-center pt-4">
        <button
          onClick={onRestart}
          className="flex items-center space-x-2 px-6 py-2.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-white font-semibold border border-slate-700 transition-all shadow"
        >
          <RotateCcw className="w-4 h-4" />
          <span>Start Another Assessment</span>
        </button>
      </div>
    </div>
  );
};
