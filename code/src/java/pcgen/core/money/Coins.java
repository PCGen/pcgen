/*
 * Coins.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on April 21, 2001, 2:15 PM
 *
 * $Id$
 */
package pcgen.core.money;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Coins class encapsulates a collection of coins objects,
 *
 * @author  Brad Stiles (brasstilde@yahoo.com)
 * @version    $Revision$
 */
final class Coins
{
	private Coin defaultCoin;
	private List coins;

	/**
	 * Constructor creates a new Coins collection, creating new Coin
	 * objects from the passed Denominationa object,  The passed
	 * Denominations object's defaultCoin will be used to set the
	 * new Coins object's defaultCoin.
	 *
	 * @param currency  a Denominations object from which create
	 *                  Coin objects.
	 */
	Coins(final Denominations currency)
	{
		coins = new ArrayList();

		final Iterator i = currency.iterator();

		while (i.hasNext())
		{
			final Denomination d = (Denomination) i.next();
			final Coin c = new Coin(d);
			coins.add(c);

			if ((currency.getDefaultCoin() != null) && c.isDenomination(currency.getDefaultCoin()))
			{
				this.defaultCoin = c;
			}
		}
	}

	/**
	 * Parse a string into an amount and a name.
	 *
	 * @param coinAmount  a string containing the amount and name of
	 *                    the coin to set.  The string should have the
	 *                    amount first, with an optional sign, followed
	 *                    by zero or more spaces, and the name or
	 *                    abbreviation of the coin to be set.
	 *                    e.g.: "2 gp", "3 silver", "1pp".  The name
	 *                    or abbreviation must be that of a coin in the
	 *                    collection, or the method will return null.
	 * @param someCoins
	 *
	 * @return   a Coin object with the amount and name.
	 *
	 */
	public static Coin parseCoin(String coinAmount, final Coins someCoins)
	{
		coinAmount = coinAmount.trim();

		Coin tempCoin = null; // new Coin("", "", 0, 0);

		if (coinAmount.length() > 0)
		{
			String coinName = "";
			long amount = 0L;
			int index = 0;

			// First, parse the passed string into it's constituent parts
			// If there is a plus sign at the beginning of the string,
			// strip it off, since the "parseType" functions apparently
			// don't deal with those.
			if (coinAmount.charAt(index) == '+')
			{
				coinAmount = coinAmount.substring(++index);
			}

			int pos = index;

			try
			{
				while (pos < coinAmount.length())
				{
					final char c = coinAmount.charAt(pos);

					if ((c < '0') || (c > '9'))
					{
						amount = Long.parseLong(coinAmount.substring(index, pos));
						coinName = coinAmount.substring(pos);
						coinName = coinName.trim();

						break;
					}

					++pos;
				}
			}
			catch (NumberFormatException e)
			{
				//assume default as stated below
				//exception occurs when coinAmoint contains #$%
			}

			// If we made it through the entire string without meeting something
			// that wasn't a number, then assume the default coin.
			if (pos >= coinAmount.length())
			{
				if (someCoins.defaultCoin != null)
				{
					coinName = someCoins.defaultCoin.getName();
					amount = Long.parseLong(coinAmount);
				}
				else
				{
					return null;
				}
			}

			tempCoin = someCoins.cloneCoin(coinName);

			if (tempCoin != null)
			{
				tempCoin.amount = amount;
			}
		}

		return tempCoin;
	}

	/**
	 * Get a reference to a coin in the collection by searching for the name.
	 *
	 * @param name   the name of the coin to get.
	 *
	 * @return   a reference to the coin.  If no coin with the
	 *           passed name is found, then null is returned.
	 */
	public Coin getCoin(final String name)
	{
		final Iterator i = coins.iterator();
		Coin c;

		while (i.hasNext())
		{
			c = (Coin) i.next();

			if (c.denom.getName().equalsIgnoreCase(name) || c.denom.getAbbr().equalsIgnoreCase(name))
			{
				return c;
			}
		}

		return null;
	}

