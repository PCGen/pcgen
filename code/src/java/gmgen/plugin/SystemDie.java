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


import gmgen.plugin.dice.Die;
import gmgen.plugin.dice.SystemDieConfig;

/**
 * A d20 die, applies a +10 on a 20, and a -10 on a 1
 */
class SystemDie extends Die
{
	/** Constructor for the SystemDie object
	 * @param modifier Modifier to each roll
	 */
	SystemDie(final int modifier)
	{
		super(new SystemDieConfig(1, 20, modifier, Die.rand));
	}
}
