package pcgen.core;

import pcgen.PCGenTestCase;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This class tests global areas of PCGen
 */
public class GlobalsTest extends PCGenTestCase
{
	/**
	 * Constructs a new <code>GlobalsTest</code>.
	 *
	 * @see PCGenTestCase#PCGenTestCase()
	 */
	public GlobalsTest()
	{
		super();
	}

	/**
	 * Constructs a new <code>GlobalsTest</code> with the given <var>name</var>.
	 *
	 * @param name the test case name
	 *
	 * @see PCGenTestCase#PCGenTestCase(String)
	 */
	public GlobalsTest(final String name)
	{
		super(name);
	}

	protected void setUp()
		throws Exception
	{
		Globals.clearCampaignsForRefresh();
	}

	/**
	 * Test the Preview Tab
	 */
	public void testPreviewTab()
	{
		// Expect to be initialised false
		is(SettingsHandler.isPreviewTabShown(), eq(false), "Initialised False");

		// Set true, expect to get true back
		SettingsHandler.setPreviewTabShown(true);
		is(SettingsHandler.isPreviewTabShown(), eq(true), "Show update to True");

		// Set false again to prove it toggles properly
		SettingsHandler.setPreviewTabShown(false);
		is(SettingsHandler.isPreviewTabShown(), eq(false), "Show update to False");
	}

	/**
	 * Test the Random int functionality
	 */
	public void testRandomInt()
	{
		int x;
		int rand;

		for (x = 0; x < 10000; x++)
		{
			rand = Globals.getRandomInt(6);
			is(rand, ge(0));
			is(rand, le(5));

			try
			{
				Globals.getRandomInt();
			}
			catch (Throwable t)
			{
				fail("Unbounded getRandomInt threw a " + t.toString());
			}
		}
	}

	/**
	 * Test that the tool tips work
	 */
	public void testToolTip()
	{
		// Expect initialised True
		is(SettingsHandler.isToolTipTextShown(), eq(true), "Show default value");

		// Set False , expect to get false back
		SettingsHandler.setToolTipTextShown(false);
		is(SettingsHandler.isToolTipTextShown(), eq(false), "Show update to false");

		// Set true again to prove it toggles
		SettingsHandler.setToolTipTextShown(true);
		is(SettingsHandler.isToolTipTextShown(), eq(true), "Show update to true");
	}

	/**
	 * I was going to add separate test methods for
	 *
	 * addAbility (Ability anAbility)
	 * removeAbilityKeyed (String category, String aKey)
	 * removeAbilityNamed (String category, String aName)
	 * getAbilityKeyed (String category, String aKey)
	 * getAbilityNamed (String category, String aName)
	 *
	 * but the only way to test them properly is to ensure they return consistent
	 * data between themselves
	 */
	public void testAbilityDatabaseBasicMethods()
	{
		Ability ab = new Ability();
		ab.setName("First Ability");
		ab.setCategory(Constants.FEAT_CATEGORY);

		is(ab.getDisplayName(), strEq("First Ability"), "Name of first Ability is correct");
		is(ab.getKeyName(), strEq("First Ability"), "Key of first Ability is correct");


		is(ab.getCategory(), strEq(Constants.FEAT_CATEGORY), "Category of first Ability is correct");

		Ability resAb = Globals.getAbilityKeyed(Constants.FEAT_CATEGORY, "First Ability");
		is(resAb, eqnull(), "Search for unadded Ability returns null");

		Iterator i = Globals.getAbilityKeyIterator (Constants.FEAT_CATEGORY);
		is(i.hasNext(), eq(false), "Feat list is empty");

		boolean added = Globals.addAbility(ab);
		is(added, eq(true), "First Ability added successfully");

		resAb = Globals.getAbilityKeyed(Constants.FEAT_CATEGORY, "First Ability");
		is(resAb, not(eqnull()), "Search for added Ability does not return null");
		is(resAb.getKeyName(), strEq("First Ability"), "Added ability has correct name");

		i = Globals.getAbilityKeyIterator (Constants.FEAT_CATEGORY);
		is(i.hasNext(), eq(true), "Feat list is not empty");

		ab = new Ability();
		ab.setName("Second Ability");
		ab.setCategory(Constants.FEAT_CATEGORY);

		added = Globals.addAbility(ab);
		is(added, eq(true), "Second Ability added successfully");

		resAb = Globals.getAbilityKeyed(Constants.FEAT_CATEGORY, "Second Ability");
		is(resAb, not(eq(null)), "Search for Second Ability added does not return null");
		is(resAb.getKeyName(), strEq("Second Ability"), "Second ability has correct name");

		resAb = Globals.getAbilityKeyed(Constants.FEAT_CATEGORY, "First Ability");
		is(resAb, not(eqnull()), "First ability is still avilable");
		is(resAb.getKeyName(), strEq("First Ability"), "First Ability has correct name after second add");

		boolean removed = Globals.removeAbilityKeyed(Constants.FEAT_CATEGORY, "First Ability");
		is(removed, eq(true), "Remove of First Ability worked");

		resAb = Globals.getAbilityKeyed(Constants.FEAT_CATEGORY, "First Ability");
		is(resAb, eqnull(), "Query for First ability returns null");

		resAb = Globals.getAbilityKeyed(Constants.FEAT_CATEGORY, "Second Ability");
		is(resAb, not(eq(null)), "Search for Second Ability added does" +
				" not return null after deletion of First");
		is(resAb.getKeyName(), strEq("Second Ability"), "Second ability has correct name");

		i = Globals.getAbilityKeyIterator (Constants.FEAT_CATEGORY);
		is(i.hasNext(), eq(true), "Feat list is not empty");

		removed = Globals.removeAbilityKeyed(Constants.FEAT_CATEGORY, "First Ability");
		is(removed, eq(false), "Second attempt at Removal of First Ability fails");

		removed = Globals.removeAbilityKeyed(Constants.FEAT_CATEGORY, "Second Ability");
		is(removed, eq(true), "Remove of Second Ability worked");

		i = Globals.getAbilityKeyIterator (Constants.FEAT_CATEGORY);
		is(i.hasNext(), eq(false), "Feat list is empty");

		/* TODO When the changes are finished and categories actually
		 * work, test this with differect categories */
	}

