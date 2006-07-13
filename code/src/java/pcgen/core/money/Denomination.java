/*
 * Denomination.java
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


/**
 * Denomination class represents a specific denomination of currency.
 *
 * @author  Brad Stiles (brasstilde@yahoo.com)
 * @version    $Revision$
 */
public final class Denomination implements Comparable<Object>
{
	protected String abbr;
	protected String name;
	protected float weight;
	protected int factor;

	/*
	 * For future expansion of capabilities involving regions.
	 * The regions should be it's own object, so that various
	 * entities can use it.
	 */

	//private Region region;

	/**
	 * Class constructor
	 *
	 * @param name    the name of the denomination
	 * @param abbr    the abbreviation for the denomination
	 * @param factor  the factor that describes this denominations's
	 *                relationship to other denominations
	 * @param weight  the coin's weight
	 */
	Denomination(final String name, final String abbr, final int factor, final float weight)
	{
		this.name = name;
		this.factor = factor;
		this.abbr = abbr;
		this.weight = weight;
	}

	/**
	 * Get abbreviation
	 * @return abbreviation
	 */
	public String getAbbr()
	{
		return this.abbr;
	}

	/**
	 * Get factor
	 * @return factor
	 */
	public int getFactor()
	{
		return this.factor;
	}

	/**
	 * Get name
	 * @return name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Get weight
	 * @return weight
	 */
	public float getWeight()
	{
		return this.weight;
	}

	/**
	 * Required, as part of the Comparable interface, for sorting
	 * these objects.  Note that this method implements a
	 * descending sort on the factor for denomination objects.
	 *
	 * @param d  an object of type Denomination
	 *
	 * @return   1 if the passed object's factor is less than this
	 *           object's factor, 0 if they are equal and -1 if the
	 *           passed object's factor is greater than this one.
	 */
	public int compareTo(final Object d)
	{
		if (this.factor > ((Denomination) d).factor)
		{
			return -1;
		}
		return (this.factor == ((Denomination) d).factor) ? 0 : 1;
	}

	public String toString()
	{
		return name + "/" + factor;
	}
}
