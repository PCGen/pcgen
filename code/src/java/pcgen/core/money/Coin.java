/*
 * Coin.java
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


/**
 * Coin class encapsulates a particular denomination of coin,
 * adding an amount to the other fields.
 *
 * @author  Brad Stiles (brasstilde@yahoo.com)
 * @version    $Revision$
 */
final class Coin
{
	protected Denomination denom;
	protected long amount;

	/**
	 * Constructor creates a new coin corresponding to a denomination,
	 * and sets the amount to the passed amount
	 *
	 * @param denom  the name of the denomination
	 * @param amount the amount of coins
	 */
	Coin(final Denomination denom, final long amount)
	{
		this.denom = denom;
		this.amount = amount;
	}

	/**
	 * Constructor creates a new coin corresponding to a denomination,
	 * and sets the amount to 0
	 *
	 * @param aDenom   the abbreviation for the denomination
	 */
	Coin(final Denomination aDenom)
	{
		this(aDenom, 0L);
	}

	/**
	 * Sets the value of the coin to the passed amount.
	 * @param amount
	 */
	public void setAmount(final long amount)
	{
		this.amount = amount;
	}

	/**
	 * Retrieves the current value of the coin.
	 * @return amount
	 */
	public long getAmount()
	{
		return amount;
	}

	/**
	 * Get the denomination
	 * @return denomination
	 */
	public Denomination getDenomination()
	{
		return this.denom;
	}

	/**
	 * Compares this object to the passed denomination for
	 * equality of the name.
	 *
	 * @param d   An object of type denomination.
	 *
	 * @return    True if the object's names are equal.
	 */
	public boolean isDenomination(final Denomination d)
	{
		final boolean val = false;

		try
		{
			return denom.getName().equals(d.getName());
		}
		catch (Exception e)
		{
			// not sure if false should be the default behaviour but its better than an exception
			return val;
		}
	}

	/**
	 * Get the factor
	 * @return factor
	 */
	public int getFactor()
	{
		return this.denom.getFactor();
	}

	/**
	 * Retrieves the current value of the coin in terms of
	 * the base coin.
	 * @return factored amount
	 */
	public long getFactoredAmount()
	{
		return amount * denom.factor;
	}

	/**
	 * Get the name
	 * @return name
	 */
	public String getName()
	{
		return denom.getName();
	}

	/**
	 * Get the weight
	 * @return weight
	 */
	public float getWeight()
	{
		return denom.getWeight() * amount;
	}

	/**
	 * Adds the passed argAmount to the current value of the coin.
	 *
	 * @param argAmount   the argAmount to be added
	 *
	 * @return   the new value of the coin
	 */
	public long add(final long argAmount)
	{
		return (this.amount += argAmount);
	}

	/**
	 * Get a string representation of the coin value.
	 *
	 * @return  the amount, followed by a space, followed
	 *          by the name of the coin
	 */
	public String toString()
	{
		return toString(false);
	}

	/**
	 * Get a string representation of the coin value, optionally using
	 * the abbreviation.
	 *
	 * @param useAbbr    a boolean value which determines whether to
	 *                   use the abbreviation (true) or the name (false)
	 *
	 * @return  the amount, followed by a space, followed
	 *          by the name or abbreviation of the coin
	 */
	String toString(final boolean useAbbr)
	{
		return amount + " " + (useAbbr ? denom.getAbbr() : denom.getName());
	}
}
