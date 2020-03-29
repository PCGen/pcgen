package pcgen.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import pcgen.ControlTestSupport;
import pcgen.cdom.content.BaseDice;
import pcgen.cdom.util.CControl;
import pcgen.core.system.LoadInfo;
import pcgen.persistence.GameModeFileLoader;
import pcgen.util.TestHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This class tests global areas of PCGen
 */
@SuppressWarnings("nls")
class GlobalsTest
{
	@BeforeEach
	void setUp() throws Exception
	{
		Globals.clearCampaignsForRefresh();
		final GameMode gamemode = new GameMode("3.5");
		gamemode.setBonusFeatLevels("3|3");
		ControlTestSupport.enableFeature(gamemode.getModeContext(), CControl.ALIGNMENTFEATURE);
		gamemode.addLevelInfo("Normal", new LevelInfo());
		gamemode.addXPTableName("Normal");
		gamemode.setDefaultXPTableName("Normal");
		gamemode.clearLoadContext();
		LoadInfo loadable =
				gamemode.getModeContext().getReferenceContext().constructNowIfNecessary(
						LoadInfo.class, gamemode.getName());
		loadable.addLoadScoreValue(0, BigDecimal.ONE);
		GameModeFileLoader.addDefaultTabInfo(gamemode);
		SystemCollections.addToGameModeList(gamemode);
		SettingsHandler.setGame("3.5");

		TestHelper.makeSizeAdjustments();
	}

