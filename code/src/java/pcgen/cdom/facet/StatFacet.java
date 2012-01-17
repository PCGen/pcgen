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

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PCStat;
import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.BonusUtilities;

/**
 * StatFacet is a Facet that tracks the PCStat that have been granted to a
 * Player Character.
 */
public class StatFacet extends AbstractListFacet<PCStat>
{
	private BonusCheckingFacet bonusCheckingFacet;
	private PrerequisiteFacet prerequisiteFacet;

	public Map<BonusObj, PCStat> getBonusListOfType(CharID id,
			final String aType, final String aName)
	{
		final Map<BonusObj, PCStat> aList = new IdentityHashMap<BonusObj, PCStat>();

		for (PCStat stat : getSet(id))
		{
			List<BonusObj> bonuses = BonusUtilities.getBonusFromList(stat
					.getSafeListFor(ListKey.BONUS), aType, aName);
			for (BonusObj bonus : bonuses)
			{
				aList.put(bonus, stat);
			}
		}

		return aList;
	}

	public double getStatBonusTo(CharID id, String type, String name)
	{
		final Map<BonusObj, PCStat> map = getBonusListOfType(id, type
				.toUpperCase(), name.toUpperCase());
		for (Iterator<Map.Entry<BonusObj, PCStat>> it = map.entrySet()
				.iterator(); it.hasNext();)
		{
			Entry<BonusObj, PCStat> me = it.next();
			BonusObj bo = me.getKey();
			if (!prerequisiteFacet.qualifies(id, bo, me.getValue()))
			{
				it.remove();
			}
		}
		return bonusCheckingFacet.calcBonus(id, map);
	}

	public void setBonusCheckingFacet(BonusCheckingFacet bonusCheckingFacet)
	{
		this.bonusCheckingFacet = bonusCheckingFacet;
	}

	public void setPrerequisiteFacet(PrerequisiteFacet prerequisiteFacet)
	{
		this.prerequisiteFacet = prerequisiteFacet;
	}

}
