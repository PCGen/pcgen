/*
 * Copyright 2026 (C) Vest <Vest@users.noreply.github.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package pcgen.gui3.sources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import pcgen.core.Campaign;
import pcgen.core.campaign.CampaignAdditionPlanner;
import pcgen.core.campaign.CampaignDependencyOrder;
import pcgen.core.campaign.CampaignAvailability;
import pcgen.core.campaign.CampaignRejectionDiagnosis;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.persistence.CampaignFileLoader;
import pcgen.system.ConfigurationSettings;
import pcgen.system.FacadeFactory;
import pcgen.system.Main;
import pcgen.system.PCGenSettings;
import pcgen.system.PropertyContextFactory;
import pcgen.util.Logging;
import pcgen.util.TestHelper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * End-to-end tests for the Advanced tab's "add selected" action, run against
 * the real published campaign data.
 *
 * <p>These exercise the production path exactly as
 * {@code AdvancedSourceSelectionController.onAddSelectedAction} does:
 * {@link CampaignDependencyOrder} decides what to offer first, then
 * {@link CampaignAdditionPlanner} judges the set with the engine's own
 * {@code FacadeFactory.findUnqualified}.
 *
 * <p>The planner's unit tests use a simplified stand-in for the engine, and a
 * hand-built model of the Pathfinder conflicts was not enough to reproduce the
 * reported bug either: it took the real book list, with its real
 * {@code BOOKTYPE=} counting and {@code [self]} exclusions, to show "Core
 * Rulebook" being refused in favour of an edition nothing depends on. Loading
 * the data is slow, but a model that does not reproduce the defect is worse
 * than no test at all.
 */
class SourceSelectionDataTest
{
	/** The name of our dummy config file, matching the other data-driven tests. */
	private static final String TEST_CONFIG_FILE = "config.ini.junit";

	@TempDir
	static Path tempDir;

	@BeforeAll
	static void loadRealCampaignData()
	{
		String pccLoc = TestHelper.findDataFolder();
		Path settingsDir = tempDir.resolve("testsuite");
		Path configFile = tempDir.resolve(TEST_CONFIG_FILE);
		try
		{
			Files.createDirectories(settingsDir);
			TestHelper.createDummySettingsFile(configFile.toString(), settingsDir.toString(), pccLoc);
		}
		catch (IOException e)
		{
			Logging.errorPrint("SourceSelectionDataTest.loadRealCampaignData failed", e);
		}

		var configFactory = new PropertyContextFactory(tempDir.toString());
		configFactory.registerAndLoadPropertyContext(ConfigurationSettings.getInstance(TEST_CONFIG_FILE));
		SettingsHandler.setOutputDeprecationMessages(false);
		SettingsHandler.setInputUnconstructedMessages(false);
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(PCGenSettings.OPTION_ALLOW_OVERRIDE_DUPLICATES, true);
		Main.loadProperties(false);
		Main.runBootstrapTasks();
	}

	@Test
	void loadingCampaignMetadata_should_warnOnceAboutCaseOnlyReferences() throws Exception
	{
		Path folder = Files.createDirectory(tempDir.resolve("case-warning")).toRealPath();
		Files.writeString(folder.resolve("source.pcc"),
				"CAMPAIGN:Case Warning Source\nGAMEMODE:Pathfinder_RPG\n"
						+ "PRECAMPAIGN:1,INCLUDES=case warning target\n"
						+ "PRECAMPAIGN:1,INCLUDES=case warning target\n");
		Files.writeString(folder.resolve("target.pcc"),
				"CAMPAIGN:Case Warning Target Display\nKEY:Case Warning Target\nGAMEMODE:Pathfinder_RPG\n");

		var field = Globals.class.getDeclaredField("CAMPAIGN_MAP");
		field.setAccessible(true);
		@SuppressWarnings("unchecked")
		var campaigns = (Map<URI, Campaign>) field.get(null);
		var previousCampaigns = new HashMap<>(campaigns);
		List<LogRecord> warnings = new ArrayList<>();
		var handler = new Handler()
		{
			@Override
			public void publish(LogRecord record)
			{
				if (record.getLevel().equals(Logging.LST_WARNING))
				{
					warnings.add(record);
				}
			}

			@Override
			public void flush()
			{
			}

			@Override
			public void close()
			{
			}
		};
		Logger logger = Logger.getLogger(CampaignFileLoader.class.getName());
		Level previousLevel = logger.getLevel();
		logger.setLevel(Level.ALL);
		logger.addHandler(handler);
		try
		{
			campaigns.clear();
			var loader = new CampaignFileLoader();
			loader.setAlternateSourceFolder(folder.toFile());
			loader.run();
			assertEquals(2, Globals.getCampaignList().size(), "both fixture PCC files must load");
			assertTrue(FacadeFactory.findUnqualified(Globals.getCampaignList()).isEmpty(),
					"case-only spelling differences remain valid for prerequisite evaluation");
			CampaignDependencyOrder.mostDependedUponFirst(Globals.getCampaignList());
			CampaignDependencyOrder.mostDependedUponFirst(Globals.getCampaignList());
		}
		finally
		{
			logger.removeHandler(handler);
			logger.setLevel(previousLevel);
			campaigns.clear();
			campaigns.putAll(previousCampaigns);
		}

		assertEquals(1, warnings.size(), "report once per source/reference, not during repeated ordering");
		String message = warnings.getFirst().getMessage();
		assertTrue(message.contains("case warning target"), message);
		assertTrue(message.contains("Case Warning Target"), message);
		assertTrue(message.contains("source.pcc"), message);
	}

