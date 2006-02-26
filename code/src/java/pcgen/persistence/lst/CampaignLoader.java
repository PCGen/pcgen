/*
 * CampaignLoader.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on February 22, 2002, 10:29 PM
 *
 * Current Ver: $Revision: 1.85 $
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2006/02/15 16:32:42 $
 *
 */
package pcgen.persistence.lst;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pcgen.core.Campaign;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.SourceUtilities;
import pcgen.core.utils.CoreUtility;
import pcgen.io.PCGFile;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;

/**
 * @author David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.85 $
 */
public class CampaignLoader extends LstLineFileLoader
{
	private Campaign campaign = null;

	/**
	 * Creates a new instance of CampaignLoader
	 */
	public CampaignLoader()
	{
	    // Empty Constructor
	}

	/**
	 * This method initializes any campaigns that include other campaigns,
	 * avoiding an infinite loop in the event of recursive, for example 
	 * interdependent campaigns
	 *
	 * @throws PersistenceLayerException if an error occurs reading a
	 *                                   newly-encountered campaign
	 */
	public void initRecursivePccFiles() throws PersistenceLayerException
	{
		// This may modify the globals list; need a local copy so
		// the iteration doesn't fail.
		List initialCampaigns = new ArrayList(Globals.getCampaignList());

		Iterator iter = initialCampaigns.iterator();

		while (iter.hasNext())
		{
			initRecursivePccFiles((Campaign) iter.next());
		}
	}

	/**
	 * @see pcgen.persistence.lst.LstLineFileLoader#loadLstFile(java.lang.String)
	 */
	public void loadLstFile(String fileName) throws PersistenceLayerException
	{
		campaign = new Campaign();

		super.loadLstFile(fileName);

		finishCampaign();
	}

	public void parseLine(String inputLine, URL sourceUrl) throws PersistenceLayerException
	{
		final int idxColon = inputLine.indexOf(':');
		if (idxColon < 0)
		{
			Logging.errorPrint("Unparsed line: " + inputLine + " in " + sourceUrl.toString());
			return;
		}
		final String key = inputLine.substring(0, idxColon);
		final String value = inputLine.substring(idxColon + 1);
		Map tokenMap = TokenStore.inst().getTokenMap(CampaignLstToken.class);
		CampaignLstToken token = (CampaignLstToken) tokenMap.get(key);

		if (token != null)
		{
			LstUtils.deprecationCheck(token, campaign, value);
			if (!token.parse(campaign, value, sourceUrl))
			{
				Logging.errorPrint("Error parsing campaign " + campaign.getName() + ':' + inputLine);
			}
		}
		else if (PObjectLoader.parseTag(campaign, inputLine))
		{
			return;
		}
		else
		{
			Logging.errorPrint("Unparsed line: " + inputLine + " in " + sourceUrl.toString());
		}
	}

	/**
	 * This method finishes the campaign being loaded by saving its section 15
	 * information as well as adding it to Globals, if it has not already been
	 * loaded.
	 */
	protected void finishCampaign()
	{
		if (Globals.getCampaignByFilename(campaign.getSourceFile(), false) == null)
		{
			final String sect15 = campaign.getSection15String();

			if ((sect15 != null) && (sect15.trim().length() > 0))
			{
				Globals.getSection15().append("<br><b>Source Material:</b>");
				Globals.getSection15().append(SourceUtilities.returnSourceInForm(campaign, Constants.SOURCELONG, true));
				Globals.getSection15().append("<br>");
				Globals.getSection15().append("<b>Section 15 Entry in Source Material:</b><br>");
				Globals.getSection15().append(sect15);
			}

			Globals.addCampaign(campaign);
		}
	}

	/**
	 * This method converts the provided filePath to either a URL
	 * or absolute path as appropriate.
	 *
	 * @param pccPath  URL where the Campaign that contained the source was at
	 * @param basePath String path that is to be converted
	 * @return String containing the converted absolute path or URL
	 *         (as appropriate)
	 */
	public static String convertFilePath(URL pccPath, String basePath)
	{
		String convertedPath = "";

		if (basePath.length() <= 0)
		{
			return convertedPath;
		}

		// Check if the basePath was a complete URL to begin with
		if (CoreUtility.isURL(basePath))
		{
			convertedPath = basePath;

			// if it's a URL, then we are all done
			return convertedPath;
		}
		/* Figure out where the PCC file came from that we're
		 * processing, so that we can prepend its path onto
		 * any LST file references (or PCC refs, for that
		 * matter) that are relative. If the source line in
		 * question already has path info, then don't bother
		 */
		if (basePath.charAt(0) == '@')
		{
			final String pathNoLeader = trimLeadingFileSeparator(basePath.substring(1));
			convertedPath = SettingsHandler.getPccFilesLocation().getAbsolutePath() + File.separator + pathNoLeader;
		}
		else if (basePath.charAt(0) == '&')
		{
			final String pathNoLeader = trimLeadingFileSeparator(basePath.substring(1));
			convertedPath = SettingsHandler.getPcgenVendorDataDir().getAbsolutePath() + File.separator + pathNoLeader;
		}

		// the line doesn't use "@" or "&" then it's a relative path,
		else //if (aLine.indexOf('@') < 0) and (aLine.indexOf('&') < 0)
		{
			/*
			 * 1) If the path starts with '/data',
			 * assume it means the PCGen data dir
			 * 2) Otherwise, assume that the path is
			 * relative to the current PCC file URL
			 */
			final String pathNoLeader = trimLeadingFileSeparator(basePath);

			if (pathNoLeader.startsWith("data"))
			{
				convertedPath = SettingsHandler.getPccFilesLocation() + pathNoLeader.substring(4);
			}
			else
			{
				convertedPath = pccPath.getPath();
				// URLs always use forward slash; take off the file name
				int separatorLoc = convertedPath.lastIndexOf("/");
				convertedPath = convertedPath.substring(0, separatorLoc) + "/" + basePath;
			}
		}

		// Not a URL; make sure to fix the path syntax
		convertedPath = CoreUtility.fixFilenamePath(convertedPath);

		// Make sure the path starts with a separator
		if (!convertedPath.startsWith(File.separator))
		{
			convertedPath = File.separator + convertedPath;
		}

		// Return the final result
		try
		{
			return new URL("file:" + convertedPath).toString();
		}
		catch (MalformedURLException e)
		{
			Logging.errorPrint("failed to convert " + convertedPath + " to true URL.");

			return convertedPath;
		}
	}

