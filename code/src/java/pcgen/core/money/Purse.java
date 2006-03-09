/*
 * Purse.java
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
 */
package pcgen.core.money;

import java.util.Iterator;

/**
 * Manage a collection of Coins, allowing adding to each coin in various ways.
 * TODO: This class defines a covariant version of the equals() method, but calls the normal equals(Object) method defined in the base java.lang.Object class. The class should probably define a non-covariant version of equals(). (I.e., a method with the signature boolean equals(java.lang.Object).
 * TODO: This class overrides equals(Object), but does not override hashCode(), and inherits the implementation of hashCode() from java.lang.Object (which returns the identity hash code, an arbitrary value assigned to the object by the VM). Therefore, the class is very likely to violate the invariant that equal objects must have equal hashcodes.
 *
 * @author   Brad Stiles (brasstilde@yahoo.com)
 * @version    $Revision: 1.13 $
 */
public final class Purse
{
	private Coins coins;

	/**
	 * This constructor uses the passed Coins collection directly.  It does
	 * not copy it before using it.  Thus, if two Purse objects are created
	 * using the same Coins collection, they will both point to that same
	 * collection, and will each reflect changes made by the other.
	 *
	 * @param coins  a Coins object to manage
	 *
	 * @deprecated Unused
	 */
	public Purse(final Coins coins)
	{
		this.coins = coins;
	}

	/**
	 * This constructor uses the passed Denominations collection to create
	 * a new coins collection that will be unrelated to any other. Thus, even
	 * if two Purse objects are created using the same Denominations collection,
	 * they will each point to their own Coins collection, and won't affect
	 * each other.
	 *
	 * @param coins  a Denominations object from which a Coins collection
	 *               is created.
	 */
	public Purse(final Denominations coins)
	{
		this.coins = new Coins(coins);
	}

	/**
	 * Sets the amount for a particular coin, overriding any amount
	 * already present.
	 *
	 * @param amount   the amount to which to set the coin
	 * @param coinName the name or abbreviation of the coin
	 *                 whose value is to be set
	 */
	public void setAmount(final long amount, final String coinName)
	{
		if (coinName.equals(""))
		{
			coins.getDefaultCoin().setAmount(amount);
		}
		else
		{
			final Coin c = coins.getCoin(coinName);

			if (c != null)
			{
				c.setAmount(amount);
			}
		}
	}

	/**
	 * Sets the amount for a particular coin, overriding any amount
	 * already present.
	 *
	 * @param coinAmount  a string containing the amount and name of
	 *                    the coin to set.  The string should have the
	 *                    amount first, with an optional sign, followed
	 *                    by zero or more spaces, and the name or
	 *                    abbreviation of the coin to be set.
	 *                    e.g.: "2 gp", "3 silver", "1pp".  The name
	 *                    or abbreviation must be that of a coin in the
	 *                    collection, or nothing will be done.
	 *
	 * @deprecated Unused
	 */
	public void setAmount(final String coinAmount)
	{
		final Coin tempCoin = Coins.parseCoin(coinAmount, coins);
		setAmount(tempCoin.getAmount(), tempCoin.getName());
	}

	/**
	 * Set the passed amount as the value of the purse, assuming that it is
	 * expressed as a fraction of the base amount.
	 *
	 * @param amount   the amount to be added to the purse.
	 */
	public void setAmount(final double amount)
	{
		setTotalValue((long) (amount * coins.getDefaultCoin().getFactor()));
	}

	/**
	 * Get the current value of a particular coin.
	 *
	 * @param coinName  the name of the coin
	 *
	 * @return the value of the requested coin
	 *
	 * @deprecated Unused
	 */
	public long getAmount(final String coinName)
	{
		final Coin c = coins.getCoin(coinName);

		if (c != null)
		{
			return c.getAmount();
		}
		return 0;
	}

	/**
	 * Rearranges the current coins so that the least number of coins that still
	 * represents the total value is presented.  For instance, 3 gold, 24 silver
	 * and 432 copper would be rearranged to 9 gold, 7 silver and 2 copper.
	 *
	 * @deprecated Unused
	 */
	public void setLCDValue()
	{
		setTotalValue(getTotalValue());
	}

