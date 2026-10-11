CREATE TABLE questions (
                           id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                               title VARCHAR(255) NOT NULL,
                               description Text NOT NULL,
                              difficulty VARCHAR(20) NOT NULL
                                 CHECK ( difficulty IN ('Easy', 'Medium','Hard' )),
                              created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE test_cases (
                            id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            question_id BIGINT NOT NULL
                                REFERENCES questions(id) ON DELETE CASCADE,
                            input TEXT NOT NULL,
                            expected_output TEXT NOT NULL,
                            is_hidden BOOLEAN NOT NULL DEFAULT FALSE,
                            created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE assessment_sessions (
                                     id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     session_code VARCHAR(20) NOT NULL UNIQUE,
                                     status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS'
                                         CHECK (status IN ('IN_PROGRESS', 'SUBMITTED', 'EXPIRED')),
                                     started_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     expires_at TIMESTAMPTZ NOT NULL,
                                     submitted_at TIMESTAMPTZ
);
CREATE TABLE assessment_questions (
                                      id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

                                      assessment_session_id BIGINT NOT NULL
                                          REFERENCES assessment_sessions(id) ON DELETE CASCADE,

                                      question_id BIGINT NOT NULL
                                          REFERENCES questions(id),

                                      question_order INTEGER NOT NULL
                                          CHECK (question_order BETWEEN 1 AND 4),

                                      created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                      UNIQUE (assessment_session_id, question_id),
                                      UNIQUE (assessment_session_id, question_order)
);
CREATE TABLE question_attempts (
                                   id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

                                   assessment_session_id BIGINT NOT NULL
                                       REFERENCES assessment_sessions(id) ON DELETE CASCADE,

                                   question_id BIGINT NOT NULL
                                       REFERENCES questions(id),

                                   language VARCHAR(30) NOT NULL,

                                   source_code TEXT NOT NULL,

                                   updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                   UNIQUE (assessment_session_id, question_id)
);
CREATE TABLE submissions (
                             id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

                             assessment_session_id BIGINT NOT NULL UNIQUE
                                 REFERENCES assessment_sessions(id) ON DELETE CASCADE,

                             status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                                 CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED')),

                             submitted_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             completed_at TIMESTAMPTZ
);CREATE TABLE submission_results (
                                      id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

                                      submission_id BIGINT NOT NULL
                                          REFERENCES submissions(id) ON DELETE CASCADE,

                                      question_id BIGINT NOT NULL
                                          REFERENCES questions(id),

                                      language VARCHAR(30) NOT NULL,
                                      source_code TEXT NOT NULL,

                                      total_test_cases INTEGER NOT NULL
                                          CHECK (total_test_cases >= 0),

                                      passed_test_cases INTEGER NOT NULL
                                          CHECK (
                                              passed_test_cases >= 0
                                                  AND passed_test_cases <= total_test_cases
                                              ),

                                      status VARCHAR(30) NOT NULL
                                          CHECK (
                                              status IN (
                                                         'PENDING',
                                                         'ACCEPTED',
                                                         'WRONG_ANSWER',
                                                         'COMPILATION_ERROR',
                                                         'RUNTIME_ERROR',
                                                         'TIME_LIMIT_EXCEEDED',
                                                         'INTERNAL_ERROR'
                                                  )
                                              ),

                                      execution_time_ms INTEGER
                                          CHECK (execution_time_ms >= 0),

                                      memory_used_kb INTEGER
                                          CHECK (memory_used_kb >= 0),

                                      created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                      UNIQUE (submission_id, question_id)
  );