# AGENTS — Working Effectively in this Repository

This document captures the concrete commands, structure, conventions, and gotchas observed in this codebase so future agents can work productively without guesswork.

## Project Overview

- Project: PCGen — Java desktop application to create/manage RPG player characters
- Build tool: Gradle (via wrapper)
- Java toolchain: Java 25 (Temurin)
- UI: JavaFX; headless testing uses TestFX/Monocle
- Packaging: jlink/jpackage with custom runtimes and native installers

Key entry point: `pcgen.system.Main` (code/src/java/pcgen/system/Main.java)

## Repository Layout

- code/
  - src/java … main Java sources (pcgen.* packages)
  - src/utest … unit tests
  - src/test … slow tests
  - src/itest … integration tests
  - src/testcommon, src/testResources … shared test fixtures
  - gradle/ … build logic split across .gradle files
  - LICENSE
  - scripts: pcgen.sh, pcgen.bat (also duplicated in repo root)
- plugins/ … plugin jars and plugin folders used at runtime
- data/, outputsheets/, system/, preview/, vendordata/, homebrewdata/ … runtime datasets and assets
- docs/ … user documentation (Jekyll site, FAQs, listfile docs)
- installers/ … installer assets (mac/win/linux), release notes
- PCGen-base/, PCGen-Formula/ … separate Gradle builds for base/formula modules
- Root Gradle files: build.gradle, settings.gradle, gradle.properties, gradlew*
- CI: `.github/workflows/gradle-test.yml`, `gradle-release.yml`, `codeql-analysis.yml`

## Essential Commands

Always use the wrapper (./gradlew). Java 25 is required; Gradle will fetch dependencies and JavaFX modules as part of tasks.

- List tasks
  - ./gradlew tasks
- Build (default)
  - ./gradlew build
- Run unit tests (JUnit 5; headless JavaFX)
  - ./gradlew test
- Integration tests (defined source set)
  - ./gradlew itest
- Slow tests (longer-running suite)
  - ./gradlew slowtest
- Data tests
  - ./gradlew datatest
- Pathfinder integration tests
  - ./gradlew pfinttest
- Additional inttest variants
  - ./gradlew inttest sfinttest rsrdinttest srdinttest msrdinttest
- Coverage (Jacoco report in build/reports/jacoco/testCoverage/html)
  - ./gradlew testCoverage
- Assemble files for distribution (zips for data/docs/program/libs + runtime)
  - ./gradlew buildDist
- Quick dev binary to output/
  - ./gradlew qbuild
- Run the app (ensures JavaFX modules for host platform)
  - ./gradlew run
- Create native app image/installer via jpackage
  - ./gradlew jpackage
- Clean outputs and auxiliary folders (extended)
  - ./gradlew clean (also triggers cleanPlugins, cleanOutput, cleanJre, cleanMods, cleanMasterSheets)

Notes
- Some tasks trigger downloads of JDKs/JavaFX for all platforms (downloadJRE, downloadJavaFXModules) or host SDK (downloadJavaFXLocal/extractJavaFXLocal). CI caches build/jre and build/libs.
- Runtime bundles expect assets in data/, system/, outputsheets/, preview/, vendordata/, homebrewdata/.

## Running From Source

- Entry point: application plugin sets main class to `pcgen.system.Main`.
- ./gradlew run sets module-path and add-modules for JavaFX automatically and enables preview features.

Command-line flags (see code/src/java/pcgen/system/CommandLineArguments.java; tests in code/src/utest/pcgen/system/CommandLineArgumentsTest.java):
- --verbose | -v (counted, interpreted as boolean)
- --version | -V
- --name-generator
- --tab | -D <name>
- --settingsdir | -s <dir> (must exist and be readable)
- --configfilename | -S <file>
- --campaignmode | -m <name>
- --exportsheet | -E <file> (must exist/readable)
- --outputfile | -o <file> (new or writable file)
- --character | -c <file> (must exist)
- --party | -p <file> (must exist)

Batch export path exists in Main.startupWithoutGUI(). Tests demonstrate usage in PcgenFtlTestCase.

## Testing Approach

- JUnit 5 with Jupiter, xmlunit for XML comparisons, TestFX for JavaFX UI components.
- Source sets:
  - test: code/src/utest + testcommon
  - itest: code/src/itest + testcommon
  - slowtest: code/src/test + testcommon
- CI runs: build, then test itest datatest slowtest, then coverage.
- Headless settings applied to Test tasks:
  - -Dtestfx.robot=glass, -Dtestfx.headless=true
  - JavaFX module-path and add-modules configured
  - Assertions enabled; 1024m heap; maxParallelForks=1; forkEvery used for slow/int tests

