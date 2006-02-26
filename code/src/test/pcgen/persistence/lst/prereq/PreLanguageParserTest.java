/*
 * PreLanguageParserTest.java
 *
 * Copyright 2004 (C) Frugal <frugal@purplewombat.co.uk>
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
 * Current Ver: $Revision: 1.6 $
 *
 * Last Editor: $Author: karianna $
 *
 * Last Edited: $Date: 2005/09/12 11:48:36 $
 *
 */
package pcgen.persistence.lst.prereq;

import pcgen.core.prereq.Prerequisite;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class PreLanguageParserTest extends TestCase
{
	public static void main(String args[]) {
		junit.swingui.TestRunner.run(PreLanguageParserTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite() {
		return new TestSuite(PreLanguageParserTest.class);
	}

	/**
	 * @throws Exception
	 */
	public void test1LanguageOf2() throws Exception {
		PreLanguageParser parser = new PreLanguageParser();
		Prerequisite prereq = parser.parse("LANG", "1,Dwarven,Elven", false, false);

		System.out.println(prereq);
		assertEquals(
				"<prereq operator=\"gteq\" operand=\"1\" >\n"
				+"<prereq kind=\"lang\" count-multiples=\"true\" key=\"Dwarven\" operator=\"eq\" operand=\"1\" >\n"
				+"</prereq>\n"
				+"<prereq kind=\"lang\" count-multiples=\"true\" key=\"Elven\" operator=\"eq\" operand=\"1\" >\n"
				+"</prereq>\n"
				+"</prereq>\n",
				prereq.toString());
	}
	
	/**
	 * @throws Exception
	 */
	public void testNot1LanguageOf2() throws Exception {
		PreLanguageParser parser = new PreLanguageParser();
		Prerequisite prereq = parser.parse("LANG", "1,Dwarven,Elven", true, false);

		System.out.println(prereq);
		assertEquals(
				"<prereq operator=\"lt\" operand=\"1\" >\n"
				+"<prereq kind=\"lang\" count-multiples=\"true\" key=\"Dwarven\" operator=\"eq\" operand=\"1\" >\n"
				+"</prereq>\n"
				+"<prereq kind=\"lang\" count-multiples=\"true\" key=\"Elven\" operator=\"eq\" operand=\"1\" >\n"
				+"</prereq>\n"
				+"</prereq>\n",
				prereq.toString());
	}
	
	/**
	 * @throws Exception
	 */
	public void test2LanguageOfAny() throws Exception {
		PreLanguageParser parser = new PreLanguageParser();
		Prerequisite prereq = parser.parse("LANG", "2,ANY", false, false);

		System.out.println(prereq);
		assertEquals(
				"<prereq kind=\"lang\" count-multiples=\"true\" key=\"ANY\" operator=\"gteq\" operand=\"2\" >\n"
				+"</prereq>\n",
				prereq.toString());
	}
	
	/**
	 * @throws Exception
	 */
	public void testNot2LanguageOfAny() throws Exception {
		PreLanguageParser parser = new PreLanguageParser();
		Prerequisite prereq = parser.parse("LANG", "2,ANY", true, false);

		System.out.println(prereq);
		assertEquals(
				"<prereq kind=\"lang\" count-multiples=\"true\" key=\"ANY\" operator=\"lt\" operand=\"2\" >\n"
				+"</prereq>\n",
				prereq.toString());
	}
	
}
