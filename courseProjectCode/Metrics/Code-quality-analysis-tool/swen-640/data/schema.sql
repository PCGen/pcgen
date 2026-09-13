-- Drop existing tables to ensure a clean schema before creating objects. There are many other ways to do this, but I'm making it explicit here for your convenience
DROP TABLE IF EXISTS run_log CASCADE;
DROP TABLE IF EXISTS commit_parents CASCADE;
DROP TABLE IF EXISTS commit_files CASCADE;
DROP TABLE IF EXISTS commit_stats CASCADE;
DROP TABLE IF EXISTS commits CASCADE;
DROP TABLE IF EXISTS issues CASCADE;
DROP TABLE IF EXISTS pull_requests CASCADE;
DROP TABLE IF EXISTS ci_pipelines CASCADE;
DROP TABLE IF EXISTS ci_jobs CASCADE;
DROP TABLE IF EXISTS code_identifiers CASCADE;
DROP TABLE IF EXISTS code_comments CASCADE;
DROP TABLE IF EXISTS file_data CASCADE;

CREATE TABLE IF NOT EXISTS commits (
    id SERIAL PRIMARY KEY,
    commit_hash TEXT NOT NULL,
    author_name TEXT NOT NULL,
    message TEXT NOT NULL,
    commit_ts TIMESTAMPTZ NOT NULL
);
-- Ensure no duplicate commits by hash
CREATE UNIQUE INDEX IF NOT EXISTS idx_commits_hash ON commits(commit_hash);

-- Per-commit aggregate stats
CREATE TABLE IF NOT EXISTS commit_stats (
    commit_id INTEGER PRIMARY KEY REFERENCES commits(id) ON DELETE CASCADE,
    files_changed INTEGER NOT NULL,
    insertions INTEGER NOT NULL,
    deletions INTEGER NOT NULL
);

-- Per-file change details for each commit
CREATE TABLE IF NOT EXISTS commit_files (
    id SERIAL PRIMARY KEY,
    commit_id INTEGER NOT NULL REFERENCES commits(id) ON DELETE CASCADE,
    file_path TEXT NOT NULL,
    change_type TEXT NOT NULL,         -- 'A','M','D','R' (added, modified, deleted, renamed) best-effort from GitPython stats
    additions INTEGER DEFAULT 0,
    deletions INTEGER DEFAULT 0
);
-- Ensure idempotent inserts per (commit_id, file_path)
CREATE UNIQUE INDEX IF NOT EXISTS uq_commit_files_commit_path ON commit_files(commit_id, file_path);

-- Parent relationships (to support DAG traversals/merges)
CREATE TABLE IF NOT EXISTS commit_parents (
    commit_id INTEGER NOT NULL REFERENCES commits(id) ON DELETE CASCADE,
    parent_hash TEXT NOT NULL,
    PRIMARY KEY (commit_id, parent_hash)
);

CREATE TABLE IF NOT EXISTS run_log (
    id SERIAL PRIMARY KEY,
    started_at TIMESTAMP NOT NULL DEFAULT NOW(),
    repo_path TEXT NOT NULL,
    head_hash TEXT NOT NULL,
    commit_count INTEGER NOT NULL
);

-- Issues (DC2)
CREATE TABLE IF NOT EXISTS issues (
    id SERIAL PRIMARY KEY,
    provider TEXT NOT NULL,                 -- e.g., 'github', 'gitlab'
    repo TEXT NOT NULL,                     -- 'owner/repo' or 'group/project'
    issue_number INTEGER NOT NULL,
    title TEXT NOT NULL,
    author TEXT,
    state TEXT NOT NULL,                    -- 'open', 'closed', etc.
    created_at TIMESTAMPTZ NOT NULL,
    closed_at TIMESTAMPTZ
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_issue_identity ON issues(provider, repo, issue_number);

-- Pull / Merge Requests (DC2)
CREATE TABLE IF NOT EXISTS pull_requests (
    id SERIAL PRIMARY KEY,
    provider TEXT NOT NULL,
    repo TEXT NOT NULL,
    pr_number INTEGER NOT NULL,
    title TEXT NOT NULL,
    author TEXT,
    state TEXT NOT NULL,                    -- 'open', 'closed', 'merged' (normalized, see notes in readme)
    created_at TIMESTAMPTZ NOT NULL,
    merged_at TIMESTAMPTZ,
    closed_at TIMESTAMPTZ
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_pr_identity ON pull_requests(provider, repo, pr_number);

-- CI Pipelines (DC2)
CREATE TABLE IF NOT EXISTS ci_pipelines (
    id SERIAL PRIMARY KEY,
    provider TEXT NOT NULL,
    repo TEXT NOT NULL,
    pipeline_id TEXT NOT NULL,              -- provider-visible id (string to handle big ints)
    status TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ,
    sha TEXT                                -- commit hash the pipeline ran for (if known)
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_pipeline_identity ON ci_pipelines(provider, repo, pipeline_id);

-- CI Jobs (DC2)
CREATE TABLE IF NOT EXISTS ci_jobs (
    id SERIAL PRIMARY KEY,
    provider TEXT NOT NULL,
    repo TEXT NOT NULL,
    pipeline_id TEXT NOT NULL,
    job_id TEXT NOT NULL,
    name TEXT,
    status TEXT,
    started_at TIMESTAMPTZ,
    finished_at TIMESTAMPTZ,
    duration_seconds INTEGER
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_job_identity ON ci_jobs(provider, repo, job_id);

CREATE TABLE IF NOT EXISTS code_identifiers (
    id          SERIAL PRIMARY KEY,
    file_path   TEXT NOT NULL,
    name        TEXT NOT NULL,          -- raw identifier (e.g. "getUserData")
    kind        TEXT NOT NULL,          -- function | class | variable | parameter
    tokens      TEXT
);

CREATE TABLE IF NOT EXISTS code_comments (
    id           SERIAL PRIMARY KEY,
    file_path    TEXT NOT NULL,
    comment_text TEXT NOT NULL          -- cleaned comment text (markers stripped)
);

CREATE TABLE IF NOT EXISTS file_data (
    id              SERIAL PRIMARY KEY,
    file_path       TEXT NOT NULL,
    file_line_count INTEGER NOT NULL, -- number of lines in a file
    function_count  INTEGER NOT NULL -- number of functions in a file
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_file_data_path ON file_data(file_path);
