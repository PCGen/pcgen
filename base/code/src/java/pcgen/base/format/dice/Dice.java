/*
 * Copyright (c) 2016-7 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.base.format.dice;

import java.util.Objects;

/**
 * Dice represents a quantity of Die instances (e.g. 5d6)
 * die The underlying Die size for this Dice object.
 * quantity - The quantity of times the given Die size is in this Dice object.
 */

public record Dice(int quantity, Die die)
{

	/**
	 * Constructs a new Dice object with the given quantity of the given Die size
	 *
	 * @param quantity The quantity of times the given Die size is in this Dice object
	 * @param die      The underlying Die size for this Dice object
	 */
	public Dice(int quantity, Die die)
	{
		if (quantity < 0)
		{
			throw new IllegalArgumentException("Quantity cannot be < 0 in Dice");
		}
		this.quantity = quantity;
		this.die = Objects.requireNonNull(die);
	}


}
