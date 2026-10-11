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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.StreamSupport;

import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
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

}
