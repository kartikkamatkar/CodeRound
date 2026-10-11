import {
  AssessmentSession,
  CodeRunResult,
  QuestionDetail,
  SubmissionResponse,
} from '../types/assessment';

const API_BASE = '/api/v1';

async function handleResponse<T>(res: Response): Promise<T> {
  if (!res.ok) {
    let errorMsg = `HTTP Error ${res.status}`;
    try {
      const errorJson = await res.json();
      if (errorJson && errorJson.error) {
        errorMsg = errorJson.error;
      }
    } catch {
      // ignore json parse error
    }
    throw new Error(errorMsg);
  }
  return res.json() as Promise<T>;
}

export const assessmentApi = {
  async startAssessment(questionIds?: number[]): Promise<AssessmentSession> {
    const res = await fetch(`${API_BASE}/assessments`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ questionIds }),
    });
    return handleResponse<AssessmentSession>(res);
  },

  async getAssessment(sessionCode: string): Promise<AssessmentSession> {
    const res = await fetch(`${API_BASE}/assessments/${encodeURIComponent(sessionCode)}`);
    return handleResponse<AssessmentSession>(res);
  },

  async getAssignedQuestion(sessionCode: string, questionId: number): Promise<QuestionDetail> {
    const res = await fetch(`${API_BASE}/assessments/${encodeURIComponent(sessionCode)}/questions/${questionId}`);
    return handleResponse<QuestionDetail>(res);
  },

  async saveCode(sessionCode: string, questionId: number, language: string, sourceCode: string) {
    const res = await fetch(`${API_BASE}/assessments/${encodeURIComponent(sessionCode)}/questions/${questionId}/code`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ language, sourceCode }),
    });
    return handleResponse<{ questionId: number; language: string; sourceCode: string }>(res);
  },

  async getCode(sessionCode: string, questionId: number, language: string = 'JAVA') {
    const res = await fetch(`${API_BASE}/assessments/${encodeURIComponent(sessionCode)}/questions/${questionId}/code?language=${encodeURIComponent(language)}`);
    return handleResponse<{ questionId: number; language: string; sourceCode: string }>(res);
  },

  async runCode(sessionCode: string, questionId: number, language: string, sourceCode: string): Promise<CodeRunResult> {
    const res = await fetch(`${API_BASE}/assessments/${encodeURIComponent(sessionCode)}/questions/${questionId}/run`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ language, sourceCode }),
    });
    return handleResponse<CodeRunResult>(res);
  },

  async submitAssessment(sessionCode: string): Promise<SubmissionResponse> {
    const res = await fetch(`${API_BASE}/assessments/${encodeURIComponent(sessionCode)}/submit`, {
      method: 'POST',
    });
    return handleResponse<SubmissionResponse>(res);
  },

  async getSubmission(submissionId: number): Promise<SubmissionResponse> {
    const res = await fetch(`${API_BASE}/submissions/${submissionId}`);
    return handleResponse<SubmissionResponse>(res);
  },

  async getSubmissionBySessionCode(sessionCode: string): Promise<SubmissionResponse> {
    const res = await fetch(`${API_BASE}/assessments/${encodeURIComponent(sessionCode)}/submission`);
    return handleResponse<SubmissionResponse>(res);
  },
};
