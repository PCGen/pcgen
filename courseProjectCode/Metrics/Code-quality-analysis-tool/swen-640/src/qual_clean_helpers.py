# ---------------------------------------------------------------------------
# Database helpers - Modify if you need to, but most likely you do not.
# ---------------------------------------------------------------------------
from src import db_utils
from src.qual_clean import *


def ensure_columns():
    """
    Create normalized columns on database tables if they don't exist.
    Idempotent - safe to run multiple times.
    """
    if db_utils is None:
        return
    stmts = [
        "ALTER TABLE issues ADD COLUMN IF NOT EXISTS title_clean TEXT;",
        "ALTER TABLE issues ADD COLUMN IF NOT EXISTS author_norm TEXT;",
        "ALTER TABLE issues ADD COLUMN IF NOT EXISTS is_bot BOOLEAN DEFAULT FALSE;",

        "ALTER TABLE pull_requests ADD COLUMN IF NOT EXISTS title_clean TEXT;",
        "ALTER TABLE pull_requests ADD COLUMN IF NOT EXISTS author_norm TEXT;",
        "ALTER TABLE pull_requests ADD COLUMN IF NOT EXISTS is_bot BOOLEAN DEFAULT FALSE;",

        "ALTER TABLE commits ADD COLUMN IF NOT EXISTS subject TEXT;",
        "ALTER TABLE commits ADD COLUMN IF NOT EXISTS body TEXT;",
        "ALTER TABLE commits ADD COLUMN IF NOT EXISTS cc_type TEXT;",
        "ALTER TABLE commits ADD COLUMN IF NOT EXISTS cc_scope TEXT;",
        "ALTER TABLE commits ADD COLUMN IF NOT EXISTS cc_breaking BOOLEAN DEFAULT FALSE;",
    ]
    for s in stmts:
        db_utils.exec_commit(s)


def clean_issues_db(limit: Optional[int] = None) -> int:
    """
    Apply normalization to issues table. Returns count of rows processed.
    """
    if db_utils is None:
        return 0
    ensure_columns()
    rows = db_utils.exec_get_all(
        "SELECT id, title, author FROM issues"
        + (f" LIMIT {int(limit)}" if limit else "")
        + ";"
    )
    count = 0
    for (iid, title, author) in rows:
        title_clean = normalize_text(title or "")
        author_norm, is_bot = canonicalize_user(author)
        db_utils.exec_commit(
            """
            UPDATE issues SET
                title_clean=%(title_clean)s,
                author_norm=%(author_norm)s,
                is_bot=%(is_bot)s
            WHERE id=%(id)s;
            """,
            {
                "title_clean": title_clean,
                "author_norm": author_norm,
                "is_bot": is_bot,
                "id": iid,
            }
        )
        count += 1
    return count


def clean_prs_db(limit: Optional[int] = None) -> int:
    """
    Apply normalization to pull_requests table. Returns count of rows processed.
    """
    if db_utils is None:
        return 0
    ensure_columns()
    rows = db_utils.exec_get_all(
        "SELECT id, title, author FROM pull_requests"
        + (f" LIMIT {int(limit)}" if limit else "")
        + ";"
    )
    count = 0
    for (pid, title, author) in rows:
        title_clean = normalize_text(title or "")
        author_norm, is_bot = canonicalize_user(author)
        db_utils.exec_commit(
            """
            UPDATE pull_requests SET
                title_clean=%(title_clean)s,
                author_norm=%(author_norm)s,
                is_bot=%(is_bot)s
            WHERE id=%(id)s;
            """,
            {
                "title_clean": title_clean,
                "author_norm": author_norm,
                "is_bot": is_bot,
                "id": pid,
            }
        )
        count += 1
    return count


def clean_commits_db(limit: Optional[int] = None) -> int:
    """
    Apply commit message parsing to commits table. Returns count of rows processed.
    """
    if db_utils is None:
        return 0
    ensure_columns()
    rows = db_utils.exec_get_all(
        "SELECT id, message FROM commits"
        + (f" LIMIT {int(limit)}" if limit else "")
        + ";"
    )
    count = 0
    for (cid, message) in rows:
        parts = split_commit_message(message or "")
        db_utils.exec_commit(
            """
            UPDATE commits SET
                subject=%(subject)s,
                body=%(body)s,
                cc_type=%(cc_type)s,
                cc_scope=%(cc_scope)s,
                cc_breaking=%(cc_breaking)s
            WHERE id=%(id)s;
            """,
            {
                "subject": parts["subject"],
                "body": parts["body"],
                "cc_type": parts["type"],
                "cc_scope": parts["scope"],
                "cc_breaking": parts["breaking"],
                "id": cid,
            }
        )
        count += 1
    return count