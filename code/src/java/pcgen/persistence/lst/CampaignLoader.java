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

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.core.prereq.Prerequisite;
import pcgen.io.PCGFile;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * @author David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public class CampaignLoader extends LstLineFileLoader
{
    /**
     * The {@link pcgen.core.Campaign Campaign} being loaded by {@link #loadCampaignLstFile(java.net.URI) loadCampaignLstFile}.
     */
	private Campaign campaign = null;
	private final List<Campaign> inittedCampaigns = new ArrayList<Campaign>();

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
	 * interdependent campaigns.
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
     * This method initializes any campaigns that include other campaigns,
     * avoiding an infinite loop in the event of recursive (for example
     * interdependent campaigns)
     *
     * This specific overloading will recurse down the given
     * campaign object dependency tree, then return
     *
     * @param baseCampaign Campaign object that may or may not require
     *                     other campaigns
     * @throws PersistenceLayerException if an error occurs reading a
     *                                   newly-encountered campaign
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
                Campaign globalSubCampaign =
                        Globals.getCampaignByURI(fName, false);

                // If this campaign has not already been loaded, do so
                if (globalSubCampaign == null)
                {
                    try
                    {
                        loadCampaignLstFile(fName);
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
    private void initRecursivePccFiles(Campaign baseCampaign,
                                       Campaign subCampaign)
    {
        if (subCampaign == null)
        {
            return;
        }

        baseCampaign.addAllToListFor(ListKey.FILE_LST_EXCLUDE, subCampaign.getSafeListFor(ListKey.FILE_LST_EXCLUDE));
        baseCampaign.addAllToListFor(ListKey.FILE_RACE, subCampaign.getSafeListFor(ListKey.FILE_RACE));
        baseCampaign.addAllToListFor(ListKey.FILE_CLASS, subCampaign.getSafeListFor(ListKey.FILE_CLASS));
        baseCampaign.addAllToListFor(ListKey.FILE_COMPANION_MOD, subCampaign.getSafeListFor(ListKey.FILE_COMPANION_MOD));
        baseCampaign.addAllToListFor(ListKey.FILE_COVER, subCampaign.getSafeListFor(ListKey.FILE_COVER));
        baseCampaign.addAllToListFor(ListKey.FILE_SKILL, subCampaign.getSafeListFor(ListKey.FILE_SKILL));
        baseCampaign.addAllToListFor(ListKey.FILE_ABILITY_CATEGORY, subCampaign.getSafeListFor(ListKey.FILE_ABILITY_CATEGORY));
        baseCampaign.addAllToListFor(ListKey.FILE_ABILITY, subCampaign.getSafeListFor(ListKey.FILE_ABILITY));
        baseCampaign.addAllToListFor(ListKey.FILE_FEAT, subCampaign.getSafeListFor(ListKey.FILE_FEAT));
        baseCampaign.addAllToListFor(ListKey.FILE_DEITY, subCampaign.getSafeListFor(ListKey.FILE_DEITY));
        baseCampaign.addAllToListFor(ListKey.FILE_DOMAIN, subCampaign.getSafeListFor(ListKey.FILE_DOMAIN));
        baseCampaign.addAllToListFor(ListKey.FILE_ARMOR_PROF, subCampaign.getSafeListFor(ListKey.FILE_ARMOR_PROF));
        baseCampaign.addAllToListFor(ListKey.FILE_SHIELD_PROF, subCampaign.getSafeListFor(ListKey.FILE_SHIELD_PROF));
        baseCampaign.addAllToListFor(ListKey.FILE_WEAPON_PROF, subCampaign.getSafeListFor(ListKey.FILE_WEAPON_PROF));
        baseCampaign.addAllToListFor(ListKey.FILE_EQUIP, subCampaign.getSafeListFor(ListKey.FILE_EQUIP));
        baseCampaign.addAllToListFor(ListKey.FILE_SPELL, subCampaign.getSafeListFor(ListKey.FILE_SPELL));
        baseCampaign.addAllToListFor(ListKey.FILE_LANGUAGE, subCampaign.getSafeListFor(ListKey.FILE_LANGUAGE));
        baseCampaign.addAllToListFor(ListKey.FILE_TEMPLATE, subCampaign.getSafeListFor(ListKey.FILE_TEMPLATE));
        baseCampaign.addAllToListFor(ListKey.FILE_EQUIP_MOD, subCampaign.getSafeListFor(ListKey.FILE_EQUIP_MOD));
        baseCampaign.addAllToListFor(ListKey.FILE_KIT, subCampaign.getSafeListFor(ListKey.FILE_KIT));
        baseCampaign.addAllToListFor(ListKey.FILE_BIO_SET, subCampaign.getSafeListFor(ListKey.FILE_BIO_SET));
        baseCampaign.addAllToListFor(ListKey.FILE_ALIGNMENT, subCampaign.getSafeListFor(ListKey.FILE_ALIGNMENT));
        baseCampaign.addAllToListFor(ListKey.FILE_STAT, subCampaign.getSafeListFor(ListKey.FILE_STAT));
        baseCampaign.addAllToListFor(ListKey.FILE_SAVES, subCampaign.getSafeListFor(ListKey.FILE_SAVES));
        baseCampaign.addAllToListFor(ListKey.FILE_DATACTRL, subCampaign.getSafeListFor(ListKey.FILE_DATACTRL));
    }

    /**
     * Parses a campaign LST file and adds it to the Global container if not already added.
     * @param filePath The file path to load.
     * @throws PersistenceLayerException
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
            List<String> copyright = campaign.getListFor(ListKey.SECTION_15);
            if (copyright != null)
            {
                StringBuilder sec15 = Globals.getSection15();
                sec15.append("<br><b>Source Material:</b>");
                sec15.append(SourceFormat.getFormattedString(campaign,
                        SourceFormat.LONG, true));
                sec15.append("<br>");
                sec15.append("<b>Section 15 Entry in Source Material:</b><br>");
                for (String license : copyright)
                {
                    sec15.append(license).append("<br>");
                }
            }

            // Adds this campaign to the Global container.
            Globals.addCampaign(campaign);
        }
	}

	@Override
	public void parseLine(LoadContext context, String inputLine, URI sourceURI)
		throws PersistenceLayerException
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
				final PrerequisiteWriter prereqWriter =
						new PrerequisiteWriter();
				ArrayList<Prerequisite> displayList = new ArrayList<Prerequisite>();
				displayList.add(prereq);
				String lstString =
						prereqWriter.getPrerequisiteString(displayList,
							Constants.TAB);
				Logging.log(Logging.LST_ERROR, "Prereq " + prereq.getKind()
					+ " is not supported in PCC files. Prereq was " + lstString
					+ " in " + campaign.getSourceURI() + ". Prereq will be ignored.");
			}
			else
			{
				validatePrereqs(prereq.getPrerequisites());
			}
		}
	}
}
