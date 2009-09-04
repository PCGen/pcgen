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

import java.math.BigDecimal;

import pcgen.cdom.enumeration.CharID;
import pcgen.core.SettingsHandler;

public class MoneyFacet
{

	private static final BigDecimal ZERO = new BigDecimal(0);

	private final Class<?> thisClass = getClass();

	private MoneyCacheInfo getConstructingInfo(CharID id)
	{
		MoneyCacheInfo rci = getInfo(id);
		if (rci == null)
		{
			rci = new MoneyCacheInfo();
			FacetCache.set(id, thisClass, rci);
		}
		return rci;
	}

	private MoneyCacheInfo getInfo(CharID id)
	{
		return (MoneyCacheInfo) FacetCache.get(id, thisClass);
	}

	public void setGold(CharID id, BigDecimal gold)
	{
		getConstructingInfo(id).gold = gold;
	}

	public void setAllowDebt(CharID id, boolean allowDebt)
	{
		getConstructingInfo(id).allowDebt = allowDebt;
	}

	public void setIgnoreCost(CharID id, boolean ignoreCost)
	{
		getConstructingInfo(id).ignoreCost = ignoreCost;
	}

	public BigDecimal getGold(CharID id)
	{
		MoneyCacheInfo rci = getInfo(id);
		return rci == null ? ZERO : rci.gold;
	}

	public boolean isAllowDebt(CharID id)
	{
		MoneyCacheInfo rci = getInfo(id);
		return rci == null ? SettingsHandler.getGearTab_AllowDebt()
				: rci.allowDebt;
	}

	public boolean isIgnoreCost(CharID id)
	{
		MoneyCacheInfo rci = getInfo(id);
		return rci == null ? SettingsHandler.getGearTab_IgnoreCost()
				: rci.ignoreCost;
	}

	private class MoneyCacheInfo
	{
		public BigDecimal gold = ZERO;
		public boolean allowDebt;
		public boolean ignoreCost;
	}

	public void copyContents(CharID source, CharID destination)
	{
		MoneyCacheInfo sourceRCI = getInfo(source);
		if (sourceRCI != null)
		{
			MoneyCacheInfo destRCI = getConstructingInfo(destination);
			// Safe since BigDecimal is immutable
			destRCI.gold = sourceRCI.gold;
			destRCI.allowDebt = sourceRCI.allowDebt;
			destRCI.ignoreCost = sourceRCI.ignoreCost;
		}
	}

	public void adjustGold(CharID id, double delta)
	{
		BigDecimal old = getGold(id);
		// I don't really like this hack, but setScale just won't work right...
		BigDecimal newGold = new BigDecimal(old.doubleValue() + delta).divide(
				BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_EVEN);
		getConstructingInfo(id).gold = newGold;
	}

}