	/**
	 * Test the Preview Tab
	 */
	@Test
	public void testPreviewTab()
	{
		// Expect to be initialised false
		assertThat("Initialised False", SettingsHandler.isIgnoreMonsterHDCap(), is(false));

		// Set true, expect to get true back
		SettingsHandler.setIgnoreMonsterHDCap(true);
		assertThat("Show update to True", SettingsHandler.isIgnoreMonsterHDCap(), is(true));

		// Set false again to prove it toggles properly
		SettingsHandler.setIgnoreMonsterHDCap(false);
		assertThat("Show update to False", SettingsHandler.isIgnoreMonsterHDCap(), is(false));
	}

//	/**
//	 * I was going to add separate test methods for
//	 *
//	 * addAbility (Ability anAbility)
//	 * removeAbilityKeyed (String category, String aKey)
//	 * removeAbilityNamed (String category, String aName)
//	 * getAbilityKeyed (String category, String aKey)
//	 * getAbilityNamed (String category, String aName)
//	 *
//	 * but the only way to test them properly is to ensure they return consistent
//	 * data between themselves
//	 */
//	public void testAbilityDatabaseBasicMethods()
//	{
//		Ability ab = new Ability();
//		ab.setName("First Ability");
//		ab.setCDOMCategory(AbilityCategory.FEAT);
//
//		is(ab.getDisplayName(), strEq("First Ability"),
//			"Name of first Ability is correct");
//		is(ab.getKeyName(), strEq("First Ability"),
//			"Key of first Ability is correct");
//
//		is(ab.getCategory(), strEq(Constants.FEAT_CATEGORY),
//			"Category of first Ability is correct");
//
//		Ability resAb =
//				Globals.getAbilityKeyed(Constants.FEAT_CATEGORY,
//					"First Ability");
//		is(resAb, eqnull(), "Search for unadded Ability returns null");
//
//		Iterator<? extends Categorisable> i =
//				Globals.getAbilityKeyIterator(Constants.FEAT_CATEGORY);
//		is(i.hasNext(), eq(false), "Feat list is empty");
//
//		boolean added = Globals.addAbility(ab);
//		is(added, eq(true), "First Ability added successfully");
//
//		resAb =
//				Globals.getAbilityKeyed(Constants.FEAT_CATEGORY,
//					"First Ability");
//		is(resAb, not(eqnull()),
//			"Search for added Ability does not return null");
//		is(resAb.getKeyName(), strEq("First Ability"),
//			"Added ability has correct name");
//
//		i = Globals.getAbilityKeyIterator(Constants.FEAT_CATEGORY);
//		is(i.hasNext(), eq(true), "Feat list is not empty");
//
//		ab = new Ability();
//		ab.setName("Second Ability");
//		ab.setCDOMCategory(AbilityCategory.FEAT);
//
//		added = Globals.addAbility(ab);
//		is(added, eq(true), "Second Ability added successfully");
//
//		resAb =
//				Globals.getAbilityKeyed(Constants.FEAT_CATEGORY,
//					"Second Ability");
//		is(resAb, not(eq(null)),
//			"Search for Second Ability added does not return null");
//		is(resAb.getKeyName(), strEq("Second Ability"),
//			"Second ability has correct name");
//
//		resAb =
//				Globals.getAbilityKeyed(Constants.FEAT_CATEGORY,
//					"First Ability");
//		is(resAb, not(eqnull()), "First ability is still avilable");
//		is(resAb.getKeyName(), strEq("First Ability"),
//			"First Ability has correct name after second add");
//
//		boolean removed =
//				Globals.removeAbilityKeyed(Constants.FEAT_CATEGORY,
//					"First Ability");
//		is(removed, eq(true), "Remove of First Ability worked");
//
//		resAb =
//				Globals.getAbilityKeyed(Constants.FEAT_CATEGORY,
//					"First Ability");
//		is(resAb, eqnull(), "Query for First ability returns null");
//
//		resAb =
//				Globals.getAbilityKeyed(Constants.FEAT_CATEGORY,
//					"Second Ability");
//		is(resAb, not(eq(null)), "Search for Second Ability added does"
//			+ " not return null after deletion of First");
//		is(resAb.getKeyName(), strEq("Second Ability"),
//			"Second ability has correct name");
//
//		i = Globals.getAbilityKeyIterator(Constants.FEAT_CATEGORY);
//		is(i.hasNext(), eq(true), "Feat list is not empty");
//
//		removed =
//				Globals.removeAbilityKeyed(Constants.FEAT_CATEGORY,
//					"First Ability");
//		is(removed, eq(false),
//			"Second attempt at Removal of First Ability fails");
//
//		removed =
//				Globals.removeAbilityKeyed(Constants.FEAT_CATEGORY,
//					"Second Ability");
//		is(removed, eq(true), "Remove of Second Ability worked");
//
//		i = Globals.getAbilityKeyIterator(Constants.FEAT_CATEGORY);
//		is(i.hasNext(), eq(false), "Feat list is empty");
//
//		/* TODO When the changes are finished and categories actually
//		 * work, test this with differect categories */
//	}
//
//	/**
//	 * Test names with choices
//	 */
//	public void testAbilityDatabaseNameMethods()
//	{
//		Ability ab = new Ability();
//		ab.setName("Ability with choices");
//		ab.setCDOMCategory(AbilityCategory.FEAT);
//
//		boolean added = Globals.addAbility(ab);
//		is(added, eq(true), "Ability with choices added successfully");
//
//		Ability retrievedAb =
//				Globals.getAbilityKeyed(Constants.FEAT_CATEGORY,
//					"Ability with choices");
//		is(retrievedAb, not(eqnull()),
//			"Search for Ability with choices added does not return null");
//		is(retrievedAb.getKeyName(), strEq("Ability with choices"),
//			"Second ability has correct name");
//
//		retrievedAb =
//				AbilityUtilities.retrieveAbilityKeyed(Constants.FEAT_CATEGORY,
//					"Ability with choices (chosen)");
//		is(retrievedAb, not(eqnull()),
//			"Search with choice made does not return null");
//		is(retrievedAb.getKeyName(), strEq("Ability with choices"),
//			"Second ability has correct name");
//
//		boolean removed =
//				Globals.removeAbilityKeyed(Constants.FEAT_CATEGORY,
//					"Ability with choices");
//		is(removed, eq(true), "Remove of Ability with choices worked");
//
//		Iterator<? extends Categorisable> it =
//				Globals.getAbilityNameIterator(Constants.FEAT_CATEGORY);
//		is(it, not(eqnull()), "Iterator over empty list isn't null");
//
//		is(it.hasNext(), eq(false), "there are no Abilities in the iterator");
//
//		removed =
//				Globals.removeAbilityKeyed(Constants.FEAT_CATEGORY,
//					"Ability with choices");
//		is(removed, eq(false), "Remove of Ability with choices worked");
//
//		ab = new Ability();
//		ab.setName("Ability with choices (already there)");
//		ab.setCDOMCategory(AbilityCategory.FEAT);
//
//		added = Globals.addAbility(ab);
//		is(added, eq(true),
//			"Ability with choices (already there) added successfully");
//
//		retrievedAb =
//				Globals.getAbilityKeyed(Constants.FEAT_CATEGORY,
//					"Ability with choices (already there)");
//		is(retrievedAb, not(eqnull()),
//			"Search for Ability with choices (already there) does not return null");
//		is(retrievedAb.getKeyName(),
//			strEq("Ability with choices (already there)"),
//			"ability has correct name");
//
//		/* Clean up after ourselves (so later tests start with empty ability store) */
//		removed =
//				Globals.removeAbilityKeyed(Constants.FEAT_CATEGORY,
//					"Ability with choices (already there)");
//		is(removed, eq(true),
//			"Remove of Ability with choices (already there) worked");
//
//	}
//
//	/**
//	 * Test ability database methods for groups of abilities
//	 */
//	public void testAbilityDatabaseGroupMethods()
//	{
//		Ability ab = new Ability();
//		ab.setName("Ability002");
//		ab.put(StringKey.KEY_NAME, "BBB");
//		ab.setCDOMCategory(AbilityCategory.FEAT);
//
//		is(ab.getDisplayName(), strEq("Ability002"),
//			"Name of first Ability is correct");
//		is(ab.getKeyName(), strEq("BBB"), "Key of first Ability is correct");
//		is(ab.getCategory(), strEq(Constants.FEAT_CATEGORY),
//			"Category of first Ability is correct");
//
//		boolean added = Globals.addAbility(ab);
//		is(added, eq(true), "Ability002 added successfully");
//
//		ab = new Ability();
//		ab.setName("Ability001");
//		ab.put(StringKey.KEY_NAME, "CCC");
//		ab.setCDOMCategory(AbilityCategory.FEAT);
//
//		is(ab.getDisplayName(), strEq("Ability001"),
//			"Name of second Ability is correct");
//		is(ab.getKeyName(), strEq("CCC"), "Key of second Ability is correct");
//		is(ab.getCategory(), strEq(Constants.FEAT_CATEGORY),
//			"Category of second Ability is correct");
//
//		added = Globals.addAbility(ab);
//		is(added, eq(true), "Ability001 added successfully");
//
//		ab = new Ability();
//		ab.setName("Ability003");
//		ab.put(StringKey.KEY_NAME, "AAA");
//		ab.setCDOMCategory(new AbilityCategory("Other_Random_Category"));
//
//		is(ab.getDisplayName(), strEq("Ability003"),
//			"Name of third Ability is correct");
//		is(ab.getKeyName(), strEq("AAA"), "Key of third Ability is correct");
//		is(ab.getCategory(), strEq("Other_Random_Category"),
//			"Category of third Ability is correct");
//
//		added = Globals.addAbility(ab);
//		is(added, eq(true), "Ability003 added successfully");
//
//		ab = Globals.getAbilityKeyed(Constants.FEAT_CATEGORY, "Ability003");
//		is(ab, eq(null), "third Ability is not is the feat category");
//
//		Iterator<? extends Categorisable> it =
//				Globals.getAbilityNameIterator(Constants.FEAT_CATEGORY);
//		is(it.hasNext(), eq(true), "Iterator has abilities 01");
//
//		ab = (Ability) it.next();
//		is(ab.getDisplayName(), strEq("Ability001"),
//			"first Ability from Name Iterator is correct 01");
//
//		ab = (Ability) it.next();
//		is(ab.getDisplayName(), strEq("Ability002"),
//			"second Ability from Name Iterator is correct 01");
//
//		is(it.hasNext(), eq(false), "Iterator has abilities 02");
//
//		it = Globals.getAbilityNameIterator("ALL");
//
//		is(it.hasNext(), eq(true), "Iterator has abilities 03");
//
//		ab = (Ability) it.next();
//		is(ab.getDisplayName(), strEq("Ability001"),
//			"first Ability from Name Iterator is correct 02");
//
//		ab = (Ability) it.next();
//		is(ab.getDisplayName(), strEq("Ability002"),
//			"second Ability from Name Iterator is correct 02");
//
//		is(it.hasNext(), eq(true), "Iterator has abilities 04");
//
//		ab = (Ability) it.next();
//		is(ab.getDisplayName(), strEq("Ability003"),
//			"third Ability from Name Iterator is correct 02");
//
//		is(it.hasNext(), eq(false), "Iterator has abilities 05");
//
//		/* Now test Key methods, should give Abilities in different order */
//
//		it = Globals.getAbilityKeyIterator(Constants.FEAT_CATEGORY);
//
//		is(it.hasNext(), eq(true), "Iterator has abilities 01");
//
//		ab = (Ability) it.next();
//		is(ab.getDisplayName(), strEq("Ability002"),
//			"first Ability from Key Iterator is correct 03");
//		is(ab.getKeyName(), strEq("BBB"),
//			"first Ability from Key Iterator is correct 03");
//
//		ab = (Ability) it.next();
//		is(ab.getDisplayName(), strEq("Ability001"),
//			"second Ability from Key Iterator is correct 03");
//		is(ab.getKeyName(), strEq("CCC"),
//			"second Ability from Key Iterator is correct 03");
//
//		is(it.hasNext(), eq(false), "Iterator has abilities 02");
//
//		it = Globals.getAbilityKeyIterator("ALL");
//
//		is(it.hasNext(), eq(true), "Iterator has abilities 03");
//
//		ab = (Ability) it.next();
//		is(ab.getDisplayName(), strEq("Ability003"),
//			"first Ability from Key Iterator is correct 04");
//		is(ab.getKeyName(), strEq("AAA"),
//			"first Ability from Key Iterator is correct 04");
//
//		ab = (Ability) it.next();
//		is(ab.getDisplayName(), strEq("Ability002"),
//			"second Ability from Key Iterator is correct 04");
//		is(ab.getKeyName(), strEq("BBB"),
//			"second Ability from Key Iterator is correct 04");
//
//		is(it.hasNext(), eq(true), "Iterator has abilities 04");
//
//		ab = (Ability) it.next();
//		is(ab.getDisplayName(), strEq("Ability001"),
//			"third Ability from Key Iterator is correct 04");
//		is(ab.getKeyName(), strEq("CCC"),
//			"third Ability from Key Iterator is correct 04");
//
//		is(it.hasNext(), eq(false), "Iterator has abilities 05");
//
//		List<? extends Categorisable> li =
//				Globals.getUnmodifiableAbilityList("FOO");
//		is(li, eq(Collections.emptyList()), "list of FOO is Empty");
//
//		li = Globals.getUnmodifiableAbilityList(Constants.FEAT_CATEGORY);
//
//		is(li, not(eq(null)), "list of FEAT is not null");
//
//		ab = (Ability) li.get(0);
//		is(ab.getDisplayName(), strEq("Ability001"),
//			"first Ability from list is correct 01");
//
//		ab = (Ability) li.get(1);
//		is(ab.getDisplayName(), strEq("Ability002"),
//			"second Ability from list is correct 01");
//
//	}

	@Test
	public void testAdjustDamage()
	{
		GameMode gameMode = SettingsHandler.getGameAsProperty().get();
		assertThat("size list initialised",
				Globals.getContext().getReferenceContext().getConstructedObjectCount(SizeAdjustment.class),
			is(greaterThan(0)));
		BaseDice d6 = gameMode.getModeContext().getReferenceContext().constructCDOMObject(BaseDice.class, "1d6");
		d6.addToDownList(new RollInfo("1d4"));
		d6.addToDownList(new RollInfo("1d3"));
		d6.addToDownList(new RollInfo("1d2"));
		d6.addToDownList(new RollInfo("1"));
		d6.addToUpList(new RollInfo("1d8"));
		d6.addToUpList(new RollInfo("2d6"));
		d6.addToUpList(new RollInfo("3d6"));
		d6.addToUpList(new RollInfo("4d6"));
		d6.addToUpList(new RollInfo("6d6"));
		d6.addToUpList(new RollInfo("8d6"));
		d6.addToUpList(new RollInfo("12d6"));
		Globals.getContext().getReferenceContext().importObject(d6);
		assertEquals(
				"1d4",
				Globals.adjustDamage("1d6", -1),
				"reduction of damage due to smaller size"
		);
	}
}
