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
 *
 *
 */
package pcgen.persistence.lst.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.SystemCollections;
import pcgen.io.PCGFile;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.system.ConfigurationSettings;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;

/**
 * The Class {@code VariableReport} produces a report on variable
 * definitions within the PCGen LST data.
 *
 * 
 */

public class VariableReport
{

	/**
	 * Produce the variable report in the requested formats.
	 * 
	 * @param reportNameMap A map of formats to be output and the names of 
	 * the files to contain the report for the format. 
	 * @throws IOException If the template cannot be accessed or the file cannot be written to.
	 * @throws TemplateException If there is an error in processing the template.
	 */
	public void runReport(Map<ReportFormat, String> reportNameMap) throws IOException, TemplateException
	{
		List<GameMode> games = SystemCollections.getUnmodifiableGameModeList();
		Map<String, List<VarDefine>> gameModeVarMap = new TreeMap<>();
		Map<String, Integer> gameModeVarCountMap = new TreeMap<>();
		for (GameMode gameMode : games)
		{
			List<VarDefine> varList = new ArrayList<>();
			Map<String, Integer> varCountMap = new HashMap<>();
			Set<File> processedLstFiles = new HashSet<>();
			List<Campaign> campaignsForGameMode = getCampaignsForGameMode(gameMode);
			for (Campaign campaign : campaignsForGameMode)
			{
				processCampaign(campaign, varList, varCountMap, processedLstFiles);
			}
			Collections.sort(varList);
			gameModeVarMap.put(gameMode.getName(), varList);
			gameModeVarCountMap.put(gameMode.getName(), varCountMap.size());
		}

		for (Entry<ReportFormat, String> reportRequest : reportNameMap.entrySet())
		{
			try (Writer file = new FileWriter(new File(reportRequest.getValue()), StandardCharsets.UTF_8))
			{
				outputReport(gameModeVarMap, gameModeVarCountMap, reportRequest.getKey(), file);
			}
		}
	}

	/**
	 * Output the variable report for the supplied data in a particular format.
	 * 
	 * @param gameModeVarMap The map of variable definitions for each game mode. 
	 * @param gameModeVarCountMap The map of the number of variables for each game mode.
	 * @param reportFormat The format in which to output the report.
	 * @param outputWriter The writer to output the report to.
	 * @throws IOException If the template cannot be accessed or the writer cannot be written to.
	 * @throws TemplateException If there is an error in processing the template.
	 */
	public void outputReport(Map<String, List<VarDefine>> gameModeVarMap, Map<String, Integer> gameModeVarCountMap,
		ReportFormat reportFormat, Writer outputWriter) throws IOException, TemplateException
	{
		// Configuration
		Configuration cfg = new Configuration();
		int dataPathLen = ConfigurationSettings.getPccFilesDir().length();

		// Set Directory for templates
		File codeDir = new File("code");
		File templateDir = new File(codeDir, "templates");
		cfg.setDirectoryForTemplateLoading(templateDir);
		// load template
		Template template = cfg.getTemplate(reportFormat.getTemplate());

		// data-model
		Map<String, Object> input = new HashMap<>();
		input.put("gameModeVarMap", gameModeVarMap);
		input.put("gameModeVarCountMap", gameModeVarCountMap);
		input.put("pathIgnoreLen", dataPathLen + 1);

		// Process the template
		template.process(input, outputWriter);
		outputWriter.flush();

	}

	private List<Campaign> getCampaignsForGameMode(GameMode game)
	{
		List<String> gameModeList = new ArrayList<>(game.getAllowedModes());

		// Only add those campaigns in the user's chosen folder and game mode
		List<Campaign> allCampaigns = Globals.getCampaignList();
		Set<Campaign> gameModeCampaigns = new HashSet<>();
		for (Campaign campaign : allCampaigns)
		{
			if (campaign.containsAnyInList(ListKey.GAME_MODE, gameModeList))
			{
				gameModeCampaigns.add(campaign);
				for (CampaignSourceEntry fName : campaign.getSafeListFor(ListKey.FILE_PCC))
				{
					URI uri = fName.getURI();
					if (PCGFile.isPCGenCampaignFile(uri))
					{
						Campaign c = Globals.getCampaignByURI(uri, false);
						if (c != null)
						{
							gameModeCampaigns.add(c);
						}
					}
				}
			}
		}

		return new ArrayList<>(gameModeCampaigns);
	}

