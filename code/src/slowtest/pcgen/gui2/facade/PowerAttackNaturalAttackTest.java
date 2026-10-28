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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Campaign;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.ChooserFacade;
import pcgen.facade.core.DataSetFacade;
import pcgen.facade.core.InfoFacade;
import pcgen.facade.core.UIDelegate;
import pcgen.facade.util.DefaultListFacade;
import pcgen.io.PCGVer2Creator;
import pcgen.persistence.SourceFileLoader;
import pcgen.system.CharacterManager;
import pcgen.system.ConfigurationSettings;
import pcgen.system.Main;
import pcgen.system.PropertyContextFactory;
import pcgen.util.TestHelper;
import pcgen.util.chooser.ChooserFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Power Attack on natural attacks, using the real Pathfinder data and the same code path as the
 * Temporary Bonuses tab. A 7th-level summoner's eidolon has BAB +6, so Power Attack is -2 to hit
 * and +4 damage: +6 (x1.5) on a primary natural attack that adds 1-1/2 times Strength (the eidolon
 * Improved Bite evolution, a dragon disciple's Dragon Bite, or a creature's only natural attack such as the
 * wolf's bite) and +2 (halved) on a secondary natural attack.
 */
class PowerAttackNaturalAttackTest
{
	private static final String TEST_CONFIG_FILE = "config.ini.junit";

	private static final List<String> SOURCES = List.of("Core Rulebook", "Advanced Player's Guide");

	private static final String PA_PRIMARY = "Power Attack (Natural Primary)";
	private static final String PA_PRIMARY_STR = "Power Attack (Natural Primary - 1.5x Str)";
	private static final String PA_SECONDARY = "Power Attack (Natural Secondary)";

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

		@Override
		public boolean showGeneralChooser(ChooserFacade chooser)
		{
			// The dragon disciple needs the Draconic bloodline; any other chooser takes the first choice.
			for (int i = 0; i < chooser.getAvailableList().getSize(); i++)
			{
				InfoFacade item = chooser.getAvailableList().getElementAt(i);
				if (item.toString().startsWith("Draconic"))
				{
					chooser.addSelected(item);
					chooser.commit();
					return true;
				}
			}
			return super.showGeneralChooser(chooser);
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

	@Test
	void primaryNaturalAttackGetsFullDamage()
	{
		PlayerCharacter pc = createSerpentineEidolonWithPowerAttack("pa-primary", false).getTheCharacter();

		assertEquals(List.of("Bite"), targetNames(pc, PA_PRIMARY));
		assertPowerAttack(pc, PA_PRIMARY, "Bite", -2, 4);
	}

	@Test
	void secondaryNaturalAttackGetsHalfDamage()
	{
		PlayerCharacter pc = createSerpentineEidolonWithPowerAttack("pa-secondary", false).getTheCharacter();

		assertEquals(List.of("Tail Slap"), targetNames(pc, PA_SECONDARY));
		assertPowerAttack(pc, PA_SECONDARY, "Tail Slap", -2, 2);
	}

	@Test
	void improvedBiteGetsOneAndAHalfDamage()
	{
		PlayerCharacter pc = createSerpentineEidolonWithPowerAttack("pa-improved", true).getTheCharacter();

		assertTrue(hasAbility(pc, PA_PRIMARY_STR), "1.5x Str variant granted with Improved Bite");
		assertEquals(List.of("Bite"), targetNames(pc, PA_PRIMARY_STR));
		assertPowerAttack(pc, PA_PRIMARY_STR, "Bite", -2, 6);
	}

	@Test
	void singleNaturalAttackGetsOneAndAHalfDamage()
	{
		// Core Rulebook wolf companion: a bite as its only natural attack, 2 Companion HD (BAB +1),
		// so Power Attack is -1 to hit and +2 damage, +3 with the 1-1/2 Strength bite.
		CharacterFacadeImpl wolf = (CharacterFacadeImpl) CharacterManager.createNewCharacter(UI, dataset);
		wolf.getTheCharacter().setFileName("pa-wolf.pcg");
		wolf.setRace(find(Race.class, "Companion (Wolf)"));
		wolf.setScoreBase(find(PCStat.class, "STR"), 14);
		PlayerCharacter pc = wolf.getTheCharacter();
		// Without a master the companion has no feat slots; grant one as a bonus feat would.
		pc.adjustAbilities(AbilityCategory.FEAT, BigDecimal.ONE);
		wolf.addAbility(AbilityCategory.FEAT, feat("Power Attack"));
		pc.calcActiveBonuses();
		assertTrue(hasAbility(pc, "Power Attack"), "Power Attack taken");
		assertEquals(1, pc.baseAttackBonus(), "wolf BAB");

		assertTrue(hasAbility(pc, PA_PRIMARY_STR), "1.5x Str variant granted for a single natural attack");
		assertPowerAttack(pc, PA_PRIMARY, "Bite", -1, 2);
		assertPowerAttack(pc, PA_PRIMARY_STR, "Bite", -1, 3);
	}

	@Test
	void dragonDiscipleBiteGetsOneAndAHalfDamage()
	{
		// Dragon Bite adds 1-1/2 times Strength although it comes with the Draconic bloodline's two claws.
		// Sorcerer 1 (claws) + Dragon Disciple 2 (bite) has BAB +1, so Power Attack is -1 to hit and +3.
		CharacterFacadeImpl disciple = (CharacterFacadeImpl) CharacterManager.createNewCharacter(UI, dataset);
		disciple.getTheCharacter().setFileName("pa-dragon-disciple.pcg");
		disciple.setRace(find(Race.class, "Human"));
		disciple.setScoreBase(find(PCStat.class, "STR"), 14);
		PlayerCharacter pc = disciple.getTheCharacter();
		// Levels are added directly: class requirements (e.g. Dragon Disciple's Draconic and 5 ranks of
		// Knowledge (Arcana)) are not under test.
		pc.incrementClassLevel(1, find(PCClass.class, "Sorcerer"), true, true);
		// The bloodline pick opens a chooser, which UI answers with the Draconic bloodline.
		disciple.addAbility(find(AbilityCategory.class, "Bloodline"),
			internalAbility("Sorcerer ~ Standard Bloodline Selection"));
		pc.incrementClassLevel(2, find(PCClass.class, "Dragon Disciple"), true, true);
		disciple.addAbility(AbilityCategory.FEAT, feat("Power Attack"));
		pc.calcActiveBonuses();
		assertTrue(hasAbility(pc, "Power Attack"), "Power Attack taken");
		assertTrue(hasAbility(pc, "Draconic Bloodline ~ Claws"), "Draconic bloodline claws");
		assertTrue(hasAbility(pc, "Dragon Disciple ~ Dragon Bite"), "Dragon Disciple bite");
		assertEquals(3, pc.getVariableValue("NaturalAttacks", "").intValue(), "a bite and two claws");
		assertEquals(1, pc.baseAttackBonus(), "Sorcerer 1 / Dragon Disciple 2 BAB");

		assertTrue(hasAbility(pc, PA_PRIMARY_STR), "1.5x Str variant granted for Dragon Bite");
		assertPowerAttack(pc, PA_PRIMARY_STR, "Bite", -1, 3);
	}

	@Test
	void noOneAndAHalfStrVariantWithoutASingleOrImprovedBite()
	{
		// Serpentine eidolon: bite and tail slap, i.e. two natural attacks and no Improved Bite
		CharacterFacadeImpl eidolon = createSerpentineEidolonWithPowerAttack("pa-plain", false);
		PlayerCharacter pc = eidolon.getTheCharacter();

		assertTrue(hasAbility(pc, PA_PRIMARY), "Natural Primary variant granted");
		assertFalse(hasAbility(pc, PA_PRIMARY_STR), "1.5x Str variant must not be granted");
	}

	@Test
	void temporaryTypeIsAddedOnlyOnceToATemporaryBonusItem()
	{
		CharacterFacadeImpl eidolon = createSerpentineEidolonWithPowerAttack("pa-temporary", false);
		PlayerCharacter pc = eidolon.getTheCharacter();
		Equipment tempItem = applyPowerAttack(pc, PA_PRIMARY, "Bite");

		for (int i = 0; i < 5; i++)
		{
			pc.setCalcEquipmentList(true);
		}
		long temporaryCount = tempItem.getSafeListFor(ListKey.TYPE).stream()
			.filter(Type.TEMPORARY::equals).count();
		assertEquals(1, temporaryCount, "TEMPORARY type on the temporary bonus item");
	}

	@Test
	void savingKeepsTheSameToHitBonusOnEachNaturalWeapon()
	{
		// Both variants share the identical "WEAPON|TOHIT|-PowerAttackModifier" bonus text;
		// the save used to keep only one of them, so one weapon lost its -2 on reload.
		CharacterFacadeImpl eidolon = createSerpentineEidolonWithPowerAttack("pa-save", false);
		PlayerCharacter pc = eidolon.getTheCharacter();
		applyPowerAttack(pc, PA_PRIMARY, "Bite");
		applyPowerAttack(pc, PA_SECONDARY, "Tail Slap");

		String pcg = new PCGVer2Creator(pc, SettingsHandler.getGameAsProperty().get(), List.of()).createPCGString();
		for (String paKey : List.of(PA_PRIMARY, PA_SECONDARY))
		{
			String line = pcg.lines().filter(l -> l.startsWith("TEMPBONUS:FEAT=" + paKey + "|")).findFirst()
				.orElseThrow(() -> new AssertionError("no saved temporary bonus for " + paKey));
			assertTrue(line.contains("TBBONUS:WEAPON&pipe;TOHIT"), paKey + " to hit saved: " + line);
			assertTrue(line.contains("TBBONUS:WEAPON&pipe;DAMAGE"), paKey + " damage saved: " + line);
		}
	}

	private static void assertPowerAttack(PlayerCharacter pc, String paKey, String weaponName, int toHit, int damage)
	{
		Equipment weapon = naturalWeapon(pc, weaponName);
		int baseHit = weapon.getBonusToHit(pc, true);
		int baseDamage = weapon.getBonusToDamage(pc, true);
		Equipment tempItem = applyPowerAttack(pc, paKey, weaponName);
		assertEquals(toHit, tempItem.getBonusToHit(pc, true) - baseHit, paKey + " to hit on " + weaponName);
		assertEquals(damage, tempItem.getBonusToDamage(pc, true) - baseDamage, paKey + " damage on " + weaponName);
	}

	/** Apply a Power Attack variant to a natural weapon the way the Temporary Bonuses tab does. */
	private static Equipment applyPowerAttack(PlayerCharacter pc, String paKey, String weaponName)
	{
		Equipment tempItem = naturalWeapon(pc, weaponName).clone();
		tempItem.makeVirtual();
		tempItem.setAppliedName(paKey);
		assertNotNull(TempBonusHelper.applyBonusToCharacterEquipment(tempItem, feat(paKey), pc), paKey);
		pc.calcActiveBonuses();
		return tempItem;
	}

	private static List<String> targetNames(PlayerCharacter pc, String paKey)
	{
		List<String> names = new ArrayList<>();
		for (InfoFacade target : TempBonusHelper.getListOfApplicableEquipment(feat(paKey), pc))
		{
			names.add(((Equipment) target).getBaseItemName());
		}
		return names;
	}

	private static Equipment naturalWeapon(PlayerCharacter pc, String baseName)
	{
		for (Equipment eq : pc.getDisplay().getEquipmentSet())
		{
			if (baseName.equals(eq.getBaseItemName()) && eq.isNatural())
			{
				return eq;
			}
		}
		throw new AssertionError("No natural weapon " + baseName);
	}

	private static boolean hasAbility(PlayerCharacter pc, String key)
	{
		return pc.getCNAbilities().stream().anyMatch(cna -> key.equals(cna.getAbilityKey()));
	}

	private static Ability feat(String key)
	{
		Ability ability = Globals.getContext().getReferenceContext().getManufacturerId(AbilityCategory.FEAT)
			.getActiveObject(key);
		assertNotNull(ability, key);
		return ability;
	}

	private static CharacterFacadeImpl createSerpentineEidolonWithPowerAttack(String name, boolean improvedBite)
	{
		CharacterFacadeImpl master = createSummoner(name + "-master");
		levelSummonerTo(master, 7);
		CharacterFacadeImpl eidolon = createEidolon(master, name + "-eidolon");
		eidolon.addAbility(find(AbilityCategory.class, "Eidolon Selection"), internalAbility("Standard Eidolon"));
		eidolon.addAbility(find(AbilityCategory.class, "Eidolon Type Selection"),
			internalAbility("Eidolon Type ~ Serpentine"));
		if (improvedBite)
		{
			eidolon.addAbility(find(AbilityCategory.class, "Eidolon Evolution"), Globals.getContext()
				.getReferenceContext().getManufacturerId(find(AbilityCategory.class, "Special Ability"))
				.getActiveObject("Evolution ~ Improved Bite"));
		}
		// Power Attack needs Str 13
		eidolon.setScoreBase(find(PCStat.class, "STR"), 14);
		eidolon.addAbility(AbilityCategory.FEAT, feat("Power Attack"));
		PlayerCharacter pc = eidolon.getTheCharacter();
		pc.calcActiveBonuses();
		assertTrue(hasAbility(pc, "Power Attack"), "Power Attack taken");
		assertEquals(6, pc.baseAttackBonus(), "eidolon BAB at summoner level 7");
		return eidolon;
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
