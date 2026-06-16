# AGENTS — Working Effectively in this Repository

This document captures the concrete commands, structure, conventions, and gotchas observed in this codebase so future agents can work productively without guesswork.

> **⚠️ IMPORTANT: Keep this file up to date!**
> Any time LLM-based development (Copilot, ChatGPT, Claude, or any AI coding assistant) is used to make changes to this repository, the developer (or the LLM agent itself) **must** update this file to reflect any relevant changes. This includes but is not limited to: new build commands, dependency changes, structural changes, new CI workflows, convention changes, new source sets, or anything else that would help a future agent work effectively. Treat this file as living documentation that evolves with the codebase.

## Project Overview

- Project: PCGen — Java desktop application to create/manage RPG player characters
- Build tool: Gradle 9.5.0 (via wrapper `./gradlew`)
- Java toolchain: Java 25 (Eclipse Temurin)
- UI: JavaFX (OpenJFX, version matched to Java 25); headless testing uses TestFX/Monocle
- Packaging: jlink (org.beryx.jlink 4.0.0) / jpackage with custom runtimes and native installers
- Current version: defined in `gradle.properties` (e.g. `6.09.08.RC1`)
- Configuration cache: enabled (`org.gradle.configuration-cache=true` in gradle.properties)

Key entry point: `pcgen.system.Main` (code/src/java/pcgen/system/Main.java)

## Repository Layout

- code/
  - src/java … main Java sources (pcgen.* packages)
  - src/utest … unit tests (fast, run by `test` task)
  - src/test … slow tests (run by `slowtest` task)
  - src/itest … integration tests (run by `itest` task)
  - src/testcommon … shared test utilities (compiled once, consumed by test/itest/slowtest)
  - src/testResources … shared test resources
  - src/resources … main resources (includes pcgen/system/prop/PCGenProp.properties)
  - gradle/ … build logic split across .gradle files (reporting, distribution, autobuild, release, plugins)
  - standards/ … checkstyle.xml, ruleset.xml (PMD), spotbugs_ignore.xml
  - LICENSE
  - scripts: pcgen.sh, pcgen.bat
- plugins/ … plugin jars built from compiled classes at build time
- data/, outputsheets/, system/, preview/, vendordata/, homebrewdata/ … runtime datasets and assets
- docs/ … user documentation (Jekyll site, FAQs, listfile docs)
- installers/ … installer assets (mac/win/linux), release notes
- PCGen-base/, PCGen-Formula/ … separate Gradle builds for base/formula modules (consumed as Maven dependencies)
- Root Gradle files: build.gradle, settings.gradle, gradle.properties, gradlew*
- CI: `.github/workflows/gradle-test.yml`, `gradle-nightly.yml`, `gradle-release.yml`, `gradle-release-manual.yml`, `codeql-analysis.yml`

## Essential Commands

Always use the wrapper (`./gradlew`). Java 25 (Temurin) is required; Gradle will fetch dependencies and JavaFX modules as part of tasks.

| Task                                                     | Description                                                                                  |
|----------------------------------------------------------|----------------------------------------------------------------------------------------------|
| `./gradlew build`                                        | Compile + unit tests (includes `test` task)                                                  |
| `./gradlew test`                                         | Unit tests only (JUnit 6 Jupiter + JUnit 4 vintage; headless JavaFX)                         |
| `./gradlew itest`                                        | Integration tests                                                                            |
| `./gradlew slowtest`                                     | Slow/long-running tests (depends on itest)                                                   |
| `./gradlew datatest`                                     | Data loading/validation tests                                                                |
| `./gradlew pfinttest`                                    | Pathfinder integration tests                                                                 |
| `./gradlew inttest`                                      | All character integration tests                                                              |
| `./gradlew sfinttest rsrdinttest srdinttest msrdinttest` | Per-game-mode integration test variants                                                      |
| `./gradlew testCoverage`                                 | Jacoco coverage report (build/reports/jacoco/testCoverage/html)                              |
| `./gradlew allReports`                                   | Checkstyle + PMD + SpotBugs reports                                                          |
| `./gradlew buildDist`                                    | Assemble distribution zips (data/docs/program/libs + runtime)                                |
| `./gradlew qbuild`                                       | Quick dev binary to output/                                                                  |
| `./gradlew run`                                          | Run the app (JavaFX modules configured automatically)                                        |
| `./gradlew fullJpackage`                                 | Create native app image/installer via jpackage                                               |
| `./gradlew clean`                                        | Clean all (also triggers cleanPlugins, cleanOutput, cleanJdks, cleanMods, cleanMasterSheets) |
| `./gradlew tasks`                                        | List all available tasks                                                                     |