	/**
	 * Every campaign the Advanced tree would offer for Pathfinder.
	 *
	 * <p>{@code FacadeFactory.getSupportedCampaigns} is only populated from the
	 * GUI startup path, so the list is rebuilt here the same way that method
	 * builds it: a campaign is offered for every game mode it declares.
	 */
	private static List<Campaign> everyPathfinderBook()
	{
		GameMode pathfinder = SystemCollections.getGameModeNamed("Pathfinder_RPG");
		assertNotNull(pathfinder, () -> "the Pathfinder game mode must be present in the data; loaded modes: "
				+ SystemCollections.getUnmodifiableGameModeList().stream().map(GameMode::getName).toList());
		List<Campaign> available = Globals.getCampaignList().stream()
				.filter(campaign -> StreamSupport
						.stream(campaign.getGameModes().spliterator(), false)
						.anyMatch(pathfinder::equals))
				.toList();
		assertFalse(available.isEmpty(), "no Pathfinder campaigns were loaded");
		return available;
	}

	private static CampaignAdditionPlanner.Plan planSelectingEverything()
	{
		List<Campaign> available = everyPathfinderBook();
		return CampaignAdditionPlanner.plan(
				List.of(),
				CampaignDependencyOrder.mostDependedUponFirst(available),
				FacadeFactory::findUnqualified);
	}

	private static List<String> keys(List<Campaign> campaigns)
	{
		return campaigns.stream().map(Campaign::getKeyName).toList();
	}

	/**
	 * The reported bug. Selecting every Pathfinder book refused "Core Rulebook"
	 * -- and with it every book whose prerequisite reads
	 * {@code INCLUDES=Core Rulebook} -- while adding "Core Rulebook (PFS)",
	 * which nothing else depends on, in its place.
	 *
	 * <p>Selecting everything cannot succeed outright: the MultiSet bundles and
	 * the alternate editions deliberately exclude the individual books. What
	 * matters is which side of each conflict survives.
	 */
	@Test
	void addingEveryBook_should_keepTheCoreRulebook()
	{
		var plan = planSelectingEverything();

		assertTrue(keys(plan.accepted()).contains("Core Rulebook"),
				() -> "Core Rulebook must survive: a dozen books require it, and the alternate "
						+ "PFS edition requires nothing. Rejected: " + keys(plan.rejected()));
	}

	/**
	 * The consequence of keeping the Core Rulebook: the supplements that need
	 * nothing but it must come in too. These were collateral damage of losing
	 * the tie-break, not genuine conflicts.
	 */
	@Test
	void addingEveryBook_should_keepTheSupplementsThatOnlyNeedTheCoreRulebook()
	{
		var plan = planSelectingEverything();

		assertTrue(keys(plan.accepted()).contains("Advanced Player's Guide"),
				() -> "Advanced Player's Guide requires only the Core Rulebook. Rejected: "
						+ keys(plan.rejected()));
		assertTrue(keys(plan.accepted()).contains("Bestiary"),
				() -> "Bestiary requires only the Core Rulebook. Rejected: " + keys(plan.rejected()));
	}

