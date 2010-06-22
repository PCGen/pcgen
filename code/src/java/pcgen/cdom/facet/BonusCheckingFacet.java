/*
 * Copyright (c) Thomas Parker, 2009.
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
package pcgen.cdom.facet;

import java.util.Collection;
import java.util.Map;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.core.PlayerCharacter;
import pcgen.core.bonus.BonusObj;

/**
 * This is a transition class, designed to allow things to be taken out of
 * PlayerCharacter while a transition is made to a sytem where bonuses are
 * captured when items are entered into the PlayerCharacter and can be
 * subscribed to by facets... and is thus different than today's (5.x) core.
 */
public class BonusCheckingFacet
{
	private final Class<?> thisClass = getClass();

	public void associatePlayerCharacter(CharID id, PlayerCharacter pc)
	{
		FacetCache.set(id, thisClass, pc);
	}

	public double getBonus(CharID id, String bonusType, String bonusName)
	{
		PlayerCharacter pc = (PlayerCharacter) FacetCache.get(id, thisClass);
		return pc.getTotalBonusTo(bonusType, bonusName);
	}

	public double calcBonus(CharID id, Map<BonusObj, ? extends CDOMObject> map)
	{
		double iBonus = 0;

		for (Map.Entry<BonusObj, ? extends CDOMObject> me : map.entrySet())
		{
			BonusObj bonus = me.getKey();
			CDOMObject source = me.getValue();
			iBonus += getBonusValue(id, bonus, source.getQualifiedKey())
					.doubleValue();
		}

		return iBonus;
	}

	private Number getBonusValue(CharID id, BonusObj bonus, String qualifiedKey)
	{
		PlayerCharacter pc = (PlayerCharacter) FacetCache.get(id, thisClass);
		return bonus.resolve(pc, qualifiedKey);
	}

	public double getAllBonusValues(CharID id, Collection<BonusObj> bonuses,
			String qualifiedKey)
	{
		PlayerCharacter pc = (PlayerCharacter) FacetCache.get(id, thisClass);
		double value = 0;
		for (BonusObj bo : bonuses)
		{
			value += bo.resolve(pc, qualifiedKey).doubleValue();
		}
		return value;
	}

}