Notes
- jlink/jpackage build only the host platform. The download/extract tasks (downloadJdk, extractJdk, downloadJfxMods, extractJfxMods) target the host OS/arch automatically; the host SDK helper for local dev is downloadJavaFXLocal/extractJavaFXLocal. CI caches build/jre and build/libs.
- Runtime bundles expect assets in data/, system/, outputsheets/, preview/, vendordata/, homebrewdata/.
- Test tasks use parallel forks: `Runtime.runtime.availableProcessors().intdiv(2) ?: 1` for test/itest; `forkEvery = 1` for slowtest/inttest variants.

## Running From Source

- Entry point: application plugin sets main class to `pcgen.system.Main`.
- `./gradlew run` sets module-path and add-modules for JavaFX automatically.

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

- JUnit 6 (BOM 6.0.3) with Jupiter, JUnit 4 legacy tests via vintage engine (~870 tests still use JUnit 4).
- xmlunit 2.11.0 for XML comparisons, TestFX 4.0.18 + Monocle 21.0.2 for JavaFX UI components.
- Source sets:
  - test: code/src/utest + testcommon (fast unit tests)
  - itest: code/src/itest + testcommon (integration tests)
  - slowtest: code/src/test + testcommon (slow/character integration tests)
- CI runs: `build` → `itest datatest slowtest` → `testCoverage` (coverage report posted to PR).
- Headless settings applied to all Test tasks:
  - `-Djava.awt.headless=true`, `-Djavafx.macosx.embedded=true`
  - `--module-path mods/lib --add-modules javafx.controls,javafx.web,javafx.swing,javafx.fxml,javafx.graphics`
  - `--enable-native-access=javafx.graphics`
  - Assertions enabled; 1024m heap; maxParallelForks=1 (global default, overridden for test/itest)
- Unit test task additionally sets: `-Dtestfx.robot=glass`, `-Dtestfx.headless=true`, `-Dprism.order=sw`

## Code Quality and Style

- **Checkstyle**: config at code/standards/checkstyle.xml; toolVersion 13.2.0. Newline at EOF; 201 char line length; prohibits `System.exit` (use `pcgen.util.GracefulExit.exit`).
- **PMD**: ruleset at code/standards/ruleset.xml; toolVersion 7.21.0; dependencies pmd-java 7.24.0 and pmd-ant 7.24.0. Incremental analysis enabled; ignoreFailures true.
- **SpotBugs**: plugin 6.5.4; toolVersion 4.9.8; exclude filter code/standards/spotbugs_ignore.xml; omitVisitors: Naming, CrossSiteScripting, DontUseEnum, DoInsideDoPrivileged; ignoreFailures true; extra findsecbugs plugin 1.14.0.
- **Jacoco**: toolVersion 0.8.14.
- Aggregate quality task: `./gradlew allReports` (runs checkstyleMain, pmdMain, spotbugsMain)

