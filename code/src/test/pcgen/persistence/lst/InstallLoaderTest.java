/*
 * InstallLoaderTest.java
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 27/12/2007
 *
 * $Id$
 */
package pcgen.persistence.lst;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.InstallableCampaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.TestHelper;

/**
 * A collection of tests to validate the functioning of the InstallLoader class.
 */
public final class InstallLoaderTest extends TestCase
{
	private static final String PUBNAMESHORT = "PCGen";
	private static final String PUBNAMELONG = "PCGen Open Source Team";
	private static final String SOURCEDATE = "2007-12";
	private static final String SOURCEWEB = "http://pcgen.sourceforge.net/";
	private static final String SOURCESHORT = "PCGen TOCC";
	private static final String SOURCELONG = "PCGen Test Out of Cycle Releases";
	private static final String INFOTEXT =
			"PCGen Open Gaming Content objects are detailed in the PCGen "
				+ "documentation under the Source Help section";
	private static final String COPYRIGHT_3 =
			"PCGen OGC dataset Copyright 2006, PCGen Data team (Including, "
				+ "but not limited to Eddy Anthony (MoSaT), Andrew McDougall (Tir Gwaith)).";
	private static final String COPYRIGHT_2 =
			"System Reference Document Copyright 2000-2003, Wizards of the "
				+ "Coast, Inc.; Authors Jonathan Tweet, Monte Cook, Skip Williams, "
				+ "Rich Baker, Andy Collins, David Noonan, Rich Redman, Bruce R. "
				+ "Cordell, John D. Rateliff, Thomas Reid, James Wyatt, based on "
				+ "original material by E. Gary Gygax and Dave Arneson.";
	private static final String COPYRIGHT_1 =
			"Open Game License v 1.0a Copyright 2000, Wizards of the Coast, Inc.";
	private static final String DEST = "DATA";
	private static final String MINDEVVER = "5.13.5";
	private static final String MINVER = "5.14.0";
	private static final String CAMPAIGN_NAME = "PCGen Test OCC";
	
	/**
	 * The sample install data for testing.
	 */
	private final static String[] INSTALL_DATA =
			new String[]{"CAMPAIGN:" + CAMPAIGN_NAME, "MINVER:" + MINVER,
				"MINDEVVER: " + MINDEVVER, "DEST:" + DEST,
				"COPYRIGHT:" + COPYRIGHT_1, "COPYRIGHT:" + COPYRIGHT_2,
				"COPYRIGHT:" + COPYRIGHT_3, "INFOTEXT:" + INFOTEXT,
				"SOURCELONG:" + SOURCELONG, "SOURCESHORT:" + SOURCESHORT,
				"SOURCEWEB:" + SOURCEWEB, "SOURCEDATE:" + SOURCEDATE,
				"PUBNAMELONG:" + PUBNAMELONG, "PUBNAMESHORT:" + PUBNAMESHORT,
				"PUBNAMEWEB:" + SOURCEWEB};

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
    @Override
	protected void setUp() throws Exception
	{
		super.setUp();
		TestHelper.loadPlugins();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
    @Override
	protected void tearDown() throws Exception
	{
	}

	/**
	 * Basic constructor, name only.
	 */
	public InstallLoaderTest()
	{
		// Do Nothing
	}

	/**
	 * Basic constructor, name only.
	 *
	 * @param name The name of the test class.
	 */
	public InstallLoaderTest(final String name)
	{
		super(name);
	}

	/**
	 * Load the supplied installable campaign set data.
	 *
	 * @param installData The data to be loaded.
	 * @throws URISyntaxException 
	 * @throws PersistenceLayerException 
	 *
	 * @throws Exception If a problem occurs when loading the data
	 */
	private static InstallableCampaign loadInstallData(
		final String[] installData) throws PersistenceLayerException,
		URISyntaxException
	{
		final InstallLoader loader = new InstallLoader();
		StringBuilder data = new StringBuilder();
		for (int i = 0; i < installData.length; i++)
		{
			final String line = installData[i];
			data.append(line);
			data.append("\n");
		}
		loader.loadLstString(null, new URI("http://UNIT_TEST_CASE"), data.toString());
		return loader.getCampaign();
	}

	/**
	 * Run the tests standalone from the command line.
	 *
	 * @param args Command line args - ignored.
	 */
	public static void main(final String[] args)
	{
		junit.textui.TestRunner.run(InstallLoaderTest.class);
	}

	/**
	 * Quick test suite creation - adds all methods beginning with "test".
	 *
	 * @return The Test suite
	 */
	public static Test suite()
	{
		return new TestSuite(InstallLoaderTest.class);
	}

	/**
	 * Validate the test data can be loaded successfully.
	 */
	public void testParseLine() throws Exception
	{
		InstallableCampaign camp = loadInstallData(INSTALL_DATA);

		assertEquals("Checking campaign name", CAMPAIGN_NAME, camp
			.getDisplayName());
		assertEquals("Checking copyright 1", COPYRIGHT_1, camp.getSafeListFor(ListKey.SECTION_15)
			.get(0));
		assertEquals("Checking copyright 2", COPYRIGHT_2, camp.getSafeListFor(ListKey.SECTION_15)
			.get(1));
		assertEquals("Checking copyright 3", COPYRIGHT_3, camp.getSafeListFor(ListKey.SECTION_15)
			.get(2));
		assertEquals("Checking info text", INFOTEXT, camp.getSafeListFor(ListKey.INFO_TEXT)
			.get(0));
		assertEquals("Checking pub name short", PUBNAMESHORT, camp.getSafe(StringKey.PUB_NAME_SHORT));
		assertEquals("Checking pub name long", PUBNAMELONG, camp.getSafe(StringKey.PUB_NAME_LONG));
		assertEquals("Checking pub name web", SOURCEWEB, camp.getSafe(StringKey.PUB_NAME_WEB));
		assertEquals("Checking campaign name", CAMPAIGN_NAME, camp
			.getDisplayName());
		assertEquals("Checking source name short", SOURCESHORT, camp
				.get(StringKey.SOURCE_SHORT));
		assertEquals("Checking source name long", SOURCELONG, camp
				.get(StringKey.SOURCE_LONG));
		assertEquals("Checking source name web", SOURCEWEB, camp
				.get(StringKey.SOURCE_WEB));

		Date theDate = null;
		DateFormat df = new SimpleDateFormat("yyyy-MM"); //$NON-NLS-1$
		try
		{
			theDate = df.parse(SOURCEDATE);
		}
		catch (ParseException pe)
		{
			df = DateFormat.getDateInstance();
			theDate = df.parse(SOURCEDATE);
		}
		assertEquals("Checking source date", theDate, camp
				.get(ObjectKey.SOURCE_DATE));

		assertEquals("Checking min ver", MINVER, camp.getSafe(StringKey.MINVER));
		assertEquals("Checking min dev ver", MINVER, camp.getSafe(StringKey.MINVER));
		assertEquals("Checking destination", DEST, camp.get(ObjectKey.DESTINATION).toString());
	}

}
