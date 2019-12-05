/*
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pcgen.persistence.lst.prereq;

import static org.junit.jupiter.api.Assertions.assertEquals;

import pcgen.EnUsLocaleDependentTestCase;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import plugin.pretokens.parser.PreSkillParser;

import org.junit.jupiter.api.Test;


public class PreSkillParserTest extends EnUsLocaleDependentTestCase
{

    @Test
    public void test1() throws PersistenceLayerException
    {
        PreSkillParser producer = new PreSkillParser();

        Prerequisite prereq =
                producer.parse("SKILL",
                        "3,Decipher Script=7,Disable Device=7,Escape Artist=7", false,
                        false);

        assertEquals(
                "<prereq operator=\"GTEQ\" operand=\"3\" >\n"
                        + "<prereq kind=\"skill\" count-multiples=\"true\" key=\"Decipher Script\" operator=\"GTEQ\" operand=\"7\" >\n"
                        + "</prereq>\n"
                        + "<prereq kind=\"skill\" count-multiples=\"true\" key=\"Disable Device\" operator=\"GTEQ\" operand=\"7\" >\n"
                        + "</prereq>\n"
                        + "<prereq kind=\"skill\" count-multiples=\"true\" key=\"Escape Artist\" operator=\"GTEQ\" operand=\"7\" >\n"
                        + "</prereq>\n" + "</prereq>\n", prereq.toString());
    }


    @Test
    public void test2() throws PersistenceLayerException
    {
        PreSkillParser producer = new PreSkillParser();

        Prerequisite prereq =
                producer.parse("SKILL", "2,TYPE.Knowledge=10,TYPE.Knowledge=10",
                        false, false);

        assertEquals(
                "<prereq operator=\"GTEQ\" operand=\"2\" >\n"
                        + "<prereq kind=\"skill\" count-multiples=\"true\" key=\"TYPE.Knowledge\" operator=\"GTEQ\" operand=\"10\" >\n"
                        + "</prereq>\n"
                        + "<prereq kind=\"skill\" count-multiples=\"true\" key=\"TYPE.Knowledge\" operator=\"GTEQ\" operand=\"10\" >\n"
                        + "</prereq>\n" + "</prereq>\n", prereq.toString());
    }

    @Test
    public void test3() throws Exception
    {
        PreSkillParser producer = new PreSkillParser();

        Prerequisite prereq =
                producer.parse("SKILL",
                        "3,TYPE.Knowledge=10", false,
                        false);

        assertEquals(
                "<prereq operator=\"GTEQ\" operand=\"3\" >\n"
                        + "<prereq kind=\"skill\" count-multiples=\"true\" key=\"TYPE.Knowledge\" operator=\"GTEQ\" operand=\"10\" >\n"
                        + "</prereq>\n" + "</prereq>\n", prereq.toString());
    }

    @Test
    public void testTypeEquals() throws Exception
    {
        PreSkillParser producer = new PreSkillParser();

        Prerequisite prereq =
                producer.parse("SKILL",
                        "3,TYPE=Knowledge=10", false,
                        false);

        assertEquals(
                "<prereq operator=\"GTEQ\" operand=\"3\" >\n"
                        + "<prereq kind=\"skill\" count-multiples=\"true\" key=\"TYPE=Knowledge\" operator=\"GTEQ\" operand=\"10\" >\n"
                        + "</prereq>\n" + "</prereq>\n", prereq.toString());
    }

    @Test
    public void testTypeEqualsMult() throws Exception
    {
        PreSkillParser producer = new PreSkillParser();

        Prerequisite prereq =
                producer.parse("SKILL",
                        "3,TYPE=Knowledge=10,TYPE=Craft=6", false,
                        false);

        assertEquals(
                "<prereq operator=\"GTEQ\" operand=\"3\" >\n"
                        + "<prereq kind=\"skill\" count-multiples=\"true\" key=\"TYPE=Knowledge\" operator=\"GTEQ\" operand=\"10\" >\n"
                        + "</prereq>\n"
                        + "<prereq kind=\"skill\" count-multiples=\"true\" key=\"TYPE=Craft\" operator=\"GTEQ\" operand=\"6\" >\n"
                        + "</prereq>\n"
                        + "</prereq>\n", prereq.toString());
    }
}
