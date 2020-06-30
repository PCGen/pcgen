/*
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
 *
 *
 */
package pcgen.persistence.lst;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.core.prereq.Prerequisite;
import pcgen.io.PCGFile;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public class CampaignLoader extends LstLineFileLoader
{
	/**
	 * The {@link pcgen.core.Campaign Campaign}
	 * being loaded by {@link #loadCampaignLstFile(java.net.URI) loadCampaignLstFile}.
	 */
	private Campaign campaign = null;
	private final List<Campaign> inittedCampaigns = new ArrayList<>();

	public static final List<ListKey<CampaignSourceEntry>> OTHER_FILE_LISTKEY = List.of(ListKey.FILE_LST_EXCLUDE, ListKey.FILE_COVER);

	public static final List<ListKey<CampaignSourceEntry>> OBJECT_FILE_LISTKEY = List.of(ListKey.FILE_RACE, ListKey.FILE_CLASS,
		ListKey.FILE_COMPANION_MOD, ListKey.FILE_SKILL, ListKey.FILE_ABILITY_CATEGORY, ListKey.FILE_ABILITY,
		ListKey.FILE_FEAT, ListKey.FILE_DEITY, ListKey.FILE_DOMAIN, ListKey.FILE_ARMOR_PROF, ListKey.FILE_SHIELD_PROF,
		ListKey.FILE_WEAPON_PROF, ListKey.FILE_EQUIP, ListKey.FILE_SPELL, ListKey.FILE_LANGUAGE, ListKey.FILE_TEMPLATE,
		ListKey.FILE_EQUIP_MOD, ListKey.FILE_KIT, ListKey.FILE_BIO_SET, ListKey.FILE_ALIGNMENT, ListKey.FILE_STAT,
		ListKey.FILE_SAVE, ListKey.FILE_SIZE, ListKey.FILE_DATACTRL, ListKey.FILE_VARIABLE, ListKey.FILE_DYNAMIC,
		ListKey.FILE_DATATABLE, ListKey.FILE_GLOBALMOD);

	/**
	 * This method initializes any campaigns that include other campaigns,
	 * avoiding an infinite loop in the event of recursive (for example
	 * interdependent campaigns)
	 *
	 * This specific overloading will recurse down the given
	 * campaign object dependency tree, then return
	 *
	 * @param baseCampaign Campaign object that may or may not require
	 *                     other campaigns
	 */
	public void initRecursivePccFiles(Campaign baseCampaign)
	//throws PersistenceLayerException
	{
		if (baseCampaign == null || inittedCampaigns.contains(baseCampaign))
		{
			return;
		}

		inittedCampaigns.add(baseCampaign);

		// Add all sub-files to the base campaign, regardless of exclusions
		for (CampaignSourceEntry cse : baseCampaign.getSafeListFor(ListKey.FILE_PCC))
		{
			URI fName = cse.getURI();
			if (PCGFile.isPCGenCampaignFile(fName))
			{
				// Find referenced campaign if loaded
				Campaign globalSubCampaign = Globals.getCampaignByURI(fName, false);

				// If this campaign has not already been loaded, do so
				if (globalSubCampaign == null)
				{
					try
					{
						loadCampaignLstFile(fName);
						globalSubCampaign = Globals.getCampaignByURI(fName, false);
					}
					catch (PersistenceLayerException e)
					{
						Logging.errorPrint("Recursive init failed on file " + fName, e);
					}
				}

				// add all sub-subs etc to the list
				initRecursivePccFiles(globalSubCampaign);

				// add subfile to the parent campaign for loading
				initRecursivePccFiles(baseCampaign, globalSubCampaign);
			}
		}
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

		for (ListKey<CampaignSourceEntry> lk : OBJECT_FILE_LISTKEY)
		{
			addToBaseCampaign(baseCampaign, subCampaign, lk);
		}
		for (ListKey<CampaignSourceEntry> lk : OTHER_FILE_LISTKEY)
		{
			addToBaseCampaign(baseCampaign, subCampaign, lk);
		}
	}

	private <T> void addToBaseCampaign(Campaign baseCampaign, Campaign subCampaign, ListKey<T> lk)
	{
		baseCampaign.addAllToListFor(lk, subCampaign.getSafeListFor(lk));
	}

	/**
	 * Parses a campaign LST file and adds it to the Global container if not already added.
	 * @param filePath The file path to load.
	 * @throws PersistenceLayerException  if problems with lst file.
	 */
	public void loadCampaignLstFile(URI filePath) throws PersistenceLayerException
	{
		// Instantiate a Campaign, which will automatically establish a LoadContext
		campaign = new Campaign();
		campaign.setSourceURI(filePath);

		// Parses the data in the referenced URI and loads it into a LoadContext;
		// this quickly goes to the parseLine method below
		super.loadLstFile(campaign.getCampaignContext(), filePath);

		// Make sure this campaign has not already been added to the Global container
		if (Globals.getCampaignByURI(campaign.getSourceURI(), false) == null)
		{
			// Check the campaign's prerequisites, generating errors if any are not met but proceeding
			validatePrereqs(campaign.getPrerequisiteList());

			// Adds this campaign to the Global container.
			Globals.addCampaign(campaign);
		}
	}

	@Override
	public void parseLine(LoadContext context, String inputLine, URI sourceURI)
    {
		LstUtils.processToken(context, campaign, sourceURI, inputLine);
	}

	/**
	 * Check that all prerequisites specified in the PCC file are 
	 * supported. Any unsupported prereqs will be reported as LST 
	 * errors. This is a recursive function allowing it to 
	 * check nested prereqs.
	 * 
	 * @param prereqList The prerequisites to be checked.
	 */
	private void validatePrereqs(List<Prerequisite> prereqList)
	{
		if (prereqList == null || prereqList.isEmpty())
		{
			return;
		}

		for (Prerequisite prereq : prereqList)
		{
			if (prereq.isCharacterRequired())
			{
				final PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
				ArrayList<Prerequisite> displayList = new ArrayList<>();
				displayList.add(prereq);
				String lstString = prereqWriter.getPrerequisiteString(displayList, Constants.TAB);
				Logging.log(Logging.LST_ERROR,
					"Prereq " + prereq.getKind() + " is not supported in PCC files. Prereq was " + lstString + " in "
						+ campaign.getSourceURI() + ". Prereq will be ignored.");
			}
			else
			{
				validatePrereqs(prereq.getPrerequisites());
			}
		}
	}
}
