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

/**
 * A Die is an object with a given quantity of sequentially numbered sides, starting at 1.
 */
public class Die
{

    /**
     * The number of sides on this Die.
     */
    private final int sides;

    /**
     * Constructs a new Die with the given number of sides.
     *
     * @param sides The number of sides on the Die
     */
    public Die(int sides)
    {
        this.sides = sides;
    }

    /**
     * Returns the number of sides on the Die.
     *
     * @return The number of sides on the Die
     */
    public int getSides()
    {
        return sides;
    }

    @Override
    public int hashCode()
    {
        return 73489 + sides;
    }

    @Override
    public boolean equals(Object o)
    {
        return (o instanceof Die) && (((Die) o).sides == sides);
    }
}
