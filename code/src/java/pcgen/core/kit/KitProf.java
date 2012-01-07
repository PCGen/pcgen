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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.BasicChooseInformation;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.WeaponProf;
import pcgen.core.chooser.CDOMChoiceManager;

/**
 * <code>KitFeat</code>.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
public final class KitProf extends BaseKit
{
	private Integer choiceCount;

	private final List<CDOMSingleRef<WeaponProf>> profList =
			new ArrayList<CDOMSingleRef<WeaponProf>>();
	private Boolean racialProf;

	// These members store the state of an instance of this class.  They are
	// not cloned.
	private transient CDOMObject thePObject = null;
	private transient List<WeaponProf> weaponProfs = null;

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
		final StringBuffer info = new StringBuffer(maxSize * 10);

		if ((getSafeCount() != 1) || (maxSize != 1))
		{
			info.append(getSafeCount()).append(" of ");
		}

		info.append(StringUtil.join(profList, ", "));

		return info.toString();
	}

	@Override
	public boolean testApply(Kit aKit, PlayerCharacter aPC,
		List<String> warnings)
	{
		thePObject = null;
		weaponProfs = null;

		Collection<CDOMReference<WeaponProf>> wpBonus = null;
		if (isRacial())
		{
			final Race pcRace = aPC.getRace();

			if (pcRace == null)
			{
				warnings.add("PROF: PC has no race");

				return false;
			}
			List<WeaponProf> bonusProfs = aPC.getBonusWeaponProfs(pcRace);
			if (!bonusProfs.isEmpty())
			{
				warnings
					.add("PROF: Race has already selected bonus weapon proficiency");

				return false;
			}
			thePObject = pcRace;
			wpBonus = pcRace.getListMods(WeaponProf.STARTING_LIST);
		}
		else
		{
			Collection<PCClass> pcClasses = aPC.getClassSet();
			if (pcClasses == null || pcClasses.size() == 0)
			{
				warnings.add("PROF: No owning class found.");

				return false;
			}

			// Search for a class that has bonusWeaponProfs.
			PCClass pcClass = null;
			for (Iterator<PCClass> i = pcClasses.iterator(); i.hasNext();)
			{
				pcClass = i.next();
				wpBonus = pcClass.getListMods(WeaponProf.STARTING_LIST);
				if (wpBonus != null && wpBonus.size() > 0)
				{
					break;
				}
			}
			thePObject = pcClass;
			List<WeaponProf> bonusProfs = aPC.getBonusWeaponProfs(pcClass);
			if (!bonusProfs.isEmpty())
			{
				warnings
					.add("PROF: Class has already selected bonus weapon proficiency");

				return false;
			}
		}
		if ((wpBonus == null) || (wpBonus.size() == 0))
		{
			warnings.add("PROF: No optional weapon proficiencies");

			return false;
		}

		final List<WeaponProf> aProfList = new ArrayList<WeaponProf>();

		ChooseInformation<WeaponProf> tc = new BasicChooseInformation<WeaponProf>(
				"WEAPONBONUS", new ReferenceChoiceSet<WeaponProf>(wpBonus));
		tc.setChoiceActor(WeaponProf.STARTING_ACTOR);
		CDOMChoiceManager<WeaponProf> mgr = new CDOMChoiceManager<WeaponProf>(
				thePObject, tc, 1, 1);

		for (CDOMSingleRef<WeaponProf> profKey : profList)
		{
			WeaponProf wp = profKey.resolvesTo();
			boolean found = false;
			if (mgr.conditionallyApply(aPC, wp))
			{
				found = true;
				aProfList.add(wp);
				break;
			}
			if (!found)
			{
				warnings.add("PROF: Weapon proficiency \"" + wp.getKeyName()
					+ "\" is not in list of choices");
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
				xs =
						Globals
							.getChoiceFromList("Choose Proficiencies",
								aProfList, new ArrayList<WeaponProf>(),
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
		for (WeaponProf prof : xs)
		{
			if (weaponProfs == null)
			{
				weaponProfs = new ArrayList<WeaponProf>();
			}
			weaponProfs.add(prof);
		}
		return false;
	}

	@Override
	public void apply(PlayerCharacter aPC)
	{
		Collection<CDOMReference<WeaponProf>> wpBonus = thePObject
				.getListMods(WeaponProf.STARTING_LIST);
		ChooseInformation<WeaponProf> tc = new BasicChooseInformation<WeaponProf>(
				"WEAPONBONUS", new ReferenceChoiceSet<WeaponProf>(wpBonus));
		tc.setChoiceActor(WeaponProf.STARTING_ACTOR);
		for (WeaponProf prof : weaponProfs)
		{
			CDOMChoiceManager<WeaponProf> mgr = new CDOMChoiceManager<WeaponProf>(
					thePObject, tc, 1, 1);
			mgr.conditionallyApply(aPC, prof);
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
