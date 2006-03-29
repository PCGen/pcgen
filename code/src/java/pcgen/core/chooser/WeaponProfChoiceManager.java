/**
 * WeaponProfChoiceManager.java
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
 * Current Version: $Revision: 285 $
 * Last Editor:     $Author: nuance $
 * Last Edited:     $Date: 2006-03-17 15:19:49 +0000 (Fri, 17 Mar 2006) $
 *
 * Copyright 2006 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import pcgen.core.Ability;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.EquipmentList;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.WeaponProf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * This is the chooser that deals with choosing a Weapon Proficiency
 */
public class WeaponProfChoiceManager extends AbstractComplexChoiceManager {

	/**
	 * Make a new Weapon Proficiency chooser.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public WeaponProfChoiceManager(
			PObject         aPObject,
			String          choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		title = "Weapon Prof Choice";
		chooserHandled = "WEAPONPROFS";
		
		if (choices != null && choices.size() > 0 &&
				((String) choices.get(0)).equals(chooserHandled)) {
			choices = choices.subList(1, choices.size());
		}
	}

	/**
	 * Parse the Choice string and build a list of available choices.
	 * 
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 */
	public void getChoices(
			final PlayerCharacter aPc,
			final List            availableList,
			final List            selectedList)
	{
		Iterator choicesIt = choices.iterator();
		
		while (choicesIt.hasNext())
		{
			final String aString = (String) choicesIt.next();

			if ("LIST".equals(aString))
			{
				String bString;

				for (Iterator setIter = aPc.getWeaponProfList().iterator(); setIter.hasNext();)
				{
					bString = (String) setIter.next();

					if (!availableList.contains(bString))
					{
						availableList.add(bString);
					}
				}
			}
			else if (aString.equals("DEITYWEAPON"))
			{
				if (aPc.getDeity() != null)
				{
					String weaponList = aPc.getDeity().getFavoredWeapon();

					if ("ALL".equalsIgnoreCase(weaponList) || "ANY".equalsIgnoreCase(weaponList))
					{
						weaponList = Globals.getWeaponProfNames("|", false);
					}

					final StringTokenizer bTok = new StringTokenizer(weaponList, "|");

					while (bTok.hasMoreTokens())
					{
						final String bString = bTok.nextToken();
						availableList.add(bString);
					}
				}
			}
			else if (aString.startsWith("Size."))
			{
				if ((aPc.sizeInt() >= Globals.sizeInt(aString.substring(5, 6)))
					&& aPc.getWeaponProfList().contains(aString.substring(7))
					&& !availableList.contains(aString.substring(7)))
				{
					availableList.add(aString.substring(7));
				}
			}
			else if (aString.startsWith("WSize."))
			{
				String bString;
				WeaponProf wp;
				final StringTokenizer bTok = new StringTokenizer(aString, ".");
				bTok.nextToken(); // should be WSize

				final String sString = bTok.nextToken(); // should be Light, 1 handed, 2 handed choices above
				final List typeList = new ArrayList();

				while (bTok.hasMoreTokens()) // any additional constraints
				{
					final String dString = bTok.nextToken().toUpperCase();
					typeList.add(dString);
				}

				for (Iterator setIter = aPc.getWeaponProfList().iterator(); setIter.hasNext();)
				{
					bString = (String) setIter.next();
					wp = Globals.getWeaponProfNamed(bString);

					if (wp == null)
					{
						continue;
					}

					//
					// get an Equipment object based on the named WeaponProf
					//
					Equipment eq = EquipmentList.getEquipmentNamed(wp.getName());

					if (eq == null)
					{
						//
						// Sword (Bastard/Exotic), Sword (Bastard/Martial), Katana (Martial), Katana(Exotic)
						//
						int len = 0;

						if (bString.endsWith("Exotic)"))
						{
							len = 7;
						}

						if ((len == 0) && bString.endsWith("Martial)"))
						{
							len = 8;
						}

						if (len != 0)
						{
							if (bString.charAt(bString.length() - len - 1) == '/')
							{
								++len;
							}

							String tempString = bString.substring(0, bString.length() - len) + ")";

							if (tempString.endsWith("()"))
							{
								tempString = tempString.substring(0, tempString.length() - 3).trim();
							}

							eq = EquipmentList.getEquipmentNamed(tempString);
						}
						else
						{
							//
							// Couldn't find equipment with matching name, look for 1st weapon that uses it
							//
							for (Iterator eqIter = EquipmentList.getEquipmentListIterator(); eqIter.hasNext(); )
							{
								final Map.Entry entry = (Map.Entry)eqIter.next();
								final Equipment tempEq = (Equipment) entry.getValue();

								if (tempEq.isWeapon())
								{
									if (tempEq.profName(aPc).equals(wp.getName()))
									{
										eq = tempEq;

										break;
									}
								}
							}
						}
					}

					boolean isValid = false; // assume we match unless...

					if (eq != null)
					{
						if (typeList.size() == 0)
						{
							isValid = true;
						}
						else
						{
							//
							// search all the optional type strings, just one match passes the test
							//
							for (Iterator wpi = typeList.iterator(); wpi.hasNext();)
							{
								final String wpString = (String) wpi.next();

								if (eq.isType(wpString))
								{
									isValid = true; // if it contains even one of the TYPE strings, it passes

									break;
								}
							}
						}
					}

					if (!isValid)
					{
						continue;
					}

					if (!availableList.contains(bString))
					{
						if ("Light".equals(sString) && Globals.isWeaponLightForPC(aPc, eq))
						{
							availableList.add(bString);
						}

						if ("1 handed".equals(sString) && Globals.isWeaponOneHanded(aPc, eq, wp))
						{
							availableList.add(bString);
						}

						if ("2 handed".equals(sString) && Globals.isWeaponTwoHanded(aPc, eq, wp))
						{
							availableList.add(bString);
						}
					}
				}
			}
			else if (aString.startsWith("SpellCaster."))
			{
				if (aPc.isSpellCaster(1) && !availableList.contains(aString.substring(12)))
				{
					availableList.add(aString.substring(12));
				}
			}
			else if (aString.startsWith("ADD."))
			{
				if (!availableList.contains(aString.substring(4)))
				{
					availableList.add(aString.substring(4));
				}
			}
			else if (aString.startsWith("TYPE.") || aString.startsWith("TYPE="))
			{
				String sString = aString.substring(5);
				boolean adding = true;
				Iterator setIter = aPc.getWeaponProfList().iterator();

				if (sString.startsWith("Not."))
				{
					sString = sString.substring(4);
					setIter = availableList.iterator();
					adding = false;
				}

				String bString;
				WeaponProf wp;
				Equipment eq;

				while (setIter.hasNext())
				{
					bString = (String) setIter.next();
					wp = Globals.getWeaponProfNamed(bString);

					if (wp == null)
					{
						continue;
					}

					eq = EquipmentList.getEquipmentKeyed(wp.getKeyName());

					if (eq == null)
					{
						if (!wp.isType("Natural")) //natural weapons are not in the global eq.list
						{
							continue;
						}

						if (adding && !availableList.contains(wp.getName()))
						{
							availableList.add(wp.getName());
						}
					}
					else if (eq.typeStringContains(sString))
					{
						// if this item is of the desired type, add it to the list
						if (adding && !availableList.contains(wp.getName()))
						{
							availableList.add(wp.getName());
						}

						// or try to remove it and reset the iterator since remove cause fits
						else if (!adding && availableList.contains(wp.getName()))
						{
							availableList.remove(wp.getName());
							setIter = availableList.iterator();
						}
					}
					else if (sString.equalsIgnoreCase("LIGHT"))
					{
						// if this item is of the desired type, add it to the list
						if (adding && !availableList.contains(wp.getName()) && Globals.isWeaponLightForPC(aPc, eq))
						{
							availableList.add(wp.getName());
						}
						// or try to remove it and reset the iterator since remove cause fits
						else if (!adding && availableList.contains(wp.getName()) && Globals.isWeaponLightForPC(aPc, eq))
						{
							availableList.remove(wp.getName());
							setIter = availableList.iterator();
						}
					}
				}
			}
			else
			{
				if (aPc.getWeaponProfList().contains(aString) && !availableList.contains(aString))
				{
					availableList.add(aString);
				}
			}
		}

		pobject.addAssociatedTo(selectedList);
	}

	
	/**
	 * Apply the choices selected to the associated PObject (the one passed
	 * to the constructor)
	 * @param aPC
	 * @param selected
	 *
	 */
	public void applyChoices(
			PlayerCharacter  aPC,
			List             selected)
	{
		pobject.clearAssociated();

		String objPrefix = "";

		if (pobject instanceof Domain)
		{
			objPrefix = chooserHandled + '?';
		}

		if (pobject instanceof Ability) {
			((Ability)pobject).clearSelectedWeaponProfBonus(); //Cleans up the feat
		}

		for (int i = 0; i < selected.size(); ++i)
		{
			final String chosenItem = (String) selected.get(i);

			if (multiples && !dupsAllowed)
			{
				if (!pobject.containsAssociated(objPrefix + chosenItem))
				{
					pobject.addAssociated(objPrefix + chosenItem);
				}
			}
			else
			{
				pobject.addAssociated(objPrefix + chosenItem);
			}

			if (Globals.weaponTypesContains(chooserHandled))
			{
				aPC.addWeaponProf(objPrefix + chosenItem);
			}
		}

		double featCount = aPC.getFeats();
		if (numberOfChoices > 0)
		{
			if (cost > 0)
			{
				featCount -= cost;
			}
		}
		else
		{
			if (cost > 0)
			{
				featCount = ((maxSelections - selected.size()) * cost);
			}
		}

		aPC.adjustFeats(featCount - aPC.getFeats());

		// This will get assigned by autofeat (if a feat)

		if (objPrefix.length() != 0)
		{
			aPC.setAutomaticFeatsStable(false);
		}
	}

	
}