	/**
	 * A book may only be refused because it genuinely conflicts with something
	 * that was accepted. Without this the tests above could be satisfied by a
	 * planner that accepted everything regardless of the prerequisites.
	 */
	@Test
	void addingEveryBook_should_produceASelectionTheEngineAccepts()
	{
		var plan = planSelectingEverything();

		assertTrue(FacadeFactory.findUnqualified(plan.accepted()).isEmpty(),
				() -> "the accepted set must itself qualify, but these do not: "
						+ keys(FacadeFactory.findUnqualified(plan.accepted())));
	}

	/**
	 * Pins down exactly which books may be refused, because the tests above
	 * would also pass for a planner that refused far too much. Every entry here
	 * is a deliberate mutual exclusion in the published data:
	 *
	 * <ul>
	 * <li>the three {@code BOOKTYPE:MultiSet} bundles repackage the individual
	 * books and exclude every Core Rules book and each other;</li>
	 * <li>each "Player Options Only" edition is a cut-down version of the full
	 * Bestiary it excludes;</li>
	 * <li>"Bonus Bestiary" is superseded by, and excludes, Bestiary 3;</li>
	 * <li>"DSP - Ultimate Psionics" replaces the two separate DSP psionics
	 * books and excludes them.</li>
	 * </ul>
	 *
	 * <p>Nothing else may be refused: anything more means a conflict was
	 * settled against the book that other books depend on.
	 */
	@Test
	void addingEveryBook_should_refuseNothingButTheDeliberateExclusions()
	{
		var plan = planSelectingEverything();

		assertEquals(
				List.of("Bestiary 2 ~ Player Options Only", "Bestiary 3 ~ Player Options Only",
						"Bestiary 4 ~ Player Options Only", "Bestiary ~ Player Options Only",
						"Bonus Bestiary", "DSP - Ultimate Psionics",
						"Pathfinder RPG Core Supplements", "Pathfinder RPG for Game Masters",
						"Pathfinder RPG for Players"),
				keys(plan.rejected()).stream().sorted().toList(),
				"only the data's deliberate mutual exclusions may be refused");
	}


	/** The collection the reporting user had left selected from an earlier session. */
	private static final String GM_COLLECTION = "Pathfinder RPG for Game Masters";

	/**
	 * Diagnoses the rejections produced by adding every book while
	 * {@code preSelected} is already selected, exactly as the Advanced tab
	 * does.
	 */
	private static List<CampaignRejectionDiagnosis.Diagnosis> diagnoseAddingEverythingWith(
			String... preSelectedKeys)
	{
		List<Campaign> available = everyPathfinderBook();
		List<String> wanted = List.of(preSelectedKeys);
		List<Campaign> preSelected = available.stream()
				.filter(campaign -> wanted.contains(campaign.getKeyName()))
				.toList();
		assertEquals(wanted.size(), preSelected.size(),
				() -> "the pre-selected books must exist in the data: " + wanted);
		List<Campaign> candidates = available.stream()
				.filter(campaign -> !preSelected.contains(campaign))
				.toList();
		var plan = CampaignAdditionPlanner.plan(preSelected,
				CampaignDependencyOrder.mostDependedUponFirst(candidates),
				FacadeFactory::findUnqualified);
		var selected = new ArrayList<>(preSelected);
		selected.addAll(plan.accepted());
		return CampaignRejectionDiagnosis.diagnose(plan.rejected(), selected,
				Campaign::getSubCampaigns, FacadeFactory::findUnqualified);
	}

	private static List<String> keysDiagnosedAs(List<CampaignRejectionDiagnosis.Diagnosis> diagnoses,
			Class<? extends CampaignRejectionDiagnosis.Reason> reason)
	{
		return diagnoses.stream()
				.filter(diagnosis -> reason.isInstance(diagnosis.reason()))
				.map(diagnosis -> diagnosis.campaign().getKeyName())
				.sorted()
				.toList();
	}

