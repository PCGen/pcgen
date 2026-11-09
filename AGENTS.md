# Working in PCGen

Update this guide only when workflow, architecture, or non-obvious constraints change.
Keep implementation details, individual test descriptions, and change history in code or Git.
See [README.md](README.md) for development setup and the general build workflow.

## Layout and commands

PCGen is a Java desktop character generator; its entry point is `pcgen.system.Main`.
Use Java 25 (Temurin) and `./gradlew`. Java and JavaFX versions are configured in `gradle.properties`.

| Path | Purpose |
|---|---|
| `code/src/java` | Production Java: `pcgen.gui2` is Swing, `pcgen.gui3` is JavaFX |
| `code/src/resources` | FXML, templates, language bundles, and application resources |
| `code/src/test`, `code/src/itest`, `code/src/slowtest` | Unit, integration, and data/character tests |
| `code/src/testcommon`, `code/src/testResources` | Shared test support and resources |
| `code/gradle`, `code/standards` | Build tasks and static-analysis configuration |
| `base`, `formula` | Separate builds used as module dependencies |

| Command | Purpose |
|---|---|
| `./gradlew build` | Compile and run unit tests |
| `./gradlew :test --tests 'pcgen.gui3.sources.*'` | Targeted root-project tests |
| `./gradlew :itest` | Integration tests |
| `./gradlew :slowtest` | Data/bootstrap and character tests; also runs `:itest` |
| `./gradlew :slowtest --tests 'pcgen.gui3.sources.SourceSelectionDataTest' -x :itest` | Published-data selection scenarios only |
| `./gradlew datatest` | Data loading, encoding, and validation |
| `./gradlew testCoverage` | JaCoCo report under `build/reports/jacoco/testCoverage/html` |
| `./gradlew allReports` | Checkstyle, PMD, and SpotBugs |
| `./gradlew run` | Run with the required JavaFX modules |
| `./gradlew run --args="--name-generator"` | Run the name generator |
| `./gradlew qbuild` | Development distribution under `output` |
| `./gradlew fullJpackage` | Host-platform app image and installer |

Use a leading colon for targeted root-project tests; otherwise Gradle also selects subproject tasks
and can fail with "No tests found." Add `-x :itest` when running an isolated slow-test scenario.
Use `./gradlew tasks` for other packaging and per-game integration tasks.

`clean` also removes generated plugins, output, downloaded JDKs/modules, and master sheets.
Do not clean, rename, or rebuild a checkout while its application is running:
`./gradlew run` loads exploded classes from `build/classes/java/main`.

## Coding conventions

- Follow existing formatting and localization. Use American English in new Javadoc.
  Document nontrivial helpers and FXML handlers with concise contracts.
- Validate user input at the UI boundary; trust established internal contracts.
  Keep checks for real cancellation, missing data, lifecycle states, and demonstrated cost.
- Use `pcgen.util.Logging`; `Logging.errorPrint` reports severe errors.
  Do not swallow failures or replace mandatory callbacks with no-ops.
- Use `GracefulExit.exit`, not `System.exit`. `Main.shutdown` must isolate cleanup steps and exit
  from `finally`; use lambdas inside guards because bound method references can fail before entering them.
- Create directory trees with `Files.createDirectories` or `mkdirs`. Reuse `PCGenSettings.USER_DIR_NAME`
  for user-facing paths and preserve Linux's configured documents directory for character saves.
- `ConfigurationSettings.findInstallRoot` checks bounded ancestors and their `app` subdirectory only.
  Resolve installation-relative assets through that root, including relative `INFOSHEET` paths.

## Swing and JavaFX

- Construct and mutate JavaFX UI on the FX thread. Facade mutations observed by Swing models run
  on the EDT; observable FX rows update back on FX. Run archive I/O on workers.
- Use `PanelFromResource` for standalone FXML stages and `JFXPanelFromResource` for Swing embedding.
  Never move an embedded scene onto a standalone stage; this corrupts its peer on macOS HiDPI.
  Reuse `ConvertedJavaFXPanel` for embedded preference forms.
- JavaFX modality does not disable Swing windows. Source selection and installation share an
  EDT-owned launch flag and must restore their disabled Swing owner even if initialization fails.
- Give nested dialogs an FX owner. `DialogPane` buttons dismiss the dialog unless their action is
  consumed; validate before dismissal and keep actions that leave the dialog open outside its button bar.
  Use `ButtonData` for platform placement and default/cancel semantics.
- Attach double-click actions to populated rows so headers and empty space cannot activate stale selections.
  Recycled cells must reset text, graphics, colors, and tooltips; restore configured colors, not `null`.
  Read replaceable maps through the current field or supplier, not a captured map.
- Language-bundle HTML requires an HTML-capable view; use plain-text sibling keys for plain-text controls.
  HTML attributes in `MessageFormat` patterns need double quotes.
