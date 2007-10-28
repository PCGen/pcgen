/*
 * FeatLoader.java
 *
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 12-Jan-2004
 *
 * Current Ver: $Revision$
 *
 * Last Editor: $Author$
 *
 * Last Edited: $Date$
 *
 */
package pcgen.persistence.lst.utils;

import pcgen.core.*;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

import java.util.*;

/**
 * Parses Feats
 */
public class FeatParser
{

	/**
	 * Must be of the form:
	 * Feat1|Feat2|PRExx:abx
	 * or
	 * Feat1|Feat2|PREMULT:[PRExxx:abc],[PRExxx:xyz]
	 * 
	 * @param aString The string to be parsed
	 * @return List of Feats
	 */
	public static List<Ability> parseVirtualFeatList(final String aString)
	{
		String preString = "";
		final List<Ability> aList = new ArrayList<Ability>();

		final StringTokenizer aTok = new StringTokenizer(aString, "|");

		while (aTok.hasMoreTokens())
		{
			final String tok = aTok.nextToken();

			if (tok.length() <= 0)
			{
				continue;
			}

			if (PreParserFactory.isPreReqString(tok))
			{
				// We have a PRExxx tag!
				preString = tok;
			}
			else
			{
				final Collection<String> choices = new ArrayList<String>();
				final String abilityName =
						AbilityUtilities.getUndecoratedName(tok, choices);
				Ability anAbility = AbilityUtilities.getAbilityFromList(
						aList, "FEAT", abilityName, Ability.Nature.ANY);

				if (anAbility == null)
				{
					anAbility = Globals.getAbilityKeyed("FEAT", abilityName);
					if (anAbility != null)
					{
						anAbility = anAbility.clone();
						anAbility.setFeatType(Ability.Nature.VIRTUAL);
						anAbility.clearPreReq();
					}
				}

				if (anAbility != null)
				{
					for (final String choice : choices)
					{
						anAbility.addAssociated(choice);
					}

					aList.add(anAbility);
				}
			}
		}

		if ((preString.length() > 0) && !aList.isEmpty())
		{
			for (final PrereqObject prO : aList)
			{
				try
				{
					final PreParserFactory factory = PreParserFactory.getInstance();
					final Prerequisite prereq = factory.parse(preString);
					prO.addPreReq(prereq);
				}
				catch (PersistenceLayerException ple)
				{
					Logging.errorPrint(ple.getMessage(), ple);
				}
			}
		}

		return aList;
	}

	/**
	 * Parse a virtual feat list definition. Must be of the form:
	 * Feat1|Feat2|PRExx:abx
	 * or
	 * Feat1|Feat2|PREMULT:[PRExxx:abc],[PRExxx:xyz]
	 * 
	 * @param aString The string to parse
	 * @return List of Feat names and their prereqs
	 */
	public static List<QualifiedObject<String>> parseVirtualFeatListToQualObj(
			final String aString)
	{
		String preString = "";
		final Collection<String> abilityList = new ArrayList<String>();

		final StringTokenizer aTok = new StringTokenizer(aString, "|");

		while (aTok.hasMoreTokens())
		{
			final String tok = aTok.nextToken();

			if (tok.length() <= 0)
			{
				continue;
			}

			if (PreParserFactory.isPreReqString(tok))
			{
				// We have a PRExxx tag!
				preString = tok;
			}
			else
			{
				abilityList.add(tok);
			}
		}

		final List<QualifiedObject<String>> aList =
				new ArrayList<QualifiedObject<String>>();
		for (final String ability : abilityList)
		{
			try
			{
				final List<Prerequisite> prereqList = new ArrayList<Prerequisite>();
				if (preString.length() > 0)
				{
					final PreParserFactory factory = PreParserFactory.getInstance();
					final Prerequisite prereq = factory.parse(preString);
					prereqList.add(prereq);
				}
				aList.add(new QualifiedObject<String>(ability, prereqList));
			}
			catch (PersistenceLayerException ple)
			{
				Logging.errorPrint(ple.getMessage(), ple);
			}
		}

		return aList;
	}
}
