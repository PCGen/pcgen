/*
 * PreRuleParserTest.java
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 * Created on February 6, 2007
 *
 * Current Ver: $Revision: 1777 $
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2006-12-17 15:36:01 +1100 (Sun, 17 Dec 2006) $
 *
 */
package pcgen.persistence.lst.prereq;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.core.prereq.Prerequisite;
import plugin.pretokens.parser.PreRuleParser;

/**
 * <code>PreRuleParserTest</code> is ...
 *
 * Last Editor: $Author:  $
 * Last Edited: $Date:  $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: $
 */
public class PreRuleParserTest extends TestCase
{

	/**
	 * Main
	 * @param args
	 */
	public static void main(String args[])
	{
		TestRunner.run(PreRuleParserTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreRuleParserTest.class);
	}

	/**
	 * @throws Exception
	 */
	public void testPositive() throws Exception
	{
		PreRuleParser parser = new PreRuleParser();
		Prerequisite prereq = parser.parse("RULE", "DISPLAYTYPETRAITS", false, false);

		assertEquals(
			"<prereq kind=\"rule\" key=\"DISPLAYTYPETRAITS\" operator=\"gteq\" operand=\"1\" >\n</prereq>\n",
			prereq.toString());
	}

	/**
	 * @throws Exception
	 */
	public void testNegative() throws Exception
	{
		PreRuleParser parser = new PreRuleParser();
		Prerequisite prereq = parser.parse("RULE", "DISPLAYTYPETRAITS", true, false);

		assertEquals(
			"<prereq kind=\"rule\" key=\"DISPLAYTYPETRAITS\" operator=\"lt\" operand=\"1\" >\n</prereq>\n",
			prereq.toString());
	}

}
