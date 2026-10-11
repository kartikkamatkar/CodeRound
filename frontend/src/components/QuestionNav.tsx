import React from 'react';
import { QuestionSummary } from '../types/assessment';

interface QuestionNavProps {
  questions: QuestionSummary[];
  currentIndex: number;
  onSelect: (index: number) => void;
  savedQuestionIds: Set<number>;
}

export const QuestionNav: React.FC<QuestionNavProps> = ({
  questions,
  currentIndex,
  onSelect,
  savedQuestionIds,
}) => {
  const getDifficultyBadge = (diff: string) => {
    switch (diff) {
      case 'Easy':
        return 'bg-emerald-950/80 text-emerald-400 border-emerald-800';
      case 'Medium':
        return 'bg-amber-950/80 text-amber-400 border-amber-800';
      case 'Hard':
        return 'bg-rose-950/80 text-rose-400 border-rose-800';
      default:
        return 'bg-slate-800 text-slate-400 border-slate-700';
    }
  };

  return (
    <nav className="bg-slate-850 border-b border-slate-750 px-6 py-2 flex items-center space-x-2 bg-slate-900/60 overflow-x-auto">
      {questions.map((q, idx) => {
        const isActive = idx === currentIndex;
        const isSaved = savedQuestionIds.has(q.id);

        return (
          <button
            key={q.id}
            onClick={() => onSelect(idx)}
            className={`flex items-center space-x-2.5 px-4 py-2 rounded-lg text-sm font-medium transition-all ${
              isActive
                ? 'bg-slate-800 text-white shadow border border-slate-600'
                : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/50 border border-transparent'
            }`}
          >
            <span className="font-semibold">Question {idx + 1}</span>
            <span className={`text-[11px] px-2 py-0.5 rounded border font-mono ${getDifficultyBadge(q.difficulty)}`}>
              {q.difficulty}
            </span>
            {isSaved && (
              <span className="w-2 h-2 rounded-full bg-emerald-400 shadow-sm" title="Code saved" />
            )}
          </button>
        );
      })}
    </nav>
  );
};
