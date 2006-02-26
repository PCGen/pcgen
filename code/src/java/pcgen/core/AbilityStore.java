/*
 * AbilityStore.java
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
 * Current Version: $Revision: 1.7 $
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2005/10/24 12:04:22 $
 * Copyright 2005 Andrew Wilson <nuance@sourceforge>
 */
package pcgen.core;

import pcgen.util.Logging;

import java.util.StringTokenizer;

/**
 * Subclass of CategorisableStore, specialised for dealing with Ability and
 * AbilityInfo objects
 *
 * @author   Andrew Wilson <nuance@sourceforge.net>
 * @version  $Revision: 1.7 $
 */
public class AbilityStore extends CategorisableStore
{
	/** CLEAR_ TOKEN = ".CLEAR" */
	public static final String CLEAR_TOKEN = ".CLEAR";
	/** CATEGORY_START_TOKEN = "CATEGORY=" */
	public static final String CATEGORY_START_TOKEN = "CATEGORY=";

	/**
	 * This method takes a string in the form "feat1|feat2|feat3"and splits it
	 * on the given delimiters (in this example "|").  It may also be given a
	 * category in either (or both) of two ways.  Firstly, the string may begin
	 * with "CATEGORY=foo|", this will place all following abilities in category
	 * foo until another "CATEGORY=" string is encountered.  Secondly, default
	 * category may be passed as a string. This is intended for use when parsing
	 * "FEAT"s which should not be setting their category.  Setting the
	 * parameter lockCategory true, means that the routine will only accept the
	 * string passed as the default Category.  It is thus possible to specify
	 * FEAT and only FEAT. A blank default category forces the string to begin
	 * with a "CATEGORY="record. It is an error if the default category is blank
	 * and the category is locked.
	 *
	 * @param  abilities        a String (appropriately delimited) representing
	 *                          the abilities to be added
	 * @param  defaultCategory  the default category of ability to add
	 * @param  delimiter        the delimiter used to split the string
	 * @param  lockCategory     whether the default category is the only
	 *                          acceptable one.
	 * @param  getAbility       If true, retrieve the Ability object from
	 *                          Globals, otherwise, create an AbilityInfo object
	 *                          to represent it.
	 */
	public void addAbilityInfo(
		final String abilities,
		String       defaultCategory,
		String       delimiter,
		boolean      lockCategory,
		boolean      getAbility)
	{
		if (CLEAR_TOKEN.equals(abilities))
		{
			this.clear();

			return;
		}

		final StringTokenizer aTok = new StringTokenizer(abilities, delimiter, false);

		String cat = getInitialCategory(defaultCategory, lockCategory, aTok);

		if ("".equals(cat))
		{
			return;
		}

		while (aTok.hasMoreTokens())
		{
			final String token = aTok.nextToken();

			if (token.startsWith(CATEGORY_START_TOKEN))
			{
				if (lockCategory)
				{
					Logging.errorPrint("Attempting to change the Category of a Feat");
					return;
				}
				cat = token.substring(CATEGORY_START_TOKEN.length());

				continue;
			}

			addAsPerParsedInfo(getAbility, cat, token);
		}
	}

	/**
	 * @param getAbility
	 * @param cat
	 * @param token
	 */
	private void addAsPerParsedInfo(boolean getAbility, String cat, final String token) {
		Categorisable toAdd = (getAbility)
			? (Categorisable) AbilityUtilities.retrieveAbilityKeyed(cat, token)
			: (Categorisable) new AbilityInfo(cat, token);

		if (toAdd == null)
		{
			if (getAbility)
			{
				Logging.errorPrint(
				    "Couldn't retrieve Ability! Category: " + cat + ", KeyName: " +
				    token);
			}
		}
		else
		{
			if (!this.addNewCategory(toAdd))
			{
				String error = (getAbility) ? "Ability object" : "AbilityInfo object";
				Logging.errorPrint("problem adding " + error);
			}
		}
	}

	/**
	 * This routine is called by addAbilityInfo to get a Category for the
	 * Abilities (or AbilityInfo) to be stored. If the default Category is
	 * blank, it attempts to extract a category for the first field of the input
	 * string (which is now tokenised in aTok). If the Category is locked or the
	 * first field is not a category, an error is produced and the method
	 * returns a blank string (i.e. no category set).
	 *
	 * @param   defaultCategory
	 * @param   lockCategory
	 * @param   aTok
	 *
	 * @return  a category or a blank string
	 */
	private String getInitialCategory(
	    String                defaultCategory,
	    boolean               lockCategory,
	    final StringTokenizer aTok)
	{
		if (!"".equalsIgnoreCase(defaultCategory)) {
			return defaultCategory;
		}

		if (lockCategory)
		{
			Logging.errorPrint("No Category set for Ability");
			return "";
		}

		final String token = aTok.nextToken();

		if (token.startsWith(CATEGORY_START_TOKEN))
		{
			return token.substring(CATEGORY_START_TOKEN.length());
		}
		Logging.errorPrint("No Category set for Ability");

		return "";
	}
}
