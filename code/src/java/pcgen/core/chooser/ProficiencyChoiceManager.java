/**
 * ProficiencyChoiceManager.java
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
 * Current Version: $Revision$
 * Last Editor:     $Author$
 * Last Edited:     $Date: 2006-03-17 15:19:49 +0000 (Fri, 17 Mar 2006) $
 *
 * Copyright 2006 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.WeaponProf;
import pcgen.core.utils.ListKey;
import pcgen.util.Logging;
import java.util.ArrayList;

/**
 * This is the chooser that deals with choosing a Weapon Proficiency
 */
public class ProficiencyChoiceManager extends AbstractComplexChoiceManager<WeaponProf>
{
	static final int SCOPE_PC		= 0;
	static final int SCOPE_ALL		= 1;
	static final int SCOPE_UNIQUE	= 2;
	int              intScope		= -1;
	String           typeOfProf		= "";

	/**
	 * Make a new Weapon Proficiency chooser.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public ProficiencyChoiceManager(
			PObject         aPObject,
			String          choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		title = "Choose Proficiency";
		chooserHandled = "PROFICIENCY";

		if (choices != null && choices.size() > 0 &&
				choices.get(0).equals(chooserHandled)) {
			choices = choices.subList(1, choices.size());
		}

		if (choices.size() < 3)
		{
			Logging.errorPrint("CHOOSE:PROFICIENCY - Incorrect format, not enough tokens. " + choiceString);
		}
		else
		{
			typeOfProf = choices.get(0);

			if ("PC".equals(choices.get(1)))
			{
				intScope = SCOPE_PC;
			}
			else if ("ALL".equals(choices.get(1)))
			{
				intScope = SCOPE_ALL;
			}
			else if ("UNIQUE".equals(choices.get(1)))
			{
				intScope = SCOPE_UNIQUE;
			}
			else
			{
				Logging.errorPrint("CHOOSE:PROFICIENCY - Expecting PC, ALL or UNIQUE - got " + choices.get(1));
			}
		}
	}

	/**
	 * Parse the Choice string and build a list of available choices.
	 *	 CHOOSE:PROFICIENCY|<Type of Prof>|<scope>|<list of profs>
	 * Type of Prof = WEAPON, ARMOR, SHIELD
	 * scope = PC (proficiencies already possessed by PC), ALL (all profs of type), UNIQUE (all profs not already possessed by PC)
	 * list of profs = Either a list of specific profs or a prof TYPE
	 * XXX Note that ARMOR and SHIELD don't work at the moment since I can't get a list of
	 * armor or weapon proficiencies.
	 *
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 */
	public void getChoices(
			final PlayerCharacter aPc,
			final List<WeaponProf>            availableList,
			final List<WeaponProf>            selectedList)
	{
		Iterator<String> It = choices.subList(2, choices.size()).iterator();
		if ("WEAPON".equals(typeOfProf))
		{
			Set<WeaponProf> profs = new TreeSet<WeaponProf>();
			while (It.hasNext())
			{
				final String prof = It.next();
				if (prof.startsWith("TYPE.") || prof.startsWith("TYPE="))
				{
					String typeString = prof.substring(5);
					for (Iterator<WeaponProf> i = Globals.getWeaponProfs(typeString, aPc).iterator();i.hasNext();)
					{
						profs.add(i.next());
					}
				}
				else
				{
					WeaponProf aProf = Globals.getWeaponProfKeyed(prof);
					if (aProf != null)
					{
						profs.add(aProf);
					}
				}
			}

			if (intScope == SCOPE_ALL)
			{
				availableList.addAll(profs);
			}
			else
			{

				Set<WeaponProf> pcProfs = aPc.getWeaponProfs();

				if (intScope == SCOPE_PC)
				{
					profs.retainAll(pcProfs);
					availableList.addAll(profs);
				}
				else if (intScope == SCOPE_UNIQUE)
				{

					// Get a new set which is the intersection of all the Weapon profs
					// specified by the chooser and the Weapon profs that the Pc has
					Set<WeaponProf> pcHas = new TreeSet<WeaponProf>();
					pcHas.addAll(profs);
					pcHas.retainAll(pcProfs);

					for ( WeaponProf wp : pcHas )
					{
						// may have martial and exotic, etc.
						if (wp.getMyTypeCount() != 1)
						{
							availableList.add(wp);
						}
					}

					// since this is a unique list, add all the ones the pc hasn't got
					profs.removeAll(pcProfs);
					availableList.addAll(profs);
				}
			}

		}

		else if ("ARMOR".equals(typeOfProf))
		{
//			List checkList = null;
//			if (intScope == SCOPE_ALL)
//			{
//				checkList = Globals.getArmorProfList();
//			}
//			else
//			{
//				checkList = aPC.getArmorProfList();
//			}
//			while (aTok.hasMoreTokens())
//			{
//				String prof = aTok.nextToken();
//				if ("ALL".equals(prof))
//				{
//					if (intScope == SCOPE_UNIQUE)
//					{
//						List allProfs = Globals.getArmorProfList();
//						for (Iterator i = allProfs.iterator(); i.hasNext();)
//						{
//							String aProf = (String)i.next();
//							if (!checkList.contains(aProf))
//							{
//								availableList.add(aProf);
//							}
//						}
//					}
//					else
//					{
//						availableList.addAll(checkList);
//					}
//					return;
//				}
//				if (prof.startsWith("TYPE") == false)
//				{
//					prof = "TYPE." + prof;
//				}
//				if (checkList.contains(prof))
//				{
//					availableList.add(prof);
//				}
//
//			}
		}
		else if ("SHIELD".equals(typeOfProf))
		{
//			List checkList = null;
//			if (intScope == SCOPE_ALL)
//			{
//				checkList = Globals.getShieldProfList();
//			}
//			else
//			{
//				checkList = aPC.getShieldProfList();
//			}
//			while (aTok.hasMoreTokens())
//			{
//				String prof = aTok.nextToken();
//				if ("ALL".equals(prof))
//				{
//					if (intScope == SCOPE_UNIQUE)
//					{
//						List allProfs = Globals.getArmorProfList();
//						for (Iterator i = allProfs.iterator(); i.hasNext();)
//						{
//							String aProf = (String)i.next();
//							if (!checkList.contains(aProf))
//							{
//								availableList.add(aProf);
//							}
//						}
//					}
//					else
//					{
//						availableList.addAll(checkList);
//					}
//					return;
//				}
//				if (prof.startsWith("TYPE") == false)
//				{
//					prof = "TYPE." + prof;
//				}
//				if (checkList.contains(prof))
//				{
//					availableList.add(prof);
//				}
//
//			}
		}
		else
		{
			Logging.errorPrint("CHOOSE:PROFICIENCY - Unknown type " + typeOfProf);
		}
		List<String> wpKeys = new ArrayList<String>();
		pobject.addAssociatedTo( wpKeys );
		for ( String key : wpKeys )
		{
			WeaponProf wp = Globals.getWeaponProfKeyed( key );
			if ( wp != null )
			{
				selectedList.add( wp );
			}
		}
	}
}