## Code Quality and Style

- Checkstyle config: code/standards/checkstyle.xml (enforced via reporting.gradle; toolVersion 12.1.2). Newline at EOF; 201 char line length; prohibits `System.exit` (use pcgen.util.GracefulExit.exit).
- PMD: ruleset at code/standards/ruleset.xml (referenced from reporting.gradle).
- SpotBugs: plugin 6.4.7; toolVersion 4.9.8; exclude filter code/standards/spotbugs_ignore.xml; ignoreFailures true; extra findsecbugs plugin.
- Aggregate quality task: ./gradlew allReports

Conventions/gotchas observed
- Use `GracefulExit.exit` instead of `System.exit` (Checkstyle enforces via RegexpMultiline)
- Java version and JavaFX are tightly coupled to project.ext.javaVersion (25). Tests and run tasks add the needed JavaFX modules explicitly.
- Source sets are nonstandard (itest, slowtest); when adding new tests, place them in the correct source set to be picked up by the corresponding Gradle task.
- Plugins are built from compiled classes into plugin jars via tasks in code/gradle/plugins.gradle; main jar depends on jarAllPlugins.
- Some ivy/maven repos are over HTTP (allowInsecureProtocol true). Do not change without coordinating with maintainers.

## Build/Release Flow

- For releases, see `code/gradle/release.gradle` and CI workflow `.github/workflows/gradle-release.yml`.
- Release tasks:
  - `prepareRelease` (build; version handling is done by helper groovy script applied as `releaseUtils.groovy`)
  - `pcgenRelease` (assemble artifacts, checksum)
  - `pcgenReleaseOfficial` (pcgenRelease + updateVersionRelease)
- Artifacts collected into build/release; jpackage produces platform installers under build/jpackage.

## Data and Outputsheets

- Distribution zips combine program, data, docs, libs; copy specs in code/gradle/distribution.gradle control inclusion/exclusion.
- copyMasterSheets/cleanMasterSheets manage templated outputsheets across genre folders.

## CI Details

- gradle-test.yml
  - Java 25 (temurin)
  - ./gradlew build
  - ./gradlew test itest datatest slowtest
  - ./gradlew testCoverage and publish report
- codeql-analysis.yml analyzes Java
- gradle-release.yml builds jpackage artifacts for macOS, Windows, Ubuntu (x64) and Ubuntu ARM on tag pushes `v*.*.*`

## How to Add/Modify Code

- Place production code under code/src/java/pcgen/... matching existing packages.
- Ensure tests accompany new behavior:
  - Fast unit tests → code/src/utest
  - Integration tests → code/src/itest
  - Slow/long-running → code/src/test
- Run: ./gradlew test itest slowtest locally; fix failing tests before PRs.
- Follow style checks; do not use `System.exit` directly.

## Useful Paths

- Main: code/src/java/pcgen/system/Main.java
- CLI parsing: code/src/java/pcgen/system/CommandLineArguments.java
- CLI tests: code/src/utest/pcgen/system/CommandLineArgumentsTest.java
- FTL export test base: code/src/test/pcgen/inttest/PcgenFtlTestCase.java

## Local Run Examples

- Launch GUI:
  - ./gradlew run
- Run name generator directly:
  - ./gradlew run --args="--name-generator"
- Export a character to XML (batch mode example from tests):
  - java -jar build/libs/pcgen-<version>.jar --character /path/to/char.pcg --exportsheet code/testsuite/base-xml.ftl --outputfile /tmp/char.xml --configfilename config.ini.junit
  - Or via Main in Gradle: ./gradlew run --args="--character ... --exportsheet ... --outputfile ... --configfilename ..."

## Security/Defensive Notes

- CodeQL workflow is enabled.
- SpotBugs runs with findsecbugs; some visitors are omitted; failures ignored in reporting task (manual review advised).
- Avoid introducing insecure HTTP endpoints unless required by existing build constraints noted in build.gradle.

## Project-Specific “Don’t Break” Items

- Java version and JavaFX module handling are intertwined across build.gradle and distribution tasks — changing one often requires adjusting tasks (run, test, JavaCompile, runtime/jpackage) and CI.
- The distribution relies on file layout in data/, outputsheets/, system/, preview/ — deletions or renames will break runtime validation in Main.validateEnvironment().
- GracefulExit should be used for controlled termination (tests hook the exit function).

## Maintainer/Issue Tracking Context

- Primary docs: README.md (development setup, essential Gradle tasks)
- Issue tracker: Jira at https://pcgenorg.atlassian.net (CODE/DATA/etc.) as referenced in README and docs/faqpages/faqsubmittingabugreport.md

This file documents only observed behavior and commands present in this repository as of the current state.