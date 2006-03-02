/*
 * PreFeatParserTest.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on November 28, 2003
 *
 * Current Ver: $Revision: 1.12 $
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2006/01/29 03:57:14 $
 *
 */
package pcgen.persistence.lst.prereq;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;
import pcgen.core.prereq.Prerequisite;
import plugin.pretokens.parser.PreFeatParser;

/**
 * @author wardc
 *
 */
public class PreFeatParserTest extends TestCase
{
	public static void main(String[] args)
	{
		TestRunner.run(PreFeatParserTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreFeatParserTest.class);
	}

	/**
	 * @throws Exception
	 */
	public void testFeat1() throws Exception
	{
		PreFeatParser parser = new PreFeatParser();
		// "PREFEAT:1,Alertness";

		Prerequisite prereq = parser.parse("feat", "1,Alertness", false, false);
		
		assertEquals("<prereq kind=\"feat\" key=\"Alertness\" operator=\"gteq\" operand=\"1\" >\n"+
				"</prereq>\n", prereq.toString());
	}

	/**
	 * @throws Exception
	 */
	public void testFeatOldStyle() throws Exception
	{
		PreFeatParser parser = new PreFeatParser();
		// "PREFEAT:Alertness|Cleave";

		Prerequisite prereq = parser.parse("feat", "Alertness|Cleave", false, false);
		assertEquals("<prereq operator=\"gteq\" operand=\"1\" >\n"+
				"<prereq kind=\"feat\" key=\"Alertness\" operator=\"gteq\" operand=\"1\" >\n"+
				"</prereq>\n" + 
				"<prereq kind=\"feat\" key=\"Cleave\" operator=\"gteq\" operand=\"1\" >\n"+
				"</prereq>\n" + 
				"</prereq>\n", prereq.toString());
	}

	/**
	 * @throws Exception
	 */
	public void testSubFeat() throws Exception
	{
		PreFeatParser parser = new PreFeatParser();

		Prerequisite prereq = parser.parse("feat", "1,Weapon Focus (Rapier)", false, false);
		
		assertEquals("<prereq kind=\"feat\" key=\"Weapon Focus\" sub-key=\"Rapier\" operator=\"gteq\" operand=\"1\" >\n"+
				"</prereq>\n", prereq.toString());
	}
	
	/**
	 * @throws Exception
	 */
	public void test966023() throws Exception
	{
		PreFeatParser parser = new PreFeatParser();
		String bonus = "1,Spell Focus(Conjuration)";

		Prerequisite prereq = parser.parse("feat", bonus, false, false);
		
		assertEquals("<prereq kind=\"feat\" key=\"Spell Focus\" sub-key=\"Conjuration\" operator=\"gteq\" operand=\"1\" >\n"+
				"</prereq>\n", prereq.toString());
	}
	
	
	/**
	 * @throws Exception
	 */
	public void testTwoOfType() throws Exception
	{
		PreFeatParser parser = new PreFeatParser();

		Prerequisite prereq = parser.parse("feat", "2,TYPE=ItemCreation", false, false);
		
		assertEquals("<prereq kind=\"feat\" key=\"TYPE=ItemCreation\" operator=\"gteq\" operand=\"2\" >\n"
				+"</prereq>\n", prereq.toString());
	}
	
	
	/**
	 * Test that exclusions are parsed properly.
	 * @throws Exception
	 */
	public void testExclusions() throws Exception
	{
		PreFeatParser parser = new PreFeatParser();

		Prerequisite prereq = parser.parse("feat", "2,TYPE=ItemCreation,[Scribe Scroll]", false, false);
		
		assertEquals("<prereq operator=\"gteq\" operand=\"3\" >\n" +
				"<prereq kind=\"feat\" count-multiples=\"true\" key=\"TYPE=ItemCreation\" operator=\"gteq\" operand=\"1\" >\n" +
				"</prereq>\n" +
				"<prereq kind=\"feat\" count-multiples=\"true\" key=\"Scribe Scroll\" operator=\"lt\" operand=\"1\" >\n" +
				"</prereq>\n</prereq>\n", prereq.toString());
	}
}
