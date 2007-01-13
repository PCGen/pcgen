/*
 * Created on 22-Dec-2003
 * 
 * To change the template for this generated file go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
package pcgen.persistence.lst.prereq;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.core.prereq.Prerequisite;
import plugin.pretokens.parser.PreMoveParser;

/**
 * @author Valued Customer
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code
 * and Comments
 */
public class PreMoveParserTest extends TestCase
{

	/**
	 * Main
	 * @param args
	 */
	public static void main(String args[])
	{
		TestRunner.run(PreMoveParserTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreMoveParserTest.class);
	}

	/**
	 * @throws Exception
	 */
	public void testFly1() throws Exception
	{
		PreMoveParser parser = new PreMoveParser();
		Prerequisite prereq = parser.parse("MOVE", "1,Fly=1", false, false);

		assertEquals(
			"<prereq kind=\"move\" key=\"Fly\" operator=\"gteq\" operand=\"1\" >\n</prereq>\n",
			prereq.toString());
	}

	/**
	 * @throws Exception
	 */
	public void testFlyAndWalk() throws Exception
	{
		PreMoveParser parser = new PreMoveParser();
		Prerequisite prereq =
				parser.parse("MOVE", "1,Walk=30,Fly=20", false, false);

		assertEquals(
			"<prereq operator=\"gteq\" operand=\"1\" >\n"
				+ "<prereq kind=\"move\" count-multiples=\"true\" key=\"Walk\" operator=\"gteq\" operand=\"30\" >\n"
				+ "</prereq>\n"
				+ "<prereq kind=\"move\" count-multiples=\"true\" key=\"Fly\" operator=\"gteq\" operand=\"20\" >\n"
				+ "</prereq>\n" + "</prereq>\n", prereq.toString());
	}

	/**
	 * @throws Exception
	 */
	public void testNotFly1() throws Exception
	{
		PreMoveParser parser = new PreMoveParser();
		Prerequisite prereq = parser.parse("MOVE", "1,Fly=1", true, false);

		assertEquals(
			"<prereq kind=\"move\" key=\"Fly\" operator=\"lt\" operand=\"1\" >\n</prereq>\n",
			prereq.toString());
	}

	/**
	 * @throws Exception
	 */
	public void testNotFlyAndWalk() throws Exception
	{
		PreMoveParser parser = new PreMoveParser();
		Prerequisite prereq =
				parser.parse("MOVE", "1,Walk=30,Fly=20", true, false);

		assertEquals(
			"<prereq operator=\"lt\" operand=\"1\" >\n"
				+ "<prereq kind=\"move\" count-multiples=\"true\" key=\"Walk\" operator=\"gteq\" operand=\"30\" >\n"
				+ "</prereq>\n"
				+ "<prereq kind=\"move\" count-multiples=\"true\" key=\"Fly\" operator=\"gteq\" operand=\"20\" >\n"
				+ "</prereq>\n" + "</prereq>\n", prereq.toString());
	}

}
