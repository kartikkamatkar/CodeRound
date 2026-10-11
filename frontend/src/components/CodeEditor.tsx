import React from 'react';
import Editor from '@monaco-editor/react';
import { Play, Save, Check, RefreshCw } from 'lucide-react';

interface CodeEditorProps {
  language: string;
  sourceCode: string;
  onLanguageChange: (lang: string) => void;
  onCodeChange: (code: string) => void;
  onSave: () => void;
  onRun: () => void;
  isSaving: boolean;
  isRunning: boolean;
  lastSavedAt: string | null;
}

export const CodeEditor: React.FC<CodeEditorProps> = ({
  language,
  sourceCode,
  onLanguageChange,
  onCodeChange,
  onSave,
  onRun,
  isSaving,
  isRunning,
  lastSavedAt,
}) => {
  const getMonacoLanguage = (lang: string) => {
    switch (lang.toUpperCase()) {
      case 'PYTHON':
        return 'python';
      case 'CPP':
      case 'C++':
        return 'cpp';
      case 'C':
        return 'c';
      case 'JAVASCRIPT':
      case 'JS':
        return 'javascript';
      default:
        return 'java';
    }
  };

  return (
    <div className="h-full flex flex-col bg-slate-900 border-l border-slate-800">
      {/* Editor Top Bar */}
      <div className="bg-slate-850 px-4 py-2 border-b border-slate-800 flex items-center justify-between">
        <div className="flex items-center space-x-3">
          <label htmlFor="language-select" className="text-xs font-semibold text-slate-400 uppercase tracking-wider">
            Language:
          </label>
          <select
            id="language-select"
            value={language}
            onChange={(e) => onLanguageChange(e.target.value)}
            className="bg-slate-900 text-slate-200 border border-slate-700 rounded px-2.5 py-1 text-sm font-medium focus:outline-none focus:border-emerald-500"
          >
            <option value="JAVA">Java (OpenJDK)</option>
            <option value="PYTHON">Python 3</option>
            <option value="CPP">C++ (GCC)</option>
            <option value="C">C (GCC)</option>
            <option value="JAVASCRIPT">JavaScript (Node.js)</option>
          </select>
        </div>

        {/* Save Status */}
        <div className="flex items-center space-x-2 text-xs text-slate-400">
          {lastSavedAt && (
            <span className="flex items-center space-x-1 text-slate-400">
              <Check className="w-3.5 h-3.5 text-emerald-400" />
              <span>Saved</span>
            </span>
          )}
        </div>
      </div>

      {/* Monaco Editor Container */}
      <div className="flex-1 min-h-[300px]">
        <Editor
          height="100%"
          language={getMonacoLanguage(language)}
          value={sourceCode}
          theme="vs-dark"
          onChange={(val) => onCodeChange(val || '')}
          options={{
            fontSize: 14,
            minimap: { enabled: false },
            scrollBeyondLastLine: false,
            automaticLayout: true,
            tabSize: 4,
            lineNumbers: 'on',
            fontFamily: 'Consolas, "Fira Code", monospace',
          }}
        />
      </div>

      {/* Editor Action Bar */}
      <div className="bg-slate-850 px-4 py-3 border-t border-slate-800 flex items-center justify-between">
        <button
          onClick={onSave}
          disabled={isSaving}
          className="flex items-center space-x-1.5 px-3 py-1.5 rounded text-sm font-medium bg-slate-800 hover:bg-slate-750 text-slate-200 border border-slate-700 transition-colors disabled:opacity-50"
        >
          {isSaving ? (
            <RefreshCw className="w-4 h-4 animate-spin text-slate-400" />
          ) : (
            <Save className="w-4 h-4 text-slate-400" />
          )}
          <span>{isSaving ? 'Saving...' : 'Save Code'}</span>
        </button>

        <button
          onClick={onRun}
          disabled={isRunning}
          className="flex items-center space-x-2 px-5 py-1.5 rounded text-sm font-semibold bg-emerald-600 hover:bg-emerald-500 text-white shadow transition-colors disabled:bg-slate-800 disabled:text-slate-500"
        >
          {isRunning ? (
            <RefreshCw className="w-4 h-4 animate-spin" />
          ) : (
            <Play className="w-4 h-4 fill-white" />
          )}
          <span>{isRunning ? 'Running Sample Cases...' : 'Run Code'}</span>
        </button>
      </div>
    </div>
  );
};
