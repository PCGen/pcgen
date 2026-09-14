from src import db_utils

def test_can_execute_schema_and_count():
    row = db_utils.exec_get_one("SELECT COUNT(*) FROM commits;")
    assert row is not None
    assert isinstance(row[0], int)
