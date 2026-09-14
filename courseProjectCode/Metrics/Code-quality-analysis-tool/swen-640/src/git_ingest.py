from datetime import datetime, timezone
from typing import Optional, Iterable, Dict, Any
from src.git_ingest_helpers import *
from src.db_utils import *


# =============================
# DC2: Ecosystem Artifacts
# =============================

def _normalize_timestamp_to_utc(value: Any) -> datetime:
    """Coerce timestamps to timezone-aware UTC datetime."""
    if value is None or value == "":
        return None
    if isinstance(value, datetime):
        # If already datetime, ensure it's UTC-aware
        if value.tzinfo is None:
            return value.replace(tzinfo=timezone.utc)
        return value.astimezone(timezone.utc)

    # Parse string timestamp
    v = str(value)

    # Try common formats with explicit UTC 'Z' suffix
    for fmt in ("%Y-%m-%dT%H:%M:%S.%fZ", "%Y-%m-%dT%H:%M:%SZ"):
        try:
            dt = datetime.strptime(v, fmt)
            return dt.replace(tzinfo=timezone.utc)
        except ValueError:
            continue

    # Try formats without timezone (assume UTC)
    for fmt in ("%Y-%m-%dT%H:%M:%S.%f", "%Y-%m-%dT%H:%M:%S", "%Y-%m-%d %H:%M:%S", "%Y-%m-%d"):
        try:
            dt = datetime.strptime(v, fmt)
            return dt.replace(tzinfo=timezone.utc)
        except ValueError:
            continue

    # Fallback: fromisoformat
    try:
        dt = datetime.fromisoformat(v.replace('Z', '+00:00'))
        return dt.astimezone(timezone.utc) if dt.tzinfo else dt.replace(tzinfo=timezone.utc)
    except Exception:
        raise ValueError(f"Unrecognized timestamp: {value}")


# ---- Issues ----

def upsert_issue(provider: str, repo: str, issue: Dict[str, Any]) -> int:
    """Insert/update a single issue idempotently; return db id.

    Implementation guidance:
    - Build an INSERT ... ON CONFLICT (provider, repo, issue_number) DO UPDATE statement.
    - Extract: provider, repo, issue["number"], issue["title"], issue["author"],
      issue["state"], issue["created_at"], issue["closed_at"].
    - Use _normalize_timestamp_to_utc() for timestamp fields.
    - ON UPDATE: set title=EXCLUDED.title, state=EXCLUDED.state,
      author=COALESCE(EXCLUDED.author, issues.author),
      created_at=LEAST(issues.created_at, EXCLUDED.created_at),
      closed_at=COALESCE(EXCLUDED.closed_at, issues.closed_at).
    - Use RETURNING id to get the row id.
    - If no rows returned (shouldn't happen with RETURNING), fallback SELECT.
    """
    commitData = {"provider": provider, "repo": repo, "issue_number": issue["number"], "title": issue["title"],
                  "author": issue["author"], "state": issue["state"],
                  "created_at": _normalize_timestamp_to_utc(issue["created_at"]),
                  "closed_at": _normalize_timestamp_to_utc(issue.get("closed_at"))}
    returnData = exec_commit_returning("""INSERT INTO issues (provider, repo, issue_number, title, author, state,
                                                              created_at, closed_at)
                                          VALUES (%(provider)s, %(repo)s, %(issue_number)s, %(title)s, %(author)s,
                                                  %(state)s, %(created_at)s, %(closed_at)s) 
                                              ON CONFLICT (provider, repo, issue_number) 
                                              DO UPDATE SET title=EXCLUDED.title, state=EXCLUDED.state,
                                              author=COALESCE(EXCLUDED.author, issues.author),
                                              created_at=LEAST(issues.created_at, EXCLUDED.created_at),
                                              closed_at=COALESCE(EXCLUDED.closed_at, issues.closed_at)
                                              RETURNING id""", commitData)
    if not returnData:
        return exec_get_one("""SELECT id
                               FROM issues
                               WHERE provider = %s AND repo = %s AND issue_number = %s""",
                            (provider, repo, issue["number"]))[0]
    else:
        return returnData[0][0]  # weird formatting but it just works


def ingest_issues(provider: str, repo: str, issues: Iterable[Dict[str, Any]]) -> int:
    """Ingest multiple issues; return count processed.

    Implementation guidance:
    - Loop over issues iterable.
    - Call upsert_issue(provider, repo, issue) for each.
    - Return total count processed.
    """
    i = 0
    for issue in issues:
        upsert_issue(provider, repo, issue)
        i += 1
    return i


