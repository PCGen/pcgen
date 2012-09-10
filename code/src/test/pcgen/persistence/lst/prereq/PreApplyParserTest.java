/*
 * PreApplyParser.java
 * 
 * Copyright 2004 (C) Frugal <frugal@purplewombat.co.uk>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Created on 18-Dec-2003
 * 
 * Current Ver: $Revision$
 * 
 * Last Editor: $Author$
 * 
 * Last Edited: $Date$
 *  
 */
package pcgen.persistence.lst.prereq;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pcgen.EnUsLocaleDependentTestCase;
import pcgen.core.prereq.Prerequisite;
import plugin.pretokens.parser.PreApplyParser;

@SuppressWarnings("nls")
public class PreApplyParserTest extends EnUsLocaleDependentTestCase
{
	/**
	 * @throws Exception
	 */
	@Test
	public void testAnyPc() throws Exception
	{
		PreApplyParser parser = new PreApplyParser();
		Prerequisite prereq = parser.parse("APPLY", "ANYPC", false, false);

		//System.out.println(prereq);
		assertEquals("<prereq kind=\"APPLY\" operator=\"EQ\" operand=\"1\" >\n"
			+ "<prereq kind=\"APPLY\" operator=\"EQ\" operand=\"ANYPC\" >\n"
			+ "</prereq>\n" + "</prereq>\n", prereq.toString());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testRangedOrMelee() throws Exception
	{
		PreApplyParser parser = new PreApplyParser();
		Prerequisite prereq =
				parser.parse("APPLY", "Ranged;Melee", false, false);

		//System.out.println(prereq);
		assertEquals("<prereq kind=\"APPLY\" operator=\"EQ\" operand=\"1\" >\n"
			+ "<prereq operator=\"GTEQ\" operand=\"1\" >\n"
			+ "<prereq kind=\"APPLY\" operator=\"EQ\" operand=\"Ranged\" >\n"
			+ "</prereq>\n"
			+ "<prereq kind=\"APPLY\" operator=\"EQ\" operand=\"Melee\" >\n"
			+ "</prereq>\n" + "</prereq>\n" + "</prereq>\n", prereq.toString());
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testWoodenAndBlunt() throws Exception
	{
		PreApplyParser parser = new PreApplyParser();
		Prerequisite prereq =
				parser.parse("APPLY", "Wooden,Blunt", false, false);

		//System.out.println(prereq);
		assertEquals("<prereq kind=\"APPLY\" operator=\"EQ\" operand=\"2\" >\n"
			+ "<prereq kind=\"APPLY\" operator=\"EQ\" operand=\"Wooden\" >\n"
			+ "</prereq>\n"
			+ "<prereq kind=\"APPLY\" operator=\"EQ\" operand=\"Blunt\" >\n"
			+ "</prereq>\n" + "</prereq>\n", prereq.toString());
	}

}