Conventions/gotchas observed:
- Use `GracefulExit.exit` instead of `System.exit` (Checkstyle enforces via RegexpMultiline).
- Java version and JavaFX are tightly coupled to `project.ext.javaVersion` (25). Tests and run tasks add the needed JavaFX modules explicitly.
- Source sets are nonstandard (itest, slowtest, testcommon); when adding new tests, place them in the correct source set to be picked up by the corresponding Gradle task.
- Plugins are built from compiled classes into plugin jars via tasks in code/gradle/plugins.gradle; main jar depends on `jarAllPlugins`.
- Some ivy/maven repos are over HTTP (`allowInsecureProtocol true`). Do not change without coordinating with maintainers.
- Gradle configuration cache is enabled — tasks that are not compatible should declare `notCompatibleWithConfigurationCache(...)`.

## Build/Release Flow

- For releases, see `code/gradle/release.gradle` and CI workflows:
  - `.github/workflows/gradle-release.yml` — triggered on tag push `v*.*.*` or manual dispatch with existing tag (validates tag vs `gradle.properties`).
  - `.github/workflows/gradle-release-manual.yml` — manual dispatch that takes a release version, commits the bump to `gradle.properties` on the dispatched branch, tags + builds across all platforms, then commits the next `-SNAPSHOT` dev version back to the same branch on success.
  - `.github/workflows/gradle-nightly.yml` — scheduled daily; skips if no commits to `master` in the last 24h; otherwise publishes a pre-release tagged `v<base>-NIGHTLY.<YYYYMMDD>` with all four platform builds and prunes nightlies older than the most recent 7.