# ---- Pull Requests ----

def upsert_pull_request(provider: str, repo: str, pr: Dict[str, Any]) -> int:
    """Insert/update a single pull/merge request idempotently; return db id.

    Implementation guidance:
    - Similar to upsert_issue, but for pull_requests table.
    - Extract: provider, repo, pr["number"], pr["title"], pr["author"],
      pr["state"], pr["created_at"], pr["merged_at"], pr["closed_at"].
    - Use _normalize_timestamp_to_utc() for timestamp fields.
    - ON CONFLICT (provider, repo, pr_number) DO UPDATE with similar COALESCE/LEAST logic.
    - Use RETURNING id; fallback SELECT if needed.
    """
    commitData = {"provider": provider, "repo": repo, "pr_number": pr["number"], "title": pr["title"],
                  "author": pr["author"], "state": pr["state"],
                  "created_at": _normalize_timestamp_to_utc(pr.get("created_at")),
                  "merged_at": _normalize_timestamp_to_utc(pr.get("merged_at")),
                  "closed_at": _normalize_timestamp_to_utc(pr.get("closed_at"))}
    returnData = exec_commit_returning("""INSERT INTO pull_requests (provider, repo, pr_number, title, author, state,
                                                              created_at, merged_at, closed_at)
                                          VALUES (%(provider)s, %(repo)s, %(pr_number)s, %(title)s, %(author)s,
                                                  %(state)s, %(created_at)s, %(merged_at)s, %(closed_at)s) ON CONFLICT 
                                                  (provider, repo, pr_number) DO
                                        UPDATE SET title=EXCLUDED.title, state =EXCLUDED.state,
                                            author= COALESCE (EXCLUDED.author, pull_requests.author),
                                            created_at=LEAST(pull_requests.created_at, EXCLUDED.created_at),
                                            merged_at=COALESCE(EXCLUDED.merged_at, pull_requests.merged_at),
                                            closed_at= COALESCE (EXCLUDED.closed_at, pull_requests.closed_at)
                                            RETURNING id""", commitData)
    if not returnData:
        return exec_get_one("""SELECT id
                               FROM pull_requests
                               WHERE provider = %s
                                 AND repo = %s
                                 AND pr_number = %s""",
                            (provider, repo, pr["number"]))[0]
    else:
        return returnData[0][0]  # weird formatting but it just works


def ingest_pull_requests(provider: str, repo: str, prs: Iterable[Dict[str, Any]]) -> int:
    """Ingest multiple pull requests; return count processed.

    Implementation guidance:
    - Loop over prs iterable.
    - Call upsert_pull_request(provider, repo, pr) for each.
    - Return total count processed.
    """
    i = 0
    for pr in prs:
        updatedPr = pr
        if "merged_at" in updatedPr:
            updatedPr["state"] = "merged"
        upsert_pull_request(provider, repo, updatedPr)
        i += 1
    return i


# ---- CI Pipelines & Jobs ----

def upsert_ci_pipeline(provider: str, repo: str, pipe: Dict[str, Any]) -> int:
    """Insert/update a CI pipeline idempotently; return db id.

    Implementation guidance:
    - INSERT into ci_pipelines with fields: provider, repo, pipeline_id,
      status, created_at, updated_at, sha.
    - Use str(pipe["pipeline_id"]) to handle large integers.
    - Use _normalize_timestamp_to_utc() for timestamp fields.
    - ON CONFLICT (provider, repo, pipeline_id) DO UPDATE:
      status=EXCLUDED.status,
      created_at=LEAST(ci_pipelines.created_at, EXCLUDED.created_at),
      updated_at=COALESCE(EXCLUDED.updated_at, ci_pipelines.updated_at),
      sha=COALESCE(EXCLUDED.sha, ci_pipelines.sha).
    - Use RETURNING id; fallback SELECT if needed.
    """
    commitData = {"provider": provider, "repo": repo, "pipeline_id": str(pipe["pipeline_id"]), "status": pipe["status"],
                  "created_at": _normalize_timestamp_to_utc(pipe["created_at"]),
                  "updated_at": _normalize_timestamp_to_utc(pipe["updated_at"]), "sha": pipe["sha"]}
    returnData = exec_commit_returning("""INSERT INTO ci_pipelines (provider, repo, pipeline_id, status, created_at, 
                                                                     updated_at, sha)
                                          VALUES (%(provider)s, %(repo)s, %(pipeline_id)s, %(status)s, %(created_at)s,
                                                  %(updated_at)s, %(sha)s) ON CONFLICT 
                                                  (provider, repo, pipeline_id) DO
    UPDATE SET status=EXCLUDED.status,
      created_at=LEAST(ci_pipelines.created_at, EXCLUDED.created_at),
      updated_at=COALESCE(EXCLUDED.updated_at, ci_pipelines.updated_at),
      sha=COALESCE(EXCLUDED.sha, ci_pipelines.sha)
        RETURNING id""", commitData)
    if not returnData:
        return exec_get_one("""SELECT id
                               FROM ci_pipelines
                               WHERE provider = %s
                                 AND repo = %s
                                 AND pipeline_id = %s""",
                            (provider, repo, pipe["pipeline_id"]))[0]
    else:
        return returnData[0][0]  # weird formatting but it just works


