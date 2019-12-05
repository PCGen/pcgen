/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 */
package gmgen.plugin.dice;

/**
 * A normal die
 */
public class Dice extends Die
{
    /**
     * Constructor for the Dice object
     *
     * @param num   Number of dice
     * @param sides Number of sides
     * @param bias  Modifier to the die roll
     */
    public Dice(final int num, final int sides, final int bias)
    {
        /* Holds the rolls of each die */
        super(new NSidedModifiedDieConfig(num, sides, bias, Die.RAND));
    }

    /**
     * Constructor for the Dice object
     *
     * @param num   Number of dice
     * @param sides Number of sides per die
     */
    public Dice(final int num, final int sides)
    {
        this(num, sides, 0);
    }

}
