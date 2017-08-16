package pcgen.core;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;

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
	 * @throws PersistenceLayerException
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

		pc.setAlignment(le);
		assertEquals("Non-negate returns false", false, PrereqHandler.passes(
			prereq, pc, null));
		assertEquals("Negate returns false", false, PrereqHandler.passes(
			prereqNeg, pc, null));

		pc.setAlignment(tn);
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

		assertTrue("No feat should return true", PrereqHandler.passes(prereq,
			pc, null));

		final Ability ud = new Ability();
		ud.setName("Uncanny Dodge");
		ud.setCDOMCategory(AbilityCategory.FEAT);
		ud.put(StringKey.KEY_NAME, "Uncanny Dodge");
		addAbility(AbilityCategory.FEAT, ud);
		assertFalse("Feat should return false", PrereqHandler.passes(prereq,
			pc, null));
	}
}
