/*
 * PreMultParserTest.java
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
 * Current Ver: $Revision$
 *
 * Last Editor: $Author$
 *
 * Last Edited: $Date$
 *
 */
package pcgen.persistence.lst.prereq;

import gmgen.pluginmgr.PluginLoader;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;
import pcgen.core.Constants;
import pcgen.core.prereq.Prerequisite;

/**
 * @author wardc
 *
 */
public class PreMultParserTest extends TestCase
{
//[PREARMORPROF:1,TYPE.Medium],[PREFEAT:1,Armor Proficiency (Medium)]
	public static void main(String[] args)
	{
		TestRunner.run(PreMultParserTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreMultParserTest.class);
	}

	public void setUp() throws Exception {
		try {
			PluginLoader ploader = PluginLoader.inst();
			ploader.startSystemPlugins(Constants.s_SYSTEM_TOKENS);
		}
		catch(Exception e) {
			// TODO Handle Exception
		}
	}

	/**
	 * @throws Exception
	 */
	public void testFeat1() throws Exception
	{
		PreMultParser parser = new PreMultParser();
		
		Prerequisite prereq = parser.parse("mult", "1,[PREARMORPROF:1,TYPE.Medium],[PREFEAT:1,Armor Proficiency (Medium)]", false, false);
		
		assertEquals("<prereq operator=\"gteq\" operand=\"1\" >\n"+
				"<prereq kind=\"armorprof\" key=\"TYPE.Medium\" operator=\"gteq\" operand=\"1\" >\n"+
				"</prereq>\n"+
				"<prereq kind=\"feat\" key=\"Armor Proficiency\" sub-key=\"Medium\" operator=\"gteq\" operand=\"1\" >\n"+
				"</prereq>\n" +
				"</prereq>\n", prereq.toString());
	}
}
