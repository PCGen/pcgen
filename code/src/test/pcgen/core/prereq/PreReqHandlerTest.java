/*
 * PreReqHandlerTest.java
 *
 * Copyright 2004 (C) Chris Ward <frugal@purplewombat.co.uk>
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
 * Created on 14-Jan-2004
 *
 * Current Ver: $Revision$
 *
 * Last Editor: $Author$
 *
 * Last Edited: $Date$
 *
 */
package pcgen.core.prereq;

import gmgen.pluginmgr.PluginLoader;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import pcgen.PCGenTestCase;
import pcgen.cdom.base.Constants;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * @author wardc
 */
@SuppressWarnings("nls")
public class PreReqHandlerTest extends PCGenTestCase
{

	/**
	 * Run the JUnit test
	 * @param args
	 */
	public static void main(final String[] args)
	{
		TestRunner.run(PreReqHandlerTest.class);
	}

	/**
	 * @return Test
	 */
	public static Test suite()
	{
		return new TestSuite(PreReqHandlerTest.class);
	}

	/**
	 * Sets up the test case by loading the system plugins.
	 * 
	 * @see pcgen.PCGenTestCase#setUp()
	 */
	@Override
	public void setUp() throws Exception
	{
		try
		{
			PluginLoader ploader = PluginLoader.inst();
			ploader.startSystemPlugins(Constants.SYSTEM_TOKENS);
		}
		catch (Exception e)
		{
			// TODO Handle exception
		}
	}

	/**
	 * Print out as HTML
	 * @throws Exception
	 */
	public void testToHtml() throws Exception
	{
		final PreParserFactory factory = PreParserFactory.getInstance();
		final List<Prerequisite> list = new ArrayList<Prerequisite>();
		list.add(factory.parse("PRESKILL:1,Spellcraft=15"));
		list.add(factory.parse("PRESPELLTYPE:1,Arcane=8"));
		list.add(factory.parse("PREFEAT:2,TYPE=Metamagic"));
		list.add(factory.parse("PREFEAT:2,TYPE=ItemCreation"));
		list.add(factory.parse("PRESKILLTOT:TYPE.Knowledge=20"));

		final String htmlString = PrereqHandler.toHtmlString(list);
		System.out.println(htmlString);
		assertEquals(
			"at least 15 ranks in Spellcraft and at least1 Arcane spell(s) of level 8 and at least 2 feat(s) of type Metamagic and at least 2 feat(s) of type ItemCreation and at least 20 of ( at least 1 ranks in TYPE.Knowledge )",
			htmlString);
	}
}