	/**
	 * Sets the total value of this Purse object, but setting each coin's
	 * value to that portion of the amount parameter which it can contain
	 * as a long integer.  For instance, if a gold coin has been defined
	 * with a factor of 100, a silver with a factor of 10, and copper with
	 * 1, then passing 3274 to this method will result in coins with the
	 * following values: Gold 32, Silver 7, copper 4.
	 *
	 * @param amount   the amount to set as the value
	 */
	public void setTotalValue(long amount)
	{
		final Iterator i = coins.iterator();

		while (i.hasNext())
		{
			final Coin c = (Coin) i.next();
			c.setAmount(amount / c.getFactor());
			amount %= c.getFactor();
		}
	}

	/**
	 * Get the total numeric value of all the coins in the collection, expressed
	 * in terms of the lowest common denominator, using each coin object's factor
	 * field as a multiplier.
	 *
	 * @return the value of all the coins.
	 */
	public long getTotalValue()
	{
		final Iterator i = coins.iterator();
		long result = 0;

		while (i.hasNext())
		{
			final Coin c = (Coin) i.next();
			result += c.getFactoredAmount();
		}

		return result;
	}

	/**
	 * @param name
	 * @return weight of named coin
	 * @deprecated Unused 
	 */
	public double getWeight(final String name)
	{
		return coins.getWeight(name);
	}

	/**
	 * Add the specified amount to the default coin
	 *
	 * @param amount   the amount to be added.
	 *
	 * @deprecated Unused
	 */
	public void add(final long amount)
	{
		coins.getDefaultCoin().add(amount);
	}

	/**
	 * Add the specified amount to the specified coin.
	 *
	 * @param amount   the amount to be added
	 * @param coinName the coin to which the amount should be added
	 */
	public void add(final long amount, final String coinName)
	{
		final Coin c;

		if (coinName.equals(""))
		{
			c = coins.getDefaultCoin(); // coins.defaultCoin.add(amount);
		}
		else
		{
			c = coins.getCoin(coinName);
		}

		if (c != null)
		{
			c.add(amount);
		}
	}

	/**
	 * Adds the amount in the passed coin to the matching coin
	 * in the local collection.
	 *
	 * @param coin   a Coin object from which to get the amount
	 */
	public void add(final Coin coin)
	{
		add(coin.getAmount(), coin.getName());
	}

	/**
	 * Add an amount to a coin, using a single String to specify both
	 * the amount and the coin.
	 *
	 * @param coinAmount  a string containing the amount and name of
	 *                    the coin on which to operate.  The string
	 *                    should have the amount first, with an
	 *                    optional sign, followed by zero or more
	 *                    spaces, and the name or abbreviation of
	 *                    the coin to be set.  e.g.: "2 gp", "3 silver",
	 *                    "1pp".  The name or abbreviation must be that
	 *                    of a coin in the collection, or nothing will
	 *                    be done.
	 *
	 * @deprecated Unused
	 */
	public void add(final String coinAmount)
	{
		final Coin tempCoin = Coins.parseCoin(coinAmount, coins);

		if (tempCoin != null)
		{
			add(tempCoin.getAmount(), tempCoin.getName());
		}
	}

	/**
	 * Add the passed amount to the purse, assuming that it is expressed as
	 * a fraction of the base amount.
	 *
	 * @param amount   the amount to be added to the purse.
	 *
	 * @deprecated Unused
	 */
	public void add(final double amount)
	{
		addLCD((long) (amount * coins.getDefaultCoin().getFactor()));
	}

	/**
	 * Add the values of each of the someCoins in a collection of someCoins
	 * to the corresponding someCoins in this collection.
	 *
	 * @param someCoins    a collection of someCoins
	 *
	 * @deprecated Unused
	 */
	public void add(final Coins someCoins)
	{
		final Iterator i = someCoins.iterator();

		while (i.hasNext())
		{
			add((Coin) i.next());
		}
	}

	/**
	 * Add the passed amount to the purse, assuming that it is exressed in
	 * terms of the lowest factor.  That is, adding 3247 would add 32 gold,
	 * 4 silver and 7 copper.
	 *
	 * @param amount  the amount to be added to the purse.
	 */
	public void addLCD(long amount)
	{
		final Iterator i = coins.iterator();

		while (i.hasNext())
		{
			final Coin c = (Coin) i.next();
			c.add(amount / c.getFactor());
			amount %= c.getFactor();
		}
	}

