/*
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
 */
package pcgen.core.kit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.WeaponProf;

/**
 * {@code KitFeat}.
 */
public final class KitProf extends BaseKit
{
	private Integer choiceCount;

	private final List<CDOMSingleRef<WeaponProf>> profList = new ArrayList<>();
	private Boolean racialProf;

	// These members store the state of an instance of this class.  They are
	// not cloned.
	private CDOMObject thePObject = null;
	private List<WeaponProf> weaponProfs = null;

	/**
	 * True if it is a racial proficiency
	 * @return True if it is a racial proficiency
	 */
	public boolean isRacial()
	{
		return racialProf != null && racialProf;
	}

	/**
	 * Set racial proficiency flag
	 * @param argRacial
	 */
	public void setRacialProf(Boolean argRacial)
	{
		racialProf = argRacial;
	}

	@Override
	public String toString()
	{
		final int maxSize = profList.size();
		final StringBuilder info = new StringBuilder(maxSize * 10);

		if ((getSafeCount() != 1) || (maxSize != 1))
		{
			info.append(getSafeCount()).append(" of ");
		}

		info.append(StringUtil.join(profList, ", "));

		return info.toString();
	}

	@Override
	public boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings)
	{
		thePObject = null;
		weaponProfs = null;

		PersistentTransitionChoice<WeaponProf> wpPTC = null;
		if (isRacial())
		{
			final Race pcRace = aPC.getRace();

			if (pcRace == null)
			{
				warnings.add("PROF: PC has no race");

				return false;
			}
			if (!aPC.hasBonusWeaponProfs(pcRace))
			{
				warnings.add("PROF: Race has already selected bonus weapon proficiency");

				return false;
			}
			thePObject = pcRace;
			wpPTC = getPTC(pcRace);
			if (wpPTC == null)
			{
				warnings.add("PROF: PC race has no WEAPONBONUS");

				return false;
			}
		}
		else
		{
			Collection<PCClass> pcClasses = aPC.getClassSet();
			if (pcClasses == null || pcClasses.isEmpty())
			{
				warnings.add("PROF: No owning class found.");

				return false;
			}

			// Search for a class that has bonusWeaponProfs.
			PCClass pcClass = null;
            for (PCClass aClass : pcClasses)
            {
                pcClass = aClass;
                wpPTC = getPTC(pcClass);
                if (wpPTC != null)
                {
                    break;
                }
            }
			if (wpPTC == null)
			{
				warnings.add("PROF: PC classes have no WEAPONBONUS");

				return false;
			}
			thePObject = pcClass;
			if (!aPC.hasBonusWeaponProfs(pcClass))
			{
				warnings.add("PROF: Class has already selected bonus weapon proficiency");

				return false;
			}
		}

		final List<WeaponProf> aProfList = new ArrayList<>();

		Collection<?> choices = wpPTC.getChoices().getSet(aPC);
		for (CDOMSingleRef<WeaponProf> profKey : profList)
		{
			WeaponProf wp = profKey.get();
			if (choices.contains(wp))
			{
				wpPTC.act(Collections.singleton(wp), thePObject, aPC);
			}
			else
			{
				warnings.add("PROF: Weapon proficiency \"" + wp.getKeyName() + "\" is not in list of choices");
			}
		}

		int numberOfChoices = getSafeCount();

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

		List<WeaponProf> xs;

		//
		// Force user to make enough selections
		//
		while (true)
		{
			xs = Globals.getChoiceFromList("Choose Proficiencies", aProfList, new ArrayList<>(), numberOfChoices,
				aPC);

			if (!xs.isEmpty())
			{
				break;
			}
		}

		//
		// Add to list of things to add to the character
		//
		for (WeaponProf prof : xs)
		{
			if (weaponProfs == null)
			{
				weaponProfs = new ArrayList<>();
			}
			weaponProfs.add(prof);
		}
		return false;
	}

	private PersistentTransitionChoice<WeaponProf> getPTC(CDOMObject cdo)
	{
		List<PersistentTransitionChoice<?>> adds = cdo.getListFor(ListKey.ADD);
		for (PersistentTransitionChoice<?> ptc : adds)
		{
			if (ptc.getChoiceClass().equals(WeaponProf.class))
			{
				return (PersistentTransitionChoice<WeaponProf>) ptc;
			}
		}
		return null;
	}

	@Override
	public void apply(PlayerCharacter aPC)
	{
		PersistentTransitionChoice<WeaponProf> wpPTC = getPTC(thePObject);
		Collection<?> choices = wpPTC.getChoices().getSet(aPC);
		for (CDOMSingleRef<WeaponProf> profKey : profList)
		{
			WeaponProf wp = profKey.get();
			if (choices.contains(wp))
			{
				wpPTC.act(Collections.singleton(wp), thePObject, aPC);
			}
		}
	}

	@Override
	public String getObjectName()
	{
		return "Proficiencies";
	}

	public void setCount(Integer quan)
	{
		choiceCount = quan;
	}

	public Integer getCount()
	{
		return choiceCount;
	}

	public int getSafeCount()
	{
		return choiceCount == null ? 1 : choiceCount;
	}

	public void addProficiency(CDOMSingleRef<WeaponProf> ref)
	{
		profList.add(ref);
	}

	public Collection<CDOMSingleRef<WeaponProf>> getProficiencies()
	{
		return profList;
	}

	public Boolean getRacialProf()
	{
		return racialProf;
	}

}
