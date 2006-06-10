/*
 * SimpleWeaponProfChoiceManager.java
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
 * Last Editor: $Author$
 * Last Edited: $Date$
 * Copyright 2005 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import pcgen.core.*;
import pcgen.core.utils.ListKey;

import java.util.*;

/**
 * <code>SimpleWeaponProfChoiceManager</code>
 *
 * Deal with choosing a weapon proficiency
 *
 * @author   Andrew Wilson <nuance@sourceforge.net>
 * @version  $Revision$
 */
public class SimpleWeaponProfChoiceManager extends AbstractSimpleChoiceManager<String>
{
	/**
	 * <code>weaponToProf</code>
	 *
	 * Inner class storing weapon proficency.
	 */
	public class weaponToProf implements Categorisable
	{
		String category;
		String keyName;

		/**
		 * Creates a new weaponToProf object.
		 *
		 * @param  category
		 * @param  name
		 */
		public weaponToProf(String category, String name)
		{
			super();
			this.category = category;
			this.keyName  = name;
		}

		public String getCategory()
		{
			return category;
		}

		public String getKeyName()
		{
			return keyName;
		}

		public String getDisplayName()
		{
			return keyName;
		}

		public String toString()
		{
			return keyName;
		}
	}

	final CategorisableStore<weaponToProf> weaponToProfMap = new CategorisableStore<weaponToProf>();

	/**
	 * Creates a new SimpleWeaponProfChoiceManager object.
	 *
	 * @param  aPObject		Object holding the choice string used to generate lists.
	 * @param  theChoices	The list of Weapon Proficency choices.
	 * @param  aPC			The Player Character.
	 */
	public SimpleWeaponProfChoiceManager(
		PObject         aPObject,
		String          theChoices,
		PlayerCharacter aPC)
	{
		super(aPObject, theChoices, aPC);
	}

	/**
	 * Get the list of appropriate Weapon Proficiencies
	 * @param  aPc            The Player Character.
	 * @param  availableList  The list weapon proficiencies are added to.
	 * @param  selectedList   Contains all entries for a weapon proficency key.
	 */
	public void getChoices(
		PlayerCharacter aPc,
		List<String>            availableList,
		List<String>            selectedList)
	{
		weaponToProfMap.clear();

		dupsAllowed = false;
		title       = "Weapon Choice(s)";

		selectedList.addAll(pobject.getSafeListFor(ListKey.SELECTED_WEAPON_PROF_BONUS));

		for ( String raw : choices )
		{
			boolean      adding   = false;
			String       parsed   = raw;
			final String unparsed = raw;

			if (raw.lastIndexOf('[') >= 0)
			{
				final StringTokenizer profTok    = new StringTokenizer(raw, "[]");
				final String          profString = profTok.nextToken();
				adding = true;

				while (profTok.hasMoreTokens())
				{
					weaponToProfMap.addCategorisable(
						new weaponToProf(profString, profTok.nextToken()));
				}

				parsed = profString;
			}

			if ("DEITYWEAPON".equals(parsed))
			{
				buildWeaponProfDeityChoices(unparsed, availableList, adding, aPc);
			}
			else if (parsed.startsWith("TYPE=") || parsed.startsWith("TYPE."))
			{
				buildWeaponProfTypeChoices(unparsed, availableList, parsed, aPc);
			}
			else if (parsed.startsWith("WIELD=") || parsed.startsWith("WIELD."))
			{
				buildWeaponProfWeildChoices(unparsed, availableList, parsed, aPc);
			}
			else if (parsed.startsWith("!TYPE=") || parsed.startsWith("!TYPE."))
			{
				removeExcludedWeaponProfTypeChoices(parsed, availableList, aPc);
			}
			else
			{
				availableList.add(parsed);
			}
		}
	}

