import React, { useState, useCallback } from 'react';
import { AssessmentSession, CodeRunResult, QuestionDetail, SubmissionResponse } from './types/assessment';
import { assessmentApi } from './api/assessmentApi';
import { Header } from './components/Header';
import { QuestionNav } from './components/QuestionNav';
import { QuestionDetail as QuestionDetailPanel } from './components/QuestionDetail';
import { CodeEditor } from './components/CodeEditor';
import { TestResults } from './components/TestResults';
import { SubmitModal } from './components/SubmitModal';
import { ResultsDashboard } from './components/ResultsDashboard';
import { StartScreen } from './components/StartScreen';

export const App: React.FC = () => {
  const [session, setSession] = useState<AssessmentSession | null>(null);
  const [currentIdx, setCurrentIdx] = useState<number>(0);
  const [currentQuestion, setCurrentQuestion] = useState<QuestionDetail | null>(null);
  const [editorCode, setEditorCode] = useState<string>('');
  const [editorLanguage, setEditorLanguage] = useState<string>('JAVA');
  const [lastSavedAt, setLastSavedAt] = useState<string | null>(null);
  const [savedQuestionIds, setSavedQuestionIds] = useState<Set<number>>(new Set());

  const [runResult, setRunResult] = useState<CodeRunResult | null>(null);
  const [submission, setSubmission] = useState<SubmissionResponse | null>(null);

  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isQuestionLoading, setIsQuestionLoading] = useState<boolean>(false);
  const [isSaving, setIsSaving] = useState<boolean>(false);
  const [isRunning, setIsRunning] = useState<boolean>(false);
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  // Load question when session or currentIdx changes
  const loadQuestion = useCallback(async (sessionCode: string, questionId: number) => {
    setIsQuestionLoading(true);
    setRunResult(null);
    try {
      const q = await assessmentApi.getAssignedQuestion(sessionCode, questionId);
      setCurrentQuestion(q);
      setEditorLanguage(q.savedLanguage || 'JAVA');
      setEditorCode(q.savedSourceCode || '');
      if (q.savedSourceCode) {
        setSavedQuestionIds((prev) => new Set(prev).add(questionId));
      }
    } catch (err: any) {
      setError(err.message || 'Failed to load question details');
    } finally {
      setIsQuestionLoading(false);
    }
  }, []);

  const handleStartNew = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const newSession = await assessmentApi.startAssessment();
      setSession(newSession);
      setCurrentIdx(0);
      if (newSession.questions.length > 0) {
        await loadQuestion(newSession.sessionCode, newSession.questions[0].id);
      }
    } catch (err: any) {
      setError(err.message || 'Failed to start new assessment');
    } finally {
      setIsLoading(false);
    }
  };

  const handleResume = async (code: string) => {
    setIsLoading(true);
    setError(null);
    try {
      const resumed = await assessmentApi.getAssessment(code);
      if (resumed.status === 'SUBMITTED') {
        const sub = await assessmentApi.getSubmissionBySessionCode?.(code) || null;
        if (sub) {
          setSubmission(sub);
          setSession(resumed);
          return;
        }
      }
      setSession(resumed);
      setCurrentIdx(0);
      if (resumed.questions.length > 0) {
        await loadQuestion(resumed.sessionCode, resumed.questions[0].id);
      }
    } catch (err: any) {
      setError(err.message || 'Failed to resume session');
    } finally {
      setIsLoading(false);
    }
  };

  const handleSaveCode = async () => {
    if (!session || !currentQuestion) return;
    setIsSaving(true);
    try {
      await assessmentApi.saveCode(
        session.sessionCode,
        currentQuestion.id,
        editorLanguage,
        editorCode
      );
      setSavedQuestionIds((prev) => new Set(prev).add(currentQuestion.id));
      setLastSavedAt(new Date().toLocaleTimeString());
    } catch (err: any) {
      alert(`Save failed: ${err.message}`);
    } finally {
      setIsSaving(false);
    }
  };

  const handleLanguageChange = async (newLang: string) => {
    setEditorLanguage(newLang);
    if (!session || !currentQuestion) return;
    try {
      const codeResp = await assessmentApi.getCode(session.sessionCode, currentQuestion.id, newLang);
      setEditorCode(codeResp.sourceCode);
    } catch {
      // Ignore
    }
  };

  const handleSelectQuestion = async (newIndex: number) => {
    if (!session || newIndex === currentIdx) return;
    // Auto-save current question before switching
    if (currentQuestion && editorCode.trim()) {
      try {
        await assessmentApi.saveCode(
          session.sessionCode,
          currentQuestion.id,
          editorLanguage,
          editorCode
        );
        setSavedQuestionIds((prev) => new Set(prev).add(currentQuestion.id));
      } catch {
        // Silently continue
      }
    }

    setCurrentIdx(newIndex);
    const nextQ = session.questions[newIndex];
    if (nextQ) {
      await loadQuestion(session.sessionCode, nextQ.id);
    }
  };

  const handleRunCode = async () => {
    if (!session || !currentQuestion) return;
    setIsRunning(true);
    setRunResult(null);
    try {
      const result = await assessmentApi.runCode(
        session.sessionCode,
        currentQuestion.id,
        editorLanguage,
        editorCode
      );
      setRunResult(result);
      setSavedQuestionIds((prev) => new Set(prev).add(currentQuestion.id));
      setLastSavedAt(new Date().toLocaleTimeString());
    } catch (err: any) {
      alert(`Execution failed: ${err.message}`);
    } finally {
      setIsRunning(false);
    }
  };

  const handleConfirmSubmit = async () => {
    if (!session) return;
    setIsSubmitting(true);
    try {
      // Auto-save active question first
      if (currentQuestion && editorCode.trim()) {
        await assessmentApi.saveCode(
          session.sessionCode,
          currentQuestion.id,
          editorLanguage,
          editorCode
        );
      }
      const sub = await assessmentApi.submitAssessment(session.sessionCode);
      setSubmission(sub);
      setIsModalOpen(false);
    } catch (err: any) {
      alert(`Submission failed: ${err.message}`);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleRestart = () => {
    setSession(null);
    setSubmission(null);
    setCurrentQuestion(null);
    setRunResult(null);
    setSavedQuestionIds(new Set());
    setError(null);
  };

  // View: Final Results Dashboard
  if (submission) {
    return (
      <div className="min-h-screen bg-slate-950">
        <ResultsDashboard submission={submission} onRestart={handleRestart} />
      </div>
    );
  }

  // View: Start / Welcome Screen
  if (!session) {
    return (
      <StartScreen
        onStartNew={handleStartNew}
        onResume={handleResume}
        isLoading={isLoading}
        error={error}
      />
    );
  }

  // View: Assessment in Progress
  return (
    <div className="h-screen flex flex-col bg-slate-950 overflow-hidden">
      {/* Top Header */}
      <Header
        session={session}
        onSubmitClick={() => setIsModalOpen(true)}
        isSubmitting={isSubmitting}
      />

      {/* Question Navigation Tabs */}
      <QuestionNav
        questions={session.questions}
        currentIndex={currentIdx}
        onSelect={handleSelectQuestion}
        savedQuestionIds={savedQuestionIds}
      />

      {/* Main Split Layout: Left = Description, Right = Editor + Results */}
      <div className="flex-1 flex overflow-hidden">
        {/* Left Panel: Problem Statement & Sample Cases */}
        <div className="w-1/2 h-full overflow-y-auto border-r border-slate-800 bg-slate-900/40">
          <QuestionDetailPanel question={currentQuestion} loading={isQuestionLoading} />
        </div>

        {/* Right Panel: Editor & Execution Results */}
        <div className="w-1/2 h-full flex flex-col bg-slate-900">
          <div className="flex-1 overflow-hidden">
            <CodeEditor
              language={editorLanguage}
              sourceCode={editorCode}
              onLanguageChange={handleLanguageChange}
              onCodeChange={setEditorCode}
              onSave={handleSaveCode}
              onRun={handleRunCode}
              isSaving={isSaving}
              isRunning={isRunning}
              lastSavedAt={lastSavedAt}
            />
          </div>

          {/* Test Results Output Drawer */}
          <TestResults results={runResult} isRunning={isRunning} />
        </div>
      </div>

      {/* Submit Confirmation Modal */}
      <SubmitModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onConfirm={handleConfirmSubmit}
        isSubmitting={isSubmitting}
        totalQuestions={session.questions.length}
        savedCount={savedQuestionIds.size}
      />
    </div>
  );
};

export default App;
