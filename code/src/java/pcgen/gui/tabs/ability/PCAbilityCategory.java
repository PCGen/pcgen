/*
 * PCAbilityCategory.java
 * Copyright 2007 (C) James Dempsey
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
 * Created on 11/08/2007
 *
 * $Id$
 */

package pcgen.gui.tabs.ability;

import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.util.BigDecimalHelper;

/**
 * <code>PCAbilityCategory</code> is a placeholder for a character's
 * ability category for a PObjectNode. It's primary purpose is to 
 * provide custom output for displaying in a tree. 
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class PCAbilityCategory
{

	private AbilityCategory category;
	private PlayerCharacter pc;
	
	/**
	 * @param aCategory
	 * @param aPc
	 */
	PCAbilityCategory(AbilityCategory aCategory, PlayerCharacter aPc)
	{
		category = aCategory;
		pc = aPc;
	}
	
	@Override
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		result.append(category.getDisplayName());
		result.append(" (");
		result.append(BigDecimalHelper.trimBigDecimal(
			pc.getAbilityPoolSpent(category)).toString());
		result.append('/');
		result.append(BigDecimalHelper.trimBigDecimal(
			pc.getTotalAbilityPool(category)).toString());
		result.append(')');

		return result.toString();
	}

	/**
	 * @return the category
	 */
	AbilityCategory getCategory()
	{
		return category;
	}

	/**
	 * @return the pc
	 */
	PlayerCharacter getPc()
	{
		return pc;
	}
	
}
