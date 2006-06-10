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
import java.util.Collection;

/**
 * This is the chooser that deals with choosing a Weapon Proficiency
 */
public class WeaponProfChoiceManager extends AbstractComplexChoiceManager<WeaponProf> {

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
				choices.get(0).equals(chooserHandled)) {
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
			final List<WeaponProf>            availableList,
			final List<WeaponProf>            selectedList)
	{
		Iterator<String> choicesIt = choices.iterator();

		while (choicesIt.hasNext())
		{
			final String aString = choicesIt.next();
			final String ucString = aString.toUpperCase();

			if ("LIST".equals(ucString))
			{
				for ( WeaponProf wp : aPc.getWeaponProfList() )
				{
					if (wp == null)
					{
						continue;
					}

					if (!availableList.contains(wp))
					{
						availableList.add(wp);
					}
				}
			}
			else if (ucString.equals("DEITYWEAPON"))
			{
				if (aPc.getDeity() != null)
				{
					String weaponList = aPc.getDeity().getFavoredWeapon();

					if ("ALL".equalsIgnoreCase(weaponList) || "ANY".equalsIgnoreCase(weaponList))
					{
						Collection<WeaponProf> wpList = Globals.getAllWeaponProfs();
						availableList.addAll(wpList);
					}
					else
					{
						final StringTokenizer bTok = new StringTokenizer(weaponList, "|");

						while (bTok.hasMoreTokens())
						{
							final String bString = bTok.nextToken();
							final WeaponProf wp = Globals.getWeaponProfKeyed(bString);
							availableList.add(wp);
						}
					}

				}
			}
			else if (ucString.startsWith("SIZE."))
			{
				final String profKey = aString.substring(7);
				if ((aPc.sizeInt() >= Globals.sizeInt(aString.substring(5, 6)))
					&& aPc.getWeaponProfList().contains(profKey))
				{
					final WeaponProf wp = Globals.getWeaponProfKeyed(profKey);
					if (!availableList.contains(wp))
					{
						availableList.add(wp);
					}
				}
			}
			else if (ucString.startsWith("WSIZE."))
			{
				final StringTokenizer bTok = new StringTokenizer(aString, ".");
				bTok.nextToken(); // should be WSize

				final String sString = bTok.nextToken(); // should be Light, 1 handed, 2 handed choices above
				final List<String> typeList = new ArrayList<String>();

				while (bTok.hasMoreTokens()) // any additional constraints
				{
					final String dString = bTok.nextToken().toUpperCase();
					typeList.add(dString);
				}

				for ( WeaponProf wp : aPc.getWeaponProfList() )
				{
					if (wp == null)
					{
						continue;
					}

					//
					// get an Equipment object based on the named WeaponProf
					//
					final String profKey = wp.getKeyName();
					Equipment eq = EquipmentList.getEquipmentNamed(profKey);

					if (eq == null)
					{
						//
						// Sword (Bastard/Exotic), Sword (Bastard/Martial), Katana (Martial), Katana(Exotic)
						//
						int len = 0;

						if (profKey.endsWith("Exotic)"))
						{
							len = 7;
						}

						if ((len == 0) && profKey.endsWith("Martial)"))
						{
							len = 8;
						}

						if (len != 0)
						{
							if (profKey.charAt(profKey.length() - len - 1) == '/')
							{
								++len;
							}

							String tempString = profKey.substring(0, profKey.length() - len) + ")";

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
							for (Iterator<Map.Entry<String, Equipment>> eqIter = EquipmentList.getEquipmentListIterator(); eqIter.hasNext(); )
							{
								final Map.Entry<String, Equipment> entry = eqIter.next();
								final Equipment tempEq = entry.getValue();

								if (tempEq.isWeapon())
								{
									if (tempEq.profKey(aPc).equals(profKey))
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
							for (Iterator<String> wpi = typeList.iterator(); wpi.hasNext();)
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

					if (!availableList.contains(wp))
					{
						if ("Light".equals(sString) && eq.isWeaponLightForPC(aPc))
						{
							availableList.add(wp);
						}

						if ("1 handed".equals(sString) && eq.isWeaponOneHanded(aPc))
						{
							availableList.add(wp);
						}

						if ("2 handed".equals(sString) && eq.isWeaponTwoHanded(aPc))
						{
							availableList.add(wp);
						}
					}
				}
			}
			else if (ucString.startsWith("SPELLCASTER."))
			{
				// TODO this should not be hardcoded.
				String profKey = aString.substring(12);
				final WeaponProf wp = Globals.getWeaponProfKeyed(profKey);
				if (wp == null)
				{
					continue;
				}
				if (aPc.isSpellCaster(1) && !availableList.contains(wp))
				{
					availableList.add(wp);
				}
			}
			else if (ucString.startsWith("TYPE.") || ucString.startsWith("TYPE="))
			{
				String sString = aString.substring(5);
				boolean adding = true;
				Iterator<WeaponProf> setIter = aPc.getWeaponProfList().iterator();

				if (sString.startsWith("Not."))
				{
					sString = sString.substring(4);
					setIter = availableList.iterator();
					adding = false;
				}

				WeaponProf wp;
				Equipment eq;

				while (setIter.hasNext())
				{
					wp = setIter.next();
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

						if (adding && !availableList.contains(wp))
						{
							availableList.add(wp);
						}
					}
					else if (eq.typeStringContains(sString))
					{
						// if this item is of the desired type, add it to the list
						if (adding && !availableList.contains(wp))
						{
							availableList.add(wp);
						}

						// or try to remove it and reset the iterator since remove cause fits
						else if (!adding && availableList.contains(wp.getKeyName()))
						{
							availableList.remove(wp);
							setIter = availableList.iterator();
						}
					}
					else if (sString.equalsIgnoreCase("LIGHT"))
					{
						// if this item is of the desired type, add it to the list
						if (adding && !availableList.contains(wp) && eq.isWeaponLightForPC(aPc))
						{
							availableList.add(wp);
						}
						// or try to remove it and reset the iterator since remove cause fits
						else if (!adding && availableList.contains(wp) && eq.isWeaponLightForPC(aPc))
						{
							availableList.remove(wp);
							setIter = availableList.iterator();
						}
					}
				}
			}
			else
			{
				String profKey = aString;
				if (ucString.startsWith("ADD."))
				{
					profKey = aString.substring(4);
				}
				final WeaponProf wp = Globals.getWeaponProfKeyed(profKey);
				if (wp != null && aPc.getWeaponProfList().contains(wp) && !availableList.contains(wp))
				{
					availableList.add(wp);
				}
			}
		}

		for ( WeaponProf wp : selectedList )
		{
			pobject.addAssociated( wp. getKeyName() );
		}
	}
}
