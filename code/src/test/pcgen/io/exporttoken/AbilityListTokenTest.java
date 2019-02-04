/*
 * Copyright 2006 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.io.exporttoken;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.io.ExportHandler;
import pcgen.util.TestHelper;
import pcgen.util.enumeration.Visibility;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;

/**
 * {@code AbilityListTokenTest} tests the functioning of the ABILITYLIST
 * token processing code. 
 */
public class AbilityListTokenTest extends AbstractCharacterTestCase
{

	@BeforeEach
    @Override
	protected void setUp() throws Exception
	{
		super.setUp();
		// Make some ability categories and add them to the game mode
		AbilityCategory bardCategory = Globals.getContext().getReferenceContext()
				.constructNowIfNecessary(AbilityCategory.class, "BARDIC");

		Ability ab1 = TestHelper.makeAbility("Perform (Dance)",
			BuildUtilities.getFeatCat(), "General.Fighter");
		ab1.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
		ab1.put(ObjectKey.VISIBILITY, Visibility.DEFAULT);
		addAbility(BuildUtilities.getFeatCat(), ab1);

		Ability ab2 = TestHelper.makeAbility("Perform (Dance)", "BARDIC", "General.Bardic");
		ab2.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
		addAbility(bardCategory, ab2);

		Ability ab3 = TestHelper.makeAbility("Perform (Oratory)",
			BuildUtilities.getFeatCat(), "General.Fighter");
		ab3.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
		addAbility(BuildUtilities.getFeatCat(), ab3);

		Ability ab4 = TestHelper.makeAbility("Silent Step", BuildUtilities.getFeatCat(),
			"General");
		ab4.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
		addAbility(BuildUtilities.getFeatCat(), ab4);
	}

	/**
	 * Test the output for positive numbers with fractions.
	 */
	public void testTypes()
	{
		AbilityListToken tok = new AbilityListToken();
		ExportHandler eh = new ExportHandler(null);
		PlayerCharacter character = getCharacter();

		assertEquals("ABILITYLIST.FEAT",
			"Perform (Dance), Perform (Oratory), Silent Step",
			tok.getToken("ABILITYLIST.FEAT", character, eh));

		assertEquals("ABILITYLIST.FEAT.TYPE=Fighter",
			"Perform (Dance), Perform (Oratory)",
			tok.getToken("ABILITYLIST.FEAT.TYPE=Fighter", character, eh));

		assertEquals("ABILITYLIST.FEAT.!TYPE=Fighter",
			"Silent Step",
			tok.getToken("ABILITYLIST.FEAT.!TYPE=Fighter", character, eh));
	}

	/**
	 * Test the output for negative numbers with fractions.
	 */
	public void testCategory()
	{
		AbilityListToken tok = new AbilityListToken();
		ExportHandler eh = new ExportHandler(null);
		PlayerCharacter character = getCharacter();

		assertEquals("ABILITYLIST.BARDIC",
			"Perform (Dance)",
			tok.getToken("ABILITYLIST.BARDIC", character, eh));
	}

	/**
	 * Test the JEP count function on abilities.  
	 */
	public void testCount()
	{
		PlayerCharacter character = getCharacter();

		AbilityCategory featCategory = 
			SettingsHandler.getGame().getAbilityCategory("Feat");

		Ability ab5 = TestHelper.makeAbility("Silent Step (Greater)",
			BuildUtilities.getFeatCat(), "General");
		ab5.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
		ab5.put(ObjectKey.VISIBILITY, Visibility.OUTPUT_ONLY);
		addAbility(featCategory, ab5);

		Ability ab6 = TestHelper.makeAbility("Perform (Fiddle)",
			BuildUtilities.getFeatCat(), "Bardic");
        ab6.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
        addAbility(featCategory, ab6);


        assertThat(
		(double) character.getVariableValue("count(\"ABILITIES\",\"CATEGORY=FEAT\",\"VISIBILITY=DEFAULT\")", ""),
            closeTo(4.0, 0.1));

		assertThat(
				(double) character.getVariableValue("count(\"ABILITIES\",\"CATEGORY=FEAT\","
						+ "\"VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY\")", ""),
				closeTo(5.0, 0.1));

		assertThat(
				(double) character.getVariableValue("count(\"ABILITIES\",\"CATEGORY=FEAT[and]TYPE=Fighter\",\"VISIBILITY=DEFAULT[or]VISIBILITY=OUTPUT_ONLY\")", ""),
				closeTo(2.0, 0.1));

		assertThat(
				(double) character.getVariableValue("count(\"ABILITIES\",\"CATEGORY=BARDIC[and]TYPE=Bardic.General\")", ""),
				closeTo(1.0, 0.1));

		assertThat(
				(double) character.getVariableValue("count(\"ABILITIES\",\"NATURE=AUTOMATIC\")", ""),
				closeTo(0.0, 0.1));

		assertThat(
				(double) character.getVariableValue("count(\"ABILITIES\",\"NATURE=VIRTUAL\")", ""),
				closeTo(0.0, 0.1));

		assertThat(
				(double) character.getVariableValue("count(\"ABILITIES\",\"NATURE=NORMAL\")", ""),
				closeTo(6.0, 0.1));

		assertThat(
				(double) character.getVariableValue("count(\"ABILITIES\")", ""),
				closeTo(6.0, 0.1));
	}

	/**
	 * Test the mechanism of splitting FOR node parameters to
	 * ensure it copes with JEP functions with multiple comma 
	 * separated parameters. 
	 */
	public void testForNodeSplit()
	{
		String testStr =
				"|FOR,%feat,0,count(\"ABILITIES\",\"CATEGORY=FEAT\",\"VISIBILITY=VISIBLE\")-1,1,0|";

		List<String> result = ExportHandler.getParameters(testStr);
		Assert.assertEquals("Complex split len", 6, result.size());
		Assert.assertEquals("Complex split combined token 0", "|FOR", result.get(0));
		Assert.assertEquals("Complex split combined token 1", "%feat", result.get(1));
		Assert.assertEquals("Complex split combined token 2", "0", result.get(2));
		Assert.assertEquals("Complex split combined token 3",
			"count(\"ABILITIES\",\"CATEGORY=FEAT\",\"VISIBILITY=VISIBLE\")-1",
			result.get(3));
		Assert.assertEquals("Complex split combined token 4", "1", result.get(4));
		Assert.assertEquals("Complex split combined token 5", "0|", result.get(5));
	}

	public void testForNodeSplitNonJEP()
	{
		String testStr =
				"|FOR,%equip1,0,(COUNT[EQUIPMENT.MERGELOC.Not.Coin.NOT.Gem]-1)/2,1,0|";

		List<String> result = ExportHandler.getParameters(testStr);
		Assert.assertEquals("Complex split len", 6, result.size());
	}
}