	/**
	 * Get the choices for a Deity weapon proficiency
	 *
	 * @param  unparsed			Unparsed string of choices.
	 * @param  availableList	The list weapon proficiencies are added to.
	 * @param  adding			Set if weapon proficiencies should be added.
	 * @param  aPC				The Player Character.
	 */
	private void buildWeaponProfDeityChoices(
		final String          unparsed,
		final List<String>            availableList,
		boolean               adding,
		final PlayerCharacter aPC)
	{
		if (aPC.getDeity() != null)
		{
			String weaponList = aPC.getDeity().getFavoredWeapon();

			if ("ALL".equalsIgnoreCase(weaponList) ||
				"ANY".equalsIgnoreCase(weaponList))
			{
				weaponList = Globals.getWeaponProfNames("|", false);
			}

			final StringTokenizer bTok = new StringTokenizer(weaponList, "|");

			while (bTok.hasMoreTokens())
			{
				final String bString = bTok.nextToken();
				availableList.add(bString);

				if (adding)
				{
					final StringTokenizer cTok = new StringTokenizer(unparsed, "[]");
					cTok.nextToken(); // Read and throw away a token

					while (cTok.hasMoreTokens())
					{
						weaponToProfMap.addCategorisable(new weaponToProf(bString, cTok.nextToken()));
					}
				}
			}
		}
	}

	/**
	 * Get a list of Weapon proficiencies by weapon type
	 *
	 * @param  unparsed			The unparsed string of choices.
	 * @param  availableList	The list weapon proficiencies are added to.
	 * @param  parsed			Parsed string, the first part id ignored the rest is types.
	 * @param  aPC				The Player Character.
	 */
	private void buildWeaponProfTypeChoices(
		final String          unparsed,
		final List<String>            availableList,
		String                parsed,
		final PlayerCharacter aPC)
	{
		final String          types    = parsed.substring(5);
		final StringTokenizer aTok     = new StringTokenizer(types, ".");
		final List<String>            typeList = new ArrayList<String>();
		int                   iSize    = -1;

		while (aTok.hasMoreTokens())
		{
			final String bString = aTok.nextToken();

			if (bString.startsWith("SIZE=") || parsed.startsWith("SIZE."))
			{
				iSize = Globals.sizeInt(bString.substring(5));
			}
			else
			{
				typeList.add(bString);
			}
		}

		Iterator<Map.Entry<String, Equipment>> ei = EquipmentList.getEquipmentListIterator();

		while (ei.hasNext())
		{
			final Equipment aEq   = ei.next().getValue();

			if (!aEq.isWeapon())
			{
				continue;
			}

			boolean bOk = true;

			for ( String ti : typeList )
			{
				if (!aEq.isType(ti) )
				{
					break;
				}

				if (iSize >= 0)
				{
					bOk &= (Globals.sizeInt(aEq.getSize()) == iSize);
				}

				if (bOk)
				{
					String wpName = aEq.profKey(aPC);
					addtoToAvailableAndMap(unparsed, availableList, wpName);
				}
			}
		}
	}

	/**
	 * Get a list of weapon proficiencies by wield category
	 *
	 * @param  unparsed			The unparsed string of choices.
	 * @param  availableList	The list weapon proficiencies are added to.
	 * @param  parsed			Parsed string, the first part id ignored, the rest is added to the weild list
	 * @param  aPC				The Player Character.
	 */
	private void buildWeaponProfWeildChoices(
		final String          unparsed,
		final List<String>            availableList,
		String                parsed,
		final PlayerCharacter aPC)
	{
		final StringTokenizer bTok      = new StringTokenizer(parsed.substring(6), ".");
		final List<String>            wieldList = new ArrayList<String>();

		while (bTok.hasMoreTokens())
		{
			wieldList.add(bTok.nextToken());
		}

		for (Iterator<Map.Entry<String, Equipment>> ei = EquipmentList.getEquipmentListIterator(); ei.hasNext();)
		{
			final Equipment aEq   = ei.next().getValue();

			if (!aEq.isWeapon())
			{
				continue;
			}

			for ( String wield : wieldList )
			{
				if (
					!aEq.hasWield() ||
					!aEq.getWield().equalsIgnoreCase(wield))
				{
					break;
				}

				String wpName = aEq.profKey(aPC);
				addtoToAvailableAndMap(unparsed, availableList, wpName);
			}
		}
	}

