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
 *  SystemDie.java
 */
package gmgen.plugin;


import gmgen.plugin.dice.DiceConfig;
import gmgen.plugin.dice.SystemDieConfig;

/** A d20 die, applies a +10 on a 20, and a -10 on a 1
 * @author Soulcatcher
 * @since May 24, 2003
 */
class SystemDie extends Die
{
	private final DiceConfig dc;

	/** Constructor for the SystemDie object
	 * @param modifier Modifier to each roll
	 */
	private SystemDie(final int modifier)
	{
		this.dc = new SystemDieConfig(1, 20, modifier, Die.random);
	}

	/**  Constructor for the SystemDie object */
	SystemDie()
	{
		this(0);
	}

	/** Roll the die.
	 * @return result from the roll
	 */
    @Override
	public int roll()
	{
		return dc.roll();
	}

	/** Name of the die in nds+m form
	 * @return Name of the die
	 */
	@Override
	public String toString()
	{
		return dc.toFormula();
	}
}
