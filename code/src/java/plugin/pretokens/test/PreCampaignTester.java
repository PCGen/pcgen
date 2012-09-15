/*
 * PreCampaignTester.java
 * Copyright 2008 (C) James Dempsey
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
 * Created on 12/07/2008 12:38:47
 *
 * $Id: $
 */
package plugin.pretokens.test;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.persistence.PersistenceManager;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;

/**
 * The Class <code>PreCampaignTester</code> is responsible for testing if the 
 * currently loaded sources satisfy the campaign prerequisite.
 * 
 * Last Editor: $Author: $
 * Last Edited: $Date:  $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision:  $
 */
public class PreCampaignTester extends AbstractPrerequisiteTest implements PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character, CDOMObject source) throws PrerequisiteException
	{
		final int number;
		try
		{
			number = Integer.parseInt(prereq.getOperand());
		}
		catch (NumberFormatException exceptn)
		{
			throw new PrerequisiteException(LanguageBundle.getFormattedString(
				"PreFeat.error", prereq.toString())); //$NON-NLS-1$
		}

		int runningTotal = 0;
		if (prereq.getKey().startsWith("BOOKTYPE="))
		{
			runningTotal +=
					countCampaignByBookType(prereq.getKey().substring(9));
		}
		else
		{
			runningTotal += countCampaignByName(prereq.getKey(), source);
		}

		runningTotal = prereq.getOperator().compare(runningTotal, number);
		return countedTotal(prereq, runningTotal);
	}

	/**
	 * Count the number of campaign currently loaded or selected that 
	 * are of the book type.
	 * 
	 * @param bookType the book type
	 * 
	 * @return the number of matching campaigns
	 */
	private int countCampaignByBookType(String bookType)
	{
		Set<Campaign> matchingCampaigns = new HashSet<Campaign>();
		List<Campaign> campList = Globals.getCampaignList();
		PersistenceManager pMan = PersistenceManager.getInstance();
		for (Campaign campaign : campList)
		{
			if (pMan.isLoaded(campaign)
				&& bookType.equalsIgnoreCase(campaign.getSafe(StringKey.BOOK_TYPE)))
			{
				Logging.debugPrint("Adding campaign " + pMan.isLoaded(campaign) + " type:" + campaign.getSafe(StringKey.BOOK_TYPE));
				matchingCampaigns.add(campaign);
			}
		}

		List<URI> selCampaigns = pMan.getChosenCampaignSourcefiles();
		for (URI element : selCampaigns)
		{
			final Campaign aCampaign = Globals.getCampaignByURI(element, false);

			if (aCampaign != null && bookType.equalsIgnoreCase(aCampaign.getSafe(StringKey.BOOK_TYPE)))
			{
				matchingCampaigns.add(aCampaign);
			}
		}
		return matchingCampaigns.size();
	}

	/**
	 * Count the campaigns currently loaded or selected that match the 
	 * supplied key name.
	 * 
	 * @param key The key to be checked for
	 * @return The number of matching campaigns
	 */
	private int countCampaignByName(final String key, CDOMObject source)
	{
		int total = 0;
		Campaign campaign = Globals.getCampaignKeyedSilently(key);
		if (campaign != null)
		{
			PersistenceManager pMan = PersistenceManager.getInstance();
			if (campaign.getKeyName().equals(key) && pMan.isLoaded(campaign))
			{
				++total;
			}
			else
			{
				List<URI> selCampaigns = pMan.getChosenCampaignSourcefiles();
				for (URI element : selCampaigns)
				{
					final Campaign aCampaign =
							Globals.getCampaignByURI(element);

					if (campaign.equals(aCampaign))
					{
						++total;
					}
				}
			}
		}
		else
		{
			Logging.errorPrint("Unable to find campaign " + key //$NON-NLS-1$
				+ " used in prereq for source " + source + " at " //$NON-NLS-1$ //$NON-NLS-2$
				+ source.getSourceURI());
		}
		return total;
	}

	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
	public String kindHandled()
	{
		return "CAMPAIGN"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		final String foo = LanguageBundle.getFormattedString(
				"PreCampaign.toHtml", //$NON-NLS-1$
				new Object[] { prereq.getOperator().toDisplayString(),
						prereq.getOperand(), prereq.getKey() });
		return foo;
	}

}
