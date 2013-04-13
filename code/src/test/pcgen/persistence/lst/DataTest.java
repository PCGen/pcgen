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
 * Created on 07/04/2013 9:02:42 AM
 *
 * $Id$
 */
package pcgen.persistence.lst;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

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

/**
 * The Class <code>DataTest</code> checks the data files for known issues.
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */

public class DataTest
{

	/**
	 * Initialise the plugins and load the game mode and campaign files.
	 */
	@BeforeClass
	public static void onceOnly()
	{
		loadGameModes();
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
		
		Set<String> allowedNames =
				new HashSet<String>(Arrays.asList(
					"pf11_classes_monster_pfrpg.lst",
					"skeletons_of_scarwall_pfrpg.pcc",
					"pf4_abilities_racial_pfrpg.lst",
					"rrpg_kits_races_animals_pfrpg.lst",
					"curse_of_the_crimson_throne_players_guide.pcc",
					"cotct_pg_kits_pfrpg.lst", 
					"cotct_pg_feats_pfrpg.lst",
					"cotct_pg_races_pfrpg.lst",
					"seven_days_to_the_grave_pfrpg.pcc",
					"escape_from_old_korvosa_pfrpg.pcc",
					"the_hook_mountain_massacre_pfrpg.pcc",
					"cotct_pg_abilities_pfrpg.lst",
					"fortress_of_the_stone_giants_pfrpg.pcc",
					"rise_of_the_runelords_players_guide_pfrpg.pcc",
					"curse_of_the_crimson_throne_players_guide_pfrpg.pcc"));
		List<File> newLongPaths = new ArrayList<File>();
		
		int dataPathLen = dataPath.length();
		List<String> longPaths = new ArrayList<String>();
		
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
		for (String msg : longPaths)
		{
			System.out.println(msg);
		}
				
		// Flag any change for the worse.
		assertEquals(
			"New data file(s) with name longer than 150 characters detected.",
			"[]", newLongPaths.toString());
	}

	/**
	 * Produce the variable report in html and csv formats.
	 * @throws Exception if the report fails.
	 */
	@Test
	public void produceVariableReport() throws Exception
	{
		Map<ReportFormat, String> reportNameMap =
				new HashMap<VariableReport.ReportFormat, String>();
		reportNameMap.put(ReportFormat.HTML, "variable_report.html");
		reportNameMap.put(ReportFormat.CSV, "variable_report.csv");
		VariableReport vReport = new VariableReport();
		vReport.runReport(reportNameMap);
		
		for (Entry<ReportFormat, String> repType : reportNameMap.entrySet())
		{
			System.out.println("Variable report in " + repType.getKey()
				+ " format output to "
				+ new File(repType.getValue()).getAbsolutePath());
		}
	}
	
	/**
	 * Scan for any campaigns referring to missing data files. 
	 * This test should be activated once DATA-1040 has been actioned. 
	 */
	@Ignore
	public void missingFilesTest()
	{
		int dataPathLen = ConfigurationSettings.getPccFilesDir().length();

		List<Object[]> missingLstFiles = new ArrayList<Object[]>();

		for (Campaign campaign : Globals.getCampaignList())
		{
			List<CampaignSourceEntry> cseList =
					getLstFilesForCampaign(campaign);
			for (CampaignSourceEntry cse : cseList)
			{
				File lstFile = new File(cse.getURI());
				if (!lstFile.exists())
				{
					missingLstFiles.add(new Object[]{campaign, lstFile});
				}
			}
		}

		StringBuilder report = new StringBuilder();
		for (Object[] missing : missingLstFiles)
		{
			report.append("Missing file ");
			report.append(((File)missing[1]).getPath().substring(dataPathLen+1));
			report.append(" used by ");
			report.append((new File(((Campaign) missing[0]).getSourceURI())).getPath().substring(dataPathLen+1));
			report.append("\r\n");
		}
		
		// Flag any missing files
		assertEquals(
			"Some data files are missing.",
			"", report);
	}
	
	/**
	 * Scan for any data files that are not referred to by any campaign.
	 * This test should be activated once DATA-1039 has been actioned. 
	 */
	@Ignore
	public void orphanFilesTest()
	{
		File dataFolder = new File(ConfigurationSettings.getPccFilesDir());
		Collection<File> listFiles =
				FileUtils.listFiles(dataFolder, new String[]{"lst"}, true);
		int dataPathLen = dataFolder.getPath().length();
		
		for (Campaign campaign : Globals.getCampaignList())
		{
			List<CampaignSourceEntry> cseList =
					getLstFilesForCampaign(campaign);
			for (CampaignSourceEntry cse : cseList)
			{
				File lstFile = new File(cse.getURI());
				listFiles.remove(lstFile);
			}
		}

		StringBuilder report = new StringBuilder();
		for (File orphan : listFiles)
		{
			report.append(orphan.getPath().substring(dataPathLen+1));
			report.append("\r\n");
		}
		
		// Flag any missing files
		assertEquals(
			"Some data files are orphaned.",
			"", report);
	}

	private List<CampaignSourceEntry> getLstFilesForCampaign(Campaign campaign)
	{
		List<CampaignSourceEntry> cseList =
				new ArrayList<CampaignSourceEntry>();
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_LST_EXCLUDE));
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_RACE));
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_CLASS));
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_COMPANION_MOD));
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_SKILL));
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_ABILITY_CATEGORY));
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_ABILITY));
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_FEAT));
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_DEITY));
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_DOMAIN));
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_WEAPON_PROF));
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_ARMOR_PROF));
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_SHIELD_PROF));
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_EQUIP));
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_SPELL));
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_LANGUAGE));
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_TEMPLATE));
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_EQUIP_MOD));
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_KIT));
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_BIO_SET));
		return cseList;
	}
	
	
	private static void loadGameModes()
	{
		PropertyContextFactory configFactory = new PropertyContextFactory(SystemUtils.USER_DIR);
		configFactory.registerAndLoadPropertyContext(ConfigurationSettings.getInstance());
		Main.loadProperties(true);
		PCGenTask loadPluginTask = Main.createLoadPluginTask();
		loadPluginTask.execute();
		GameModeFileLoader gameModeFileLoader = new GameModeFileLoader();
		gameModeFileLoader.execute();
		CampaignFileLoader campaignFileLoader = new CampaignFileLoader();
		campaignFileLoader.execute();
	}
}