	/**
	 * The reported complaint. With the "Pathfinder RPG for Game Masters"
	 * collection left selected from an earlier session, adding every book
	 * refused fourteen of them, including the Core Rulebook and the Advanced
	 * Player's Guide. The planner is right to refuse them -- the collection
	 * carries a {@code !PRECAMPAIGN} against every book it repackages -- but
	 * the dialog could only quote each book's own requirements, which never
	 * mention the collection.
	 *
	 * <p>Ten of the fourteen are reachable through that collection's
	 * {@code PCC:} files, so they are already loaded and there is nothing for
	 * the user to do about them at all. Six are named in the collection
	 * directly; the reduced "Player Options Only" editions arrive one level
	 * deeper, because each full Bestiary loads its own reduced edition.
	 */
	@Test
	void addingEveryBook_whileACollectionIsSelected_should_reportItsOwnBooksAsAlreadyIncluded()
	{
		var diagnoses = diagnoseAddingEverythingWith(GM_COLLECTION);

		assertEquals(
				List.of("Advanced Class Guide", "Advanced Player's Guide", "Bestiary", "Bestiary 2",
						"Bestiary 2 ~ Player Options Only", "Bestiary 3",
						"Bestiary 3 ~ Player Options Only", "Bestiary 4 ~ Player Options Only",
						"Bestiary ~ Player Options Only", "Core Rulebook"),
				keysDiagnosedAs(diagnoses, CampaignRejectionDiagnosis.AlreadyIncluded.class),
				() -> "every book the selected collection loads must be reported as already included, "
						+ "not as a failure. Diagnosed: " + describe(diagnoses));
	}

	/**
	 * The rival collections conflict with the selected one, even when accepted
	 * additions introduce further blockers. Every MultiSet excludes the others.
	 */
	@Test
	void addingEveryBook_whileACollectionIsSelected_should_nameItAmongTheBlockers_forRivalCollections()
	{
		var diagnoses = diagnoseAddingEverythingWith(GM_COLLECTION);

		assertEquals(List.of("Pathfinder RPG Core Supplements", "Pathfinder RPG for Players"),
				keysDiagnosedAs(diagnoses, CampaignRejectionDiagnosis.ConflictsWith.class),
				() -> "Diagnosed: " + describe(diagnoses));
		diagnoses.stream()
				.filter(diagnosis -> diagnosis.reason() instanceof CampaignRejectionDiagnosis.ConflictsWith)
				.forEach(diagnosis -> assertTrue(
						((CampaignRejectionDiagnosis.ConflictsWith) diagnosis.reason()).blockers().stream()
								.map(Campaign::getKeyName).toList().contains(GM_COLLECTION),
						() -> "the selected collection is the obstacle for "
								+ diagnosis.campaign().getKeyName()));
	}

	/**
	 * What is left are the data's deliberate exclusions, which stand whatever
	 * the user has selected: a Bonus Bestiary superseded by Bestiary 3, and a
	 * Psionics edition that supersedes two others. Neither is the selected
	 * collection's doing, and neither may be blamed on it.
	 */
	@Test
	void addingEveryBook_whileACollectionIsSelected_should_leaveTheDataExclusionsUnmet()
	{
		var diagnoses = diagnoseAddingEverythingWith(GM_COLLECTION);

		assertEquals(List.of("Bonus Bestiary", "DSP - Ultimate Psionics"),
				keysDiagnosedAs(diagnoses, CampaignRejectionDiagnosis.Unmet.class),
				() -> "Diagnosed: " + describe(diagnoses));
	}

	/** Newly accepted books form the context for diagnosing the remaining refusals. */
	@Test
	void addingEveryBook_fromAnEmptySelection_should_reportTheReducedEditionsAsAlreadyIncluded()
	{
		var plan = planSelectingEverything();

		var diagnoses = CampaignRejectionDiagnosis.diagnose(plan.rejected(), plan.accepted(),
				Campaign::getSubCampaigns, FacadeFactory::findUnqualified);

		assertEquals(List.of("Bestiary 2 ~ Player Options Only", "Bestiary 3 ~ Player Options Only",
						"Bestiary 4 ~ Player Options Only", "Bestiary ~ Player Options Only"),
				keysDiagnosedAs(diagnoses, CampaignRejectionDiagnosis.AlreadyIncluded.class),
				() -> "Diagnosed: " + describe(diagnoses));
	}

	private static String describe(List<CampaignRejectionDiagnosis.Diagnosis> diagnoses)
	{
		return diagnoses.stream()
				.map(diagnosis -> diagnosis.campaign().getKeyName() + " -> " + diagnosis.reason())
				.toList()
				.toString();
	}

	private static Map<CampaignAvailability.Status, List<String>> availabilityWith(String... selectedKeys)
	{
		List<Campaign> available = everyPathfinderBook();
		List<String> wanted = List.of(selectedKeys);
		List<Campaign> selected = available.stream()
				.filter(campaign -> wanted.contains(campaign.getKeyName()))
				.toList();
		assertEquals(wanted.size(), selected.size(), () -> "the selected books must exist: " + wanted);
		return CampaignAvailability
				.classify(available, selected, Campaign::getSubCampaigns, FacadeFactory::findUnqualified)
				.entrySet().stream()
				.collect(Collectors.groupingBy(Map.Entry::getValue,
						Collectors.mapping(entry -> entry.getKey().getKeyName(),
								Collectors.collectingAndThen(Collectors.toList(),
										keys -> keys.stream().sorted().toList()))));
	}

