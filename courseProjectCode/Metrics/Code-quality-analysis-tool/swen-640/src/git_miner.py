from datetime import datetime
from typing import Optional, Iterable, Tuple
from git import Repo
from . import db_utils
from .db_utils import *


def upsert_commit(repo_commit) -> int:
    """Insert commit if missing and return its DB primary-key id.

  Behavior:
  - Build a dict with `hash`, `author_name`, `message`, `timestamp`.
  - INSERT ... ON CONFLICT DO NOTHING RETURNING id; if no row is
    returned, SELECT id FROM commits WHERE commit_hash=%(hash)s.
  - Return the integer id.
  """
    commitData = {"hash": repo_commit.hexsha, "author_name": repo_commit.author.name, "message": repo_commit.message,
                  "commit_ts": datetime.fromtimestamp(repo_commit.committed_date)}
    returnData = exec_commit_returning("""INSERT INTO commits (commit_hash, author_name, message, commit_ts)
                                          VALUES (%(hash)s, %(author_name)s, %(message)s,
                                                  %(commit_ts)s) ON CONFLICT DO NOTHING RETURNING id""", commitData)
    if not returnData:
        return exec_get_one("""SELECT id
                               FROM commits
                               WHERE commit_hash = %s""", (repo_commit.hexsha,))[0]
    else:
        return returnData[0][0]  # weird formatting but it just works


def insert_parents(commit_id: int, parents: Iterable[str]) -> None:
    """Record parent links for `commit_id`.

    Insert rows into `commit_parents(commit_id, parent_hash)` using
    `ON CONFLICT DO NOTHING` to keep the operation idempotent.
    """
    for parent in parents:
        commitData = {"commit_id": commit_id, "parent_hash": parent}

        exec_commit("""INSERT INTO commit_parents (commit_id, parent_hash)
                       VALUES (%(commit_id)s, %(parent_hash)s) ON CONFLICT DO NOTHING""", commitData)


def insert_stats(commit_id: int, repo_commit) -> None:
    """Insert aggregate stats for a commit.

    Use `repo_commit.stats.total` (or default zeros) and write into
    `commit_stats(commit_id, files_changed, insertions, deletions)` with
    `ON CONFLICT (commit_id) DO NOTHING`.
    """
    commitData = {"commit_id": commit_id, "files_changed": repo_commit.stats.total["files"],
                  "insertions": repo_commit.stats.total["insertions"],
                  "deletions": repo_commit.stats.total["deletions"]}
    exec_commit("""INSERT INTO commit_stats(commit_id, files_changed, insertions, deletions)
                   VALUES (%(commit_id)s, %(files_changed)s, %(insertions)s,
                           %(deletions)s) ON CONFLICT (commit_id) DO NOTHING""", commitData)


def insert_files(commit_id: int, repo_commit) -> None:
    """Insert per-file change rows for a commit.

    General structure (implement this pattern):

    - Obtain per-file stats dict:
      files = getattr(repo_commit.stats, 'files', {}) or {}

    - Attempt to infer change types via a diff to the parent commit:
      change_types = {}
      try:
        parent = repo_commit.parents[0] if repo_commit.parents else None
        if parent is not None:
          for diff in parent.diff(repo_commit):
            # set change_types[diff.b_path or diff.a_path]
            # to diff.change_type.upper() (A/M/D/R/T)
            # Note: `diff.b_path` is the path in the new commit (useful for
            # additions/renames); `diff.a_path` is the path in the parent (useful
            # for deletions). Preferring `b_path` records the post-change path,
            # while falling back to `a_path` preserves the old path when the file
            # was removed.
        else:
          # root commit: mark all paths in `files` as 'A'
          for path in files.keys():
            change_types[path] = 'A'
      except Exception:
        # If diffing fails, fall back to a conservative default
        # (e.g. treat unknown files as 'M') and continue.
        pass

    - For each (path, data) in `files.items()`:
      # compute additions = int(data.get('insertions', 0))
      # compute deletions = int(data.get('deletions', 0))
      # choose change_type = change_types.get(path, 'M')
      # INSERT into `commit_files(commit_id, file_path, change_type, additions, deletions)`
      # using `ON CONFLICT DO NOTHING` keyed by (commit_id, file_path).

    The goal is to be best-effort and idempotent; tests will assert
    that rows exist with non-negative additions/deletions and reasonable
    change_type values.
    """
    files = getattr(repo_commit.stats, 'files', {}) or {}
    change_types = {}
    try:
        parent = repo_commit.parents[0] if repo_commit.parents else None
        if parent is not None:
            for diff in parent.diff(repo_commit):
                change_types[diff.b_path] = diff.change_type.upper()
        else:
            for path in files.keys():
                change_types[path] = 'A'
    except Exception:
        pass

    for (path, data) in files.items():
        additions = int(data.get('insertions', 0))
        deletions = int(data.get('deletions', 0))
        change_type = change_types.get(path, 'M')
        commitData = {"commit_id": commit_id, "file_path": path, "change_type": change_type, "additions": additions,
                      "deletions": deletions}
        exec_commit("""INSERT INTO commit_files(commit_id, file_path, change_type, additions, deletions)
                       VALUES (%(commit_id)s, %(file_path)s, %(change_type)s, %(additions)s,
                               %(deletions)s) ON CONFLICT (commit_id, file_path) DO NOTHING""", commitData)


