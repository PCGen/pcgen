/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.choiceset;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.helper.AbilitySelection;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Ability;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.pclevelinfo.PCLevelInfo;

public class AbilityFromClassChoiceSet implements
		PrimitiveChoiceSet<AbilitySelection>
{

	private final CDOMSingleRef<PCClass> cl;

	public AbilityFromClassChoiceSet(CDOMSingleRef<PCClass> pcc)
	{
		cl = pcc;
	}

	public String getLSTformat(boolean useAny)
	{
		return "CLASS." + cl.getLSTformat();
	}

	/**
	 * Returns true if this AbilityFromClassReference is equal to the given
	 * Object. Equality is defined as being another AbilityFromClassReference
	 * object with equal Class represented by the reference, an equal staring
	 * CDOMGroupRef and an equal pattern. This may or may not be a deep .equals,
	 * depending on the behavior of the underlying CDOMGroupRef. You should
	 * check the documentation for the .equals(Object) method of that class to
	 * establish the actual behavior of this method.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof AbilityFromClassChoiceSet)
		{
			AbilityFromClassChoiceSet other = (AbilityFromClassChoiceSet) o;
			return cl.equals(other.cl);
		}
		return false;
	}

	/**
	 * Returns the consistent-with-equals hashCode for this
	 * AbilityFromClassReference
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return cl.hashCode();
	}

	public Class<? super AbilitySelection> getChoiceClass()
	{
		return AbilitySelection.class;
	}

	public Set<AbilitySelection> getSet(PlayerCharacter pc)
	{
		PCClass aClass = cl.resolvesTo().getActiveEquivalent(pc);
		Set<AbilitySelection> set = new HashSet<AbilitySelection>();
		if (aClass != null)
		{
			for (PCLevelInfo element : pc.getLevelInfo())
			{
				if (element.getClassKeyName().equalsIgnoreCase(
						aClass.getKeyName()))
				{
					for (Ability aFeat : (List<Ability>) element.getObjects())
					{
						set.add(new AbilitySelection(aFeat, aFeat
								.getAbilityNature()));
					}
				}
			}
			List<Ability> abilityList = pc.getAssocList(aClass,
					AssociationListKey.ADDED_FEAT);
			if (abilityList != null)
			{
				for (Ability aFeat : abilityList)
				{
					set.add(new AbilitySelection(aFeat, aFeat
							.getAbilityNature()));
				}
			}
			for (int lvl = 0; lvl < aClass.getLevel(); lvl++)
			{
				PCClassLevel pcl = aClass.getClassLevel(lvl);
				abilityList = pc.getAssocList(pcl,
						AssociationListKey.ADDED_FEAT);
				if (abilityList != null)
				{
					for (Ability aFeat : abilityList)
					{
						set.add(new AbilitySelection(aFeat, aFeat
								.getAbilityNature()));
					}
				}
			}
		}
		return null;
	}
}