- Release tasks:
  - `prepareRelease` (build; version handling via `releaseUtils.groovy`)
  - `pcgenRelease` (prepareRelease + assembleArtifacts + checksum)
  - `pcgenReleaseOfficial` (pcgenRelease + updateVersionRelease)
  - `updateVersionToNext` (Groovy helper in `releaseUtils.groovy` that bumps the trailing numeric segment by 1, zero-padded to two digits, and appends `-SNAPSHOT`; used by the manual release workflow's post-build bump step).
- Artifacts collected into build/release; jpackage produces platform installers under build/jpackage.
- Release CI builds on: ubuntu-latest (x64), ubuntu-24.04-arm, macos-latest, windows-latest.
- CI updates `PCGenProp.properties` with version number and release date at build time (per-platform, in-memory, never committed).

## Data and Outputsheets

- Distribution zips combine program, data, docs, libs; copy specs in code/gradle/distribution.gradle control inclusion/exclusion.
- copyMasterSheets/cleanMasterSheets manage templated outputsheets across genre folders (historical, horror, sciencefiction, western).

## CI Details

- **gradle-test.yml** (runs on pull_request):
  - Java 25 (temurin), Gradle cache via `gradle/actions/setup-gradle@v4`
  - `./gradlew build`
  - `./gradlew itest datatest slowtest`
  - `./gradlew testCoverage`
  - Publishes test results via `EnricoMi/publish-unit-test-result-action@v2`
  - Coverage report uploaded as artifact and posted to PR via `madrapps/jacoco-report@v1.7.2`
- **codeql-analysis.yml** (push/PR to master + weekly schedule):
  - Analyzes Java via CodeQL autobuild
- **gradle-release.yml** (tag push `v*.*.*` or workflow_dispatch with existing tag):
  - Validates tag vs gradle.properties version
  - Creates GitHub release, builds on 4 platforms, attaches artifacts
- **gradle-release-manual.yml** (workflow_dispatch — full release pipeline with version management):
  - Inputs: `release_version` (e.g. `6.09.08`; no `v` prefix, no `-SNAPSHOT`) and `prerelease` boolean.
  - `prepare_release`: validates the version, ensures the tag is free, commits `version=<release_version>` to `gradle.properties` on the dispatched branch via `github-actions[bot]`, tags the commit, and creates the GitHub Release.
  - `build_release`: 4-platform matrix mirroring `gradle-release.yml`; checks out the new tag and runs `./gradlew build compileSlowtest datatest pfinttest pcgenRelease`, uploading artifacts to the Release.
  - `bump_to_next_dev` (only if every matrix job passed): runs `./gradlew --no-daemon --no-configuration-cache updateVersionToNext` and commits the new `-SNAPSHOT` version back to the dispatched branch so the source tree is ready for further development.
  - Concurrency: `manual-release-${{ github.ref }}`, no cancel-in-progress (avoid partial publish).
- **gradle-nightly.yml** (schedule `0 2 * * *` + workflow_dispatch):
  - `check_changes`: counts commits on `master` in the last 24h via `git rev-list --count --since='24 hours ago' HEAD`; on zero commits, the workflow short-circuits and the build/cleanup jobs are skipped.
  - When changes are present, derives `<base>` from `gradle.properties` (stripping any `-SNAPSHOT`), computes `nightly_version=<base>-NIGHTLY.<YYYYMMDD>` (UTC), and treats the tag `v<nightly_version>` as the release identifier.
  - `create_release`: creates and pushes the tag, then creates a GitHub pre-release at that tag.
  - `build_release`: 4-platform matrix; checks out the tag, in-memory patches `gradle.properties` and `PCGenProp.properties` to the nightly version, runs the same full build + tests as the manual release, and attaches artifacts to the pre-release.
  - `cleanup_old_nightlies`: keeps the most recent 7 nightly pre-releases (`v*-NIGHTLY.*` matching) and deletes older ones together with their git tags via `gh release delete <tag> --yes --cleanup-tag`. Failures are warnings, not workflow-fatal.
  - Concurrency: group `nightly`, no cancel-in-progress.

## How to Add/Modify Code

- Place production code under code/src/java/pcgen/... matching existing packages.
- Ensure tests accompany new behavior:
  - Fast unit tests → code/src/utest
  - Integration tests → code/src/itest
  - Slow/long-running → code/src/test
- Run: `./gradlew test itest slowtest` locally; fix failing tests before PRs.
- Follow style checks; do not use `System.exit` directly — use `pcgen.util.GracefulExit.exit`.
- Run `./gradlew allReports` to check Checkstyle/PMD/SpotBugs before submitting.
- JavaFX/FXML dialogs:
  - For **standalone Stage dialogs** (loaded from FXML and shown as a top-level window via `showAsStage(title)` or modally via `showAndBlock(title)`), use `pcgen.gui3.PanelFromResource<T>`. Both methods are thread-safe (`showAsStage` auto-dispatches to the FX thread; `showAndBlock` must be called off the FX thread and blocks the caller until the dialog closes).
  - For **embedding FXML scenes inside Swing** containers, use `pcgen.gui3.JFXPanelFromResource<T>` (it extends `JFXPanel`). Do **not** re-use it for standalone dialogs — re-parenting its embedded `Scene` onto a real `Stage` corrupts the quantum peer and triggers an NPE in `com.sun.javafx.tk.quantum.GlassScene#updateSceneState` on macOS HiDPI displays.

## Useful Paths

| Purpose                         | Path                                                        |
|---------------------------------|-------------------------------------------------------------|
| Main entry point                | code/src/java/pcgen/system/Main.java                        |
| CLI parsing                     | code/src/java/pcgen/system/CommandLineArguments.java        |
| CLI tests                       | code/src/utest/pcgen/system/CommandLineArgumentsTest.java   |
| GracefulExit                    | code/src/java/pcgen/util/GracefulExit.java                  |
| FTL export test base            | code/src/test/pcgen/inttest/PcgenFtlTestCase.java           |
| Standalone Stage dialogs (FXML) | code/src/java/pcgen/gui3/PanelFromResource.java             |
| Swing-embedded FXML panels      | code/src/java/pcgen/gui3/JFXPanelFromResource.java          |
| Thread assertions for UI code   | code/src/java/pcgen/gui3/GuiAssertions.java                 |
| Build config                    | build.gradle                                                |
| Version                         | gradle.properties                                           |
| Checkstyle rules                | code/standards/checkstyle.xml                               |
| PMD ruleset                     | code/standards/ruleset.xml                                  |
| SpotBugs exclusions             | code/standards/spotbugs_ignore.xml                          |
| Release logic                   | code/gradle/release.gradle, code/gradle/releaseUtils.groovy |
| Distribution logic              | code/gradle/distribution.gradle                             |
| Plugin jar building             | code/gradle/plugins.gradle                                  |
| Reporting (quality)             | code/gradle/reporting.gradle                                |
| CI test workflow                | .github/workflows/gradle-test.yml                           |
| CI release workflow (tag)       | .github/workflows/gradle-release.yml                        |
| CI release workflow (manual)    | .github/workflows/gradle-release-manual.yml                 |
| CI nightly workflow             | .github/workflows/gradle-nightly.yml                        |

## Local Run Examples

- Launch GUI:
  - `./gradlew run`
- Run name generator directly:
  - `./gradlew run --args="--name-generator"`
- Export a character to XML (batch mode example from tests):
  - `java -jar build/libs/pcgen-<version>.jar --character /path/to/char.pcg --exportsheet code/testsuite/base-xml.ftl --outputfile /tmp/char.xml --configfilename config.ini.junit`
  - Or via Gradle: `./gradlew run --args="--character ... --exportsheet ... --outputfile ... --configfilename ..."`

## Security/Defensive Notes

- CodeQL workflow is enabled (runs on push/PR to master + weekly schedule).
- SpotBugs runs with findsecbugs 1.14.0; some visitors are omitted; failures ignored in reporting task (manual review advised).
- Avoid introducing insecure HTTP endpoints unless required by existing build constraints noted in build.gradle.
- Java security manager is disallowed in tests (`-Djava.security.manager=disallow`).

## Project-Specific "Don't Break" Items

- Java version and JavaFX module handling are intertwined across build.gradle and distribution tasks — changing one often requires adjusting tasks (run, test, JavaCompile, runtime/jpackage) and CI.
- The distribution relies on file layout in data/, outputsheets/, system/, preview/ — deletions or renames will break runtime validation in Main.validateEnvironment().
- GracefulExit should be used for controlled termination (tests hook the exit function).
- Module compilation: PCGen-base and PCGen-Formula jars are placed on `--module-path` while all other dependencies are merged into the pcgen module via `--patch-module` (jlink `forceMerge`). This means **no source file in the pcgen module may share a package with classes in PCGen-base or PCGen-Formula jars** (Java forbids split packages across modules). Currently conflicting packages (`pcgen.base.util`, `pcgen.base.format`) have been relocated to `pcgen.util` and `pcgen.format` respectively. If adding new classes whose package exists in either jar, place them in a non-overlapping package.
- Gradle configuration cache is enabled — ensure new tasks are compatible or explicitly opt out.
- The `testcommon` source set extends test configurations — changes to test dependencies are automatically available there.
- Release tag must match `gradle.properties` version exactly (CI validates this).
- Never re-attach a `Scene` loaded into a `JFXPanel` onto a standalone `Stage`. The embedded scene peer stays bound to the JFXPanel's host and the orphaned `EmbeddedScene` will eventually fire `setPixelScaleFactors` against a null `sceneState`, throwing an NPE in `GlassScene#updateSceneState` on macOS HiDPI displays. Use `PanelFromResource` for top-level dialogs and reserve `JFXPanelFromResource` for Swing embedding only.

## Maintainer/Issue Tracking Context

- Primary docs: README.md (development setup, essential Gradle tasks)
- Issue tracker: Jira at https://pcgenorg.atlassian.net (CODE/DATA/etc.) as referenced in README and docs/faqpages/faqsubmittingabugreport.md
- Community: Discord (https://discord.gg/M7GH5BS), Slack (by invitation)

---

_This file documents observed behavior and commands present in this repository as of the current state. It should be updated whenever significant changes are made — especially during LLM-assisted development sessions._
