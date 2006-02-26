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
 * Current Ver: $Revision: 1.10 $
 *
 * Last Editor: $Author: karianna $
 *
 * Last Edited: $Date: 2005/12/01 16:40:45 $
 *
 */
package pcgen.persistence.lst.utils;

import pcgen.core.Ability;
import pcgen.core.Globals;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.Logging;

import java.util.*;

/**
 * Parses Feats
 */
public class FeatParser {
	
	/**
	 * Must be of the form:
	 * Feat1|Feat2|PRExx:abx
	 * or
	 * Feat1|Feat2|PREMULT:[PRExxx:abc],[PRExxx:xyz]
	 * @param aString
	 * @return List of Feats
	 */
	public static List parseVirtualFeatList(String aString)
	{
		String preString = "";
		List aList = new ArrayList();

		StringTokenizer aTok = new StringTokenizer(aString, "|");

		while (aTok.hasMoreTokens())
		{
			String aPart = aTok.nextToken();

			if (aPart.length() <= 0)
			{
				continue;
			}

			if ((aPart.startsWith("PRE") || aPart.startsWith("!PRE")) && (aPart.indexOf(":") > 0))
			{
				// We have a PRExxx tag!
				preString = aPart;
			}
			else
			{
				// We have a feat name
				Ability anAbility = Globals.getAbilityNamed("FEAT", aPart);

				if (anAbility != null)
				{
					anAbility = (Ability) anAbility.clone();
					anAbility.setFeatType(Ability.ABILITY_VIRTUAL);
					anAbility.clearPreReq();

					//
					// Check for crazy things like:
					//   Weapon Finesse (Claw, Bite)
					// Which means add the Weapon Finesse
					// feat and apply to Claw and Bite
					//
					if (!anAbility.getName().equalsIgnoreCase(aPart))
					{
						final int i = aPart.indexOf('(');
						final int j = aPart.indexOf(')');

						if ((i >= 0) && (j >= 0))
						{
							final StringTokenizer bTok = new StringTokenizer(aPart.substring(i + 1, j), ",");

							while (bTok.hasMoreTokens())
							{
								final String a = bTok.nextToken();

								if (!anAbility.containsAssociated(a))
								{
									anAbility.addAssociated(a);
								}
							}
						}
					}

					aList.add(anAbility);
				}
			}
		}

		if ((preString.length() > 0) && !aList.isEmpty())
		{
			for (Iterator e = aList.iterator(); e.hasNext();)
			{
				Ability anAbility = (Ability) e.next();

				try
				{
					PreParserFactory factory = PreParserFactory.getInstance();
					Prerequisite prereq = factory.parse(preString);
					anAbility.addPreReq(prereq);
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
