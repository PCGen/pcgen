/*
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
 */
package pcgen.persistence.lst.prereq;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pcgen.EnUsLocaleDependentTestCase;
import pcgen.core.prereq.Prerequisite;
import plugin.pretokens.parser.PreTypeParser;

@SuppressWarnings("nls")
public class PreTypeParserTest extends EnUsLocaleDependentTestCase
{
	/**
	 * @throws Exception
	 */
	@Test
	public void testNewStyle() throws Exception
	{
		PreTypeParser producer = new PreTypeParser();

		Prerequisite prereq =
				producer.parse("TYPE", "1,Mithral,Adamantine,Darkwood", false,
					false);

		System.out.println(prereq.toString());
		assertEquals(
			"<prereq operator=\"GTEQ\" operand=\"1\" >\n"
				+ "<prereq kind=\"type\" key=\"Mithral\" operator=\"EQ\" operand=\"1\" >\n"
				+ "</prereq>\n"
				+ "<prereq kind=\"type\" key=\"Adamantine\" operator=\"EQ\" operand=\"1\" >\n"
				+ "</prereq>\n"
				+ "<prereq kind=\"type\" key=\"Darkwood\" operator=\"EQ\" operand=\"1\" >\n"
				+ "</prereq>\n" + "</prereq>\n", prereq.toString());
	}

	/**
	 * @throws Exception
	 */
	@Test
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
		 */assertEquals(
			"<prereq kind=\"TYPE\" key=\"Animal\" operator=\"NEQ\" operand=\"1\" >\n"
				+ "</prereq>\n", prereq.toString());
	}

}
