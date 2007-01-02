/*
 * Created on 21-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pcgen.persistence.lst.prereq;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import pcgen.core.prereq.Prerequisite;
import plugin.pretokens.parser.PreSkillTotalParser;

/**
 * @author Valued Customer
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PreSkillTotParserTest extends TestCase
{
	public static void main(String args[])
	{
		junit.swingui.TestRunner.run(PreSkillTotParserTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreSkillTotParserTest.class);
	}

	/**
	 * @throws Exception
	 */
	public void test1() throws Exception
	{
		PreSkillTotalParser producer = new PreSkillTotalParser();

		Prerequisite prereq =
				producer.parse("SKILLTOT", "Spot,Listen,Search=30", false,
					false);

		assertEquals(
			"<prereq operator=\"gteq\" operand=\"30\" >\n"
				+ "<prereq kind=\"skill\" total-values=\"true\" key=\"Spot\" operator=\"gteq\" operand=\"1\" >\n"
				+ "</prereq>\n"
				+ "<prereq kind=\"skill\" total-values=\"true\" key=\"Listen\" operator=\"gteq\" operand=\"1\" >\n"
				+ "</prereq>\n"
				+ "<prereq kind=\"skill\" total-values=\"true\" key=\"Search\" operator=\"gteq\" operand=\"1\" >\n"
				+ "</prereq>\n" + "</prereq>\n", prereq.toString());
	}

	/**
	 * @throws Exception
	 */
	public void testNot() throws Exception
	{
		PreSkillTotalParser producer = new PreSkillTotalParser();

		Prerequisite prereq =
				producer
					.parse("SKILLTOT", "Spot,Listen,Search=30", true, false);

		assertEquals(
			"<prereq operator=\"lt\" operand=\"30\" >\n"
				+ "<prereq kind=\"skill\" total-values=\"true\" key=\"Spot\" operator=\"gteq\" operand=\"1\" >\n"
				+ "</prereq>\n"
				+ "<prereq kind=\"skill\" total-values=\"true\" key=\"Listen\" operator=\"gteq\" operand=\"1\" >\n"
				+ "</prereq>\n"
				+ "<prereq kind=\"skill\" total-values=\"true\" key=\"Search\" operator=\"gteq\" operand=\"1\" >\n"
				+ "</prereq>\n" + "</prereq>\n", prereq.toString());
	}

	public void testTypeKnowledge() throws Exception
	{
		PreSkillTotalParser producer = new PreSkillTotalParser();

		Prerequisite prereq =
				producer.parse("SKILLTOT", "TYPE.Knowledge=20", false, false);

		assertEquals(
			"<prereq operator=\"gteq\" operand=\"20\" >\n"
				+ "<prereq kind=\"skill\" total-values=\"true\" key=\"TYPE.Knowledge\" operator=\"gteq\" operand=\"1\" >\n"
				+ "</prereq>\n" + "</prereq>\n", prereq.toString());

	}

}
