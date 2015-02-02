/*
 * Copyright 2007, 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.helper;

import pcgen.cdom.base.Constants;

/**
 * A PointCost represents a characteristic and the cost of that characteristic
 * for a point-based spell system.
 */
public class PointCost
{

	/**
	 * The characteristic for which this PointCost defines a cost
	 */
	private final String type;

	/**
	 * The point cost of the characteristic
	 */
	private final int cost;

	/**
	 * Constructs a new PointCost for the given characteristic and cost
	 * 
	 * @param key
	 *            The characteristic for which this PointCost defines a cost
	 * @param pointcost
	 *            The point cost of the characteristic
	 * @throws IllegalArgumentException
	 *             if the given characteristic is null
	 */
	public PointCost(String key, int pointcost)
	{
		if (key == null)
		{
			throw new IllegalArgumentException(
					"Key for PointCost cannot be null");
		}
		type = key;
		cost = pointcost;
	}

	/**
	 * Returns the characteristic for which this PointCost defines a cost
	 * 
	 * @return The characteristic for which this PointCost defines a cost
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Returns the point cost of the characteristic
	 * 
	 * @return The point cost of the characteristic
	 */
	public int getCost()
	{
		return cost;
	}

	/**
	 * Returns the consistent-with-equals hashCode for this PointCost
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return type.hashCode() ^ cost;
	}

	/**
	 * Returns true if this PointCost is equal to the given Object. Equality is
	 * defined as being another PointCost object with an equal characteristic
	 * and cost
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof PointCost)
		{
			PointCost other = (PointCost) obj;
			return type.equals(other.type) && cost == other.cost;
		}
		return false;
	}

	/**
	 * Returns a String representation of this PointCost, primarily for purposes
	 * of debugging. It is strongly advised that no dependency on this method be
	 * created, as the return value may be changed without warning.
	 * 
	 * @return A String representation of this PointCost
	 */
	@Override
	public String toString()
	{
		return getType() + Constants.EQUALS + getCost();
	}
}
