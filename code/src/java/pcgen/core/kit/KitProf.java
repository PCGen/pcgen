/*
 * KitProf.java
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on September 28, 2002, 11:50 PM
 *
 * $Id$
 */
package pcgen.core.kit;

import pcgen.core.*;
import pcgen.core.utils.CoreUtility;
import pcgen.core.utils.ListKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <code>KitFeat</code>.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
public final class KitProf extends BaseKit implements Serializable, Cloneable
{
	// Only change the UID when the serialized form of the class has also changed
	private static final long serialVersionUID = 1;

	private final List<String> profList = new ArrayList<String>();
	private boolean racialProf = false;

	// These members store the state of an instance of this class.  They are
	// not cloned.
	private transient PObject thePObject = null;
	private transient List<WeaponProf> weaponProfs = null;

	/**
	 * Constructor
	 * @param argProfList
	 */
	public KitProf(final String argProfList)
	{
		final StringTokenizer aTok = new StringTokenizer(argProfList, "|");

		while (aTok.hasMoreTokens())
		{
			profList.add(aTok.nextToken());
		}
	}

	/**
	 * Get the proficiency list for this kit
	 * @return the proficiency list for this kit
	 */
	public List<String> getProfList()
	{
		return profList;
	}

	/**
	 * True if it is a racial proficiency
	 * @return True if it is a racial proficiency
	 */
	public boolean isRacial()
	{
		return racialProf;
	}

	/**
	 * Set racial proficiency flag
	 * @param argRacial
	 */
	public void setRacialProf(final boolean argRacial)
	{
		racialProf = argRacial;
	}

	@Override
	public String toString()
	{
		final int maxSize = profList.size();
		final StringBuffer info = new StringBuffer(maxSize * 10);

		if ((choiceCount != 1) || (maxSize != 1))
		{
			info.append(choiceCount).append(" of ");
		}

		info.append(CoreUtility.join(profList, ", "));

		return info.toString();
	}

	public boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings)
	{
		thePObject = null;
		weaponProfs = null;

		ListKey<String> weaponProfKey = ListKey.SELECTED_WEAPON_PROF_BONUS;

		List<String> bonusList = null;
		if (isRacial())
		{
			final Race pcRace = aPC.getRace();

			if (pcRace == null)
			{
				warnings.add("PROF: PC has no race");

				return false;
			}
			if (pcRace.getSafeSizeOfListFor(weaponProfKey) != 0)
			{
				warnings.add(
					"PROF: Race has already selected bonus weapon proficiency");

				return false;
			}
			thePObject = pcRace;
			bonusList = pcRace.getWeaponProfBonus();
		}
		else
		{
			List<PCClass> pcClasses = aPC.getClassList();
			if (pcClasses == null || pcClasses.size() == 0)
			{
				warnings.add("PROF: No owning class found.");

				return false;
			}

			// Search for a class that has bonusWeaponProfs.
			PCClass pcClass = null;
			for (Iterator<PCClass> i = pcClasses.iterator(); i.hasNext(); )
			{
				pcClass = i.next();
				bonusList = pcClass.getWeaponProfBonus();
				if (bonusList != null && bonusList.size() > 0)
				{
					break;
				}
			}
			thePObject = pcClass;
			if (pcClass.getSafeSizeOfListFor(weaponProfKey) != 0)
			{
				warnings.add(
					"PROF: Class has already selected bonus weapon proficiency");

				return false;
			}
		}
		if ((bonusList == null) || (bonusList.size() == 0))
		{
			warnings.add("PROF: No optional weapon proficiencies");

			return false;
		}

		final List<String> aProfList = new ArrayList<String>();

		for ( String profKey : getProfList() )
		{
			if (!bonusList.contains(profKey))
			{
				warnings.add(
					"PROF: Weapon proficiency \"" + profKey +
					"\" is not in list of choices");
				continue;
			}

			final WeaponProf aProf = Globals.getWeaponProfKeyed(profKey);

			if (aProf != null)
			{
				aProfList.add(profKey);
			}
			else
			{
				warnings.add(
					"PROF: Non-existant proficiency \"" + profKey + "\"");
			}
		}

		int numberOfChoices = getChoiceCount();

		//
		// Can't choose more entries than there are...
		//
		if (numberOfChoices > aProfList.size())
		{
			numberOfChoices = aProfList.size();
		}

		if (numberOfChoices == 0)
		{
			return false;
		}

		List<String> xs;

		if (numberOfChoices == aProfList.size())
		{
			xs = aProfList;
		}
		else
		{
			//
			// Force user to make enough selections
			//
			while (true)
			{
				xs = Globals.getChoiceFromList(
						"Choose Proficiencies",
						aProfList,
						new ArrayList<String>(),
						numberOfChoices);

				if (xs.size() != 0)
				{
					break;
				}
			}
		}

		//
		// Add to list of things to add to the character
		//
		for ( String profKey : xs )
		{
			final WeaponProf aProf = Globals.getWeaponProfKeyed(profKey);

			if (aProf != null)
			{
				if (weaponProfs == null)
				{
					weaponProfs = new ArrayList<WeaponProf>();
				}
				weaponProfs.add(aProf);
			}
		}
		return false;
	}

	public void apply(PlayerCharacter aPC)
	{
		for ( WeaponProf prof : weaponProfs )
		{
			thePObject.addSelectedWeaponProfBonus(prof.getKeyName());
		}
	}

	@Override
	public KitProf clone()
	{
		KitProf aClone = (KitProf)super.clone();
		aClone.profList.addAll(profList);
		aClone.racialProf = racialProf;

		return aClone;
	}

	public String getObjectName()
	{
		return "Proficiencies";
	}
}
