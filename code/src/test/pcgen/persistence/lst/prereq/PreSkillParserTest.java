/*
 * Created on 27-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pcgen.persistence.lst.prereq;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import pcgen.core.prereq.Prerequisite;
import plugin.pretokens.parser.PreSkillParser;

/**
 * @author Valued Customer
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class PreSkillParserTest extends TestCase {
    public static void main(String args[]) {
        junit.swingui.TestRunner.run(PreSkillParserTest.class);
    }

    /**
     * @return Test
     */
    public static Test suite() {
        return new TestSuite(PreSkillParserTest.class);
    }

    /**
     * @throws Exception
     */
    public void test1() throws Exception {
        PreSkillParser producer = new PreSkillParser();

        Prerequisite prereq = producer.parse("SKILL",
                "3,Decipher Script,Disable Device,Escape Artist=7", false,
                false);

        assertEquals(
                "<prereq operator=\"gteq\" operand=\"3\" >\n"
                        + "<prereq kind=\"skill\" count-multiples=\"true\" key=\"Decipher Script\" operator=\"gteq\" operand=\"7\" >\n"
                        + "</prereq>\n"
                        + "<prereq kind=\"skill\" count-multiples=\"true\" key=\"Disable Device\" operator=\"gteq\" operand=\"7\" >\n"
                        + "</prereq>\n"
                        + "<prereq kind=\"skill\" count-multiples=\"true\" key=\"Escape Artist\" operator=\"gteq\" operand=\"7\" >\n"
                        + "</prereq>\n" + "</prereq>\n", prereq.toString());
    }

    /**
     * @throws Exception
     */
    public void test2() throws Exception {
        PreSkillParser producer = new PreSkillParser();

        Prerequisite prereq = producer.parse("SKILL",
                "2,TYPE.Knowledge,TYPE.Knowledge=10", false, false);

        assertEquals(
                "<prereq operator=\"gteq\" operand=\"2\" >\n"
                        + "<prereq kind=\"skill\" count-multiples=\"true\" key=\"TYPE.Knowledge\" operator=\"gteq\" operand=\"10\" >\n"
                        + "</prereq>\n" + "</prereq>\n", prereq.toString());
    }

    public void test3() throws Exception {
        PreSkillParser producer = new PreSkillParser();

        Prerequisite prereq = producer.parse("SKILL",
                "3,TYPE.Knowledge,TYPE.Knowledge,TYPE.Knowledge=10", false,
                false);

        assertEquals(
                "<prereq operator=\"gteq\" operand=\"3\" >\n"
                        + "<prereq kind=\"skill\" count-multiples=\"true\" key=\"TYPE.Knowledge\" operator=\"gteq\" operand=\"10\" >\n"
                        + "</prereq>\n" + "</prereq>\n", prereq.toString());
    }
}
