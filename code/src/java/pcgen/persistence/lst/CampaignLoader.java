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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.persistence.lst;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.core.SourceEntry;
import pcgen.io.PCGFile;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * @author David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
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
		List<Campaign> initialCampaigns =
				new ArrayList<Campaign>(Globals.getCampaignList());

		for (Campaign c : initialCampaigns)
		{
			initRecursivePccFiles(c);
		}
	}

	/**
	 * @see pcgen.persistence.lst.LstLineFileLoader#loadLstFile(java.net.URI)
	 */
	@Override
	public void loadLstFile(LoadContext context, URI fileName) throws PersistenceLayerException
	{
		campaign = new Campaign();

		super.loadLstFile(campaign.getCampaignContext(), fileName);

		finishCampaign();
	}

	@Override
	public void parseLine(LoadContext context, String inputLine, URI sourceURI)
		throws PersistenceLayerException
	{
		final int colonLoc = inputLine.indexOf(':');
		if (colonLoc == -1)
		{
			Logging.errorPrint("Invalid line - does not contain a colon: "
					+ inputLine);
			return;
		}
		else if (colonLoc == 0)
		{
			Logging.errorPrint("Invalid line - starts with a colon: "
					+ inputLine);
			return;
		}
		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(
				CampaignLstToken.class);

		String key = inputLine.substring(0, colonLoc);
		String value = (colonLoc == inputLine.length() - 1) ? null : inputLine
				.substring(colonLoc + 1);
		if (context.processToken(campaign, key, value))
		{
			context.commit();
		}
		else if (tokenMap.containsKey(key))
		{
			CampaignLstToken token = (CampaignLstToken) tokenMap.get(key);
			LstUtils.deprecationCheck(token, campaign, value);
			if (!token.parse(campaign, value, sourceURI))
			{
				Logging.errorPrint("Error parsing campaign "
					+ campaign.getDisplayName() + ':' + inputLine);
			}
		}
		else if (!PObjectLoader.parseTag(campaign, inputLine))
		{
			Logging.replayParsedMessages();
		}
		Logging.clearParseMessages();
	}

	/**
	 * This method finishes the campaign being loaded by saving its section 15
	 * information as well as adding it to Globals, if it has not already been
	 * loaded.
	 */
	protected void finishCampaign()
	{
		if (Globals.getCampaignByURI(campaign.getSourceURI(), false) == null)
		{
			final String sect15 = campaign.getSection15String();

			if ((sect15 != null) && (sect15.trim().length() > 0))
			{
				Globals.getSection15().append("<br><b>Source Material:</b>");
				Globals.getSection15().append(
					campaign.getSourceEntry().getFormattedString(
						SourceEntry.SourceFormat.LONG, true));
				Globals.getSection15().append("<br>");
				Globals.getSection15().append(
					"<b>Section 15 Entry in Source Material:</b><br>");
				Globals.getSection15().append(sect15);
			}

			Globals.addCampaign(campaign);
		}
	}

	/**
	 * This method adds all files from the included campaigns to this one.
	 * It then strips out the excluded files via a call to stripLstExcludes.
	 *
	 * @param baseCampaign Campaign that includes another campaign
	 * @param subCampaign  Campaign included by the baseCampaign
	 */
	private void initRecursivePccFiles(Campaign baseCampaign,
		Campaign subCampaign)
	{
		if (subCampaign == null)
		{
			return;
		}

		baseCampaign.addAllLstExcludeFiles(subCampaign.getLstExcludeFiles());
		baseCampaign.addAllRaceFiles(subCampaign.getRaceFiles());
		baseCampaign.addAllClassFiles(subCampaign.getClassFiles());
		baseCampaign
			.addAllCompanionModFiles(subCampaign.getCompanionModFiles());
		baseCampaign.addAllCoverFiles(subCampaign.getCoverFiles());
		baseCampaign.addAllSkillFiles(subCampaign.getSkillFiles());
		baseCampaign.addAllAbilityCategoryFiles(subCampaign.getAbilityCategoryFiles());
		baseCampaign.addAllAbilityFiles(subCampaign.getAbilityFiles());
		baseCampaign.addAllFeatFiles(subCampaign.getFeatFiles());
		baseCampaign.addAllDeityFiles(subCampaign.getDeityFiles());
		baseCampaign.addAllDomainFiles(subCampaign.getDomainFiles());
		baseCampaign.addAllArmorProfFiles(subCampaign.getArmorProfFiles());
		baseCampaign.addAllShieldProfFiles(subCampaign.getShieldProfFiles());
		baseCampaign.addAllWeaponProfFiles(subCampaign.getWeaponProfFiles());
		baseCampaign.addAllEquipFiles(subCampaign.getEquipFiles());
		baseCampaign.addAllClassSkillFiles(subCampaign.getClassSkillFiles());
		baseCampaign.addAllClassSpellFiles(subCampaign.getClassSpellFiles());
		baseCampaign.addAllSpellFiles(subCampaign.getSpellFiles());
		baseCampaign.addAllLanguageFiles(subCampaign.getLanguageFiles());
		baseCampaign.addAllReqSkillFiles(subCampaign.getReqSkillFiles());
		baseCampaign.addAllTemplateFiles(subCampaign.getTemplateFiles());
		baseCampaign.addAllEquipModFiles(subCampaign.getEquipModFiles());
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
		for (URI fName : baseCampaign.getPccFiles())
		{
			if (PCGFile.isPCGenCampaignFile(fName))
			{
				Campaign globalSubCampaign =
						Globals.getCampaignByURI(fName, false);

				if (globalSubCampaign == null)
				{
					try
					{
						loadLstFile(null, fName);
						globalSubCampaign =
								Globals.getCampaignByURI(fName, false);
					}
					catch (PersistenceLayerException e)
					{
						Logging.errorPrint("Recursive init failed on file "
							+ fName, e);
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