def insert_run_log(repo_path: str, head_hash: str, commit_count: int) -> None:
    """Append a provenance row to `run_log`.

    Write `repo_path`, `head_hash`, and `commit_count`. `started_at` can
    be a DB default of `now()`.

    Motivation: when mining software repositories for research it's
    important to record provenance for reproducibility, auditing, and
    debugging. Recording the `repo_path`, the `head_hash` observed after a
    run, and the `commit_count` lets future analysts tie database rows to a
    specific repository state (commit SHA) and run. This supports:
    - reproducing results by checking out the recorded `head_hash`;
    - detecting incomplete runs or partial replays by comparing counts;
    - auditing which repository snapshot produced the stored facts.
    """
    commitData = {"repo_path": repo_path, "head_hash": head_hash, "commit_count": commit_count}
    exec_commit("""INSERT INTO run_log(repo_path, head_hash, commit_count)
                   VALUES (%(repo_path)s, %(head_hash)s, %(commit_count)s)""", commitData)


def validate_invariants() -> Tuple[int, int, int]:
    """Return (n_commits, n_stats, n_orphan_parents).

    Tests use this to assert `n_stats == n_commits` and `n_orphan_parents == 0`. Example of how to do orphans below.
    n_orphan_parents = db_utils.exec_get_one(
      "SELECT COUNT(*) FROM commit_parents cp WHERE NOT EXISTS (SELECT 1 FROM commits c WHERE c.commit_hash = cp.parent_hash);"
  )[0]
    """
    n_orphan_parents = db_utils.exec_get_one(
        "SELECT COUNT(*) FROM commit_parents cp WHERE NOT EXISTS (SELECT 1 FROM commits c WHERE c.commit_hash = cp.parent_hash);"
    )[0]

    n_commits = db_utils.exec_get_one(
        "SELECT COUNT(*) FROM commits;"
    )[0]

    n_stats = db_utils.exec_get_one(
        "SELECT COUNT(*) FROM commit_stats;"
    )[0]

    return n_commits, n_stats, n_orphan_parents


def extract_head_commit(repo_path: str = "."):
    repo = Repo(repo_path)
    try:
        head = repo.head.commit
        return {
            "hash": head.hexsha,
            "author_name": head.author.name or "unknown",
            "message": head.message.strip(),
            "timestamp": datetime.fromtimestamp(head.committed_date),
        }
    finally:
        # Important on Windows to release file handles
        repo.close()


def insert_commit_record(commit_info: dict):
    sql = """
          INSERT INTO commits (commit_hash, author_name, message, commit_ts)
          VALUES (%(hash)s, %(author_name)s, %(message)s, %(timestamp)s); \
          """
    db_utils.exec_commit(sql, commit_info)


def mine_and_store(repo_path: str = "."):
    info = extract_head_commit(repo_path)
    insert_commit_record(info)
    return info


def mine_history(repo_path: str = ".", max_commits: Optional[int] = None, record_run: bool = True) -> int:
    """Traverse commit history and persist commits, stats, files, and parents.

    Processing order: oldest -> newest so the HEAD commit receives the highest id.
    Idempotent: uses unique(commit_hash) and ON CONFLICT safeguards.
    Returns: number of commits traversed this call (not newly inserted).
    """
    with Repo(repo_path) as repo:
        commits = list(repo.iter_commits("HEAD"))  # newest -> oldest
        commits.reverse()  # oldest -> newest
        count = 0
        FILE_LIMIT = 5000
        for c in commits:
            file_count = c.stats.total["files"]
            if file_count > FILE_LIMIT:
                print(
                    f"Bad commit file entry, {file_count} files, which is greater than our cutoff of {FILE_LIMIT} files, killing insert")
                raise ValueError("Too many files to process")
            cid = upsert_commit(c)
            insert_parents(cid, [p.hexsha for p in c.parents])
            insert_stats(cid, c)
            insert_files(cid, c)
            count += 1
            if max_commits is not None and count >= max_commits:
                break
        if record_run:
            head_hash = repo.head.commit.hexsha  # capture after processing
            insert_run_log(repo_path, head_hash, count)
        return count

