package pcgen.core;

import pcgen.AbstractCharacterTestCase;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * <code>PrereqHandlerTest</code> tests the operation of the
 * PrereqHandler class.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
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
	 * @throws PersistenceLayerException
	 */
	public void testPasses() throws PersistenceLayerException
	{
		final PreParserFactory factory = PreParserFactory.getInstance();

		final Prerequisite prereqNeg = factory.parse("!PREALIGN:0,1,2");
		final Prerequisite prereq = factory.parse("PREALIGN:3,4,5,6,7,8");

		final PlayerCharacter pc = getCharacter();
		final Race human = new Race();
		human.setName("Human");
		pc.setRace(human);
		Globals.setCurrentPC(pc);

		pc.setAlignment(2, true);
		assertEquals("Non-negate returns false", false, PrereqHandler.passes(
			prereq, pc, null));
		assertEquals("Negate returns false", false, PrereqHandler.passes(
			prereqNeg, pc, null));

		pc.setAlignment(4, true);
		assertEquals("Non-negate returns true", true, PrereqHandler.passes(
			prereq, pc, null));
		assertEquals("Negate returns true", true, PrereqHandler.passes(
			prereqNeg, pc, null));
	}

	/**
	 * Tests PREFEAT
	 * @throws PersistenceLayerException
	 */
	public void testFeatPasses() throws PersistenceLayerException
	{
		final PreParserFactory factory = PreParserFactory.getInstance();

		final Prerequisite prereq = factory.parse("!PREFEAT:1,Uncanny Dodge");
		final PlayerCharacter pc = getCharacter();
		final Race human = new Race();
		human.setName("Human");
		pc.setRace(human);
		Globals.setCurrentPC(pc);

		assertTrue("No feat should return true", PrereqHandler.passes(
			prereq, pc, null));

		final Ability ud = new Ability();
		ud.setName("Uncanny Dodge");
		ud.setKeyName("Uncanny Dodge");
		pc.addFeat(ud, null);
		assertFalse("Feat should return false", PrereqHandler.passes(
			prereq, pc, null));
	}
}
