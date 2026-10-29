# Working in PCGen

Update this guide when build commands, architecture, or non-obvious constraints change.
Keep it concise: method signatures, implementation history, and routine edits belong in code or Git history.

## Project and layout

PCGen is a Java desktop character generator. Use the Gradle wrapper and Java 25 (Temurin);
JavaFX must match the configured Java version. The entry point is `pcgen.system.Main`.

| Path | Purpose |
|---|---|
| `code/src/java` | Production Java, including `pcgen.gui2` (Swing) and `pcgen.gui3` (JavaFX) |
| `code/src/resources` | FXML, templates, language bundles, and application resources |
| `code/src/test` | Fast unit tests |
| `code/src/itest` | Integration tests |
| `code/src/slowtest` | Data/bootstrap and character integration tests |
| `code/src/testcommon`, `code/src/testResources` | Shared test support and resources |
| `code/gradle` | Distribution, release, plugin, reporting, and benchmark tasks |
| `code/standards` | Checkstyle, PMD, and SpotBugs configuration |
| `PCGen-base`, `PCGen-Formula` | Separate builds used as module dependencies |
| `data`, `system`, `outputsheets`, `preview`, `vendordata`, `homebrewdata` | Runtime assets; preserve their distribution layout |
| `docs`, `installers`, `.github/workflows` | User documentation, packaging assets, and CI |

## Commands

Always use `./gradlew`. Use a leading colon for targeted root-project tests;
otherwise Gradle also selects subproject test tasks, which can fail with "No tests found."

| Command | Purpose |
|---|---|
| `./gradlew build` | Compile and run unit tests |
| `./gradlew :test --tests 'pcgen.gui3.sources.*'` | Targeted unit tests |
| `./gradlew :itest` | Integration tests |
| `./gradlew :slowtest` | Slow tests; also depends on integration tests |
| `./gradlew :slowtest --tests 'pcgen.gui3.sources.SourceSelectionDataTest' -x :itest` | Published-data selection scenarios only |
| `./gradlew datatest` | Data loading, UTF-8 checks, and validation |
| `./gradlew pfinttest` | Pathfinder character integration tests |
| `./gradlew inttest` | All character integration variants |
| `./gradlew testCoverage` | JaCoCo report under `build/reports/jacoco/testCoverage/html` |
| `./gradlew allReports` | Checkstyle, PMD, and SpotBugs |
| `./gradlew run` | Run with the required JavaFX modules |
| `./gradlew run --args="--name-generator"` | Run the name generator |
| `./gradlew qbuild` | Development distribution under `output` |
| `./gradlew buildDist` | Distribution archives |
| `./gradlew fullJpackage` | Host-platform app image and installer |

Per-game integration tasks include `sfinttest`, `rsrdinttest`, `srdinttest`, and `msrdinttest`.
`clean` also removes generated plugins, output, downloaded JDKs/modules, and master sheets.

CLI options and validation live in `code/src/java/pcgen/system/CommandLineArguments.java`.
Batch export accepts `--character`, `--exportsheet`, `--outputfile`, and `--configfilename`;
see `PcgenFtlTestCase` for working examples.

## Coding conventions

- Follow existing formatting and localization. Use American English in new Javadoc.
  Document nontrivial helpers and FXML handlers with concise contracts, not line-by-line explanations.
- Reuse `pcgen.util.Html` for escaping HTML text and double-quoted attribute values;
  element-text escaping is not sufficient for attributes, URLs, JavaScript, or CSS.
- Validate user input at the UI boundary; trust established internal contracts.
  Keep checks for real cancellation, missing data, lifecycle states, and demonstrated cost.
- Use `pcgen.util.Logging`; `Logging.errorPrint` reports severe errors.
  Do not swallow failures or replace mandatory callbacks with no-ops.
- Use `GracefulExit.exit`, not `System.exit`. Tests can save and restore its exit function.
- Create directory trees with `Files.createDirectories` or `mkdirs`.
  User-facing paths use `PCGenSettings.USER_DIR_NAME`; the default character directory also honors
  Linux's configured documents directory.
