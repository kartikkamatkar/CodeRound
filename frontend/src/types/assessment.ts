export interface TestCaseSample {
  id: number;
  input: string;
  expectedOutput: string;
}

export interface QuestionSummary {
  id: number;
  title: string;
  difficulty: 'Easy' | 'Medium' | 'Hard';
  questionOrder: number;
}

export interface QuestionDetail {
  id: number;
  title: string;
  description: string;
  difficulty: 'Easy' | 'Medium' | 'Hard';
  questionOrder: number;
  sampleTestCases: TestCaseSample[];
  savedLanguage: string;
  savedSourceCode: string;
}

export interface AssessmentSession {
  sessionCode: string;
  status: 'IN_PROGRESS' | 'SUBMITTED' | 'EXPIRED';
  startedAt: string;
  expiresAt: string;
  remainingSeconds: number;
  questions: QuestionSummary[];
}

export interface TestCaseResult {
  testCaseId?: number;
  input: string;
  expectedOutput: string;
  actualOutput: string;
  passed: boolean;
  status: string;
  compileOutput?: string;
  error?: string;
  executionTime?: number;
  memoryUsed?: number;
}

export interface CodeRunResult {
  status: string;
  totalTestCases: number;
  passedTestCases: number;
  totalExecutionTime: number;
  peakMemory: number;
  testCaseResults: TestCaseResult[];
}

export interface SubmissionResult {
  id: number;
  questionId: number;
  questionTitle: string;
  difficulty: string;
  language: string;
  totalTestCases: number;
  passedTestCases: number;
  status: string;
  executionTimeMs: number;
  memoryUsedKb: number;
}

export interface SubmissionResponse {
  id: number;
  sessionCode: string;
  status: string;
  submittedAt: string;
  completedAt?: string;
  totalQuestions: number;
  fullySolvedQuestions: number;
  results: SubmissionResult[];
}
