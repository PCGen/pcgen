/*
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
 */
package pcgen.persistence.lst;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.LogRecord;
import java.util.stream.Stream;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * The Class {@code DataLoadTest} checks each basic source for errors on load.
 */
public class DataLoadTest implements PCGenTaskListener
{
	/** The name of our dummy config file. */
	private static final String TEST_CONFIG_FILE = "config.ini.junit";

	private Collection<LogRecord> errors = new ArrayList<>();


	/**
	 * Tidy up the config file we created. 
	 */
	@AfterAll
	static void afterClass()
	{
		new File(TEST_CONFIG_FILE).delete();
	}

	/**
	 * Build the list of sources to be checked. Also initialises the plugins and 
	 * loads the game mode and campaign files.
	 */
	public static Stream<Object[]> data()
	{
		// Set things up
		loadGameModes();
		SettingsHandler.setOutputDeprecationMessages(false);
		SettingsHandler.setInputUnconstructedMessages(false);
		PCGenSettings.OPTIONS_CONTEXT.setBoolean(
			PCGenSettings.OPTION_ALLOW_OVERRIDE_DUPLICATES, true);

		List<SourceSelectionFacade> basicSources = getBasicSources();
		assertFalse(basicSources.isEmpty(), "No sources found");
		Collection<Object[]> params = new ArrayList<>();
		basicSources.forEach(ssf -> {
			String testName = ssf.toString().replaceAll("[(\\)]", "_");
			params.add(new Object[]{ssf, testName});
		});
		return params.stream();
	}

	/**
	 * Test the load of the current source.
	 * This will check for any load errors or warnings but ignores deprecation warnings.
	 */
	@ParameterizedTest
	@MethodSource("data")
	void testLoadSources(SourceSelectionFacade sourceSelection)
	{
		UIDelegate uiDelegate = new MockUIDelegate();

		PCGenTask loader =
				new SourceFileLoader(uiDelegate, sourceSelection.getCampaigns(), sourceSelection.getGameMode().get().getName());
		errors = new ArrayList<>();
		loader.addPCGenTaskListener(this);
		loader.run();
		GameMode selectedGame = SystemCollections
			.getGameModeNamed(sourceSelection.getGameMode().get().getName());
		selectedGame.clearLoadContext();

		Collection<String> errorList = new ArrayList<>();
		Collection<String> warningList = new ArrayList<>();
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
		assertEquals("",
			StringUtils.join(errorList, ",\n"), () -> "Errors encountered while loading " + sourceSelection
		);
		assertEquals("",
			StringUtils.join(errorList, ",\n"), () -> "Warnings encountered while loading " + sourceSelection
		);
	}

	private static void loadGameModes()
	{
		String pccLoc = TestHelper.findDataFolder();
		System.out.println("Got data folder of " + pccLoc);
		try
		{
			String configFolder = "testsuite";
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
		loadPluginTask.run();
		PCGenTask gameModeFileLoader = new GameModeFileLoader();
		gameModeFileLoader.run();
		PCGenTask campaignFileLoader = new CampaignFileLoader();
		campaignFileLoader.run();
	}

	private static List<SourceSelectionFacade> getBasicSources()
	{
		List<SourceSelectionFacade> basicSources = new ArrayList<>();
		for (Campaign campaign : Globals.getCampaignList())
		{
			if (campaign.getSafe(ObjectKey.SHOW_IN_MENU))
			{
				SourceSelectionFacade sourceSelection =
						FacadeFactory.createSourceSelection(campaign.getGameModes()
							.getElementAt(0), Collections.singletonList(campaign), campaign.getKeyName());
				basicSources.add(sourceSelection);
			}
		}
		for (GameMode mode : SystemCollections.getUnmodifiableGameModeList())
		{
			if (!mode.getDefaultDataSetList().isEmpty())
			{
				List<Campaign> qcamps = new ArrayList<>();
				List<String> sources = mode.getDefaultDataSetList();
				for (String string : sources)
				{
					Campaign camp = Globals.getCampaignKeyed(string);
					assertNotNull(camp, () -> "Cannot find source " + string
							+ " for game mode " + mode);
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
