/*
 * PObjectLoaderTest.java
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 13-Jan-2004
 *
 * Current Ver: $Revision$
 * 
 * Last Editor: $Author$
 * 
 * Last Edited: $Date$
 *
 */
package pcgen.persistence.lst;

import gmgen.pluginmgr.PluginLoader;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import pcgen.PCGenTestCase;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.core.Ability;
import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.core.PObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public class PObjectLoaderTest extends PCGenTestCase
{
	public PObjectLoaderTest(String name)
	{
		super(name);
	}

	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(PObjectLoaderTest.class);
	}

	public static Test suite()
	{
		// quick method, adds all methods beginning with "test"
		return new TestSuite(PObjectLoaderTest.class);
	}

	public void testDefine() throws Exception
	{
		PluginLoader ploader = PluginLoader.inst();
		ploader.startSystemPlugins(Constants.SYSTEM_TOKENS);
		Ability feat = new Ability();

		Globals.getContext().unconditionallyProcess(feat, "DEFINE", "Foo|0");

		assertEquals(1, feat.getVariableKeys().size());
		assertEquals("Foo", feat.getVariableKeys().iterator().next().toString());
		assertEquals("0", feat.get(VariableKey.getConstant("Foo")).toString());
	}

	public void testBadDefine() throws Exception
	{
		PluginLoader ploader = PluginLoader.inst();
		ploader.startSystemPlugins(Constants.SYSTEM_TOKENS);

		Ability feat = new Ability();

		try
		{
			is(Globals.getContext().processToken(feat, "DEFINE", "Foo"), eq(false),
				"Parse fails for badly formed define");
		}
		catch (PersistenceLayerException ple)
		{
			fail("parseTag throws exception instead of passing back false");
		}
	}

	public void testUnlockDefine() throws Exception
	{
		LoadContext context = Globals.getContext();
		
		PCStat con = new PCStat();
		con.setName("Constitution");
		con.put(StringKey.ABB, "CON");
		context.ref.registerAbbreviation(con, con.getAbb());

		PCStat intel = new PCStat();
		intel.setName("Intelligence");
		intel.put(StringKey.ABB, "INT");
		context.ref.registerAbbreviation(intel, intel.getAbb());
		
		PluginLoader ploader = PluginLoader.inst();
		ploader.startSystemPlugins(Constants.SYSTEM_TOKENS);
		Ability feat = new Ability();

		is(context.processToken(feat, "DEFINE", "UNLOCK.INT"), eq(true),
			"Parse fails for unlock");
		context.commit();
		Logging.clearParseMessages();

		List<PCStat> statList = feat.getListFor(ListKey.UNLOCKED_STATS);
		assertEquals(1, statList.size());
		assertEquals("INT", statList.get(0).getAbb());
	}

	public void testBadUnlockDefine() throws Exception
	{
		PluginLoader ploader = PluginLoader.inst();
		ploader.startSystemPlugins(Constants.SYSTEM_TOKENS);
		Ability feat = new Ability();

		is(Globals.getContext().processToken(feat, "DEFINE", "UNLOCK.INT|0"), eq(false),
			"Parse fails to catch bad unlock define");
	}

	public void testParsePreClear() throws Exception
	{
		PluginLoader ploader = PluginLoader.inst();
		ploader.startSystemPlugins(Constants.SYSTEM_TOKENS);

		PObject object = new PObject();

		LoadContext context = Globals.getContext();
		context.unconditionallyProcess(object, "PREVARLT", "GreaterRage,1");
		context.unconditionallyProcess(object, "PREFEAT", "1,Dodge");
		List<Prerequisite> list = object.getPrerequisiteList();
		assertEquals(2, list.size());

		context.unconditionallyProcess(object, "PRE", ".CLEAR");
		list = object.getPrerequisiteList();
		assertNotNull("Prereq list should never be null as it is used in foreach loops directly.", list);
		assertTrue("Prereqlist should be empty after the clear", list.isEmpty());
	}
}
