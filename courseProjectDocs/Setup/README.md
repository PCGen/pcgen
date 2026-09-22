## Running the tests
Sourced from the repository's README.md:

### (Re)Compile Java
    ./gradlew compileJava

### Build All Required Files
    ./gradlew assemble

### Run PCGen
    ./gradlew run

### Run Test Suite
    ./gradlew test

### Run Full Test Suite
Do this primarily __before__ pull requests.
This mirrors what GitHub Actions runs to verify a PR; if it fails locally your CI build will also fail and your PR will not be merged.

    ./gradlew build
    ./gradlew itest datatest slowtest

`build` already runs the unit `test` task via the standard Java lifecycle, so it is not repeated. The second command runs the integration, data, and slow test suites.

The test reports are available in the `build/reports/tests` directory as HTML. 

## Coverage Report
Run the following gradle task after running the tests:
```
./gradlew jacocoTestReport
```

The report is available in the `build/reports/jacoco` directory as HTML.