	/**
	 * Get the numeric value of the specified coin.
	 *
	 * @param coinName  the name of the coin whose value is desired
	 *
	 * @return  a number of type long representing the numeric value.
	 *
	 * @deprecated Unused
	 */
	public long coinAmount(final String coinName)
	{
		final Coin c = coins.getCoin(coinName);

		if (c != null)
		{
			return c.getAmount();
		}
		return 0;
	}

	/**
	 * Get a string, in the form "2 gold", which represents the
	 * the value of the specified coin.
	 *
	 * @param coinName   the name of the coin whose value is desired.
	 * @param useAbbr    a boolean value which determines whether the
	 *                   abbreviation will be used (true) or the full
	 *                   name (false).
	 *
	 * @return    the string representation of the coin and its value
	 *
	 * @deprecated Unused
	 */
	public String coinTotal(final String coinName, final boolean useAbbr)
	{
		final Coin c = coins.getCoin(coinName);

		if (c != null)
		{
			return c.toString(useAbbr);
		}
		return "";
	}

	/**
	 * Get a string, in the form "2 gold 3 silver", which represents the
	 * the values of all the coins in the collection.
	 *
	 * @param useAbbr    a boolean value which determines whether the
	 *                   abbreviation will be used (true) or the full
	 *                   name (false).
	 *
	 * @return    the string representation of the coins and their values
	 *
	 * @deprecated Unused
	 */
	public String coinTotal(final boolean useAbbr)
	{
		return toString(useAbbr, false, true);
	}

	/**
	 * Determines whether the value of the passed purse is
	 * equal to the amount of the total value of the
	 * coins in this collection.
	 *
	 * @param purse  a Purse object to compare
	 *
	 * @return       true if the current value of the purse
	 *               is equal to the passed value, false
	 *               if not.
	 */
	public boolean equals(final Purse purse)
	{
		return (getTotalValue() == purse.getTotalValue());
	}

	/**
	 * Determines whether the amount of the passed coin is
	 * equal to the amount of the total value of the
	 * coins in this collection.
	 *
	 * @param coin   a Coin object to compare
	 *
	 * @return       true if the current value of the coins
	 *               is equal to the passed value, false
	 *               if not.
	 *
	 * @deprecated Unused
	 */
	public boolean equals(final Coin coin)
	{
		return (getTotalValue() == coin.getFactoredAmount());
	}

	/**
	 * Determines whether the amount is equal to the
	 * amount of the total value of the coins in this
	 * collection.
	 *
	 * @param amount the amount to check
	 *
	 * @return       true if the current value of the coins
	 *               is equal to the passed value, false
	 *               if not.
	 *
	 * @deprecated Unused
	 */
	public boolean equals(final long amount)
	{
		return (getTotalValue() == amount);
	}

	/**
	 * Determines whether the amount is equal to the
	 * amount of the total value of the coins in this
	 * collection.
	 *
	 * @param amount the amount to check
	 *
	 * @return       true if the current value of the coins
	 *               is equal to the passed value, false
	 *               if not.
	 *
	 * @deprecated Unused
	 */
	public boolean equals(final double amount)
	{
		return Math.abs(getTotalValue() - amount) < (Double.MIN_VALUE * 2);
	}

	/**
	 * Determines whether the value of the passed purse is
	 * greater than the amount of the total value of the
	 * coins in this collection.
	 *
	 * @param purse  a Purse object to compare
	 *
	 * @return       true if the current value of the purse
	 *               is greater than the passed value, false
	 *               if not.
	 *
	 * @deprecated Unused
	 */
	public boolean greaterThan(final Purse purse)
	{
		return (getTotalValue() < purse.getTotalValue());
	}

	/**
	 * Determines whether the amount of the passed coin is
	 * greater than the amount of the total value of the
	 * coins in this collection.
	 *
	 * @param coin   a Coin object to compare
	 *
	 * @return       true if the current value of the coins
	 *               is greater than the passed value, false
	 *               if not.
	 *
	 * @deprecated Unused
	 */
	public boolean greaterThan(final Coin coin)
	{
		return (getTotalValue() > coin.getFactoredAmount());
	}

	/**
	 * Determines whether the amount is greater than the
	 * amount of the total value of the coins in this
	 * collection.
	 *
	 * @param amount the amount to check
	 *
	 * @return       true if the current value of the coins
	 *               is greater than the passed value, false
	 *               if not.
	 *
	 * @deprecated Unused
	 */
	public boolean greaterThan(final long amount)
	{
		return (getTotalValue() > amount);
	}

