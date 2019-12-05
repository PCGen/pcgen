package pcgen.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
public class PrereqHandlerTest extends AbstractCharacterTestCase
{
    /**
     * Test the passes method. Currently this concentrates on making sure
     * the invert result is working.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
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
        assertFalse("Non-negate returns false", PrereqHandler.passes(
                prereq, pc, null));
        assertFalse("Negate returns false", PrereqHandler.passes(
                prereqNeg, pc, null));

        AlignmentCompat.setCurrentAlignment(pc.getCharID(), tn);
        assertTrue("Non-negate returns true", PrereqHandler.passes(
                prereq, pc, null));
        assertTrue("Negate returns true", PrereqHandler.passes(
                prereqNeg, pc, null));
    }

    /**
     * Tests PREFEAT.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
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
