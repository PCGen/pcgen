import os
import tempfile
import pytest
from git import Repo
from src import db_utils
from src.db_utils import exec_sql_file
from datetime import datetime, timezone, timedelta


@pytest.fixture(scope="session", autouse=True)
def ensure_schema():
    # Ensure the commits table exists before any tests run
    db_utils.exec_sql_file('data/schema.sql')


@pytest.fixture
def temp_git_repo():
    with tempfile.TemporaryDirectory() as tmp:
        repo = Repo.init(tmp)
        try:
            with repo.config_writer() as cw:
                cw.set_value('user', 'name', 'STRATA Student')
                cw.set_value('user', 'email', 'student@example.com')

            fpath = os.path.join(tmp, 'hello.txt')
            with open(fpath, 'w') as f:
                f.write('hello strata')
            repo.index.add([fpath])
            repo.index.commit('initial commit')

            yield tmp
        finally:
            # Make sure GitPython releases all file handles
            repo.close()


@pytest.fixture
def linear_two_commit():
    with tempfile.TemporaryDirectory() as tmp:
        repo = Repo.init(tmp)
        try:
            with repo.config_writer() as cw:
                cw.set_value('user', 'name', 'STRATA Student')
                cw.set_value('user', 'email', 'student@example.com')

            fpath = os.path.join(tmp, 'hello.txt')
            with open(fpath, 'w') as f:
                f.write('hello strata')
            repo.index.add([fpath])
            repo.index.commit('initial commit')

            with open(fpath, 'a') as f:
                f.write('\nnewline!')
            repo.index.add([fpath])
            repo.index.commit('followup commit')

            yield tmp
        finally:
            # Make sure GitPython releases all file handles
            repo.close()


@pytest.fixture
def complicated_multi_commit():
    with tempfile.TemporaryDirectory() as tmp:
        repo = Repo.init(tmp)
        try:
            with repo.config_writer() as cw:
                cw.set_value('user', 'name', 'STRATA Student')
                cw.set_value('user', 'email', 'student@example.com')

            fpath = os.path.join(tmp, 'hello.txt')
            with open(fpath, 'w') as f:
                f.write('hello strata')
            repo.index.add([fpath])
            repo.index.commit('initial commit')

            with open(fpath, 'a') as f:
                f.write('\nnewline!')
            repo.index.add([fpath])

            fpathNew = os.path.join(tmp, 'newFile.txt')
            with open(fpathNew, 'w') as f:
                f.write('new text file!')
            repo.index.add([fpathNew])

            repo.index.commit('new commit')

            fpathRename = os.path.join(tmp, 'greetings.txt')
            os.rename(fpath, fpathRename)
            repo.index.remove(['hello.txt'])
            repo.index.add([fpathRename])
            repo.index.commit('rename commit')

            yield tmp
        finally:
            # Make sure GitPython releases all file handles
            repo.close()


@pytest.fixture(autouse=True, scope="function")
def clear_db_before_test():
    exec_sql_file("data/schema.sql")
    yield


def getIssueArray():
    issues = [
        {
            'number': 1,
            'title': 'issue1',
            'author': 'bob',
            'state': 'open',
            'created_at': datetime.now(timezone.utc).isoformat(),
            'closed_at': None
        },
        {
            'number': 2,
            'title': 'issue2',
            'author': 'bobTheSecond',
            'state': 'closed',
            'created_at': datetime.now(timezone.utc).isoformat(),
            'closed_at': datetime.now(timezone.utc).isoformat()
        }
    ]
    return issues


def getPrArray():
    PRs = [
        {
            'number': 10,
            'title': 'pr1',
            'author': 'bob',
            'state': 'open',
            'created_at': datetime.now(timezone.utc).isoformat()
        },
        {
            'number': 11,
            'title': 'pr2',
            'author': 'bobTheSecond',
            'state': 'closed',
            'created_at': datetime.now(timezone.utc).isoformat(),
            'merged_at': datetime.now(timezone.utc).isoformat(),
            'closed_at': datetime.now(timezone.utc).isoformat()
        }
    ]
    return PRs


def getPipelineArray(commitHash):
    timeRn = datetime.now(timezone.utc)
    pipelines = [
        {
            'pipeline_id': 1001,
            'status': 'success',
            'sha': commitHash,
            'created_at': (timeRn - timedelta(minutes=5)).isoformat(),
            'updated_at': timeRn.isoformat()
        }
    ]
    return pipelines


def getJobsByPipelineArray(pipelineId):
    timeRn = datetime.now(timezone.utc)
    jobsByPipeline = {
        '1001': [
            {
                'job_id': 2001,
                'pipeline_id': pipelineId,
                'name': 'build',
                'started_at': (timeRn - timedelta(minutes=5)).isoformat(),
                'finished_at': (timeRn - timedelta(minutes=3)).isoformat(),
                'duration_seconds': 120
            },
            {
                'job_id': 2002,
                'pipeline_id': pipelineId,
                'name': 'test',
                'started_at': (timeRn - timedelta(minutes=3)).isoformat(),
                'finished_at': timeRn.isoformat(),
                'duration_seconds': 180
            }
        ]
    }
    return jobsByPipeline


def getProvider():
    return 'github'


def getRepoString():
    return 'acme/widgets'


def getTimeStampIssue():
    issues = [
        {
            'number': 1,
            'title': 'issu2',
            'author': 'bob',
            'state': 'closed',
            'created_at': datetime.now(timezone.utc).isoformat(),
            'closed_at': datetime.now(timezone.utc).isoformat()
        }
    ]
    return issues
