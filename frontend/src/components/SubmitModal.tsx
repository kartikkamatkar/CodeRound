import React from 'react';
import { AlertTriangle, CheckCircle2, X } from 'lucide-react';

interface SubmitModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  isSubmitting: boolean;
  totalQuestions: number;
  savedCount: number;
}

export const SubmitModal: React.FC<SubmitModalProps> = ({
  isOpen,
  onClose,
  onConfirm,
  isSubmitting,
  totalQuestions,
  savedCount,
}) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm p-4">
      <div className="bg-slate-900 border border-slate-750 rounded-xl max-w-md w-full shadow-2xl p-6 text-slate-200 space-y-5 animate-in fade-in zoom-in-95 duration-150">
        <div className="flex items-start justify-between">
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 rounded-full bg-amber-500/10 border border-amber-500/30 flex items-center justify-center text-amber-400">
              <AlertTriangle className="w-5 h-5" />
            </div>
            <div>
              <h3 className="text-lg font-bold text-white">Submit Assessment?</h3>
              <p className="text-xs text-slate-400">This action cannot be undone</p>
            </div>
          </div>
          <button
            onClick={onClose}
            disabled={isSubmitting}
            className="text-slate-500 hover:text-slate-300 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="space-y-3 text-sm text-slate-300">
          <p>
            You are about to submit your round. All <strong className="text-white">{totalQuestions} questions</strong> will
            be evaluated against full test suites, including hidden evaluation test cases.
          </p>

          <div className="bg-slate-950 p-3.5 rounded-lg border border-slate-800 space-y-1 text-xs">
            <div className="flex justify-between">
              <span className="text-slate-400">Questions Saved:</span>
              <span className="font-semibold text-emerald-400 font-mono">
                {savedCount} / {totalQuestions}
              </span>
            </div>
            {savedCount < totalQuestions && (
              <p className="text-amber-400 text-[11px] pt-1">
                Notice: You have {totalQuestions - savedCount} unsaved question(s). Unattempted questions will receive 0 test cases passed.
              </p>
            )}
          </div>
        </div>

        <div className="flex items-center justify-end space-x-3 pt-2">
          <button
            onClick={onClose}
            disabled={isSubmitting}
            className="px-4 py-2 rounded-lg text-sm font-medium text-slate-400 hover:text-white hover:bg-slate-800 transition-colors"
          >
            Continue Coding
          </button>
          <button
            onClick={onConfirm}
            disabled={isSubmitting}
            className="flex items-center space-x-2 px-5 py-2 rounded-lg text-sm font-semibold bg-emerald-600 hover:bg-emerald-500 text-white shadow-lg transition-colors disabled:opacity-50"
          >
            <CheckCircle2 className="w-4 h-4" />
            <span>{isSubmitting ? 'Evaluating All Questions...' : 'Confirm Submission'}</span>
          </button>
        </div>
      </div>
    </div>
  );
};
