### Test Breakdown
Tests are broken into three high-level modules:
- `code/src/test`, within which there are the unit tests for the core `pcgen` as well as plugins.
- `code/src/itest`, short for integration tests. Also covers both `pcgen` and plugins. 
- `code/src/slowtest`, tests that seem to have been separateed as they are particularly slow. 

There also exist the `code/src/testcommon` and `code/src/testResources` directories, which contain some basic helper classes and test fixtures. 

The `code/testsuite` directory contains more comprehensive data files, which appears to be the resources that `./gradlew datatest` relies on. 

### Metrics

#### Unit tests:
- 17195 tests
- 0 failures
- 11 skipped
- 1m10.59s duration
- 25% instruction coverage, 22% branch coverage
**Note:** The `jacocoTestReport` task only covers the unit test suite, not any of the other test tasks (`itest`, `datatest`, `slowtest`).

#### Slow tests:
- 1145 tests
- 0 failures
- 3 skipped
- 11m12.41s duration

#### Integration tests:
- 2913 tests
- 0 failures
- 0 skipped
- 20.625s duration

#### Data tests:
- 18 tests
- 0 failures
- 0 skipped
- 1m13.95s duration
**Note:** This runs two test files, `DataTest.java` and `DataLoadTest.java`. These are meant specifically to test loading sources (the vast majority of the runtime) and otherwise checking edge cases with missing files, orphaned files, and different file paths/lengths. 
