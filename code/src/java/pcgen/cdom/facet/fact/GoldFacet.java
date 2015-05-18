/*
 * Copyright (c) Thomas Parker, 2009-12.
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
package pcgen.cdom.facet.fact;

import java.math.BigDecimal;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractItemFacet;

/**
 * GoldFacet is a Facet to track Gold in a simple, single monetary unit game
 * system.
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class GoldFacet extends AbstractItemFacet<CharID, BigDecimal>
{

	/**
	 * Adjusts the gold of the Player Character represented by the given CharID
	 * by the given amount. This adjustment will work if the adjustment is
	 * positive or negative.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            gold will be adjusted
	 * @param delta
	 *            The amount of gold used to adjust the gold possessed by the
	 *            Player Character represented by the given CharID
	 */
	public void adjustGold(CharID id, double delta)
	{
		BigDecimal old = get(id);
		if (old == null)
		{
			old = BigDecimal.ZERO;
		}
		// I don't really like this hack, but setScale just won't work right...
		BigDecimal newGold = new BigDecimal(old.doubleValue() + delta).divide(
				BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_EVEN);
		/*
		 * TODO What is this delta produces a negative value, but allowDebt is
		 * false?
		 */
		set(id, newGold);
	}

}
