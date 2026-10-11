import React, { useState } from 'react';
import { CodeRunResult } from '../types/assessment';
import { CheckCircle2, XCircle, AlertCircle, Clock, Cpu } from 'lucide-react';

interface TestResultsProps {
  results: CodeRunResult | null;
  isRunning: boolean;
}

export const TestResults: React.FC<TestResultsProps> = ({ results, isRunning }) => {
  const [selectedCaseIdx, setSelectedCaseIdx] = useState(0);

  if (isRunning) {
    return (
      <div className="h-48 border-t border-slate-800 bg-slate-900/90 flex flex-col items-center justify-center space-y-2 text-slate-400">
        <div className="w-6 h-6 border-2 border-emerald-500 border-t-transparent rounded-full animate-spin" />
        <span className="text-sm">Executing against sample test cases...</span>
      </div>
    );
  }

  if (!results) {
    return (
      <div className="h-28 border-t border-slate-800 bg-slate-900/60 flex items-center justify-center text-slate-500 text-xs uppercase tracking-wider font-mono">
        Click "Run Code" to test against sample test cases
      </div>
    );
  }

  const getStatusColor = (status: string) => {
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

  const selectedCase = results.testCaseResults[selectedCaseIdx];

  return (
    <div className="border-t border-slate-800 bg-slate-900 text-slate-200 text-sm flex flex-col max-h-72">
      {/* Result Summary Bar */}
      <div className="px-4 py-2.5 bg-slate-850 border-b border-slate-800 flex items-center justify-between">
        <div className="flex items-center space-x-3">
          <span
            className={`px-2.5 py-0.5 rounded border text-xs font-bold tracking-wide ${getStatusColor(
              results.status
            )}`}
          >
            {results.status.replace(/_/g, ' ')}
          </span>
          <span className="text-xs text-slate-300 font-medium">
            Passed {results.passedTestCases} / {results.totalTestCases} Test Cases
          </span>
        </div>

        <div className="flex items-center space-x-4 text-xs text-slate-400 font-mono">
          <div className="flex items-center space-x-1">
            <Clock className="w-3.5 h-3.5 text-slate-500" />
            <span>{results.totalExecutionTime}s</span>
          </div>
          {results.peakMemory > 0 && (
            <div className="flex items-center space-x-1">
              <Cpu className="w-3.5 h-3.5 text-slate-500" />
              <span>{results.peakMemory} KB</span>
            </div>
          )}
        </div>
      </div>

      {/* Case Tabs & Detail */}
      <div className="flex-1 overflow-y-auto p-4 space-y-3">
        {results.testCaseResults.length > 0 && (
          <div className="flex items-center space-x-2 border-b border-slate-800 pb-2">
            {results.testCaseResults.map((tc, idx) => (
              <button
                key={idx}
                onClick={() => setSelectedCaseIdx(idx)}
                className={`flex items-center space-x-1.5 px-3 py-1 rounded text-xs font-semibold transition-colors ${
                  selectedCaseIdx === idx
                    ? 'bg-slate-800 text-white border border-slate-700'
                    : 'text-slate-400 hover:text-slate-200'
                }`}
              >
                {tc.passed ? (
                  <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
                ) : (
                  <XCircle className="w-3.5 h-3.5 text-rose-400" />
                )}
                <span>Case {idx + 1}</span>
              </button>
            ))}
          </div>
        )}

        {selectedCase && (
          <div className="space-y-3 text-xs font-mono">
            {selectedCase.compileOutput && (
              <div className="space-y-1">
                <span className="text-amber-400 font-semibold flex items-center space-x-1">
                  <AlertCircle className="w-3.5 h-3.5" />
                  <span>Compilation Output:</span>
                </span>
                <pre className="p-2.5 rounded bg-amber-950/20 border border-amber-900/40 text-amber-200 whitespace-pre-wrap overflow-x-auto">
                  {selectedCase.compileOutput}
                </pre>
              </div>
            )}

            {selectedCase.error && (
              <div className="space-y-1">
                <span className="text-rose-400 font-semibold flex items-center space-x-1">
                  <AlertCircle className="w-3.5 h-3.5" />
                  <span>Runtime Error:</span>
                </span>
                <pre className="p-2.5 rounded bg-rose-950/20 border border-rose-900/40 text-rose-300 whitespace-pre-wrap overflow-x-auto">
                  {selectedCase.error}
                </pre>
              </div>
            )}

            <div className="grid grid-cols-2 gap-3">
              <div>
                <span className="text-slate-400 block mb-1">Input:</span>
                <pre className="p-2.5 rounded bg-slate-950 border border-slate-800 text-slate-200 overflow-x-auto">
                  {selectedCase.input || '<empty>'}
                </pre>
              </div>

              <div>
                <span className="text-slate-400 block mb-1">Expected Output:</span>
                <pre className="p-2.5 rounded bg-slate-950 border border-slate-800 text-emerald-400 overflow-x-auto">
                  {selectedCase.expectedOutput}
                </pre>
              </div>
            </div>

            <div>
              <span className="text-slate-400 block mb-1">Actual Output:</span>
              <pre
                className={`p-2.5 rounded bg-slate-950 border ${
                  selectedCase.passed ? 'border-emerald-800 text-emerald-300' : 'border-rose-900 text-rose-300'
                } overflow-x-auto`}
              >
                {selectedCase.actualOutput || '<no output>'}
              </pre>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
