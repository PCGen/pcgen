package pcgen.core;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.output.channel.compat.AlignmentCompat;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import plugin.lsttokens.testsupport.BuildUtilities;

import org.junit.jupiter.api.Test;

/**
 * {@code PrereqHandlerTest} tests the operation of the
 * PrereqHandler class.
 */
@SuppressWarnings("nls")
class PrereqHandlerTest extends AbstractCharacterTestCase
{
	/**
	 * Test the passes method. Currently this concentrates on making sure
	 * the invert result is working.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	void testPasses() throws PersistenceLayerException
	{
		final PreParserFactory factory = PreParserFactory.getInstance();

		final Prerequisite prereqNeg = factory.parse("!PREALIGN:LG,LN,LE");
		final Prerequisite prereq = factory.parse("PREALIGN:NG,TN,NE,CG,CN,CE");

		final PlayerCharacter pc = getCharacter();
		final Race human = new Race();
		human.setName("Human");
		pc.setRace(human);

		AlignmentCompat.setCurrentAlignment(pc.getCharID(), le);
		assertFalse(PrereqHandler.passes(
				prereq, pc, null), "Non-negate returns false");
		assertFalse(PrereqHandler.passes(
				prereqNeg, pc, null), "Negate returns false");

		AlignmentCompat.setCurrentAlignment(pc.getCharID(), tn);
		assertTrue(PrereqHandler.passes(
				prereq, pc, null), "Non-negate returns true");
		assertTrue(PrereqHandler.passes(
				prereqNeg, pc, null), "Negate returns true");
	}

	/**
	 * Tests PREFEAT.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	@Test
	void testFeatPasses() throws PersistenceLayerException
	{
		final PreParserFactory factory = PreParserFactory.getInstance();

		final Prerequisite prereq = factory.parse("!PREFEAT:1,Uncanny Dodge");
		final PlayerCharacter pc = getCharacter();
		final Race human = new Race();
		human.setName("Human");
		pc.setRace(human);

		assertTrue(PrereqHandler.passes(prereq,
			pc, null), "No feat should return true");

		final Ability ud = new Ability();
		ud.setName("Uncanny Dodge");
		ud.setCDOMCategory(BuildUtilities.getFeatCat());
		ud.put(StringKey.KEY_NAME, "Uncanny Dodge");
		addAbility(BuildUtilities.getFeatCat(), ud);
		assertFalse(PrereqHandler.passes(prereq,
			pc, null), "Feat should return false");
	}
}
