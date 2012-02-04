/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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

import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.bonus.BonusObj;

public class AppliedBonusFacet extends AbstractListFacet<BonusObj> implements
		DataFacetChangeListener<CDOMObject>
{
	private AddedBonusFacet addedBonusFacet;

	private PrerequisiteFacet prerequisiteFacet;

	private RaceFacet raceFacet;

	@Override
	public void dataAdded(DataFacetChangeEvent<CDOMObject> dfce)
	{
		CharID id = dfce.getCharID();
		CDOMObject cdo = dfce.getCDOMObject();
		processAdd(id, cdo, cdo.getSafeListFor(ListKey.BONUS));
		processAdd(id, cdo, addedBonusFacet.getSet(id, cdo));
	}

	private void processAdd(CharID id, CDOMObject cdo,
			List<? extends BonusObj> bonusList)
	{
		for (BonusObj bonus : bonusList)
		{
			if (prerequisiteFacet.qualifies(id, bonus, cdo))
			{
				add(id, bonus);
			}
			else
			{
				// TODO Is this necessary? Shouldn't be present anyway...
				remove(id, bonus);
			}
		}
	}

	@Override
	public void dataRemoved(DataFacetChangeEvent<CDOMObject> dfce)
	{
		CharID id = dfce.getCharID();
		CDOMObject cdo = dfce.getCDOMObject();
		processRemove(id, cdo, cdo.getSafeListFor(ListKey.BONUS));
		processRemove(id, cdo, addedBonusFacet.getSet(id, cdo));
	}

	private void processRemove(CharID id, CDOMObject cdo,
			List<? extends BonusObj> bonusList)
	{
		for (BonusObj bonus : bonusList)
		{
			remove(id, bonus);
		}
	}

	public void setAddedBonusFacet(AddedBonusFacet addedBonusFacet)
	{
		this.addedBonusFacet = addedBonusFacet;
	}

	public void setPrerequisiteFacet(PrerequisiteFacet prerequisiteFacet)
	{
		this.prerequisiteFacet = prerequisiteFacet;
	}

	public void setRaceFacet(RaceFacet raceFacet)
	{
		this.raceFacet = raceFacet;
	}

	public void init()
	{
		raceFacet.addDataFacetChangeListener(this);
	}
}