- Gradle configuration caching is enabled. Incompatible tasks must declare
  `notCompatibleWithConfigurationCache(...)`.
- Do not introduce split packages with PCGen-base or PCGen-Formula: those jars are separate modules.
  Other dependencies are merged into the application module for jlink.
- Some dependency repositories require `allowInsecureProtocol`; do not change them incidentally.

## Swing and JavaFX integration

- Construct and mutate JavaFX controls, dialogs, and windows on the FX thread.
  Facade mutations observed by Swing models run on the EDT; observable FX rows update back on the FX thread.
- Use `PanelFromResource` for standalone FXML stages and `JFXPanelFromResource` for Swing embedding.
  Never move a `JFXPanel`'s scene onto a standalone stage; this corrupts the embedded peer on macOS HiDPI.
- JavaFX modality does not disable Swing windows. The source selector blocks its Swing owner and duplicate
  launches until it closes, restoring the owner even if dialog initialization fails.
- Give nested dialogs an FX owner. Keep tab-specific actions outside `DialogPane`'s button bar:
  buttons created there dismiss the dialog unless their action is consumed.
  Load validation must happen before dismissal, including Basic-row double-clicks.
- Use `ButtonData` for platform placement and default/cancel semantics, and `SourceDialogOutcome` to
  distinguish dialog results. Install Data is identified by its own button instance.
  Tests should verify those semantics without fixing a platform's visual button order.
- Attach double-click actions to populated campaign rows so headers and empty space cannot activate
  a previous selection. Batch selection edits to avoid repeating full availability calculations.
- Language-bundle HTML requires an HTML-capable view, not `Alert.setContentText`.
  Preserve meaningful prerequisite markup. Use plain-text sibling keys for plain-text controls;
  HTML attributes in `MessageFormat` patterns need double quotes.
- Source-info and alert HTML/CSS live in FreeMarker templates. Pass plain text unescaped and let HTML
  auto-escaping handle it; reserve `?no_esc` for intentional HTML fragments. The i18n scanner also scans `.ftl` files.
- Recycled cells must reset text, graphics, colors, and tooltips. Restore configured qualified colors,
  never `setTextFill(null)`. Read replaceable maps through the current field or supplier, not a captured map.

## Campaign and source selection

- Selection rules belong in `pcgen.core.campaign`, independent of JavaFX and global startup state.
  Qualification is a whole-set operation: dependent books must be evaluated with their prerequisites.
- `CampaignSelection` compares new failures against the existing baseline. Preserve selected campaigns;
  existing failures must not hide a candidate's additional failure.
- `Campaign.equals` compares keys case-insensitively, but `PObject` lacks a matching key-based hash code.
  Membership and deduplication therefore use ordinary lists, not hash sets or `distinct()`.
  Preserve encounter order and the first candidate for each key; the planner's stateful stream is sequential.
- Use `Collections.emptyList()` with a regular import for empty lists in the campaign package and tests;
  populated `List.of(...)` is fine.
- Qualification temporarily replaces the persistence manager's chosen URIs. Its getter exposes a live list:
  snapshot it before installing a trial selection, and restore it in `finally`.
- Dependency priority uses exact campaign keys, not display names or lowercase normalization.
  Qualification remains case-insensitive; case-only positive references are warned about after metadata loading,
  not during rendering or qualification.
- Sub-campaigns can nest and cycle. Check direct selection and transitive inclusion before reporting conflicts.
  Bundles and alternate editions deliberately exclude books, so accepting every published source is not possible.
- Compute availability and explanations once per selection change, not from cell rendering.
  Keep the empty-offered early return: it avoids expensive baseline qualification.
- Available publisher/setting groups start collapsed. Expand search results and selected-source groups
  so matching and chosen books remain visible; clearing the search restores the collapsed catalog.
- Saving validates nonempty campaigns and nonblank names at the UI boundary.
  Existing custom names reuse their facade; bundled names cannot be overwritten.
  Match persistence names exactly, not display-name fallbacks, and update rows only after successful persistence.
- Advanced selection memory is per mode in the `advancedSourceSelectionPanel` UI context.
  Restoring Basic's highlight must not replace Advanced's remembered list; later Basic selections still project.
  `SOURCE_USE_BASIC_KEY` is the inverse of "always use advanced."
  Unload cancellation must not erase the edited selection.

