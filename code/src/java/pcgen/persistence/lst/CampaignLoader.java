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

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.core.Campaign;
import pcgen.core.Globals;
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
		campaign.setSourceURI(fileName);

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
			Logging.errorPrint("Invalid Line - does not contain a colon: '"
					+ inputLine + "' in Campaign " + sourceURI);
			return;
		}
		else if (colonLoc == 0)
		{
			Logging.errorPrint("Invalid Line - starts with a colon: '"
					+ inputLine + "' in Campaign " + sourceURI);
			return;
		}

		String key = inputLine.substring(0, colonLoc);
		String value = (colonLoc == inputLine.length() - 1) ? null : inputLine
				.substring(colonLoc + 1);
		if (context.processToken(campaign, key, value))
		{
			context.commit();
		}
		else
		{
			context.rollback();
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
			List<String> copyright = campaign.getListFor(ListKey.SECTION_15);
			if (copyright != null)
			{
				StringBuffer sec15 = Globals.getSection15();
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
	public void initRecursivePccFiles(Campaign baseCampaign)
		//throws PersistenceLayerException
	{
		if (baseCampaign == null || inittedCampaigns.contains(baseCampaign))
		{
			return;
		}

		inittedCampaigns.add(baseCampaign);
		
		// Add all sub-files to the main campaign, regardless of exclusions
		for (CampaignSourceEntry cse : baseCampaign.getSafeListFor(ListKey.FILE_PCC))
		{
			URI fName = cse.getURI();
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