	private List<File> processCampaign(Campaign campaign, List<VarDefine> varList, Map<String, Integer> varCountMap,
		Set<File> processedLstFiles) throws FileNotFoundException, IOException
	{
		List<CampaignSourceEntry> cseList = new ArrayList<>();
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_LST_EXCLUDE));
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_RACE));
		//TODO: Handle class with a special processor that tracks the class, sublass and level 
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
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_VARIABLE));
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_DYNAMIC));
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_DATACTRL));
		cseList.addAll(campaign.getSafeListFor(ListKey.FILE_GLOBALMOD));

		List<File> missingLstFiles = new ArrayList<>();
		for (CampaignSourceEntry cse : cseList)
		{
			File lstFile = new File(cse.getURI());
			if (!lstFile.exists())
			{
				missingLstFiles.add(lstFile);
				continue;
			}
			if (processedLstFiles.contains(lstFile))
			{
				continue;
			}
			processedLstFiles.add(lstFile);
			processLstFile(varList, varCountMap, lstFile);
		}

		return missingLstFiles;
	}

	private void processLstFile(List<VarDefine> varList, Map<String, Integer> varCountMap, File file)
		throws FileNotFoundException, IOException
	{
		try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8)))
		{
			Map<String, String> varUseMap = new HashMap<>();
			String line = br.readLine();

			while (line != null)
			{
				if (line.startsWith("###VAR:"))
				{
					// ###VAR:SomeVar<tab>USE:Explanation of usage of SomeVar
					String[] varUse = line.trim().split("\t");
					if (varUse.length >= 2 && varUse[1].startsWith("USE:"))
					{
						varUseMap.put(varUse[0].substring(7), varUse[1].substring(4));
					}
				}
				else if (!line.startsWith("#") && StringUtils.isNotBlank(line))
				{
					String[] tokens = line.split("\t");
					String object = tokens[0];
					for (String tok : tokens)
					{
						if (tok.startsWith("DEFINE:"))
						{
							String[] define = tok.split("[:|]");
							String varName = define[1];
							if (!varName.startsWith("LOCK.") && !varName.startsWith("UNLOCK."))
							{
								varList.add(new VarDefine(varName, object, file, varUseMap.get(varName)));
								Integer count = varCountMap.get(varName);
								if (count == null)
								{
									count = 0;
								}
								count++;
								varCountMap.put(varName, count);
							}
						}
					}
				}

				line = br.readLine();
			}
		}
	}

	/**
	 * A format in which the variable report can be produced. Matches the format 
	 * to the FreeMarker template to be used for that format.
	 */
	public enum ReportFormat
	{
		/** Report formatted for web viewing. */
		HTML("variable-report-html.ftl"),
		/** Report formatted for spreadsheet viewing. */
		CSV("variable-report-csv.ftl");

		private final String template;

		ReportFormat(String template)
		{
			this.template = template;
		}

		/**
		 * @return the FreeMarker template used to produce the format
		 */
		public String getTemplate()
		{
			return template;
		}
	}

	/**
	 * The Class {@code VarDefine} contains a single definition of
	 * a variable.
	 */
	public static class VarDefine implements Comparable<VarDefine>
	{

		private final String varName;
		private final String definingObject;
		private File definingFile;
		private String use;

		/**
		 * Create a new instance of VarDefine.
		 * @param varName The name of the variable.
		 * @param definingObject The name of the object defining the variable.
		 * @param definingFile The file in which the variable is defined.
		 * @param use The use as described by the author of the variable.
		 */
		public VarDefine(String varName, String definingObject, File definingFile, String use)
		{
			this.varName = varName;
			this.definingObject = definingObject;
			this.definingFile = definingFile;
			this.use = use;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((varName == null) ? 0 : varName.hashCode());
			result = prime * result + ((definingObject == null) ? 0 : definingObject.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null)
			{
				return false;
			}
			if (!(obj instanceof VarDefine))
			{
				return false;
			}
			VarDefine other = (VarDefine) obj;
			if (varName == null)
			{
				if (other.varName != null)
				{
					return false;
				}
			}
			else if (!varName.equals(other.varName))
			{
				return false;
			}
			if (definingObject == null)
			{
                return other.definingObject == null;
			}
			else return definingObject.equals(other.definingObject);
        }

		@Override
		public int compareTo(VarDefine other)
		{
			if (varName == null)
			{
				if (other.varName != null)
				{
					return -1;
				}
				return 0;
			}
			else if (other.varName == null)
			{
				return 1;
			}

			return varName.compareToIgnoreCase(other.varName);
		}

		@Override
		public String toString()
		{
			return "VarDefine [varName="
					+ varName
					+ ", definingObject="
					+ definingObject
					+ ", definingFile="
					+ definingFile
					+ ", use="
					+ use
					+ ']';
		}

		/**
		 * @return the varName
		 */
		public String getVarName()
		{
			return varName;
		}

		/**
		 * @return the definingObject
		 */
		public String getDefiningObject()
		{
			return definingObject;
		}

		/**
		 * @return the definingFile
		 */
		public File getDefiningFile()
		{
			return definingFile;
		}

		/**
		 * @param definingFile the definingFile to set
		 */
		public void setDefiningFile(File definingFile)
		{
			this.definingFile = definingFile;
		}

		/**
		 * @return the use
		 */
		public String getUse()
		{
			return use;
		}

		/**
		 * @param use the use to set
		 */
		public void setUse(String use)
		{
			this.use = use;
		}
	}
}