## Source previews

- Each Basic/Advanced preview owns one `SourceInfoRenderer` and closes it when the dialog hides.
  Basic selections can contain multiple independent campaigns; render and reload the entire bundle.
  Late persistence callbacks must not reopen a disposed preview.
- Source HTML uses a real `file:` origin for local images. Cache its document inside a private NIO temp
  directory, not directly in a shared temp directory. Register directory deletion before file deletion.
  Reused channels must truncate before rewriting; skip identical documents.
- Intercept source-info links before WebView navigation, and hand only `http`/`https` URLs to the browser.

## Testing

- Use the repository's source sets, not Maven-standard paths. JUnit Jupiter and legacy Vintage coexist.
  Existing TestFX/Monocle settings provide headless JavaFX; do not add a second toolkit.
- `ApplicationExtension` does not run test methods on the FX thread. Use statically imported `asyncFx`
  helpers for UI work. Keep cold domain initialization outside tightly timed FX callbacks.
- A WebView must belong to a shown window for meaningful layout measurements.
  Wait for load completion and the expected regenerated document, not a fixed sleep.
  Snapshot the content node without reparenting a dialog pane.
- Assert observable behavior with discriminating fixtures: distinct campaign keys, contrasting configured
  colors, populated search results, and replacement maps. Test event propagation at the parent;
  dispatch may copy events, so the original event's `isConsumed()` is not reliable evidence.
- Initialize and restore `FacadeFactory` campaign maps when testing real controllers outside GUI startup.
  Restore game modes, preferences, and persistence state changed by tests.
- `SourceSelectionDataTest` tests published-data helpers with the real prerequisite engine, not controller actions.
  Keep these scenarios: simplified fakes do not reproduce book-type counting and self-exclusions.
  Share one planning result per expensive scenario rather than repeating the same work for each assertion.
- `DataEncodingTest` checks every `.lst` and `.pcc` under `data` and `system`; keep these files as inputs
  to `datatest` and `slowtest` so data-only edits trigger validation.

## Build and release constraints

- `gradle.properties` defines the application version. Release tags must match it exactly.
  Java/JavaFX changes must stay consistent across compilation, tests, run tasks, jlink, packaging, and CI.
- Plugin jars are assembled from compiled classes by `code/gradle/plugins.gradle`.
  Distribution layout is controlled by `code/gradle/distribution.gradle`.
- `code/gradle/release.gradle` and `releaseUtils.groovy` implement release preparation and version bumps.
  Native installers copied to `build/release` use `pcgen-<version>-<hostOs>-<hostArch>.<ext>`;
  jpackage's internal version remains numeric.
- Runtime/JDK/JavaFX downloads target the host platform. CI builds Linux x64/ARM, macOS, and Windows.
- `.github/workflows/gradle-test.yml` runs build, integration/data/slow tests, then coverage.
  The tag-release workflow validates tags; the manual-release workflow commits the release version,
  builds, then bumps to the next snapshot only after success.
- The nightly workflow compares master with the moving `nightly-last-success` tag, updated only after
  all platforms build and upload successfully. Dated tags alone do not prove success. Same-commit retries
  reuse the dated tag; different commits wait until the next day. It retains seven nightly releases.
  CI patches version/date resources during builds without committing them.

## Other non-obvious constraints

- `Main.shutdown` must isolate cleanup steps and exit from `finally`.
  Use lambdas inside its guards: bound method references can fail before entering the guard.
- `ConfigurationSettings.findInstallRoot` only checks bounded ancestors and their `app` subdirectory.
  Do not scan unrelated child directories while searching for an installation.
- Relative `INFOSHEET` paths are installation-root-relative, not working-directory-relative.
  `HtmlSheetSupport` resolves them through `ConfigurationSettings.getInstallRoot()`; absolute custom sheets stay absolute.
- `./gradlew run` uses exploded classes under `build/classes/java/main`.
  Do not clean, rename, or rebuild the checkout underneath a running application.

See `README.md` for general development setup and community links.
