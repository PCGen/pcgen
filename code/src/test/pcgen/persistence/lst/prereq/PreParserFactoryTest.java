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
 * Created on 18-Dec-2003
 *
 * Current Ver: $Revision: 1.10 $
 *
 * Last Editor: $Author: karianna $
 *
 * Last Edited: $Date: 2005/09/12 11:48:36 $
 *
 */
package pcgen.persistence.lst.prereq;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;
import pcgen.AbstractCharacterTestCase;
import pcgen.core.prereq.Prerequisite;

/**
 * @author wardc
 *
 */
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

	/**
	 * @throws Exception
	 */
	public void testNotEqual() throws Exception
	{
		PreParserFactory factory = PreParserFactory.getInstance();

		Prerequisite prereq = factory.parse("PREVARNEQ:Enraged,1");

		assertEquals("<prereq kind=\"var\" key=\"Enraged\" operator=\"neq\" operand=\"1\" >\n</prereq>\n", prereq.toString());
	}


	public void testSkillTypeKnowledge() throws Exception
	{
		PreParserFactory factory = PreParserFactory.getInstance();

		Prerequisite prereq = factory.parse("PRESKILLTOT:TYPE.Knowledge=20");

		assertEquals("<prereq operator=\"gteq\" operand=\"20\" >\n"
				+"<prereq kind=\"skill\" total-values=\"true\" key=\"TYPE.Knowledge\" operator=\"gteq\" operand=\"1\" >\n"
				+"</prereq>\n"
				+"</prereq>\n", prereq.toString());

	}

	public void testClear() throws Exception
	{
		PreParserFactory factory = PreParserFactory.getInstance();

		Prerequisite prereq = factory.parse("PRE:.clear");

		assertEquals("clear", prereq.getKind());
	}

	public void testInvertResult() throws Exception
	{
		PreParserFactory factory = PreParserFactory.getInstance();

		Prerequisite prereq = factory.parse("!PREALIGN:0,1,2");
		assertEquals("<prereq operator=\"lt\" operand=\"1\" >\n"
			+ "<prereq kind=\"align\" key=\"LG\" operator=\"eq\" operand=\"1\" >\n</prereq>\n"
			+ "<prereq kind=\"align\" key=\"LN\" operator=\"eq\" operand=\"1\" >\n</prereq>\n"
			+ "<prereq kind=\"align\" key=\"LE\" operator=\"eq\" operand=\"1\" >\n</prereq>\n"
			+ "</prereq>\n", prereq.toString());

		prereq = factory.parse("PREALIGN:3,4,5,6,7,8");
		assertEquals("<prereq operator=\"gteq\" operand=\"1\" >\n"
			+ "<prereq kind=\"align\" key=\"NG\" operator=\"eq\" operand=\"1\" >\n</prereq>\n"
			+ "<prereq kind=\"align\" key=\"TN\" operator=\"eq\" operand=\"1\" >\n</prereq>\n"
			+ "<prereq kind=\"align\" key=\"NE\" operator=\"eq\" operand=\"1\" >\n</prereq>\n"
			+ "<prereq kind=\"align\" key=\"CG\" operator=\"eq\" operand=\"1\" >\n</prereq>\n"
			+ "<prereq kind=\"align\" key=\"CN\" operator=\"eq\" operand=\"1\" >\n</prereq>\n"
			+ "<prereq kind=\"align\" key=\"CE\" operator=\"eq\" operand=\"1\" >\n</prereq>\n"
			+ "</prereq>\n", prereq.toString());
	}

}
