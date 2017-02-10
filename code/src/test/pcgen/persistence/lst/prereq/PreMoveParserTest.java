/*
 * 
 * To change the template for this generated file go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
package pcgen.persistence.lst.prereq;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pcgen.EnUsLocaleDependentTestCase;
import pcgen.core.prereq.Prerequisite;
import plugin.pretokens.parser.PreMoveParser;

/**
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code
 * and Comments
 */
@SuppressWarnings("nls")
public class PreMoveParserTest extends EnUsLocaleDependentTestCase
{
	/**
	 * @throws Exception
	 */
	@Test
	public void testFly1() throws Exception
	{
		PreMoveParser parser = new PreMoveParser();
		Prerequisite prereq = parser.parse("MOVE", "1,Fly=1", false, false);

		assertEquals(
			"<prereq kind=\"move\" key=\"Fly\" operator=\"GTEQ\" operand=\"1\" >\n</prereq>\n",
			prereq.toString());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testFlyAndWalk() throws Exception
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
	 * @throws Exception
	 */
	@Test
	public void testNotFly1() throws Exception
	{
		PreMoveParser parser = new PreMoveParser();
		Prerequisite prereq = parser.parse("MOVE", "1,Fly=1", true, false);

		assertEquals(
			"<prereq kind=\"move\" key=\"Fly\" operator=\"LT\" operand=\"1\" >\n</prereq>\n",
			prereq.toString());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testNotFlyAndWalk() throws Exception
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
