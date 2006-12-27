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

import pcgen.core.Ability;
import pcgen.core.AbilityUtilities;
import pcgen.core.EquipmentUtilities;
import pcgen.core.Globals;
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
	 * @param aString
	 * @return List of Feats
	 */
	public static List<Ability> parseVirtualFeatList(String aString)
	{
		String preString = "";
		List<Ability> aList = new ArrayList<Ability>();

		StringTokenizer aTok = new StringTokenizer(aString, "|");

		while (aTok.hasMoreTokens())
		{
			String aPart = aTok.nextToken();

			if (aPart.length() <= 0)
			{
				continue;
			}

			if ((aPart.startsWith("PRE") || aPart.startsWith("!PRE"))
				&& (aPart.indexOf(":") > 0))
			{
				// We have a PRExxx tag!
				preString = aPart;
			}
			else
			{
				ArrayList<String> choices = new ArrayList<String>();
				String abilityName =
						EquipmentUtilities.getUndecoratedName(aPart, choices);
				Ability anAbility =
						AbilityUtilities.getAbilityFromList(aList, "FEAT",
							abilityName, Ability.Nature.ANY);

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
					for (String choice : choices)
					{
						anAbility.addAssociated(choice);
					}

					aList.add(anAbility);
				}
			}
		}

		if ((preString.length() > 0) && !aList.isEmpty())
		{
			for (Ability ability : aList)
			{
				try
				{
					PreParserFactory factory = PreParserFactory.getInstance();
					Prerequisite prereq = factory.parse(preString);
					ability.addPreReq(prereq);
				}
				catch (PersistenceLayerException ple)
				{
					Logging.errorPrint(ple.getMessage(), ple);
				}
			}
		}

		return aList;
	}

}