def upsert_ci_job(provider: str, repo: str, job: Dict[str, Any]) -> int:
    """Insert/update a CI job idempotently; return db id.

    Implementation guidance:
    - INSERT into ci_jobs with fields: provider, repo, pipeline_id, job_id,
      name, status, started_at, finished_at, duration_seconds.
    - Convert job_id and pipeline_id to strings.
    - Use _normalize_timestamp_to_utc() for timestamp fields (if not None).
    - For duration_seconds: int(job.get("duration_seconds", 0)) if not None else None.
    - ON CONFLICT (provider, repo, job_id) DO UPDATE with COALESCE logic.
    - Use RETURNING id; fallback SELECT if needed.
    """
    commitData = {"provider": provider, "repo": repo, "pipeline_id": str(job["pipeline_id"]), "job_id": job["job_id"],
                  "name": job["name"], "status": job.get("status"),
                  "started_at": _normalize_timestamp_to_utc(job["started_at"]),
                  "finished_at": _normalize_timestamp_to_utc(job.get("finished_at")), "duration_seconds": job["duration_seconds"]}
    returnData = exec_commit_returning("""INSERT INTO ci_jobs (provider, repo, pipeline_id, job_id, name,
                                                               status, started_at, finished_at, duration_seconds)
                                          VALUES (%(provider)s, %(repo)s, %(pipeline_id)s, %(job_id)s, %(name)s,
                                                  %(status)s, %(started_at)s, %(finished_at)s, %(duration_seconds)s) ON CONFLICT 
                                                      (provider, repo, job_id) DO
    UPDATE SET started_at=LEAST(ci_jobs.started_at, EXCLUDED.started_at),
        status= COALESCE(EXCLUDED.status, ci_jobs.status),
        finished_at= COALESCE (EXCLUDED.finished_at, ci_jobs.finished_at),
        duration_seconds= COALESCE (EXCLUDED.duration_seconds, ci_jobs.duration_seconds)
        RETURNING id""", commitData)
    if not returnData:
        return exec_get_one("""SELECT id
                               FROM ci_jobs
                               WHERE provider = %s
                                 AND repo = %s
                                 AND job_id = %s""",
                            (provider, repo, job["job_id"]))[0]
    else:
        return returnData[0][0]  # weird formatting but it just works


def ingest_ci(provider: str, repo: str, pipelines: Iterable[Dict[str, Any]],
              jobs_by_pipeline: Optional[Dict[str, Iterable[Dict[str, Any]]]] = None) -> int:
    """Ingest pipelines and their jobs. Returns number of pipelines processed.

    Implementation guidance:
    - Loop over pipelines iterable.
    - For each pipeline, call upsert_ci_pipeline(provider, repo, pipe).
    - If jobs_by_pipeline is provided:
      - Get pipeline_id as str(pipe["pipeline_id"]).
      - For each job in jobs_by_pipeline.get(pipeline_id, []):
        - Ensure job has "pipeline_id" set
        - Call upsert_ci_job(provider, repo, job).
    - Return total count of pipelines processed.
    """
    i = 0
    for pipe in pipelines:
        upsert_ci_pipeline(provider, repo, pipe)
        if jobs_by_pipeline:
            for job in jobs_by_pipeline.get(str(pipe["pipeline_id"]), []):
                if "pipeline_id" in job:
                    upsert_ci_job(provider, repo, job)
        i += 1
    return i
