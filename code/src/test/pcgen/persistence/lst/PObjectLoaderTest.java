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
import pcgen.core.Ability;
import pcgen.core.Constants;
import pcgen.core.PObject;
import pcgen.core.Variable;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;

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
		ploader.startSystemPlugins(Constants.s_SYSTEM_TOKENS);
		Ability feat = new Ability();

		PObjectLoader.parseTag(feat, "DEFINE:Foo|0");

		Variable var = feat.getVariable(0);
		assertEquals("Foo", var.getName());
		assertEquals("0", var.getValue());
	}

	public void testBadDefine() throws Exception
	{
		PluginLoader ploader = PluginLoader.inst();
		ploader.startSystemPlugins(Constants.s_SYSTEM_TOKENS);

		Ability feat = new Ability();

		try
		{
			is(PObjectLoader.parseTag(feat, "DEFINE:Foo"), eq(false),
				"Parse fails for badly formed define");
		}
		catch (PersistenceLayerException ple)
		{
			fail("parseTag throws exception instead of passing back false");
		}
	}

	public void testUnlockDefine() throws Exception
	{
		PluginLoader ploader = PluginLoader.inst();
		ploader.startSystemPlugins(Constants.s_SYSTEM_TOKENS);
		Ability feat = new Ability();

		is(PObjectLoader.parseTag(feat, "DEFINE:UNLOCK.INT"), eq(true),
			"Parse fails for unlock");

		Variable var = feat.getVariable(0);
		assertEquals("UNLOCK.INT", var.getName());
		assertEquals("", var.getValue());
	}

	public void testBadUnlockDefine() throws Exception
	{
		PluginLoader ploader = PluginLoader.inst();
		ploader.startSystemPlugins(Constants.s_SYSTEM_TOKENS);
		Ability feat = new Ability();

		is(PObjectLoader.parseTag(feat, "DEFINE:UNLOCK.INT|0"), eq(false),
			"Parse fails to catch bad unlock define");
	}

	public void testParsePreClear() throws Exception
	{
		PluginLoader ploader = PluginLoader.inst();
		ploader.startSystemPlugins(Constants.s_SYSTEM_TOKENS);

		PObject object = new PObject();

		PObjectLoader.parseTag(object, "PREVARLT:GreaterRage,1");
		PObjectLoader.parseTag(object, "PREFEAT:1,Dodge");
		List<Prerequisite> list = object.getPreReqList();
		assertEquals(2, list.size());

		PObjectLoader.parseTag(object, "PRE:.CLEAR");
		list = object.getPreReqList();
		assertNotNull("Prereq list should never be null as it is used in foreach loops directly.", list);
		assertTrue("Prereqlist should be empty after the clear", list.isEmpty());
	}
}
