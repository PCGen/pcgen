/*
 * PreText.java
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 01-Jan-2004
 *
 * Current Ver: $Revision: 1.11 $
 * Last Editor: $Author: byngl $
 * Last Edited: $Date: 2005/09/27 18:22:47 $
 *
 */
package pcgen.persistence.lst.prereq;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;
import pcgen.core.prereq.Prerequisite;

public class PreTypeParserTest extends TestCase {
	public static void main(String[] args)
	{
		TestRunner.run(PreTypeParserTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreTypeParserTest.class);
	}

	/**
	 * @throws Exception
	 */
	public void testAndOrNot() throws Exception
	{
		PreTypeParser producer = new PreTypeParser();

		Prerequisite prereq = producer.parse("TYPE", "type1,type2|type3,[type4]", false, false);

		System.out.println("testAndOrNot returned:");
		System.out.println(prereq.toString());
/*		assertEquals("<prereq operator=\"eq\" operand=\"3\" >\n"
+"<prereq kind=\"type\" key=\"type1\" operator=\"eq\" operand=\"1\" >\n"
+"</prereq>\n"
+"<prereq operator=\"gteq\" operand=\"1\" >\n"
+"<prereq kind=\"type\" key=\"type2\" operator=\"eq\" operand=\"1\" >\n"
+"</prereq>\n"
+"<prereq kind=\"type\" key=\"type3\" operator=\"eq\" operand=\"1\" >\n"
+"</prereq>\n"
+"</prereq>\n"
+"<prereq kind=\"type\" key=\"type4\" operator=\"neq\" operand=\"1\" >\n"
+"</prereq>\n"
+"</prereq>\n",
				prereq.toString());
*/		assertEquals(
				"<prereq operator=\"gteq\" operand=\"3\" >\n" +
				"<prereq kind=\"type\" key=\"type1\" operator=\"eq\" operand=\"1\" >\n" +
				"</prereq>\n" +
				"<prereq operator=\"gteq\" operand=\"1\" >\n" +
				"<prereq kind=\"type\" key=\"type2\" operator=\"eq\" operand=\"1\" >\n" +
				"</prereq>\n" +
				"<prereq kind=\"type\" key=\"type3\" operator=\"eq\" operand=\"1\" >\n" +
				"</prereq>\n" +
				"</prereq>\n" +
				"<prereq kind=\"type\" key=\"type4\" operator=\"neq\" operand=\"1\" >\n" +
				"</prereq>\n" +
				"</prereq>\n",
				prereq.toString());
	}
	
	/**
	 * @throws Exception
	 */
	public void testOr3() throws Exception
	{
		PreTypeParser producer = new PreTypeParser();

		Prerequisite prereq = producer.parse("TYPE", "Mithral|Adamantine|Darkwood", true, false);

		System.out.println("testAndOrNot returned:");
		System.out.println(prereq.toString());
/*		assertEquals("<prereq operator=\"neq\" operand=\"1\" >\n" +
				"<prereq operator=\"gteq\" operand=\"1\" >\n" +
				"<prereq kind=\"type\" key=\"Mithral\" operator=\"eq\" operand=\"1\" >\n" +
				"</prereq>\n" +
				"<prereq kind=\"type\" key=\"Adamantine\" operator=\"eq\" operand=\"1\" >\n" +
				"</prereq>\n" +
				"<prereq kind=\"type\" key=\"Darkwood\" operator=\"eq\" operand=\"1\" >\n" +
				"</prereq>\n" +
				"</prereq>\n" +
				"</prereq>\n",
				prereq.toString());
*/		assertEquals(
				"<prereq operator=\"lt\" operand=\"1\" >\n" +
				"<prereq operator=\"gteq\" operand=\"1\" >\n" +
				"<prereq kind=\"type\" key=\"Mithral\" operator=\"eq\" operand=\"1\" >\n" +
				"</prereq>\n" +
				"<prereq kind=\"type\" key=\"Adamantine\" operator=\"eq\" operand=\"1\" >\n" +
				"</prereq>\n" +
				"<prereq kind=\"type\" key=\"Darkwood\" operator=\"eq\" operand=\"1\" >\n" +
				"</prereq>\n" +
				"</prereq>\n" +
				"</prereq>\n",
				prereq.toString());
	}
	
	/**
	 * @throws Exception
	 */
	public void testNewStyle() throws Exception
	{
		PreTypeParser producer = new PreTypeParser();

		Prerequisite prereq = producer.parse("TYPE", "1,Mithral,Adamantine,Darkwood", false, false);

		System.out.println(prereq.toString());
		assertEquals("<prereq operator=\"gteq\" operand=\"1\" >\n" +
				"<prereq kind=\"type\" key=\"Mithral\" operator=\"eq\" operand=\"1\" >\n" +
				"</prereq>\n" +
				"<prereq kind=\"type\" key=\"Adamantine\" operator=\"eq\" operand=\"1\" >\n" +
				"</prereq>\n" +
				"<prereq kind=\"type\" key=\"Darkwood\" operator=\"eq\" operand=\"1\" >\n" +
				"</prereq>\n" +
				"</prereq>\n",
				prereq.toString());
	}

	/**
	 * @throws Exception
	 */
	public void testNewStyle2() throws Exception
	{
		PreTypeParser producer = new PreTypeParser();

		Prerequisite prereq = producer.parse("TYPE", "1,Animal", true, false);

		System.out.println(prereq.toString());
/*		assertEquals("<prereq operator=\"lt\" operand=\"1\" >\n" + 
				"<prereq kind=\"type\" key=\"Animal\" operator=\"eq\" operand=\"1\" >\n" + 
				"</prereq>\n" + 
				"</prereq>\n",
				prereq.toString());
*/		assertEquals(
				"<prereq kind=\"TYPE\" key=\"Animal\" operator=\"neq\" operand=\"1\" >\n" +
				"</prereq>\n",
				prereq.toString());
	}
	
}
