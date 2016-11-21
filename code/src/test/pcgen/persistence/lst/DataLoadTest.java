/*
 * DataTest.java
 * Copyright James Dempsey, 2013
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
 * Created on 23/05/2013 7:02:42 AM
 *
 * $Id$
 */
package pcgen.persistence.lst;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.LogRecord;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.facade.core.CampaignFacade;
import pcgen.facade.core.SourceSelectionFacade;
import pcgen.facade.core.UIDelegate;
import pcgen.gui2.facade.MockUIDelegate;
import pcgen.persistence.CampaignFileLoader;
import pcgen.persistence.GameModeFileLoader;
import pcgen.persistence.SourceFileLoader;
import pcgen.system.ConfigurationSettings;
import pcgen.system.FacadeFactory;
import pcgen.system.Main;
import pcgen.system.PCGenSettings;
import pcgen.system.PCGenTask;
import pcgen.system.PCGenTaskEvent;
import pcgen.system.PCGenTaskListener;
import pcgen.system.PropertyContextFactory;
import pcgen.util.Logging;
import pcgen.util.TestHelper;

/**
 * The Class <code>DataLoadTest</code> checks each basic source for errors on load.
 *
 * <br/>
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 */
@RunWith(Parameterized.class)
public class DataLoadTest implements PCGenTaskListener
{
	/** The name of our dummy config file. */
	private static final String TEST_CONFIG_FILE = "config.ini.junit";

	/** A list of sources that are currently broken, but are lower priority. 
	 * These should be activated when the data team is ready. */
	private static String[] excludedSources = new String[]{};
	
	private List<LogRecord> errors = new ArrayList<>();

	private SourceSelectionFacade sourceSelection;


	/**
	 * Tidy up the config file we created. 
	 */
	@AfterClass
	public static void afterClass()
	{
		new File(TEST_CONFIG_FILE).delete();
	}

	/**
	 * Build the list of sources to be checked. Also initialises the plugins and 
	 * loads the game mode and campaign files.
	 */
	@Parameters(name = "{1}")
	public static Collection<Object[]> data()
	{
		// Set things up
		loadGameModes();
		SettingsHandler.setOutputDeprecationMessages(false);
		SettingsHandler.setInputUnconstructedMessages(false);
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(
			PCGenSettings.OPTION_ALLOW_OVERRIDE_DUPLICATES, true);
		List<String> exclusions = Arrays.asList(excludedSources);

		List<SourceSelectionFacade> basicSources = getBasicSources();
		assertFalse("No sources found", basicSources.isEmpty());
		List<Object[]> params = new ArrayList<>();
		for (SourceSelectionFacade ssf : basicSources)
		{
			String testName = ssf.toString().replaceAll("[\\(\\)]", "_");
			if (!exclusions.contains(testName))
			{
				params.add(new Object[]{ssf, testName});
			}
		}
		return params;
	}

	/**
	 * Create a parameterised instance of the test class for a specific source.  
	 * @param sourceSelection The basic source we will be testing.
	 * @param testName The display name, needs to be sanitised for IDE display.
	 */
	public DataLoadTest(SourceSelectionFacade sourceSelection, String testName)
	{
		this.sourceSelection = sourceSelection;
	}

	/**
	 * Test the load of the current source. This will check for any load errors or warnings but ignores deprecation warnings.  
	 */
	@Test
	public void testLoadSources()
	{
		UIDelegate uiDelegate = new MockUIDelegate();

		SourceFileLoader loader =
				new SourceFileLoader(sourceSelection, uiDelegate);
		errors = new ArrayList<>();
		loader.addPCGenTaskListener(this);
		loader.execute();

		List<String> errorList = new ArrayList<>();
		List<String> warningList = new ArrayList<>();
		for (LogRecord logRecord : errors)
		{
			if (logRecord.getLevel().intValue() > Logging.WARNING.intValue())
			{
				errorList.add(logRecord.getMessage());
			}
			else if (logRecord.getLevel().intValue() > Logging.INFO.intValue())
			{
				warningList.add(logRecord.getMessage());
			}
		}
		assertEquals("Errors encountered while loading " + sourceSelection, "",
			StringUtils.join(errorList, ",\n"));
		assertEquals("Warnings encountered while loading " + sourceSelection, "",
			StringUtils.join(errorList, ",\n"));
	}

	private static void loadGameModes()
	{
		String configFolder = "testsuite";
		String pccLoc = TestHelper.findDataFolder();
		System.out.println("Got data folder of " + pccLoc);
		try
		{
			TestHelper.createDummySettingsFile(TEST_CONFIG_FILE, configFolder,
				pccLoc);
		}
		catch (IOException e)
		{
			Logging.errorPrint("DataTest.loadGameModes failed", e);
		}

		PropertyContextFactory configFactory =
				new PropertyContextFactory(SystemUtils.USER_DIR);
		configFactory.registerAndLoadPropertyContext(ConfigurationSettings
			.getInstance(TEST_CONFIG_FILE));
		Main.loadProperties(false);
		PCGenTask loadPluginTask = Main.createLoadPluginTask();
		loadPluginTask.execute();
		GameModeFileLoader gameModeFileLoader = new GameModeFileLoader();
		gameModeFileLoader.execute();
		CampaignFileLoader campaignFileLoader = new CampaignFileLoader();
		campaignFileLoader.execute();
	}

	private static List<SourceSelectionFacade> getBasicSources()
	{
		List<SourceSelectionFacade> basicSources = new ArrayList<>();
		for (Campaign campaign : Globals.getCampaignList())
		{
			if (campaign.showInMenu())
			{
				SourceSelectionFacade sourceSelection =
						FacadeFactory.createSourceSelection(campaign.getGameModes()
							.getElementAt(0), Collections.singletonList(campaign), campaign.getName());
				
				basicSources.add(sourceSelection);
			}
		}
		for (GameMode mode : SystemCollections.getUnmodifiableGameModeList())
		{
			String title = mode.getDefaultSourceTitle();
			if (title == null && !mode.getDefaultDataSetList().isEmpty())
			{
				title = mode.getName();
			}

			if (!mode.getDefaultDataSetList().isEmpty())
			{
				List<CampaignFacade> qcamps = new ArrayList<>();
				List<String> sources = mode.getDefaultDataSetList();
				for (String string : sources)
				{
					Campaign camp = Globals.getCampaignKeyed(string);
					assertNotNull("Cannot find source " + string
						+ " for game mode " + mode, camp);
					qcamps.add(camp);
				}
				basicSources.add(FacadeFactory.createSourceSelection(
					mode, qcamps, mode.getDefaultSourceTitle()));
			}
		}
		return basicSources;
	}

	@Override
	public void progressChanged(PCGenTaskEvent event)
	{
		// Ignore

	}

	/**
	 * Record any log messages written by the source load.
	 */
	@Override
	public void errorOccurred(PCGenTaskEvent event)
	{
		errors.add(event.getErrorRecord());
	}
}
