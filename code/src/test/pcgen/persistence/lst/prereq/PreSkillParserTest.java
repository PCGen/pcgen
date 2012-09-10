/*
 * Created on 27-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pcgen.persistence.lst.prereq;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pcgen.EnUsLocaleDependentTestCase;
import pcgen.core.prereq.Prerequisite;
import plugin.pretokens.parser.PreSkillParser;

/**
 * @author Valued Customer
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
@SuppressWarnings("nls")
public class PreSkillParserTest extends EnUsLocaleDependentTestCase
{
	/**
	 * @throws Exception
	 */
	@Test
	public void test1() throws Exception
	{
		PreSkillParser producer = new PreSkillParser();

		Prerequisite prereq =
				producer.parse("SKILL",
					"3,Decipher Script,Disable Device,Escape Artist=7", false,
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

	/**
	 * @throws Exception
	 */
	@Test
	public void test2() throws Exception
	{
		PreSkillParser producer = new PreSkillParser();

		Prerequisite prereq =
				producer.parse("SKILL", "2,TYPE.Knowledge,TYPE.Knowledge=10",
					false, false);

		assertEquals(
			"<prereq operator=\"GTEQ\" operand=\"2\" >\n"
				+ "<prereq kind=\"skill\" count-multiples=\"true\" key=\"TYPE.Knowledge\" operator=\"GTEQ\" operand=\"10\" >\n"
				+ "</prereq>\n" + "</prereq>\n", prereq.toString());
	}

	@Test
	public void test3() throws Exception
	{
		PreSkillParser producer = new PreSkillParser();

		Prerequisite prereq =
				producer.parse("SKILL",
					"3,TYPE.Knowledge,TYPE.Knowledge,TYPE.Knowledge=10", false,
					false);

		assertEquals(
			"<prereq operator=\"GTEQ\" operand=\"3\" >\n"
				+ "<prereq kind=\"skill\" count-multiples=\"true\" key=\"TYPE.Knowledge\" operator=\"GTEQ\" operand=\"10\" >\n"
				+ "</prereq>\n" + "</prereq>\n", prereq.toString());
	}
}
