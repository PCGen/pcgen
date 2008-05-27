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

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.*;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
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
	private static final Class<WeaponProf> WEAPONPROF_CLASS = WeaponProf.class;

	// Only change the UID when the serialized form of the class has also changed
	private static final long serialVersionUID = 1;

	private final List<CDOMSingleRef<WeaponProf>> profList = new ArrayList<CDOMSingleRef<WeaponProf>>();
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
		StringTokenizer tok = new StringTokenizer(argProfList, Constants.PIPE);
		LoadContext context = Globals.getContext();
		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			CDOMSingleRef<WeaponProf> ref = context.ref.getCDOMReference(
					WEAPONPROF_CLASS, tokText);
			if (ref == null)
			{
				Logging
						.errorPrint("  Error was encountered while parsing KitProf.  "
								+ tokText + " is not a valid WeaponProf");
				continue;
			}
			profList.add(ref);
		}
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

		info.append(StringUtil.join(profList, ", "));

		return info.toString();
	}

	public boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings)
	{
		thePObject = null;
		weaponProfs = null;

		ListKey<String> weaponProfKey = ListKey.SELECTED_WEAPON_PROF_BONUS;

		Collection<CDOMReference<WeaponProf>> wpBonus = null;
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
			wpBonus = pcRace.getListMods(WeaponProf.STARTING_LIST);
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
				wpBonus = pcClass.getListMods(WeaponProf.STARTING_LIST);
				if (wpBonus != null && wpBonus.size() > 0)
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
		if ((wpBonus == null) || (wpBonus.size() == 0))
		{
			warnings.add("PROF: No optional weapon proficiencies");

			return false;
		}

		final List<String> aProfList = new ArrayList<String>();

		for ( CDOMSingleRef<WeaponProf> profKey : profList )
		{
			WeaponProf wp = profKey.resolvesTo();
			boolean found = false;
			for (CDOMReference<WeaponProf> ref : wpBonus)
			{
				if (ref.contains(wp))
				{
					found = true;
					aProfList.add(wp.getKeyName());
					break;
				}
			}
			if (!found)
			{
				warnings.add("PROF: Weapon proficiency \"" + wp.getKeyName()
						+ "\" is not in list of choices");
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
