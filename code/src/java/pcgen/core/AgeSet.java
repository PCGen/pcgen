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
package pcgen.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pcgen.cdom.base.BonusContainer;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.TransitionChoice;
import pcgen.core.bonus.BonusObj;

/**
 * This represents the AGESET entries in the BioSettings Game Mode file
 */
public class AgeSet implements BonusContainer
{

	private List<BonusObj> bonuses = null;
	private List<TransitionChoice<Kit>> kits = null;
	private final String name;
	private final int index;

	public AgeSet(String ageName, int currentAgeSetIndex)
	{
		name = ageName;
		index = currentAgeSetIndex;
	}

	public void addBonuses(List<BonusObj> list)
	{
		if (bonuses == null)
		{
			bonuses = new ArrayList<>(list);
		}
	}

	public boolean hasBonuses()
	{
		return bonuses != null && !bonuses.isEmpty();
	}

	public int getIndex()
	{
		return index;
	}

	public String getName()
	{
		return name;
	}

	public List<BonusObj> getBonuses()
	{
		if (bonuses == null)
		{
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(bonuses);
	}

	public void addKits(List<TransitionChoice<Kit>> list)
	{
		if (kits == null)
		{
			kits = new ArrayList<>(list);
		}
	}

	public List<TransitionChoice<Kit>> getKits()
	{
		if (kits == null)
		{
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(kits);
	}

	public String getLSTformat()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(index).append('|').append(name);
		if (bonuses != null)
		{
			for (BonusObj bo : bonuses)
			{
				sb.append('\t').append(bo.getLSTformat());
			}
		}
		if (kits != null)
		{
			for (TransitionChoice<Kit> tc : kits)
			{
				sb.append('\t').append(tc.getCount()).append(Constants.PIPE);
				sb.append(tc.getChoices().getLSTformat().replaceAll(Constants.COMMA, Constants.PIPE));
			}
		}
		return sb.toString();
	}

	@Override
	public void activateBonuses(PlayerCharacter pc)
	{
		if (bonuses != null)
		{
			for (BonusObj bo : bonuses)
			{
				pc.setApplied(bo, bo.qualifies(pc, null));
			}
		}
	}

	@Override
	public List<BonusObj> getActiveBonuses(PlayerCharacter pc)
	{
		if (bonuses == null)
		{
			return Collections.emptyList();
		}
		List<BonusObj> aList = new ArrayList<>();

		for (BonusObj bo : bonuses)
		{
			if (pc.isApplied(bo))
			{
				aList.add(bo);
			}
		}

		return aList;
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (o instanceof AgeSet)
		{
			AgeSet other = (AgeSet) o;
			if (bonuses == null)
			{
				if (other.bonuses != null)
				{
					return false;
				}
			}
			else
			{
				if (!bonuses.equals(other.bonuses))
				{
					return false;
				}
			}
			if (kits == null)
			{
				if (other.kits != null)
				{
					return false;
				}
			}
			else
			{
				if (!kits.equals(other.kits))
				{
					return false;
				}
			}
			return (index == other.index) && name.equals(other.name);
		}
		return false;
	}
}
