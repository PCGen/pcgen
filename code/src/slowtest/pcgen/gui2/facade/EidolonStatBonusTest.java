/*
 * Copyright 2026 (C) PCGen contributors
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.gui2.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.DataSetFacade;
import pcgen.facade.core.UIDelegate;
import pcgen.facade.util.DefaultListFacade;
import pcgen.persistence.SourceFileLoader;
import pcgen.system.CharacterManager;
import pcgen.system.ConfigurationSettings;
import pcgen.system.Main;
import pcgen.system.PropertyContextFactory;
import pcgen.util.TestHelper;
import pcgen.util.chooser.ChooserFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * A serpentine APG eidolon (base Str 12, Dex 16) must get the eidolon table's Str/Dex bonus
 * on both stats, using the real Pathfinder data, for every summoner level from 1 to 20.
 * <p>
 * Regression: the Str bonus was dropped because a Str total read (and cached) while the
 * active bonus map was still being built was never refreshed once the formula-driven Str
 * bonus was added (see BonusManager.invalidateCachedSums).
 */
class EidolonStatBonusTest
{
	private static final String TEST_CONFIG_FILE = "config.ini.junit";

	private static final List<String> SOURCES = List.of("Core Rulebook", "Advanced Player's Guide");

	/** Str/Dex bonus by summoner level, index 0 = level 1, per the APG eidolon table. */
	private static final int[] EXPECTED_STR_DEX_BONUS =
			{0, 1, 1, 1, 2, 2, 3, 3, 3, 4, 4, 5, 5, 5, 6, 6, 7, 7, 7, 8};

	@TempDir
	static Path tempDir;

	private static DataSetFacade dataset;

	private static final UIDelegate UI = new MockUIDelegate()
	{
		@Override
		public boolean showWarningConfirm(String title, String message)
		{
			return true;
		}
	};

	@BeforeAll
	static void loadData() throws Exception
	{
		Path settingsDir = Files.createDirectories(tempDir.resolve("testsuite"));
		TestHelper.createDummySettingsFile(tempDir.resolve(TEST_CONFIG_FILE).toString(), settingsDir.toString(),
			TestHelper.findDataFolder());
		new PropertyContextFactory(tempDir.toString())
			.registerAndLoadPropertyContext(ConfigurationSettings.getInstance(TEST_CONFIG_FILE));
		Main.loadProperties(false);
		Main.runBootstrapTasks();
		ChooserFactory.setDelegate(UI);

		List<Campaign> camps = new ArrayList<>();
		for (Campaign c : Globals.getCampaignList())
		{
			if (SOURCES.contains(c.getKeyName()))
			{
				camps.add(c);
			}
		}
		SourceFileLoader loader = new SourceFileLoader(UI, new DefaultListFacade<>(camps), "Pathfinder_RPG");
		loader.run();
		dataset = loader.getDataSetFacade();
		assertNotNull(dataset);
	}

	static IntStream summonerLevels()
	{
		return IntStream.rangeClosed(1, 20);
	}

	@ParameterizedTest(name = "summoner level {0}")
	@MethodSource("summonerLevels")
	void serpentineEidolonGetsStrAndDexBonus(int summonerLevel)
	{
		CharacterFacadeImpl master = createSummoner("stat-master" + summonerLevel);
		levelSummonerTo(master, summonerLevel);
		CharacterFacadeImpl eidolon = createEidolon(master, "stat-eidolon" + summonerLevel);
		eidolon.addAbility(find(AbilityCategory.class, "Eidolon Selection"), internalAbility("Standard Eidolon"));
		eidolon.addAbility(find(AbilityCategory.class, "Eidolon Type Selection"),
			internalAbility("Eidolon Type ~ Serpentine"));

		PlayerCharacter pc = eidolon.getTheCharacter();
		int bonus = EXPECTED_STR_DEX_BONUS[summonerLevel - 1];
		String at = " at summoner level " + summonerLevel;
		assertEquals(12 + bonus, pc.getTotalStatFor(find(PCStat.class, "STR")), "Str" + at);
		assertEquals(16 + bonus, pc.getTotalStatFor(find(PCStat.class, "DEX")), "Dex" + at);
	}

	private static Ability internalAbility(String key)
	{
		Ability ability = Globals.getContext().getReferenceContext()
			.getManufacturerId(find(AbilityCategory.class, "Internal")).getActiveObject(key);
		assertNotNull(ability, key);
		return ability;
	}

	private static CharacterFacadeImpl createSummoner(String name)
	{
		CharacterFacadeImpl master = (CharacterFacadeImpl) CharacterManager.createNewCharacter(UI, dataset);
		master.getTheCharacter().setFileName(name + ".pcg");
		master.setRace(find(Race.class, "Human"));
		return master;
	}

	private static void levelSummonerTo(CharacterFacadeImpl master, int targetLevel)
	{
		PCClass summoner = find(PCClass.class, "Summoner");
		PlayerCharacter pc = master.getTheCharacter();
		while (summonerLevel(pc) < targetLevel)
		{
			master.addCharacterLevels(new PCClass[]{summoner});
			if (summonerLevel(pc) == 1)
			{
				// The level 1 class selection is an ability pool pick, not a chooser.
				Ability standard = Globals.getContext().getReferenceContext()
					.getManufacturerId(find(AbilityCategory.class, "Class"))
					.getActiveObject("Summoner ~ Standard Class");
				assertNotNull(standard, "Summoner ~ Standard Class");
				master.addAbility(find(AbilityCategory.class, "Summoner Class Selection"), standard);
			}
		}
		assertEquals(targetLevel, summonerLevel(pc));
	}

	private static int summonerLevel(PlayerCharacter pc)
	{
		PCClass cl = pc.getClassKeyed("Summoner");
		return (cl == null) ? 0 : pc.getLevel(cl);
	}

	private static CharacterFacadeImpl createEidolon(CharacterFacadeImpl master, String name)
	{
		CharacterFacade compFacade = CharacterManager.createNewCharacter(UI, dataset);
		CharacterFacadeImpl eidolon = (CharacterFacadeImpl) compFacade;
		eidolon.getTheCharacter().setFileName(name + ".pcg");
		eidolon.setRace(find(Race.class, "Eidolon"));
		master.getCompanionSupport().addCompanion(compFacade, "Eidolon");
		assertNotNull(eidolon.getTheCharacter().getDisplay().getMaster(), "eidolon was not linked to its summoner");
		return eidolon;
	}

	private static <T extends pcgen.cdom.base.Loadable> T find(Class<T> cl, String key)
	{
		T obj = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(cl, key);
		assertNotNull(obj, key);
		return obj;
	}
}
