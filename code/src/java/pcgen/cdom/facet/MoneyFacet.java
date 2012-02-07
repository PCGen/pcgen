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

/**
 * MoneyFacet is a Facet to track Money in a simple, since monetary unit game
 * system.
 */
public class MoneyFacet extends AbstractStorageFacet
{

	private static final BigDecimal ZERO = new BigDecimal(0);

	private final Class<?> thisClass = getClass();

	/**
	 * Returns the type-safe MoneyCacheInfo for this MoneyFacet and the given
	 * CharID. Will return a new, empty MoneyCacheInfo if no Money information
	 * has been set for the given CharID. Will not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The MoneyCacheInfo object is
	 * owned by MoneyFacet, and since it can be modified, a reference to that
	 * object should not be exposed to any object other than MoneyFacet.
	 * 
	 * @param id
	 *            The CharID for which the MoneyCacheInfo should be returned
	 * @return The MoneyCacheInfo for the Player Character represented by the
	 *         given CharID.
	 */
	private MoneyCacheInfo getConstructingInfo(CharID id)
	{
		MoneyCacheInfo rci = getInfo(id);
		if (rci == null)
		{
			rci = new MoneyCacheInfo();
			setCache(id, thisClass, rci);
		}
		return rci;
	}

	/**
	 * Returns the type-safe MoneyCacheInfo for this MoneyFacet and the given
	 * CharID. May return null if no Money information has been set for the
	 * given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The MoneyCacheInfo object is
	 * owned by MoneyFacet, and since it can be modified, a reference to that
	 * object should not be exposed to any object other than MoneyFacet.
	 * 
	 * @param id
	 *            The CharID for which the MoneyCacheInfo should be returned
	 * @return The MoneyCacheInfo for the Player Character represented by the
	 *         given CharID; null if no Money information has been set for the
	 *         Player Character.
	 */
	private MoneyCacheInfo getInfo(CharID id)
	{
		return (MoneyCacheInfo) getCache(id, thisClass);
	}

	/**
	 * Sets the amount of gold for the Player Character represented by the given
	 * CharID to the given value.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            gold value should be set
	 * @param gold
	 *            The amount of gold for the Player Character represented by the
	 *            given CharID
	 */
	public void setGold(CharID id, BigDecimal gold)
	{
		/*
		 * TODO What is this is negative and allowDebt is false?
		 */
		getConstructingInfo(id).gold = gold;
	}

	/**
	 * Sets whether the Player Character represented by the given CharID is
	 * allowed to go into debt in order to buy items and equipment.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            AllowDebt setting is being set
	 * @param allowDebt
	 *            The AllowDebt setting for the Player Character represented by
	 *            the given CharID
	 */
	public void setAllowDebt(CharID id, boolean allowDebt)
	{
		getConstructingInfo(id).allowDebt = allowDebt;
	}

	/**
	 * Sets whether the Player Character represented by the given CharID ignores
	 * the cost when purchasing items and equipment.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            IgnoreCost setting is being set
	 * @param ignoreCost
	 *            The IgnoreCost setting for the Player Character represented by
	 *            the given CharID
	 */
	public void setIgnoreCost(CharID id, boolean ignoreCost)
	{
		getConstructingInfo(id).ignoreCost = ignoreCost;
	}

	/**
	 * Returns the amount of gold possessed by the Player Character represented
	 * by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            amount of gold possessed should be returned
	 * @return The amount of gold possessed by the Player Character represented
	 *         by the given CharID.
	 */
	public BigDecimal getGold(CharID id)
	{
		MoneyCacheInfo rci = getInfo(id);
		return rci == null ? ZERO : rci.gold;
	}

	/**
	 * Returns the AllowDebt setting for the Player Character represented by the
	 * given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            AllowDebt setting will be returned
	 * @return The AllowDebt setting for the Player Character represented by the
	 *         given CharID
	 */
	public boolean isAllowDebt(CharID id)
	{
		MoneyCacheInfo rci = getInfo(id);
		return rci == null ? SettingsHandler.getGearTab_AllowDebt()
				: rci.allowDebt;
	}

	/**
	 * Returns the IgnoreCost setting for the Player Character represented by
	 * the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            IgnoreCost setting will be returned
	 * @return The IgnoreCost setting for the Player Character represented by
	 *         the given CharID
	 */
	public boolean isIgnoreCost(CharID id)
	{
		MoneyCacheInfo rci = getInfo(id);
		return rci == null ? SettingsHandler.getGearTab_IgnoreCost()
				: rci.ignoreCost;
	}

	/**
	 * MoneyCacheInfo is the data structure used by MoneyFacet to store a Player
	 * Character's Gold, and various Money settings (AllowDebt, IgnoreCost)
	 */
	private static class MoneyCacheInfo
	{
		public BigDecimal gold = ZERO;
		public boolean allowDebt;
		public boolean ignoreCost;
	}

	/**
	 * Copies the contents of the MoneyFacet from one Player Character to
	 * another Player Character, based on the given CharIDs representing those
	 * Player Characters.
	 * 
	 * This is a method in MoneyFacet in order to avoid exposing the mutable
	 * MoneyCacheInfo object to other classes. This should not be inlined, as
	 * MoneyCacheInfo is internal information to MoneyFacet and should not be
	 * exposed to other classes.
	 * 
	 * Note also the copy is a one-time event and no references are maintained
	 * between the Player Characters represented by the given CharIDs (meaning
	 * once this copy takes place, any change to the Money of one Player
	 * Character will only impact the Player Character where the Money was
	 * changed).
	 * 
	 * @param source
	 *            The CharID representing the Player Character from which the
	 *            Money information should be copied
	 * @param destination
	 *            The CharID representing the Player Character to which the
	 *            Money information should be copied
	 */
	@Override
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
		BigDecimal old = getGold(id);
		// I don't really like this hack, but setScale just won't work right...
		BigDecimal newGold = new BigDecimal(old.doubleValue() + delta).divide(
				BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_EVEN);
		/*
		 * TODO What is this delta produces a negative value, but allowDebt is
		 * false?
		 */
		setGold(id, newGold);
	}

}