	/**
	 * Test names with choices
	 */
	public void testAbilityDatabaseNameMethods()
	{
		Ability ab = new Ability();
		ab.setName("Ability with choices");
		ab.setCategory(Constants.FEAT_CATEGORY);

		boolean added = Globals.addAbility(ab);
		is(added, eq(true), "Ability with choices added successfully");

		Ability retrievedAb = Globals.getAbilityKeyed(
				Constants.FEAT_CATEGORY, "Ability with choices");
		is(retrievedAb, not(eqnull()), "Search for Ability with choices added does not return null");
		is(retrievedAb.getKeyName(), strEq("Ability with choices"), "Second ability has correct name");

		retrievedAb = AbilityUtilities.retrieveAbilityKeyed(Constants.FEAT_CATEGORY, "Ability with choices (chosen)");
		is(retrievedAb, not(eqnull()), "Search with choice made does not return null");
		is(retrievedAb.getKeyName(), strEq("Ability with choices"), "Second ability has correct name");

		boolean removed = Globals.removeAbilityKeyed(
				Constants.FEAT_CATEGORY, "Ability with choices");
		is(removed, eq(true), "Remove of Ability with choices worked");

		Iterator it = Globals.getAbilityNameIterator(
				Constants.FEAT_CATEGORY);
		is(it, not(eqnull()), "Iterator over empty list isn't null");

		is(it.hasNext(), eq(false), "there are no Abilities in the iterator");

		removed = Globals.removeAbilityKeyed(Constants.FEAT_CATEGORY, "Ability with choices");
		is(removed, eq(false), "Remove of Ability with choices worked");

		ab = new Ability();
		ab.setName("Ability with choices (already there)");
		ab.setCategory(Constants.FEAT_CATEGORY);

		added = Globals.addAbility(ab);
		is(added, eq(true), "Ability with choices (already there) added successfully");

		retrievedAb = Globals.getAbilityKeyed(Constants.FEAT_CATEGORY, "Ability with choices (already there)");
		is(retrievedAb, not(eqnull()), "Search for Ability with choices (already there) does not return null");
		is(retrievedAb.getKeyName(), strEq("Ability with choices (already there)"), "ability has correct name");

		/* Clean up after ourselves (so later tests start with empty ability store) */
		removed = Globals.removeAbilityKeyed(Constants.FEAT_CATEGORY, "Ability with choices (already there)");
		is(removed, eq(true), "Remove of Ability with choices (already there) worked");

	}