	/**
	 * Get the default coin
	 * @return default coin
	 */
	public Coin getDefaultCoin()
	{
		return defaultCoin;
	}

	/**
	 * Get the total weight of the specified coins
	 *
	 * @param name   the name of the coin type.
	 *
	 * @return       the total weight of the coins of the
	 *               specified type in the collection.
	 *
	 */
	public float getWeight(final String name)
	{
		final Coin c = getCoin(name);

		return (c != null) ? (c.getAmount() * c.getWeight()) : 0F;

		//if (c != null)
		//	return c.getAmount() * c.getWeight();
		//else
		//	return 0F;
	}

	/**
	 * Get the total weight of all coins in the collection
	 *
	 * @return    the total weight of all coins in the collection.
	 *
	 */
	public double getWeight()
	{
		final Iterator i = coins.iterator();
		Coin c;
		double weight = 0F;

		while (i.hasNext())
		{
			c = (Coin) i.next();
			weight += c.getWeight();
		}

		return weight;
	}

	/**
	 * Return the coins ArrayList's iterator to the caller.
	 * @return Iterator for coins
	 */
	public Iterator iterator()
	{
		return coins.iterator();
	}

	/**
	 * Get a string representation of the value of all the
	 * coins in the collection.
	 *
	 * @return  the amount, followed by a space, followed
	 *          by the name of the coin, for each coin.
	 */
	public String toString()
	{
		return toString(true, false, true);
	}

	/**
	 * Get a string representation of the value of all the coins
	 * in the collection, optionally using the abbreviations.
	 *
	 * @param useAbbr            a boolean value which determines whether to
	 *                    use the abbreviations (true) or the names (false)
	 * @param includeZeros        a boolean value which determines whether or
	 *                    not to include any coins which have a 0 value.
	 * @param startAtDefault    a boolean value which indicates whether or
	 *                    not to use coins higher than the default when
	 *                    outputing the value.
	 *
	 * @return  the amount, followed by a space, followed
	 *          by the name or abbreviation of the coin, for each coin
	 */
	public String toString(final boolean useAbbr, final boolean includeZeros, boolean startAtDefault)
	{
		//no idea what the expected size of this may be but probably larger than the
		//default of 16
		StringBuffer result = new StringBuffer(50);

		long carriedAmount = 0L;

		final Iterator i = coins.iterator();

		while (i.hasNext())
		{
			// I clone the coin here because I may have to change it's value in
			// order to properly handle the exclusion of coins higher than the
			// default coin.  bsmeister 03/04/2003
			final Coin c = cloneCoin((Coin) i.next());

			// This section excludes causes coins with a higher value than the
			// default to be excluded, but their value still be included in
			// the total.  bsmeister 03/04/2003
			if (startAtDefault)
			{
				if (c.isDenomination(defaultCoin.getDenomination()))
				{
					c.add(carriedAmount / c.getFactor());
					startAtDefault = false;
				}
				else
				{
					carriedAmount += c.getFactoredAmount();
					c.setAmount(0);
				}
			}

			if ((includeZeros && (c.getAmount() == 0)) || (c.getAmount() != 0))
			{
				result.append(c.toString(useAbbr)).append(", ");
			}
		}

		if (result.length() > 2)
		{
			result = new StringBuffer(result.substring(0, result.length() - 2));
		}

		return result.toString();
	}

	/**
	 * Clone a coin from the collection
	 *
	 * @param  name the name of the coin to clone
	 *
	 * @return  a coin object which is a copy of the one from
	 *         the collection
	 */
	private Coin cloneCoin(final String name)
	{
		final Coin c = getCoin(name);

		return (c == null) ? null : new Coin(c.denom, c.amount);
	}

	/**
	 * Clone a coin from the collection
	 *
	 * @param  name the name of the coin to clone
	 *
	 * @return  a coin object which is a copy of the one from
	 *         the collection
	 */
	private static Coin cloneCoin(final Coin name)
	{
		return (name == null) ? null : new Coin(name.denom, name.amount);
	}
}
