from src.git_miner import *
from src.git_ingest import *
from test.conftest import *


def test_issue_pr_idempotent(linear_two_commit):
    issues = getIssueArray()
    PRs = getPrArray()
    provider = getProvider()
    repo = getRepoString()

    ingest_issues(provider, repo, issues)
    ingest_pull_requests(provider, repo, PRs)

    ingest_issues(provider, repo, issues)
    ingest_pull_requests(provider, repo, PRs)

    n_issues = db_utils.exec_get_one(
        "SELECT COUNT(*) FROM issues;"
    )[0]

    n_PRs = db_utils.exec_get_one(
        "SELECT COUNT(*) FROM pull_requests;"
    )[0]

    assert n_issues == 2
    assert n_PRs == 2

    state = db_utils.exec_get_one("SELECT state FROM pull_requests WHERE merged_at IS NOT NULL LIMIT 1;")[0]

    assert state == "merged"


def test_pipelines_jobs_idempotent(linear_two_commit):
    pipeline = getPipelineArray("testHash")
    jobs = getJobsByPipelineArray(pipeline[0]["pipeline_id"])
    provider = getProvider()
    repo = getRepoString()

    ingest_ci(provider, repo, pipeline, jobs)

    ingest_ci(provider, repo, pipeline, jobs)

    n_pipelines = db_utils.exec_get_one(
        "SELECT COUNT(*) FROM ci_pipelines;"
    )[0]

    n_jobs = db_utils.exec_get_one(
        "SELECT COUNT(*) FROM ci_jobs;"
    )[0]

    assert n_pipelines == 1
    assert n_jobs == 2


def test_pipeline_commit_sha_match(linear_two_commit):
    sha = extract_head_commit(linear_two_commit)["hash"]
    pipeline = getPipelineArray(sha)
    jobs = getJobsByPipelineArray(pipeline[0]["pipeline_id"])
    provider = getProvider()
    repo = getRepoString()

    mine_history(linear_two_commit)

    ingest_ci(provider, repo, pipeline, jobs)

    n_matching_rows = db_utils.exec_get_all(
        "SELECT 1 FROM commits WHERE commit_hash = %s", (sha,)
    )

    assert len(n_matching_rows) == 1


def test_timestamp_insertion(linear_two_commit):
    issue = getTimeStampIssue()
    provider = getProvider()
    repo = getRepoString()
    timestamp = datetime.now(timezone.utc)

    issue[0]["closed_at"] = timestamp.isoformat()
    ingest_issues(provider, repo, issue)  # default test

    issue[0]["closed_at"] = timestamp.isoformat().replace("+00:00", "Z")
    ingest_issues(provider, repo, issue)  # tack on the z instead of the timestamp bit

    issue[0]["closed_at"] = timestamp.replace(microsecond=0)
    ingest_issues(provider, repo, issue)  # ditch the fractional seconds

    issue[0]["closed_at"] = timestamp.replace(microsecond=0).isoformat().replace("+00:00", "Z")
    ingest_issues(provider, repo, issue)  # ditch the fractional seconds and add the z

    # lack of blowing up is a pass enough


def test_missing_merge_sections(linear_two_commit):

    PRs = getPrArray()
    provider = getProvider()
    repo = getRepoString()

    ingest_pull_requests(provider, repo, PRs)

    n_matching_merged = db_utils.exec_get_all(
        "SELECT * FROM pull_requests WHERE merged_at IS NULL"
    )

    n_matching_closed = db_utils.exec_get_all(
        "SELECT * FROM pull_requests WHERE closed_at IS NULL"
    )

    assert len(n_matching_merged) == 1
    assert len(n_matching_closed) == 1


def test_job_status_update(linear_two_commit):
    provider = getProvider()
    repo = getRepoString()
    sha = extract_head_commit(linear_two_commit)["hash"]
    pipeline = getPipelineArray(sha)
    jobs = getJobsByPipelineArray(pipeline[0]["pipeline_id"])

    jobs["1001"][0]["status"] = "open"
    ingest_ci(provider, repo, pipeline, jobs)
    matching_rows = db_utils.exec_get_all(
        "SELECT status FROM ci_jobs WHERE job_id = %s", (str(jobs["1001"][0]["job_id"]),)
    )
    assert len(matching_rows) == 1
    assert matching_rows[0][0] == "open"

    jobs["1001"][0]["status"] = "closed"
    ingest_ci(provider, repo, pipeline, jobs)
    matching_rows = db_utils.exec_get_all(
        "SELECT status FROM ci_jobs WHERE job_id = %s", (str(jobs["1001"][0]["job_id"]),)
    )
    assert len(matching_rows) == 1
    assert matching_rows[0][0] == "closed"


