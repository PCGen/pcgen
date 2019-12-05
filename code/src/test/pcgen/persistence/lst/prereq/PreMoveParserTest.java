/*
 *
 * To change the template for this generated file go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
package pcgen.persistence.lst.prereq;

import static org.junit.Assert.assertEquals;

import pcgen.EnUsLocaleDependentTestCase;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import plugin.pretokens.parser.PreMoveParser;

import org.junit.jupiter.api.Test;

/**
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code
 * and Comments
 */
@SuppressWarnings("nls")
public class PreMoveParserTest extends EnUsLocaleDependentTestCase
{

    /**
     * Test fly 1.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testFly1() throws PersistenceLayerException
    {
        PreMoveParser parser = new PreMoveParser();
        Prerequisite prereq = parser.parse("MOVE", "1,Fly=1", false, false);

        assertEquals(
                "<prereq kind=\"move\" key=\"Fly\" operator=\"GTEQ\" operand=\"1\" >\n</prereq>\n",
                prereq.toString());
    }


    /**
     * Test fly and walk.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testFlyAndWalk() throws PersistenceLayerException
    {
        PreMoveParser parser = new PreMoveParser();
        Prerequisite prereq =
                parser.parse("MOVE", "1,Walk=30,Fly=20", false, false);

        assertEquals(
                "<prereq operator=\"GTEQ\" operand=\"1\" >\n"
                        + "<prereq kind=\"move\" count-multiples=\"true\" key=\"Walk\" operator=\"GTEQ\" operand=\"30\" >\n"
                        + "</prereq>\n"
                        + "<prereq kind=\"move\" count-multiples=\"true\" key=\"Fly\" operator=\"GTEQ\" operand=\"20\" >\n"
                        + "</prereq>\n" + "</prereq>\n", prereq.toString());
    }


    /**
     * Test not fly 1.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testNotFly1() throws PersistenceLayerException
    {
        PreMoveParser parser = new PreMoveParser();
        Prerequisite prereq = parser.parse("MOVE", "1,Fly=1", true, false);

        assertEquals(
                "<prereq kind=\"move\" key=\"Fly\" operator=\"LT\" operand=\"1\" >\n</prereq>\n",
                prereq.toString());
    }


    /**
     * Test not fly and walk.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testNotFlyAndWalk() throws PersistenceLayerException
    {
        PreMoveParser parser = new PreMoveParser();
        Prerequisite prereq =
                parser.parse("MOVE", "1,Walk=30,Fly=20", true, false);

        assertEquals(
                "<prereq operator=\"LT\" operand=\"1\" >\n"
                        + "<prereq kind=\"move\" count-multiples=\"true\" key=\"Walk\" operator=\"GTEQ\" operand=\"30\" >\n"
                        + "</prereq>\n"
                        + "<prereq kind=\"move\" count-multiples=\"true\" key=\"Fly\" operator=\"GTEQ\" operand=\"20\" >\n"
                        + "</prereq>\n" + "</prereq>\n", prereq.toString());
    }

}