	/**
	 * Test ability database methods for groups of abilities
	 */
	public void testAbilityDatabaseGroupMethods()
	{
		Ability ab = new Ability();
		ab.setName("Ability002");
		ab.setKeyName("BBB");
		ab.setCategory(Constants.FEAT_CATEGORY);

		is(ab.getDisplayName(),     strEq("Ability002"), "Name of first Ability is correct");
		is(ab.getKeyName(),  strEq("BBB"),        "Key of first Ability is correct");
		is(ab.getCategory(), strEq(Constants.FEAT_CATEGORY),       "Category of first Ability is correct");

		boolean added = Globals.addAbility(ab);
		is(added, eq(true), "Ability002 added successfully");

		ab = new Ability();
		ab.setName("Ability001");
		ab.setKeyName("CCC");
		ab.setCategory(Constants.FEAT_CATEGORY);

		is(ab.getDisplayName(),     strEq("Ability001"), "Name of second Ability is correct");
		is(ab.getKeyName(),  strEq("CCC"),        "Key of second Ability is correct");
		is(ab.getCategory(), strEq(Constants.FEAT_CATEGORY),       "Category of second Ability is correct");

		added = Globals.addAbility(ab);
		is(added, eq(true), "Ability001 added successfully");

		ab = new Ability();
		ab.setName("Ability003");
		ab.setKeyName("AAA");
		ab.setCategory("Other_Random_Category");

		is(ab.getDisplayName(),     strEq("Ability003"), "Name of third Ability is correct");
		is(ab.getKeyName(),  strEq("AAA"),        "Key of third Ability is correct");
		is(ab.getCategory(), strEq("Other_Random_Category"), "Category of third Ability is correct");

		added = Globals.addAbility(ab);
		is(added, eq(true), "Ability003 added successfully");

		ab = Globals.getAbilityKeyed(Constants.FEAT_CATEGORY, "Ability003");
		is(ab, eq(null), "third Ability is not is the feat category");

		Iterator it = Globals.getAbilityNameIterator(Constants.FEAT_CATEGORY);

		is(it.hasNext(), eq(true), "Iterator has abilities 01");

		ab = (Ability) it.next();
		is(ab.getDisplayName(), strEq("Ability001"), "first Ability from Name Iterator is correct 01");

		ab = (Ability) it.next();
		is(ab.getDisplayName(), strEq("Ability002"), "second Ability from Name Iterator is correct 01");

		is(it.hasNext(), eq(false), "Iterator has abilities 02");

		it = Globals.getAbilityNameIterator("ALL");

		is(it.hasNext(), eq(true), "Iterator has abilities 03");

		ab = (Ability) it.next();
		is(ab.getDisplayName(), strEq("Ability001"), "first Ability from Name Iterator is correct 02");

		ab = (Ability) it.next();
		is(ab.getDisplayName(), strEq("Ability002"), "second Ability from Name Iterator is correct 02");

		is(it.hasNext(), eq(true), "Iterator has abilities 04");

		ab = (Ability) it.next();
		is(ab.getDisplayName(), strEq("Ability003"), "third Ability from Name Iterator is correct 02");

		is(it.hasNext(), eq(false), "Iterator has abilities 05");

		/* Now test Key methods, should give Abilities in different order */

		it = Globals.getAbilityKeyIterator(Constants.FEAT_CATEGORY);

		is(it.hasNext(), eq(true), "Iterator has abilities 01");

		ab = (Ability) it.next();
		is(ab.getDisplayName(),    strEq("Ability002"), "first Ability from Key Iterator is correct 03");
		is(ab.getKeyName(), strEq("BBB"),        "first Ability from Key Iterator is correct 03");

		ab = (Ability) it.next();
		is(ab.getDisplayName(),    strEq("Ability001"), "second Ability from Key Iterator is correct 03");
		is(ab.getKeyName(), strEq("CCC"),        "second Ability from Key Iterator is correct 03");

		is(it.hasNext(), eq(false), "Iterator has abilities 02");

		it = Globals.getAbilityKeyIterator("ALL");

		is(it.hasNext(), eq(true), "Iterator has abilities 03");

		ab = (Ability) it.next();
		is(ab.getDisplayName(),    strEq("Ability003"), "first Ability from Key Iterator is correct 04");
		is(ab.getKeyName(), strEq("AAA"),        "first Ability from Key Iterator is correct 04");

		ab = (Ability) it.next();
		is(ab.getDisplayName(), strEq("Ability002"), "second Ability from Key Iterator is correct 04");
		is(ab.getKeyName(), strEq("BBB"),     "second Ability from Key Iterator is correct 04");

		is(it.hasNext(), eq(true), "Iterator has abilities 04");

		ab = (Ability) it.next();
		is(ab.getDisplayName(), strEq("Ability001"), "third Ability from Key Iterator is correct 04");
		is(ab.getKeyName(), strEq("CCC"),     "third Ability from Key Iterator is correct 04");

		is(it.hasNext(), eq(false), "Iterator has abilities 05");

		List li = Globals.getUnmodifiableAbilityList("FOO");

		is(li, eq(Collections.EMPTY_LIST), "list of FOO is Empty");

		li = Globals.getUnmodifiableAbilityList(Constants.FEAT_CATEGORY);

		is(li, not(eq(null)), "list of FEAT is not null");

		ab = (Ability) li.get(0);
		is(ab.getDisplayName(), strEq("Ability001"), "first Ability from list is correct 01");

		ab = (Ability) li.get(1);
		is(ab.getDisplayName(), strEq("Ability002"), "second Ability from list is correct 01");

	}
}
