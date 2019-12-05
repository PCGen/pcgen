/*
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pcgen.persistence.lst.prereq;

import static org.junit.jupiter.api.Assertions.assertEquals;

import pcgen.EnUsLocaleDependentTestCase;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import plugin.pretokens.parser.PreSkillTotalParser;

import org.junit.jupiter.api.Test;


@SuppressWarnings("nls")
public class PreSkillTotParserTest extends EnUsLocaleDependentTestCase
{


    @Test
    public void test1() throws PersistenceLayerException
    {
        PreSkillTotalParser producer = new PreSkillTotalParser();

        Prerequisite prereq =
                producer.parse("SKILLTOT", "Spot,Listen,Search=30", false,
                        false);

        assertEquals(
                "<prereq operator=\"GTEQ\" operand=\"30\" >\n"
                        + "<prereq kind=\"skill\" total-values=\"true\" key=\"Spot\" operator=\"GTEQ\" operand=\"1\" >\n"
                        + "</prereq>\n"
                        + "<prereq kind=\"skill\" total-values=\"true\" key=\"Listen\" operator=\"GTEQ\" operand=\"1\" >\n"
                        + "</prereq>\n"
                        + "<prereq kind=\"skill\" total-values=\"true\" key=\"Search\" operator=\"GTEQ\" operand=\"1\" >\n"
                        + "</prereq>\n" + "</prereq>\n", prereq.toString());
    }


    /**
     * Test not.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testNot() throws PersistenceLayerException
    {
        PreSkillTotalParser producer = new PreSkillTotalParser();

        Prerequisite prereq =
                producer
                        .parse("SKILLTOT", "Spot,Listen,Search=30", true, false);

        assertEquals(
                "<prereq operator=\"LT\" operand=\"30\" >\n"
                        + "<prereq kind=\"skill\" total-values=\"true\" key=\"Spot\" operator=\"GTEQ\" operand=\"1\" >\n"
                        + "</prereq>\n"
                        + "<prereq kind=\"skill\" total-values=\"true\" key=\"Listen\" operator=\"GTEQ\" operand=\"1\" >\n"
                        + "</prereq>\n"
                        + "<prereq kind=\"skill\" total-values=\"true\" key=\"Search\" operator=\"GTEQ\" operand=\"1\" >\n"
                        + "</prereq>\n" + "</prereq>\n", prereq.toString());
    }

    @Test
    public void testTypeKnowledge() throws Exception
    {
        PreSkillTotalParser producer = new PreSkillTotalParser();

        Prerequisite prereq =
                producer.parse("SKILLTOT", "TYPE.Knowledge=20", false, false);

        assertEquals(
                "<prereq operator=\"GTEQ\" operand=\"20\" >\n"
                        + "<prereq kind=\"skill\" total-values=\"true\" key=\"TYPE.Knowledge\" operator=\"GTEQ\" operand=\"1\" >\n"
                        + "</prereq>\n" + "</prereq>\n", prereq.toString());

    }

}