	/**
	 * Add a single weapon name to the available list and also to the data structure
	 * that ties weapon name to weapon proficiency (will be used to apply the correct
	 * proficiency later)
	 *
	 * @param  unparsed			The unparsed string of choices.
	 * @param  availableList	The list weapon proficiencies are added to.
	 * @param  wpName			Name of the weapon proficiency.
	 */
	private void addtoToAvailableAndMap(
		final String unparsed,
		final List<String>   availableList,
		String       wpKey)
	{
		final WeaponProf wp = Globals.getWeaponProfKeyed(wpKey);

		if ((wp != null) && !availableList.contains(wp.getKeyName()))
		{
			final String bString = wp.getKeyName();
			availableList.add(bString);

			final StringTokenizer cTok = new StringTokenizer(unparsed, "[]");

			if (cTok.hasMoreTokens())
			{
				cTok.nextToken(); // Read and throw away a token

				while (cTok.hasMoreTokens())
				{
					weaponToProfMap.addCategorisable(new weaponToProf(bString, cTok.nextToken()));
				}
			}
		}
	}

	/**
	 * remove any excluded choices from the available list
	 *
	 * @param  parsed			Parsed string, the first part id ignored, the rest is types.
	 * @param  availableList	The list weapon proficiencies are added to.
	 * @param  aPC				The Player Character.
	 */
	private void removeExcludedWeaponProfTypeChoices(
		String                parsed,
		final List<String>            availableList,
		final PlayerCharacter aPC)
	{
		final StringTokenizer bTok     = new StringTokenizer(parsed.substring(6), ".");
		final List<String>            typeList = new ArrayList<String>();

		while (bTok.hasMoreTokens())
		{
			typeList.add(bTok.nextToken());
		}

		for (Iterator<Map.Entry<String, Equipment>> ei = EquipmentList.getEquipmentListIterator(); ei.hasNext();)
		{
			final Equipment aEq   = ei.next().getValue();

			if (!aEq.isWeapon())
			{
				continue;
			}

			for ( String ti : typeList )
			{
				if (!aEq.isType(ti))
				{
					break;
				}

				final WeaponProf wp = Globals.getWeaponProfKeyed(aEq.profKey(aPC));

				if ((wp != null) && availableList.contains(wp.getKeyName()))
				{
					final String bString = wp.getKeyName();
					availableList.remove(bString);
				}
			}
		}
	}

	/**
	 * Apply the selected weapon profs
	 *
	 * @param  aPC					The Player Character.
	 * @param  selected				List of the choices made
	 */
	public void applyChoices(
		final PlayerCharacter  aPC,
		final List<String>             selected)
	{
		pobject.clearSelectedWeaponProfBonus();
		aPC.setAutomaticFeatsStable(false);

		Iterator<String> it = selected.iterator();

		while (it.hasNext() && !weaponToProfMap.isEmpty())
		{
			final String aChoice = it.next();
			Iterator<weaponToProf>     innerIt = weaponToProfMap.getKeyIterator(aChoice);

			while (innerIt.hasNext())
			{
				final String featOrProf = innerIt.next().toString();

				if (featOrProf == null)
				{
					continue;
				}

				if (featOrProf.startsWith("WEAPONPROF"))
				{
					pobject.addSelectedWeaponProfBonus(aChoice);
				}

				//
				// TODO: This needs to be added to the automatic feat list
				//
				else if (featOrProf.startsWith("FEAT=") ||
					featOrProf.startsWith("FEAT."))
				{
					if (pobject instanceof Domain)
					{
						pobject.clearAssociated();

						String  key       = featOrProf.substring(5);
						Ability anAbility = Globals.getAbilityKeyed("FEAT", key);

						// anAbility = (Ability) anAbility.clone();

						if (anAbility != null)
						{
							pobject.addAssociated(
								"FEAT?" + anAbility.getKeyName() + "(" + aChoice + ")");
						}
					}
					else
					{
						final String aName     = featOrProf.substring(5);
						Ability      anAbility = aPC.getFeatNamed(aName);

						if (anAbility == null)
						{
							anAbility = Globals.getAbilityKeyed("FEAT", aName);
							anAbility = (Ability) anAbility.clone();

							if (anAbility != null)
							{
								aPC.addFeat(anAbility, null);
							}
						}

						if ((anAbility != null) &&
							!anAbility.containsAssociated(aChoice))
						{
							anAbility.addAssociated(aChoice);
						}
					}
				}
			}
		}

		// make sure the list is built
		aPC.getWeaponProfList();
	}

	/**
	 * what type of chooser does this handle
	 *
	 * @return type of chooser
	 */
	public String typeHandled() {
		return chooserHandled;
	}
}
