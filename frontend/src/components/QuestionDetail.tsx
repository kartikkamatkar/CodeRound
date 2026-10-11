import React from 'react';
import { QuestionDetail as QuestionDetailType } from '../types/assessment';
import { BookOpen, Terminal } from 'lucide-react';

interface QuestionDetailProps {
  question: QuestionDetailType | null;
  loading: boolean;
}

export const QuestionDetail: React.FC<QuestionDetailProps> = ({ question, loading }) => {
  if (loading) {
    return (
      <div className="p-8 flex items-center justify-center text-slate-500 animate-pulse">
        Loading question details...
      </div>
    );
  }

  if (!question) {
    return <div className="p-8 text-slate-400">No question selected</div>;
  }

  const getDifficultyClass = (diff: string) => {
    switch (diff) {
      case 'Easy':
        return 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30';
      case 'Medium':
        return 'bg-amber-500/10 text-amber-400 border-amber-500/30';
      case 'Hard':
        return 'bg-rose-500/10 text-rose-400 border-rose-500/30';
      default:
        return 'bg-slate-700 text-slate-300 border-slate-600';
    }
  };

  return (
    <div className="h-full overflow-y-auto p-6 space-y-6 text-slate-200">
      {/* Title & Difficulty */}
      <div className="border-b border-slate-800 pb-4">
        <div className="flex items-center justify-between">
          <h2 className="text-xl font-bold text-white tracking-tight">
            {question.questionOrder}. {question.title}
          </h2>
          <span
            className={`text-xs px-2.5 py-1 rounded-full border font-semibold tracking-wide ${getDifficultyClass(
              question.difficulty
            )}`}
          >
            {question.difficulty}
          </span>
        </div>
      </div>

      {/* Description */}
      <div className="space-y-3">
        <div className="flex items-center space-x-2 text-slate-400 text-sm font-semibold uppercase tracking-wider">
          <BookOpen className="w-4 h-4 text-emerald-400" />
          <span>Description</span>
        </div>
        <div className="text-sm leading-relaxed whitespace-pre-wrap text-slate-300 font-sans bg-slate-900/40 p-4 rounded-lg border border-slate-800">
          {question.description}
        </div>
      </div>

      {/* Sample Test Cases */}
      {question.sampleTestCases && question.sampleTestCases.length > 0 && (
        <div className="space-y-4">
          <div className="flex items-center space-x-2 text-slate-400 text-sm font-semibold uppercase tracking-wider">
            <Terminal className="w-4 h-4 text-emerald-400" />
            <span>Sample Test Cases</span>
          </div>

          <div className="space-y-3">
            {question.sampleTestCases.map((sample, idx) => (
              <div
                key={sample.id || idx}
                className="bg-slate-900 border border-slate-800 rounded-lg p-4 space-y-3 shadow-inner"
              >
                <div className="text-xs font-semibold text-slate-400 uppercase tracking-wider">
                  Example {idx + 1}
                </div>

                <div>
                  <span className="text-xs text-slate-400 font-mono block mb-1">Input:</span>
                  <pre className="bg-slate-950 p-2.5 rounded border border-slate-800/80 font-mono text-xs text-slate-200 overflow-x-auto">
                    {sample.input}
                  </pre>
                </div>

                <div>
                  <span className="text-xs text-slate-400 font-mono block mb-1">Expected Output:</span>
                  <pre className="bg-slate-950 p-2.5 rounded border border-slate-800/80 font-mono text-xs text-emerald-400 overflow-x-auto">
                    {sample.expectedOutput}
                  </pre>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};
