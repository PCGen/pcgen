package pcgen.core;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.output.channel.compat.AlignmentCompat;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import plugin.lsttokens.testsupport.BuildUtilities;

/**
 * <code>PrereqHandlerTest</code> tests the operation of the
 * PrereqHandler class.
 */
@SuppressWarnings("nls")
public class PrereqHandlerTest extends AbstractCharacterTestCase
{
	/**
	 * Default constructor
	 * @param name
	 */
	public PrereqHandlerTest(final String name)
	{
		super(name);
	}

	/**
	 * Test the passes method. Currently this concentrates on making sure
	 * the invert result is working.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	public void testPasses() throws PersistenceLayerException
	{
		final PreParserFactory factory = PreParserFactory.getInstance();

		final Prerequisite prereqNeg = factory.parse("!PREALIGN:LG,LN,LE");
		final Prerequisite prereq = factory.parse("PREALIGN:NG,TN,NE,CG,CN,CE");

		final PlayerCharacter pc = getCharacter();
		final Race human = new Race();
		human.setName("Human");
		pc.setRace(human);

		AlignmentCompat.setCurrentAlignment(pc.getCharID(), le);
		assertEquals("Non-negate returns false", false, PrereqHandler.passes(
			prereq, pc, null));
		assertEquals("Negate returns false", false, PrereqHandler.passes(
			prereqNeg, pc, null));

		AlignmentCompat.setCurrentAlignment(pc.getCharID(), tn);
		assertEquals("Non-negate returns true", true, PrereqHandler.passes(
			prereq, pc, null));
		assertEquals("Negate returns true", true, PrereqHandler.passes(
			prereqNeg, pc, null));
	}

	/**
	 * Tests PREFEAT.
	 *
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	public void testFeatPasses() throws PersistenceLayerException
	{
		final PreParserFactory factory = PreParserFactory.getInstance();

		final Prerequisite prereq = factory.parse("!PREFEAT:1,Uncanny Dodge");
		final PlayerCharacter pc = getCharacter();
		final Race human = new Race();
		human.setName("Human");
		pc.setRace(human);

		assertTrue("No feat should return true", PrereqHandler.passes(prereq,
			pc, null));

		final Ability ud = new Ability();
		ud.setName("Uncanny Dodge");
		ud.setCDOMCategory(BuildUtilities.getFeatCat());
		ud.put(StringKey.KEY_NAME, "Uncanny Dodge");
		addAbility(BuildUtilities.getFeatCat(), ud);
		assertFalse("Feat should return false", PrereqHandler.passes(prereq,
			pc, null));
	}
}
