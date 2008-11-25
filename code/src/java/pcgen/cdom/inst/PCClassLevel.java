/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.inst;

import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.bonus.BonusObj;

/**
 * A PCClassLevel is a CDOMObject that represents items gained in a specific
 * level of a PCClass.
 */
public final class PCClassLevel extends CDOMObject implements Cloneable
{

	/**
	 * Returns the consistent-with-equals hashCode for this PCClassLevel
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		String name = this.getDisplayName();
		return name == null ? 0 : name.hashCode();
	}

	/**
	 * Returns true if this PCClassLevel is equal to the given Object. Equality
	 * is defined as being another PCClassLevel object with equal CDOM
	 * characteristics
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		return o instanceof PCClassLevel
				&& ((PCClassLevel) o).isCDOMEqual(this);
	}

	/**
	 * Returns true if the PCClassLevel is of the given Type; false otherwise.
	 * 
	 * @see pcgen.cdom.base.CDOMObject#isType(java.lang.String)
	 */
	@Override
	public boolean isType(String str)
	{
		return false;
	}

	@Override
	public PCClassLevel clone() throws CloneNotSupportedException
	{
		return (PCClassLevel) super.clone();
	}

	/*
	 * This is hopefully a temporary fix, this is required because PCClass 
	 * expects BONUSES that it owns to have a level prefix (and currently
	 * BONUS processing is delegated to PCClass)
	 */
	@Override
	public String bonusStringPrefix()
	{
		return get(IntegerKey.LEVEL) + "|";
	}

	/*
	 * Assigning ownership to the parent is required so that formula 
	 * items like CL are properly calculated.
	 */
	@Override
	public void ownBonuses() throws CloneNotSupportedException
	{
		List<BonusObj> bonusList = getListFor(ListKey.BONUS);
		Object parent = get(ObjectKey.PARENT);
		if (bonusList != null)
		{
			removeListFor(ListKey.BONUS);
			for (BonusObj orig : bonusList)
			{
				BonusObj bonus = orig.clone();
				addToListFor(ListKey.BONUS, bonus);
				bonus.setCreatorObject(parent);
			}
		}
	}

}
