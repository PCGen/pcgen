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
 */
package gmgen.plugin;

import gmgen.plugin.dice.Dice;

/**
 * Deals with the initiative part of the GMGen plugin 
 */
public class SystemInitiative
{
	protected Dice die;
	protected SystemAttribute attribute;
	int incrementalBonus;
	private int currentInitiative = 0;
	protected int mod = 0;
	protected int roll;

	/**
	 * Constructor
	 * @param attribute
	 * @param bonus
	 */
	public SystemInitiative(SystemAttribute attribute, int bonus)
	{
		this.attribute = attribute;
		this.incrementalBonus = bonus;
		die = new Dice(1, 20);
	}

	/**
	 * Constructor
	 * @param bonus
	 */
	SystemInitiative(int bonus)
	{
		this(new SystemAttribute("Attribute", 10), bonus);
	}

	/**
	 * Constructor
	 */
	public SystemInitiative()
	{
		this(new SystemAttribute("Attribute", 10), 0);
	}

	/**
	 * Set the attribute
	 * @param attribute
	 */
	public void setAttribute(final SystemAttribute attribute)
	{
		this.attribute = attribute;
	}

	/**
	 * Get the attribute
	 * @return the attribute
	 */
	public SystemAttribute getAttribute()
	{
		return attribute;
	}

	/**
	 * Set the bonus for the initiative
	 * @param bonus
	 */
	public void setBonus(int bonus)
	{
		this.incrementalBonus = bonus - attribute.getModifier();
		if (currentInitiative > 0)
		{
			setCurrentInitiative(roll + getModifier() + mod);
		}
	}

	/**
	 * Get the bonus to the initiative
	 * @return the bonus to the initiative
	 */
	public int getBonus()
	{
		return incrementalBonus;
	}

	/**
	 * Reset the current initiative to 0
	 */
	void resetCurrentInitiative()
	{
		currentInitiative = 0;
	}

	/**
	 * Set the current initiative
	 * @param currentInitiative
	 */
	public void setCurrentInitiative(final int currentInitiative)
	{
		this.currentInitiative = (currentInitiative >= 1) ? currentInitiative : 1;
	}

	/**
	 * Get the current initiative
	 * @return the current initiative
	 */
	public int getCurrentInitiative()
	{
		return currentInitiative;
	}

	/**
	 * Get the modifier for the initiative
	 * @return the modifier for the initiative
	 */
	public int getModifier()
	{
		return attribute.getModifier() + incrementalBonus;
	}

	/**
	 * Set the new curent initiative and return it
	 * @param modifier
	 * @return the new curent initiative
	 */
	public int check(final int modifier)
	{
		roll = die.roll();
		this.mod = modifier;
		setCurrentInitiative(roll + getModifier() + modifier);

		return currentInitiative;
	}

	/**
	 * Set the new curent initiative and return it
	 * @param aRoll
	 * @return the new curent initiative
	 */
	int checkExtRoll(int aRoll)
	{
		return checkExtRoll(aRoll, 0);
	}

	/**
	 * Set the new curent initiative and return it
	 * @param aRoll
	 * @param modifier
	 * @return the new curent initiative
	 */
	private int checkExtRoll(int aRoll, int modifier)
	{
		this.roll = aRoll;
		this.mod = modifier;
		setCurrentInitiative(aRoll + getModifier() + modifier);

		return currentInitiative;
	}

	/**
	 * Refocus with no modifier
	 * @return current initiative
	 */
	public int refocus()
	{
		return checkExtRoll(20, 0);
	}

}
