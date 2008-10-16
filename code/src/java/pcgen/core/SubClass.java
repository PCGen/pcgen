/*
 * SubClass.java
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on November 19, 2002, 10:29 PM
 *
 * $Id$
 */
package pcgen.core;

import java.util.List;

import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.persistence.lst.utils.DeferredLine;

/**
 * <code>SubClass</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class SubClass extends PCClass implements CategorizedCDOMObject<SubClass>
{
	/**
	 * Get the choice
	 * @return choice
	 */
	public String getChoice()
	{
		SpellProhibitor sp = get(ObjectKey.CHOICE);
		if (sp == null)
		{
			return "";
		}

		return sp.getValueList().get(0);
	}

	/**
	 * Returns the prohibitCost. If the prohibited cost has not already
	 * been set, then the sub-classes cost will be returned. This preserves
	 * the previous behaviour where the prohibited cost and cost were the same.
	 *
	 * @return int The prohibit cost for the sub-class.
	 */
	public int getProhibitCost()
	{
		Integer prohib = get(IntegerKey.PROHIBIT_COST);
		if (prohib != null)
		{
			return prohib;
		}
		return getSafe(IntegerKey.COST);
	}

	/**
	 * Apply the level mods to a class
	 * @param aClass
	 */
	public void applyLevelArrayModsTo(final PCClass aClass)
	{
		List<DeferredLine> levelArray = getListFor(ListKey.SUB_CLASS_LEVEL);
		if (levelArray == null)
		{
			return;
		}

		for ( DeferredLine line : levelArray )
		{
			aClass.performReallyBadHackForOldTokens(line);
		}
	}

	public String getSupplementalDisplayInfo() {
		boolean added = false;
		StringBuffer displayInfo = new StringBuffer();
		if (getSafe(IntegerKey.KNOWN_SPELLS_FROM_SPECIALTY) != 0) {
			displayInfo.append("SPECIALTY SPELLS:").append(
					getSafe(IntegerKey.KNOWN_SPELLS_FROM_SPECIALTY));
			added = true;
		}

		if (getSpellBaseStat() != null) {
			if (added) {
				displayInfo.append(" ");
			}
			displayInfo.append("SPELL BASE STAT:").append(getSpellBaseStat());
			added = true;
		}

		if (!added) {
			displayInfo.append(' ');
		}
		return displayInfo.toString();
	}

	public Category<SubClass> getCDOMCategory()
	{
		return get(ObjectKey.SUBCLASS_CATEGORY);
	}

	public void setCDOMCategory(Category<SubClass> cat)
	{
		put(ObjectKey.SUBCLASS_CATEGORY, cat);
	}

	@Override
	public String getFullKey()
	{
		return getCDOMCategory() + "." + super.getFullKey();
	}
	
	
}
