/*
 * PreParserFactoryTest.java
 *
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 *
 *
 *
 */
package pcgen.persistence.lst.prereq;

import java.util.Locale;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.AbstractCharacterTestCase;
import pcgen.LocaleDependentTestCase;
import pcgen.core.prereq.Prerequisite;

/**
 *
 */
@SuppressWarnings("nls")
public class PreParserFactoryTest extends AbstractCharacterTestCase
{
	public static void main(String[] args)
	{
		TestRunner.run(PreParserFactoryTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreParserFactoryTest.class);
	}

	/* (non-Javadoc)
	 * @see pcgen.AbstractCharacterTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		LocaleDependentTestCase.before(Locale.US);
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
		LocaleDependentTestCase.after();
	}
	
	/**
	 * @throws Exception
	 */
	public void testNotEqual() throws Exception
	{
		PreParserFactory factory = PreParserFactory.getInstance();

		Prerequisite prereq = factory.parse("PREVARNEQ:Enraged,1");

		assertEquals(
			"<prereq kind=\"var\" key=\"Enraged\" operator=\"NEQ\" operand=\"1\" >\n</prereq>\n",
			prereq.toString());
	}

	/**
	 * @throws Exception
	 */
	public void testOverrideQualifies() throws Exception
	{
		PreParserFactory factory = PreParserFactory.getInstance();

		Prerequisite prereq = factory.parse("PREVARNEQ:Q:Enraged,1");

		assertEquals(
			"<prereq kind=\"var\" key=\"Enraged\" operator=\"NEQ\" operand=\"1\" override-qualify=\"true\" >\n</prereq>\n",
			prereq.toString());
	}

	public void testSkillTypeKnowledge() throws Exception
	{
		PreParserFactory factory = PreParserFactory.getInstance();

		Prerequisite prereq = factory.parse("PRESKILLTOT:TYPE.Knowledge=20");

		assertEquals(
			"<prereq operator=\"GTEQ\" operand=\"20\" >\n"
				+ "<prereq kind=\"skill\" total-values=\"true\" key=\"TYPE.Knowledge\" operator=\"GTEQ\" operand=\"1\" >\n"
				+ "</prereq>\n" + "</prereq>\n", prereq.toString());

	}

	public void testInvertResult() throws Exception
	{
		PreParserFactory factory = PreParserFactory.getInstance();

		Prerequisite prereq = factory.parse("!PREALIGN:LG,LN,LE");
		assertEquals(
			"<prereq operator=\"LT\" operand=\"1\" >\n"
				+ "<prereq kind=\"align\" key=\"LG\" operator=\"EQ\" operand=\"1\" >\n</prereq>\n"
				+ "<prereq kind=\"align\" key=\"LN\" operator=\"EQ\" operand=\"1\" >\n</prereq>\n"
				+ "<prereq kind=\"align\" key=\"LE\" operator=\"EQ\" operand=\"1\" >\n</prereq>\n"
				+ "</prereq>\n", prereq.toString());

		prereq = factory.parse("PREALIGN:NG,TN,NE,CG,CN,CE");
		assertEquals(
			"<prereq operator=\"GTEQ\" operand=\"1\" >\n"
				+ "<prereq kind=\"align\" key=\"NG\" operator=\"EQ\" operand=\"1\" >\n</prereq>\n"
				+ "<prereq kind=\"align\" key=\"TN\" operator=\"EQ\" operand=\"1\" >\n</prereq>\n"
				+ "<prereq kind=\"align\" key=\"NE\" operator=\"EQ\" operand=\"1\" >\n</prereq>\n"
				+ "<prereq kind=\"align\" key=\"CG\" operator=\"EQ\" operand=\"1\" >\n</prereq>\n"
				+ "<prereq kind=\"align\" key=\"CN\" operator=\"EQ\" operand=\"1\" >\n</prereq>\n"
				+ "<prereq kind=\"align\" key=\"CE\" operator=\"EQ\" operand=\"1\" >\n</prereq>\n"
				+ "</prereq>\n", prereq.toString());
	}

}
