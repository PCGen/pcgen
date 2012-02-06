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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.core.PlayerCharacter;
import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.BonusPair;

/**
 * This is a transition class, designed to allow things to be taken out of
 * PlayerCharacter while a transition is made to a system where bonuses are
 * captured when items are entered into the PlayerCharacter and can be
 * subscribed to by facets... and is thus different than today's (5.x) core.
 */
public class BonusCheckingFacet
{
	private final PlayerCharacterTrackingFacet trackingFacet = FacetLibrary
			.getFacet(PlayerCharacterTrackingFacet.class);
	
	public double getBonus(CharID id, String bonusType, String bonusName)
	{
		PlayerCharacter pc = trackingFacet.getPC(id);
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
		PlayerCharacter pc = trackingFacet.getPC(id);
		return bonus.resolve(pc, qualifiedKey);
	}

	public double getAllBonusValues(CharID id, Collection<BonusObj> bonuses,
			String qualifiedKey)
	{
		PlayerCharacter pc = trackingFacet.getPC(id);
		double value = 0;
		for (BonusObj bo : bonuses)
		{
			value += bo.resolve(pc, qualifiedKey).doubleValue();
		}
		return value;
	}

	public Collection<String> getBonusInfo(CharID id, String bonusName)
	{
		PlayerCharacter pc = trackingFacet.getPC(id);
		List<String> list = new ArrayList<String>();
		for (BonusObj bonus : pc.getActiveBonusList())
		{
			if (bonus.getTypeOfBonus().equals(bonusName))
			{
				list.add(bonus.getBonusInfo());
			}
		}
		return list;
	}

	/**
     * Get back a list of bonus info with %LIST entries replaced with the choices made.
	 * @param id The id of the character
	 * @param bonusName The name of the bonus to be retrieved.
	 * @return The list of bonus info keys
	 */
	public Collection<String> getExpandedBonusInfo(CharID id, String bonusName)
	{
		PlayerCharacter pc = trackingFacet.getPC(id);
		List<String> list = new ArrayList<String>();
		for (BonusObj bonus : pc.getActiveBonusList())
		{
			if (bonus.getTypeOfBonus().equals(bonusName))
			{
				String bonusInfo = bonus.getBonusInfo();
				if (bonusInfo.indexOf("%LIST") >= 0)
				{
					// We have a %LIST that needs to be expanded
					List<BonusPair> bpList = pc.getStringListFromBonus(bonus);
					for (BonusPair bonusPair : bpList)
					{
						String key = bonusPair.bonusKey;
						// Strip off the bonus name and the trailing . 
						if (key.startsWith(bonusName))
						{
							key = key.substring(bonusName.length()+1);
						}
						list.add(key);
					}

				}
				else
				{
					list.add(bonus.getBonusInfo());
				}
			}
		}
		return list;
	}

}