- Source-info and alert markup belongs in FreeMarker templates. Pass plain text to HTML auto-escaping;
  reserve `?no_esc` for intentional HTML. Outside templates, use `pcgen.util.Html` for text and
  double-quoted attributes; URLs, JavaScript, and CSS need their own validation/escaping.
- Each preview owns and closes its `SourceInfoRenderer`; late callbacks must not reopen disposed views.
  Render all campaigns in a source bundle.
  Source HTML needs a `file:` origin for local images and a private temporary directory.
  Intercept links before WebView navigation and send only `http`/`https` URLs to the browser.

## Campaigns and data packages

- Campaign selection rules belong in `pcgen.core.campaign`, independent of JavaFX and startup state.
  Qualify dependencies together with selected books and compare new failures against the existing baseline.
  Preserve selected campaigns.
- `Campaign.equals` compares keys case-insensitively, but `PObject` lacks a matching hash code.
  Use lists for membership/deduplication; preserve encounter order and the first candidate per key.
  Dependency priority uses exact campaign keys, while qualification remains case-insensitive.
- Use `Collections.emptyList()` with a regular import in the campaign package and tests; populated `List.of(...)` is fine.
- Qualification temporarily replaces chosen URIs. The persistence getter exposes a live list:
  snapshot it before a trial selection and restore it in `finally`.
- Sub-campaigns can nest and cycle. Conflict checks must account for transitive inclusion;
  bundles and alternate editions can deliberately exclude books.
- Batch selection edits and compute availability once per change, not during cell rendering.
  Preserve the empty-offered early return to avoid expensive baseline qualification.
- Match saved source names exactly; bundled names cannot be overwritten. Validate names and campaigns
  at the UI boundary, and update rows only after successful persistence.
- Advanced selection memory is per game mode. Restoring Basic's highlight must not overwrite it,
  and unload cancellation must preserve edits. `SOURCE_USE_BASIC_KEY` is the inverse of "always use advanced."
- `DataPackageInstaller` owns archive inspection, planning, and extraction; `DataInstallerDialog` owns
  the FXML workflow. Nonstandard entries and overwrites require confirmation before extraction.
- Validate all archive paths against canonical destination roots during planning and extraction.
  Normalize backslashes before recognizing ZIP directories, and create parents when the ZIP omits them.
  Nonstandard entries resolve against the installation root, not the process directory.
- Launch installation through `PCGenFrame.showDataInstaller` so both entry points share the lifecycle guard.
  Installing files does not refresh the loaded campaign catalog.

## Testing

- Use the source sets above. Jupiter and Vintage coexist; reuse existing TestFX/Monocle setup.
  Tests of the real Swing/FX bridge need an AWT display and must skip headless runs.
- `ApplicationExtension` does not run test methods on FX. Use statically imported `asyncFx` helpers
  for UI work and keep cold domain initialization outside tightly timed FX callbacks.
- Show WebViews in a window for layout measurements. Wait for load completion and the expected
  document rather than sleeping; snapshot content without reparenting a dialog pane.
- Assert observable behavior with discriminating fixtures. Test event consumption at the parent:
  dispatch can copy events, so the original event's `isConsumed()` is unreliable.
- Initialize and restore `FacadeFactory` campaign maps outside GUI startup. Restore global game modes,
  preferences, persistence state, and exit callbacks changed by tests.
- Installer tests register and restore only required `InstallLstToken` handlers; global plugin bootstrap
  pollutes formula-function tests sharing the JVM.
- Keep published-data scenarios using the real prerequisite engine: simplified fakes miss book-type
  counting and self-exclusions. Share expensive planning results across assertions.
- Keep every `.lst` and `.pcc` under `data` and `system` as inputs to `datatest` and `slowtest`,
  so data-only edits trigger encoding validation.

## Build and release constraints

- Keep Java/JavaFX changes consistent across compilation, tests, run tasks, jlink, packaging, and CI.
- Gradle configuration caching is enabled; incompatible tasks must declare
  `notCompatibleWithConfigurationCache(...)`. Do not change `allowInsecureProtocol` settings incidentally.
- Avoid split packages with base and formula: those jars are separate modules;
  other dependencies are merged into the application module for jlink.
- Preserve the runtime layout of `data`, `system`, `outputsheets`, `preview`, `vendordata`, and `homebrewdata`.
  See `code/gradle/distribution.gradle` for packaging and `code/gradle/plugins.gradle` for plugin assembly.
- Release tags must match the version in `gradle.properties`. Installer filenames include the full
  application version, but jpackage's internal version must remain numeric.
- Windows MSI versions use major/minor plus `windowsBuildNumber` (default 0). Release workflows pass
  `git rev-list --count HEAD`, so build checkouts need `fetch-depth: 0`; rewriting `master` can lower it.
- The nightly success marker advances only after all platforms succeed; a dated release tag alone
  does not prove success. CI patches version/date resources without committing them.
  Release implementation lives in `code/gradle/release.gradle` and `.github/workflows`.