	/**
	 * This method trims the leading file separator or URL separator from the
	 * front of a string.
	 *
	 * @param basePath String containing the base path to trim
	 * @return String containing the trimmed path String
	 */
	private static String trimLeadingFileSeparator(String basePath)
	{
		String pathNoLeader = basePath;

		if (pathNoLeader.startsWith("/") || pathNoLeader.startsWith(File.separator))
		{
			pathNoLeader = pathNoLeader.substring(1);
		}

		return pathNoLeader;
	}

	/**
	 * This method adds all files from the included campaigns to this one.
	 * It then strips out the excluded files via a call to stripLstExcludes.
	 *
	 * @param baseCampaign Campaign that includes another campaign
	 * @param subCampaign  Campaign included by the baseCampaign
	 */
	private void initRecursivePccFiles(Campaign baseCampaign, Campaign subCampaign)
	{
		if (subCampaign == null)
		{
			return;
		}

		baseCampaign.addAllLstExcludeFiles(subCampaign.getLstExcludeFiles());
		baseCampaign.addAllRaceFiles(subCampaign.getRaceFiles());
		baseCampaign.addAllClassFiles(subCampaign.getClassFiles());
		baseCampaign.addAllCompanionModFiles(subCampaign.getCompanionModFiles());
		baseCampaign.addAllSkillFiles(subCampaign.getSkillFiles());
		baseCampaign.addAllAbilityFiles(subCampaign.getAbilityFiles());
		baseCampaign.addAllFeatFiles(subCampaign.getFeatFiles());
		baseCampaign.addAllDeityFiles(subCampaign.getDeityFiles());
		baseCampaign.addAllDomainFiles(subCampaign.getDomainFiles());
		baseCampaign.addAllWeaponProfFiles(subCampaign.getWeaponProfFiles());
		baseCampaign.addAllEquipFiles(subCampaign.getEquipFiles());
		baseCampaign.addAllClassSkillFiles(subCampaign.getClassSkillFiles());
		baseCampaign.addAllClassSpellFiles(subCampaign.getClassSpellFiles());
		baseCampaign.addAllSpellFiles(subCampaign.getSpellFiles());
		baseCampaign.addAllLanguageFiles(subCampaign.getLanguageFiles());
		baseCampaign.addAllReqSkillFiles(subCampaign.getReqSkillFiles());
		baseCampaign.addAllTemplateFiles(subCampaign.getTemplateFiles());
		baseCampaign.addAllEquipModFiles(subCampaign.getEquipModFiles());
		baseCampaign.addAllCoinFiles(subCampaign.getCoinFiles());
		baseCampaign.addAllKitFiles(subCampaign.getKitFiles());
		baseCampaign.addAllBioSetFiles(subCampaign.getBioSetFiles());
	}

	/**
	 * This method initializes any campaigns that include other campaigns,
	 * avoiding an infinite loop in the event of recursive (for example 
	 * interdependent campaigns)
	 * 
	 * This specific overloading will recurse down a the given
	 * campaign object dependency tree, then return
	 *
	 * @param baseCampaign Campaign object that may or may not require
	 *                     other campaigns
	 * @throws PersistenceLayerException if an error occurs reading a
	 *                                   newly-encountered campaign
	 */
	private void initRecursivePccFiles(Campaign baseCampaign)
		throws PersistenceLayerException
	{
		if (baseCampaign == null)
		{
			return;
		}

		// Add all sub-files to the main campaign, regardless of exclusions
		Iterator subIter = baseCampaign.getPccFiles().iterator();

		while (subIter.hasNext())
		{
			final String fName = (String) subIter.next();

			if (PCGFile.isPCGenCampaignFile(new File(fName)))
			{
				Campaign globalSubCampaign = Globals.getCampaignByFilename(fName, false);

				if (globalSubCampaign == null)
				{
					try
					{
						loadLstFile(fName);
						globalSubCampaign = Globals.getCampaignByFilename(fName, false);
					}
					catch (PersistenceLayerException e)
					{
						Logging.errorPrint("Recursive init failed on file " + fName, e);
					}
				}

				// add all sub-subs etc to the list
				initRecursivePccFiles(globalSubCampaign);

				// add subfiles to the parent campaign for loading
				initRecursivePccFiles(baseCampaign, globalSubCampaign);
			}
		}
	}
}
