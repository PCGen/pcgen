/*
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
 */
package plugin.pretokens.test;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.AbstractDisplayPrereqTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteException;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceManager;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

/**
 * The Class {@code PreCampaignTester} is responsible for testing if the
 * currently loaded sources satisfy the campaign prerequisite.
 * 
 * 
 */
public class PreCampaignTester extends AbstractDisplayPrereqTest
{

	@Override
	public int passes(final Prerequisite prereq, final CharacterDisplay display, CDOMObject source)
		throws PrerequisiteException
	{
		final int number;
		try
		{
			number = Integer.parseInt(prereq.getOperand());
		}
		catch (NumberFormatException exceptn)
		{
			throw new PrerequisiteException(
				LanguageBundle.getFormattedString("PreFeat.error", prereq.toString()), exceptn); //$NON-NLS-1$
		}

		int runningTotal = 0;
		if (prereq.getKey().startsWith(BOOK_TYPE))
		{
			runningTotal += countCampaignByBookType(prereq.getKey().substring(BOOK_TYPE.length()), false);
		}
		else if (prereq.getKey().startsWith(INCLUDES_BOOK_TYPE))
		{
			runningTotal +=
					countCampaignByBookType(prereq.getKey().substring(INCLUDES_BOOK_TYPE.length()), true);
		}
		else if (prereq.getKey().startsWith(INCLUDES))
		{
			runningTotal += countCampaignByName(prereq.getKey().substring(INCLUDES.length()), source, true);
		}
		else
		{
			runningTotal += countCampaignByName(prereq.getKey(), source, false);
		}

		runningTotal = prereq.getOperator().compare(runningTotal, number);
		return countedTotal(prereq, runningTotal);
	}

	/**
	 * Count the number of campaign currently loaded or selected that 
	 * are of the book type.
	 * 
	 * @param bookType the book type
	 * @param includeSubCampaigns Should we count included sub campaigns that match
	 * 
	 * @return the number of matching campaigns
	 */
	private int countCampaignByBookType(String bookType, boolean includeSubCampaigns)
	{
		Set<Campaign> matchingCampaigns = new HashSet<>();
		PersistenceManager pMan = PersistenceManager.getInstance();
		List<URI> selCampaigns = pMan.getChosenCampaignSourcefiles();
		for (URI element : selCampaigns)
		{
			final Campaign aCampaign = Globals.getCampaignByURI(element, false);
			List<Campaign> fullCampList;
			if (includeSubCampaigns)
			{
				fullCampList = getFullCampaignList(aCampaign);
			}
			else
			{
				fullCampList = new ArrayList<>();
				fullCampList.add(aCampaign);
			}
			for (Campaign camp : fullCampList)
			{
				for (String listType : camp.getSafeListFor(ListKey.BOOK_TYPE))
				{
					if (bookType.equalsIgnoreCase(listType))
					{
						matchingCampaigns.add(camp);
						break;
					}
				}
			}
		}
		return matchingCampaigns.size();
	}

	/**
	 * Count the campaigns currently loaded or selected that match the 
	 * supplied key name.
	 * 
	 * @param key The key to be checked for
	 * @param includeSubCampaigns Should we count included sub campaigns that match
	 * @return The number of matching campaigns
	 */
	private int countCampaignByName(final String key, CDOMObject source, boolean includeSubCampaigns)
	{
		int total = 0;
		Campaign campaignToFind = Globals.getCampaignKeyedSilently(key);
		if (campaignToFind != null)
		{
			PersistenceManager pMan = PersistenceManager.getInstance();
			List<URI> selCampaigns = pMan.getChosenCampaignSourcefiles();
			for (URI element : selCampaigns)
			{
				final Campaign aCampaign = Globals.getCampaignByURI(element, true);
				if (includeSubCampaigns)
				{
					List<Campaign> campList = getFullCampaignList(aCampaign);
					for (Campaign camp : campList)
					{
						if (camp.equals(campaignToFind))
						{
							++total;
						}
					}
				}
				else
				{
					if (aCampaign.equals(campaignToFind))
					{
						++total;
					}
				}
			}
		}
		else
		{
			String sourceUri = (source == null ? "" : String.valueOf(source.getSourceURI()));
			Logging.errorPrint("Unable to find campaign " + key //$NON-NLS-1$
				+ " used in prereq for source " + source + " at " //$NON-NLS-1$ //$NON-NLS-2$
				+ sourceUri);
		}
		return total;
	}

