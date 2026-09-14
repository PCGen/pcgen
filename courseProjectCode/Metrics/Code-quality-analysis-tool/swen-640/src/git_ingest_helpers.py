# ---------------------------------------------
# Helper Functions -- some are provided, others are stubbed; you can implement them or decide on your own way of providing similar functionality
# ---------------------------------------------
from typing import Optional, Iterable, Dict, Any
import requests


def fetch_json(url: str, headers: Optional[Dict[str, str]] = None,
               params: Optional[Dict[str, Any]] = None) -> Dict[str, Any]:
    """Lightweight GET JSON wrapper."""
    resp = requests.get(url, headers=headers or {}, params=params or {}, timeout=30)
    resp.raise_for_status()
    return resp.json()


def collect_github_issues(owner_repo: str, state: str = "all",
                          token: Optional[str] = None,
                          per_page: int = 100, max_pages: int = 1) -> Iterable[Dict[str, Any]]:
    """Generator yielding normalized issue dicts from GitHub REST v3."""
    owner, repo = owner_repo.split("/", 1)
    headers = {"Accept": "application/vnd.github+json"}
    if token:
        headers["Authorization"] = f"Bearer {token}"
    url = f"https://api.github.com/repos/{owner}/{repo}/issues"
    page = 1
    while page <= max_pages:
        data = fetch_json(url, headers=headers, params={"state": state, "per_page": per_page, "page": page})
        if not data:
            break
        for it in data:
            if "pull_request" in it:
                # skip PRs here; use collect_github_pulls for PR details
                continue
            yield {
                "number": it["number"],
                "title": it.get("title", ""),
                "author": (it.get("user") or {}).get("login"),
                "state": it.get("state", "open"),
                "created_at": it.get("created_at"),
                "closed_at": it.get("closed_at"),
            }
        page += 1


def collect_github_pulls(owner_repo: str, state: str = "all",
                         token: Optional[str] = None,
                         per_page: int = 100, max_pages: int = 1) -> Iterable[Dict[str, Any]]:
    """Generator yielding normalized pull request dicts from GitHub REST v3.

    Implementation guidance:
    - URL: https://api.github.com/repos/{owner}/{repo}/pulls
    - Headers: Accept: application/vnd.github+json, Authorization if token provided
    - Query params: state={state}, per_page={per_page}, page={page}
    - Normalize state: if pr.get("merged_at") then state="merged", else pr.get("state", "open")
    - Yield dict with: number, title, author (from user.login), state,
      created_at, merged_at, closed_at
    """
    owner, repo = owner_repo.split("/", 1)
    headers = {"Accept": "application/vnd.github+json"}
    if token:
        headers["Authorization"] = f"Bearer {token}"
    url = f"https://api.github.com/repos/{owner}/{repo}/pulls"
    page = 1
    while page <= max_pages:
        data = fetch_json(url, headers=headers, params={"state": state, "per_page": per_page, "page": page})
        if not data:
            break
        for pr in data:
            yield {
                "number": pr["number"],
                "title": pr.get("title", ""),
                "author": (pr.get("user") or {}).get("login"),
                "state": "merged" if pr.get("merged_at") else pr.get("state", "open"),
                "created_at": pr.get("created_at"),
                "merged_at": pr.get("merged_at"),
                "closed_at": pr.get("closed_at"),
            }
        page += 1


def collect_github_actions_runs(owner_repo: str, token: Optional[str] = None,
                                per_page: int = 100, max_pages: int = 1) -> Iterable[Dict[str, Any]]:
    """Yield pipelines from GitHub Actions workflow runs.

    Implementation guidance:
    - URL: https://api.github.com/repos/{owner}/{repo}/actions/runs
    - Headers: Accept: application/vnd.github+json, Authorization if token provided
    - Response is a dict with "workflow_runs" key containing array
    - For each run r, yield dict with:
      pipeline_id: r["id"]
      status: r.get("conclusion") or r.get("status") or "unknown"
      created_at: r.get("created_at")
      updated_at: r.get("updated_at")
      sha: r.get("head_sha")
    """
    owner, repo = owner_repo.split("/", 1)
    headers = {"Accept": "application/vnd.github+json"}
    if token:
        headers["Authorization"] = f"Bearer {token}"
    url = f"https://api.github.com/repos/{owner}/{repo}/actions/runs"
    page = 1
    while page <= max_pages:
        data = fetch_json(url, headers=headers, params={})
        if not data:
            break
        for r in data["workflow_runs"]:
            yield {
                "pipeline_id": r["id"],
                "status": r.get("conclusion") or r.get("status") or "unknown",
                "created_at": r.get("created_at"),
                "updated_at": r.get("updated_at"),
                "sha": r.get("head_sha"),
            }
        page += 1


def collect_github_actions_jobs(owner_repo: str, run_id: str,
                                token: Optional[str] = None,
                                per_page: int = 100, max_pages: int = 1) -> Iterable[Dict[str, Any]]:
    """Yield jobs for a specific GitHub Actions run.

    Implementation guidance:
    - URL: https://api.github.com/repos/{owner}/{repo}/actions/runs/{run_id}/jobs
    - Headers: Accept: application/vnd.github+json, Authorization if token provided
    - Response is a dict with "jobs" key containing array
    - For each job j, yield dict with:
      pipeline_id: str(run_id)
      job_id: j["id"]
      name: j.get("name")
      status: j.get("conclusion") or j.get("status")
      started_at: j.get("started_at")
      finished_at: j.get("completed_at")
      duration_seconds: j.get("duration_ms", 0) // 1000 if j.get("duration_ms") else None
    """
    owner, repo = owner_repo.split("/", 1)
    headers = {"Accept": "application/vnd.github+json"}
    if token:
        headers["Authorization"] = f"Bearer {token}"
    url = f"https://api.github.com/repos/{owner}/{repo}/actions/runs/{run_id}/jobs"
    page = 1
    while page <= max_pages:
        data = fetch_json(url, headers=headers, params={})
        if not data:
            break
        for j in data:
            yield {
                "pipeline_id": str(run_id),
                "job_id": j["id"],
                "name": j.get("name"),
                "status": j.get("conclusion") or j.get("status"),
                "started_at": j.get("started_at"),
                "finished_at": j.get("completed_at"),
                "duration_seconds": j.get("duration_ms", 0) // 1000 if j.get("duration_ms") else None,
            }
        page += 1