	/** An individual supplement is blocked until its prerequisite is selected. */
	@Test
	void withNothingSelected_coreIsAddableButSupplementsNeedTheirPrerequisites()
	{
		var byStatus = availabilityWith();

		assertEquals(List.of(), byStatus.getOrDefault(CampaignAvailability.Status.ALREADY_INCLUDED, List.of()),
				"nothing is selected, so nothing can be loaded through a selection");
		assertTrue(byStatus.get(CampaignAvailability.Status.ADDABLE).contains("Core Rulebook"));
		assertTrue(byStatus.get(CampaignAvailability.Status.BLOCKED).contains("Advanced Player's Guide"));
	}

	@Test
	void afterRemovingCore_otherBooksMissingCoreRemainBlocked()
	{
		var byStatus = availabilityWith("Advanced Player's Guide");

		assertTrue(byStatus.get(CampaignAvailability.Status.BLOCKED).contains("Bestiary"),
				"a broken selection must not hide another book's missing prerequisite");
		assertTrue(byStatus.get(CampaignAvailability.Status.ADDABLE).contains("Core Rulebook"),
				"the book that repairs the selection must remain addable");
	}

	/**
	 * The reported scenario. Ten of the books the collection loads must be
	 * shown as already loaded rather than as failures -- that distinction is
	 * the whole reason this is not simply Swing's single red colour.
	 */
	@Test
	void withACollectionSelected_itsOwnBooksAreOfferedAsAlreadyIncluded()
	{
		var byStatus = availabilityWith(GM_COLLECTION);

		assertTrue(byStatus.get(CampaignAvailability.Status.ALREADY_INCLUDED)
						.containsAll(List.of("Advanced Player's Guide", "Bestiary", "Core Rulebook",
								"Bestiary ~ Player Options Only")),
				() -> "Already included: " + byStatus.get(CampaignAvailability.Status.ALREADY_INCLUDED));
		assertTrue(byStatus.get(CampaignAvailability.Status.ALREADY_INCLUDED).contains(GM_COLLECTION),
				"the collection itself is selected, so it is loaded too");
	}

	/**
	 * The rival collections are the ones genuinely in the way, and the only
	 * ones that should carry the failure colour from this selection.
	 */
	@Test
	void withACollectionSelected_rivalCollectionsAreOfferedAsBlocked()
	{
		var byStatus = availabilityWith(GM_COLLECTION);

		assertTrue(byStatus.get(CampaignAvailability.Status.BLOCKED)
						.containsAll(List.of("Pathfinder RPG Core Supplements", "Pathfinder RPG for Players")),
				() -> "Blocked: " + byStatus.get(CampaignAvailability.Status.BLOCKED));
	}

	/**
	 * Swing marked every book it could not add with one colour. Showing the
	 * books a selected collection already loads in that colour is what this
	 * replaces, so the two counts must genuinely differ.
	 */
	@Test
	void withACollectionSelected_fewerBooksAreBlockedThanSwingWouldHaveMarked()
	{
		var byStatus = availabilityWith(GM_COLLECTION);
		int blocked = byStatus.getOrDefault(CampaignAvailability.Status.BLOCKED, List.of()).size();
		int alreadyIncluded =
				byStatus.getOrDefault(CampaignAvailability.Status.ALREADY_INCLUDED, List.of()).size();

		assertTrue(alreadyIncluded > 0 && blocked > 0,
				() -> "blocked=" + blocked + " alreadyIncluded=" + alreadyIncluded);
		assertTrue(alreadyIncluded >= 10,
				() -> "the collection loads at least ten of the offered books; alreadyIncluded="
						+ alreadyIncluded);
		List<Campaign> available = everyPathfinderBook();
		Campaign collection = available.stream()
				.filter(campaign -> GM_COLLECTION.equals(campaign.getKeyName()))
				.findFirst().orElseThrow();
		long swingBlocked = available.stream()
				.filter(campaign -> !FacadeFactory.passesPrereqs(List.of(collection, campaign)))
				.count();
		assertTrue(blocked < swingBlocked,
				() -> "gui3 blocked=" + blocked + ", Swing marked=" + swingBlocked);
	}
}