	/**
	 * Retrieve a list of the listed campaign and all campaigns it includes.
	 * @param aCampaign The master campaign.
	 * @return The list of included campaigns.
	 */
	private static List<Campaign> getFullCampaignList(Campaign aCampaign)
	{
		List<Campaign> campList = new ArrayList<>();
		addChildrenRecursively(campList, aCampaign);
		return campList;
	}

	/**
	 * Add the campaign and its children to the supplied list. This will recurse 
	 * through the children to include all descendants. 
	 * @param campList The list being built up.
	 * @param aCampaign The campaign to be added.
	 */
	private static void addChildrenRecursively(List<Campaign> campList, Campaign aCampaign)
	{
		campList.add(aCampaign);
		for (Campaign subCampaign : aCampaign.getSubCampaigns())
		{
			addChildrenRecursively(campList, subCampaign);
		}
	}

	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
	/** Counts directly selected books of a type. */
	private static final String BOOK_TYPE = "BOOKTYPE="; //$NON-NLS-1$

	/** Counts books of a type, including those a selected collection loads. */
	private static final String INCLUDES_BOOK_TYPE = "INCLUDESBOOKTYPE="; //$NON-NLS-1$

	/** Matches a named book, including one a selected collection loads. */
	private static final String INCLUDES = "INCLUDES="; //$NON-NLS-1$

	@Override
	public String kindHandled()
	{
		return "CAMPAIGN"; //$NON-NLS-1$
	}

	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		String required = describeKey(prereq.getKey());

		// Simplify the output when requiring a single source
		if (prereq.getOperator() == PrerequisiteOperator.GTEQ && ("1".equals(prereq.getOperand())))
		{
			return required;
		}

		return LanguageBundle.getFormattedString("PreCampaign.toHtml", //$NON-NLS-1$
			prereq.getOperator().toDisplayString(), prereq.getOperand(), required);
	}

	/**
	 * Describes what a prerequisite key requires, in words.
	 *
	 * <p>A key is the token its author typed into a {@code .pcc} file, and the
	 * prefix is how the four conditions above are told apart. Showing it as
	 * written puts that syntax in front of the user, who was told a source
	 * needed "INCLUDESBOOKTYPE=Core Rules and none of (INCLUDES=DSP - Psionics
	 * Unleashed, ...)".
	 *
	 * <p>Both name forms become the plain name. They differ -- a bare name
	 * requires the book itself to be selected, where {@code INCLUDES=} is also
	 * satisfied by a collection that loads it -- but bare keys are the
	 * commonest kind and arrive in long lists, so qualifying each one would
	 * bury the names. Where that difference decides anything, the source
	 * dialog says so itself.
	 *
	 * @param key the prerequisite's key
	 * @return the requirement in words
	 */
	private static String describeKey(final String key)
	{
		if (key.startsWith(BOOK_TYPE))
		{
			return bookTypeDescription(key.substring(BOOK_TYPE.length()));
		}
		if (key.startsWith(INCLUDES_BOOK_TYPE))
		{
			return bookTypeDescription(key.substring(INCLUDES_BOOK_TYPE.length()));
		}
		if (key.startsWith(INCLUDES))
		{
			return key.substring(INCLUDES.length());
		}
		return key;
	}

	/** A book type names a kind of book, so saying it alone would read as a title. */
	private static String bookTypeDescription(final String bookType)
	{
		return LanguageBundle.getFormattedString("PreCampaign.bookType", bookType); //$NON-NLS-1$
	}

}
