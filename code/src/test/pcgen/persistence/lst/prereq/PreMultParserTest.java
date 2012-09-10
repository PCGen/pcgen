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

import static org.junit.Assert.assertEquals;
import gmgen.pluginmgr.PluginLoader;

import org.junit.Before;
import org.junit.Test;

import pcgen.EnUsLocaleDependentTestCase;
import pcgen.cdom.base.Constants;
import pcgen.core.prereq.Prerequisite;

/*** Test
	 * [PREARMORPROF:1,TYPE.Medium],[PREFEAT:1,Armor Proficiency (Medium)]
 * @author wardc
 *
 */
@SuppressWarnings("nls")
public class PreMultParserTest extends EnUsLocaleDependentTestCase
{
	@Before
	public void setUp() throws Exception
	{
		try
		{
			PluginLoader ploader = PluginLoader.inst();
			ploader.startSystemPlugins(Constants.SYSTEM_TOKENS);
		}
		catch (Exception e)
		{
			// TODO Handle Exception
		}
	}

	@Test
	public void testFeat1() throws Exception
	{
		PreMultParser parser = new PreMultParser();

		Prerequisite prereq =
				parser
					.parse(
						"mult",
						"1,[PREPROFWITHARMOR:1,TYPE.Medium],[PREFEAT:1,Armor Proficiency (Medium)]",
						false, false);

		assertEquals(
			"<prereq operator=\"GTEQ\" operand=\"1\" >\n"
				+ "<prereq kind=\"profwitharmor\" key=\"TYPE.Medium\" operator=\"GTEQ\" operand=\"1\" >\n"
				+ "</prereq>\n"
				+ "<prereq kind=\"feat\" key=\"Armor Proficiency\" sub-key=\"Medium\" operator=\"GTEQ\" operand=\"1\" >\n"
				+ "</prereq>\n" + "</prereq>\n", prereq.toString());
	}
}
