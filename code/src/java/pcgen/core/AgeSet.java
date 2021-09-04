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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import pcgen.cdom.base.BonusContainer;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.base.TransitionChoice;
import pcgen.core.bonus.BonusObj;

/**
 * This represents the AGESET entries in the BioSettings Game Mode file
 */
public class AgeSet implements BonusContainer, Loadable
{

	/**
	 * The (lazily instantiated) list of bonuses in this AgeSet.
	 */
	private List<BonusObj> bonuses = null;
	
	/**
	 * The (lazily instantiated) list of kits in this AgeSet.
	 */
	private List<TransitionChoice<Kit>> kits = null;
	
	/**
	 * The name of this AgeSet.
	 */
	private String name;
	
	/**
	 * The AgeSet Index of this AgeSet.
	 */
	private int index;
	
	/**
	 * The source URI of this AgeSet.
	 */
	private URI sourceURI;

	/**
	 * Returns true if this AgeSet has BONUS objects.
	 * 
	 * @return true if this AgeSet has BONUS objects; false otherwise
	 */
	public boolean hasBonuses()
	{
		return bonuses != null && !bonuses.isEmpty();
	}

	/**
	 * Returns the AgeSet Index of this AgeSet.
	 * 
	 * @return the AgeSet Index of this AgeSet
	 */
	public int getIndex()
	{
		return index;
	}

	/**
	 * Sets the AgeSet Index on this AgeSet.
	 * 
	 * @param ageSetIndex
	 *            The AgeSet Index to be placed on this AgeSet.
	 */
	public void setAgeIndex(int ageSetIndex)
	{
		index = ageSetIndex;
	}

	/**
	 * Gets the List of Bonus objects on this AgeSet. Will not return null (returns empty
	 * list for no Bonuses).
	 * 
	 * @return The List of Bonus objects on this AgeSet
	 */
	public List<BonusObj> getBonuses()
	{
		if (bonuses == null)
		{
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(bonuses);
	}

	/**
	 * Adds the given Bonus to the list of Bonus objects on this AgeSet.
	 * 
	 * @param bon
	 *            The Bonus to be added to the list of Bonus objects on this AgeSet
	 */
	public void addBonus(BonusObj bon)
	{
		if (bonuses == null)
		{
			bonuses = new ArrayList<>();
		}
		bonuses.add(bon);
	}

	/**
	 * Gets the List of Kit objects on this AgeSet. Will not return null (returns empty
	 * list for no Kits).
	 * 
	 * @return The List of Kit objects on this AgeSet
	 */
	public List<TransitionChoice<Kit>> getKits()
	{
		if (kits == null)
		{
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(kits);
	}

	/**
	 * Adds the given Kit to the list of Kit objects on this AgeSet.
	 * 
	 * @param tc
	 *            The Kit to be added to the list of Kit objects on this AgeSet
	 */
	public void addKit(TransitionChoice<Kit> tc)
	{
		if (kits == null)
		{
			kits = new ArrayList<>();
		}
		kits.add(tc);
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

	/*
	 * Begin items implementing BonusContainer interface
	 */
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
	/*
	 * End items implementing BonusContainer interface
	 */

	/*
	 * Begin items implementing the Loadable interface
	 */
	@Override
	public String getKeyName()
	{
		return name;
	}

	@Override
	public String getDisplayName()
	{
		return name;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public URI getSourceURI()
	{
		return sourceURI;
	}

	@Override
	public void setSourceURI(URI source)
	{
		this.sourceURI = source;
	}

	@Override
	public boolean isInternal()
	{
		return false;
	}

	@Override
	public boolean isType(String type)
	{
		return false;
	}
	/*
	 * End items implementing the Loadable interface
	 */

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
		if (o instanceof AgeSet other)
		{
			return (index == other.index) && name.equals(other.name)
				&& Objects.equals(bonuses, other.bonuses)
				&& Objects.equals(kits, other.kits);
		}
		return false;
	}

}
