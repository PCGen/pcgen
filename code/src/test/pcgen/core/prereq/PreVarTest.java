/*
 * Created on 22-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pcgen.core.prereq;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.PlayerCharacter;
import pcgen.util.TestHelper;
import plugin.pretokens.parser.PreVariableParser;

/**
 * Tests PREVAR token
 */
public class PreVarTest extends AbstractCharacterTestCase
{

	/**
	 * Main
	 * 
	 * @param args
	 */
	public static void main(final String[] args)
	{
		TestRunner.run(PreVarTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreVarTest.class);
	}

	/**
	 * @throws Exception
	 */
	public void testVarPass() throws Exception
	{
		final PlayerCharacter character = getCharacter();

		PreVariableParser parser = new PreVariableParser();

		Prerequisite prereq = parser.parse("vareq",
				"1,count(\"ABILITIES\",\"CATEGORY=BARDIC\",\"NAME=Dancer\")",
				false, false);

		assertFalse("Test matches with no abilities.", PrereqHandler.passes(
				prereq, character, null));

		Ability ab2 = TestHelper.makeAbility("Dancer", "BARDIC",
				"General.Bardic");
		ab2.put(ObjectKey.MULTIPLE_ALLOWED, Boolean.FALSE);
		character.addAbility(TestHelper.getAbilityCategory(ab2), ab2, null);

		assertTrue("Test fails with ability present.", PrereqHandler.passes(
				prereq, character, null));
	}
	
	public void testMutiplePositive() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		setPCStat(character, str, 10);
		setPCStat(character, dex, 14);
		character.calcActiveBonuses();

		PreVariableParser parser = new PreVariableParser();
		Prerequisite prereq =
				parser.parse("VAR", "abs(STR),1,abs(DEX),3", false, false);
		assertFalse("Test matches with no stats passing", PrereqHandler.passes(
			prereq, character, null));

		setPCStat(character, str, 12);
		character.calcActiveBonuses();
		assertFalse("Test matches with no stats passing", PrereqHandler.passes(
			prereq, character, null));

		setPCStat(character, dex, 16);
		character.calcActiveBonuses();
		assertTrue("Test should match now both stats pass", PrereqHandler.passes(
			prereq, character, null));
		
	}
	
	public void testMutipleNegative() throws Exception
	{
		final PlayerCharacter character = getCharacter();
		setPCStat(character, str, 10);
		setPCStat(character, dex, 14);
		character.calcActiveBonuses();

		PreVariableParser parser = new PreVariableParser();
		Prerequisite prereq =
				parser.parse("VAR", "abs(STR),1,abs(DEX),3", true, false);
		assertTrue("Test matches with no stats passing", PrereqHandler.passes(
			prereq, character, null));

		setPCStat(character, str, 12);
		character.calcActiveBonuses();
		assertTrue("Test matches with no stats passing", PrereqHandler.passes(
			prereq, character, null));

		setPCStat(character, dex, 16);
		character.calcActiveBonuses();
		assertFalse("Test should match now both stats pass", PrereqHandler.passes(
			prereq, character, null));
		
	}
}
