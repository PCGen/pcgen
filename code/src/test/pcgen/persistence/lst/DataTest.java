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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.persistence.CampaignFileLoader;
import pcgen.persistence.GameModeFileLoader;
import pcgen.persistence.lst.utils.VariableReport;
import pcgen.persistence.lst.utils.VariableReport.ReportFormat;
import pcgen.system.ConfigurationSettings;
import pcgen.system.Main;
import pcgen.system.PCGenTask;
import pcgen.system.PropertyContextFactory;
import pcgen.util.Logging;
import pcgen.util.TestHelper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * The Class {@code DataTest} checks the data files for known issues.
 */
class DataTest
{
	/** The name of our dummy config file. */
	private static final String TEST_CONFIG_FILE = "config.ini.junit";

	/**
	 * Initialise the plugins and load the game mode and campaign files.
	 */
	@BeforeAll
	static void onceOnly()
	{
		loadGameModes();
	}

	/**
	 * Tidy up the config file we created.
	 */
	@AfterAll
	static void afterClass()
	{
		new File(TEST_CONFIG_FILE).delete();
	}

	/**
	 * Check the data for files with extraordinarily long path names. The files
	 * as at the time of writing are grandfathered in, but any new data files
	 * with path names longer than 150 characters will be flagged.
	 */
	@Test
	public void pathLengthTest()
	{
		String dataPath = ConfigurationSettings.getPccFilesDir();
		System.out.println("Got datapath of " + new File(dataPath).getAbsolutePath());

		Collection<String> allowedNames =
				new HashSet<>(Arrays.asList(
						"cotct_pg_abilities_pfrpg.lst",
						"fortress_of_the_stone_giants_pfrpg.pcc",
						"rise_of_the_runelords_players_guide_pfrpg.pcc"));
		Collection<File> newLongPaths = new ArrayList<>();

		int dataPathLen = new File(dataPath).getAbsolutePath().length();
		List<String> longPaths = new ArrayList<>();

		File dataFolder = new File(dataPath);
		Collection<File> listFiles =
				FileUtils.listFiles(dataFolder, new String[]{"pcc", "lst"},
					true);
		for (File file : listFiles)
		{
			String path = file.getAbsolutePath();
			int pathLen = path.length() - dataPathLen;

			if (pathLen > 150)
			{
				longPaths.add(pathLen + " .. " + path.substring(dataPathLen));
				if (!allowedNames.contains(file.getName()))
				{
					newLongPaths.add(file);
				}
			}
		}

		// Output the list
		Collections.sort(longPaths);
		longPaths.forEach(System.out::println);

		// Flag any change for the worse.
		assertEquals(
				"[]", newLongPaths.toString(), "New data file(s) with name longer than 150 characters detected.");
	}

	/**
	 * Produce the variable report in html and csv formats.
	 * @throws Exception if the report fails.
	 */
	public void produceVariableReport() throws Exception
	{
		Map<ReportFormat, String> reportNameMap =
				new EnumMap<>(ReportFormat.class);
		reportNameMap.put(ReportFormat.HTML, "variable_report.html");
		reportNameMap.put(ReportFormat.CSV, "variable_report.csv");
		VariableReport vReport = new VariableReport();
		vReport.runReport(reportNameMap);

		reportNameMap.entrySet().stream().map(repType -> "Variable report in " + repType.getKey()
				+ " format output to "
				+ new File(repType.getValue()).getAbsolutePath()).forEach(System.out::println);
	}

	/**
	 * Scan for any campaigns referring to missing data files.
	 * @throws IOException If the data path cannot be found.
	 */
	@Test
	public void missingFilesTest() throws IOException
	{
		File dataFolder = new File(ConfigurationSettings.getPccFilesDir());
		int dataPathLen = dataFolder.getCanonicalPath().length();

		Collection<Object[]> missingLstFiles = new ArrayList<>();

		for (Campaign campaign : Globals.getCampaignList())
		{
			List<CampaignSourceEntry> cseList =
					getLstFilesForCampaign(campaign);
			cseList.stream()
			       .map(cse -> new File(cse.getURI()))
			       .filter(lstFile -> !lstFile.exists())
			       .map(lstFile -> new Object[]{campaign, lstFile})
			       .forEach(missingLstFiles::add);
		}

		String report = missingLstFiles.stream()
		                               .map(missing -> "Missing file " + ((File) missing[1]).getPath()
		                                                                                    .substring(dataPathLen + 1)
				                               + " used by "
				                               + (new File(((Campaign) missing[0]).getSourceURI())).getPath()
				                                                                                   .substring(
						                                                                                   dataPathLen
								                                                                                   + 1)
				                               + "<br>\r\n")
		                               .collect(Collectors.joining());

		// Flag any missing files
		assertEquals(
				"", report, "Some data files are missing.");
	}

	/**
	 * Scan for any data files that are not referred to by any campaign.
	 * @throws IOException If a file path cannot be converted.
	 */
	@Test
	void orphanFilesTest() throws IOException
	{
		File dataFolder = new File(ConfigurationSettings.getPccFilesDir());
		Collection<File> listFiles =
				FileUtils.listFiles(dataFolder, new String[]{"lst"}, true);
		Collection<String> fileNames = new ArrayList<>(listFiles.size());
		for (File file : listFiles)
		{
			fileNames.add(file.getCanonicalPath());
		}
		int dataPathLen = dataFolder.getCanonicalPath().length();

		for (Campaign campaign : Globals.getCampaignList())
		{
			List<CampaignSourceEntry> cseList =
					getLstFilesForCampaign(campaign);
			for (CampaignSourceEntry cse : cseList)
			{
				File lstFile = new File(cse.getURI());
				fileNames.remove(lstFile.getCanonicalPath());
			}
		}

		String report = fileNames.stream()
		                         .map(orphan -> orphan.substring(dataPathLen + 1))
		                         .filter(srcRelPath -> !srcRelPath.startsWith("customsources"))
		                         .map(srcRelPath -> srcRelPath + "\r\n")
		                         .collect(Collectors.joining());

		// Flag any missing files
		// TODO Revert back to the below
		assertEquals("", report, "Some data files are orphaned.");
		//assertEquals("pathfinder_2e/core_rulebook/c_skills_situation.lst", report, "Some data files are orphaned.");
	}

	private static List<CampaignSourceEntry> getLstFilesForCampaign(CDOMObject campaign)
	{
		List<CampaignSourceEntry> cseList =
				new ArrayList<>();
		CampaignLoader.OBJECT_FILE_LISTKEY.stream()
		      .map((Function<ListKey, List>) campaign::getSafeListFor)
		      .forEach(cseList::addAll);
		CampaignLoader.OTHER_FILE_LISTKEY.stream()
		      .map((Function<ListKey, List>) campaign::getSafeListFor)
		      .forEach(cseList::addAll);
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_PCC));
		return cseList;
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

		PropertyContextFactory configFactory = new PropertyContextFactory(SystemUtils.USER_DIR);
		configFactory.registerAndLoadPropertyContext(ConfigurationSettings.getInstance(TEST_CONFIG_FILE));
		Main.loadProperties(false);
		PCGenTask loadPluginTask = Main.createLoadPluginTask();
		loadPluginTask.run();
		PCGenTask gameModeFileLoader = new GameModeFileLoader();
		gameModeFileLoader.run();
		PCGenTask campaignFileLoader = new CampaignFileLoader();
		campaignFileLoader.run();
	}
}