	/**
	 * Determines whether the amount is greater than the
	 * amount of the total value of the coins in this
	 * collection.
	 *
	 * @param amount the amount to check
	 *
	 * @return       true if the current value of the coins
	 *               is greater than the passed value, false
	 *               if not.
	 *
	 * @deprecated Unused
	 */
	public boolean greaterThan(final double amount)
	{
		return getTotalValue() > amount;
	}

	/**
	 * Determines whether the value of the passed purse is
	 * less than the amount of the total value of the
	 * coins in this collection.
	 *
	 * @param purse  a Purse object to compare
	 *
	 * @return       true if the current value of the purse
	 *               is less than the passed value, false
	 *               if not.
	 *
	 * @deprecated Unused
	 */
	public boolean lessThan(final Purse purse)
	{
		return (getTotalValue() < purse.getTotalValue());
	}

	/**
	 * Determines whether the amount of the passed coin is
	 * less than the amount of the total value of the
	 * coins in this collection.
	 *
	 * @param coin   a Coin object to compare
	 *
	 * @return       true if the current value of the coins
	 *               is less than the passed value, false
	 *               if not.
	 *
	 * @deprecated Unused
	 */
	public boolean lessThan(final Coin coin)
	{
		return (getTotalValue() < coin.getFactoredAmount());
	}

	/**
	 * Determines whether the amount is less than the
	 * amount of the total value of the coins in this
	 * collection.
	 *
	 * @param amount the amount to check
	 *
	 * @return       true if the current value of the coins
	 *               is less than the passed value, false
	 *               if not.
	 *
	 * @deprecated Unused
	 */
	public boolean lessThan(final long amount)
	{
		return (getTotalValue() < amount);
	}

	/**
	 * Determines whether the amount is less than the
	 * amount of the total value of the coins in this
	 * collection.
	 *
	 * @param amount the amount to check
	 *
	 * @return       true if the current value of the coins
	 *               is less than the passed value, false
	 *               if not.
	 *
	 * @deprecated Unused
	 */
	public boolean lessThan(final double amount)
	{
		return getTotalValue() < amount;
	}

	/**
	 * Subtracts the amount in the passed coin to the matching coin
	 * in the local collection.  This just makes it a little
	 * easier to subtract coins without a lot of gyrations in
	 * calling code to adjust.
	 *
	 * @param coin   a Coin object from which to get the amount
	 */
	public void subtract(final Coin coin)
	{
		add(0 - coin.getAmount(), coin.getName());
	}

	/**
	 * Add the values of each of the someCoins in a collection of someCoins
	 * to the corresponding someCoins in this collection.
	 *
	 * @param someCoins    a collection of someCoins
	 *
	 * @deprecated Unused
	 */
	public void subtract(final Coins someCoins)
	{
		final Iterator i = someCoins.iterator();

		while (i.hasNext())
		{
			subtract((Coin) i.next());
		}
	}

	public String toString()
	{
		return toString(true, false, true);
	}

	/**
	 * return information about the coins object as a String
	 * @param useAbbr
	 * @param includeZeros
	 * @param startAtDefault
	 * @return information about the coins object as a String
	 */
	public String toString(final boolean useAbbr, final boolean includeZeros, final boolean startAtDefault)
	{
		return coins.toString(useAbbr, includeZeros, startAtDefault);
	}

	/**
	 * Gets the value of the purse in terms of the default coin.
	 * This may result in some inaccuracy because of the use
	 * of a floating point type
	 *
	 * @return    the value of the purse
	 *
	 * @deprecated Unused
	 */
	public double value()
	{
		final double coinValue = getTotalValue();

		return coinValue / coins.getDefaultCoin().getFactor();
	}

	/**
	 * Gets the value of the purse in terms of the named coin.
	 * This may result in some inaccuracy because of the use
	 * of a floating point type
	 *
	 * @param coinName   name of the coin in terms of which the
	 *                   value of the purse will be expressed.
	 *
	 * @return    the value of the purse
	 *
	 * @deprecated Unused
	 */
	public double value(final String coinName)
	{
		final Coin c = coins.getCoin(coinName);
		final double coinValue = getTotalValue();

		return coinValue / c.getFactor();
	}
}
