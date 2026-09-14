from src import db_utils
from src.git_miner import *


def test_mine_and_store_inserts_head_commit(temp_git_repo):
    info = mine_and_store(temp_git_repo)
    assert 'hash' in info
    assert info['author_name'] == 'STRATA Student'

    row = db_utils.exec_get_one("SELECT commit_hash, author_name, message FROM commits ORDER BY id DESC LIMIT 1;")
    assert row[0] == info['hash']
    assert row[1] == 'STRATA Student'
    assert 'initial commit' in row[2]


def test_head_mining_compatibility(linear_two_commit):
    info = mine_and_store(linear_two_commit)

    row = db_utils.exec_get_one("SELECT commit_hash, author_name FROM commits ORDER BY id DESC LIMIT 1;")
    assert row is not None
    assert row[0] == info['hash']
    assert row[1] == info['author_name']


def test_mining_history_population(linear_two_commit):
    mine_history(linear_two_commit)
    commitCount = db_utils.exec_get_one(
        "SELECT COUNT(*) FROM commits;"
    )[0]
    commitStatsCount = db_utils.exec_get_one(
        "SELECT COUNT(*) FROM commit_stats;"
    )[0]
    row = db_utils.exec_get_one("SELECT additions, deletions FROM commit_files WHERE file_path = %s LIMIT 1;",
                                ("hello.txt",))
    assert row is not None
    assert commitCount >= 2  #commits
    assert commitCount == commitStatsCount  #commits and commit_stats
    assert row[0] >= 0  #adds
    assert row[1] >= 0  #dels


def test_idempotent_edl(linear_two_commit):
    mine_history(linear_two_commit)
    n_commits = db_utils.exec_get_one(
        "SELECT COUNT(*) FROM commits;"
    )[0]

    n_stats = db_utils.exec_get_one(
        "SELECT COUNT(*) FROM commit_stats;"
    )[0]

    n_files = db_utils.exec_get_one(
        "SELECT COUNT(*) FROM commit_files;"
    )[0]

    mine_history(linear_two_commit)
    invar_results_again = validate_invariants()
    n_commits_again = db_utils.exec_get_one(
        "SELECT COUNT(*) FROM commits;"
    )[0]

    n_stats_again = db_utils.exec_get_one(
        "SELECT COUNT(*) FROM commit_stats;"
    )[0]

    n_files_again = db_utils.exec_get_one(
        "SELECT COUNT(*) FROM commit_files;"
    )[0]

    assert n_commits == n_commits_again
    assert n_stats == n_stats_again
    assert n_files == n_files_again


def test_provenance_run_log(linear_two_commit):
    commit_count = mine_history(linear_two_commit, record_run=True)

    repo_path = linear_two_commit
    with Repo(repo_path) as repo:
        hash = repo.head.commit.hexsha
        row = db_utils.exec_get_one("SELECT repo_path, head_hash, commit_count FROM run_log ORDER BY id DESC LIMIT 1")
    assert row is not None
    assert row[0] == repo_path
    assert row[1] == hash
    assert row[2] == commit_count


def test_validation_invariants(linear_two_commit):
    mine_history(linear_two_commit)
    invarData = validate_invariants()
    assert invarData[0] == invarData[1]  #stats & commits
    assert invarData[2] == 0  #orphans


def test_double_files(complicated_multi_commit):
    mine_history(complicated_multi_commit)
    n_files_in_row = db_utils.exec_get_one(
        "SELECT COUNT(*) FROM commit_files WHERE commit_id = 2"
    )[0]
    assert n_files_in_row == 2  #multiple file rows per commit


def test_rename_file(complicated_multi_commit):
    mine_history(complicated_multi_commit)
    n_files_in_row = db_utils.exec_get_one(
        """SELECT COUNT(*) FROM commit_files WHERE change_type = %s""", ("R",)
    )[0]
    assert n_files_in_row == 1  # rename works properly
