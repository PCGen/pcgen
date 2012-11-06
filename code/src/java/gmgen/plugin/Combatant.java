/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2002 Devon D Jones
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  The author of this program grants you the ability to use this code
 *  in conjunction with code that is covered under the Open Gaming License
 *
 *  XMLCombatant.java
 *
 *  Created on January 24, 2002, 11:15 AM
 *
 *  This file is Open Game Content, covered by the OGL.
 */
package gmgen.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 *@author     devon
 *@since    March 20, 2003
 *@version $Revision$
 */
public abstract class Combatant implements InitHolder
{
	/*
	 *  History:
	 *  March 20, 2003: Cleanup for Version 1.0
	 */

	/**  The object that contains all initiative information */
	public SystemInitiative init;
	protected List<Effect> effects = new ArrayList<Effect>();
	protected String comType = "Enemy";
	protected String status = "";
	protected SystemHP hitPoints;
	protected int duration;
	protected int number;

	/**
	 *  Creates new Combatant
	 */
	public Combatant()
	{
		// Empty Constructor
	}

	/**
	 * Set CR
	 * @param cr
	 */
	public abstract void setCR(float cr);

	/**
	 * Get CR
	 * @return CR
	 */
	public abstract float getCR();

	/**
	 * Sets the Combatant Type.
	 * @param comType
	 */
	public void setCombatantType(String comType)
	{
		this.comType = comType;
	}

	/**
	 *  Gets the Combatant Type.
	 *
	 *@return    The status value
	 */
	public String getCombatantType()
	{
		return comType;
	}

	/**
	 * Set duration
	 * @param duration
	 */
	public void setDuration(int duration)
	{
		this.duration = duration;
	}

	/**
	 * Get duration
	 * @return duration
	 */
	public int getDuration()
	{
		return duration;
	}

	/**
	 * Get effects
	 * @return effects
	 */
	public List<Effect> getEffects()
	{
		return effects;
	}

	/**
	 *  Gets the SystemHP of the Combatant
	 *
	 *@return    The SystemHP value
	 */
	public SystemHP getHP()
	{
		return hitPoints;
	}

	/**
	 *  Gets the SystemInitiative of the Combatant
	 *
	 *@return    The SystemInitiative value
	 */
    @Override
	public SystemInitiative getInitiative()
	{
		return init;
	}

	/**
	 * Set name
	 * @param name
	 */
	public abstract void setName(String name);

	/**
	 *  Sets the number attribute of the XMLCombatant object
	 *
	 *@param  number  The new number value
	 */
	public void setNumber(int number)
	{
		this.number = number;
	}

	/**
	 *  Gets the number attribute of the XMLCombatant object
	 *
	 *@return    The number value
	 */
	public int getNumber()
	{
		return number;
	}

	/**
	 *  Creates a Vector intended for use as a row in a JTable
	 *
	 *@param  columnOrder  The current table's column order
	 *@return              The Row Vector
	 */
    @Override
	public Vector<String> getRowVector(List<String> columnOrder)
	{
		Vector<String> rowVector = new Vector<String>();

		//Iterate through all the columns, and create the vector in that order
		for ( String columnName : columnOrder )
		{
			if (columnName.equals("Name"))
			{ // Character's Name
				rowVector.add(getName());
			}
			else if (columnName.equals("Player"))
			{ // Player's Name
				rowVector.add(getPlayer());
			}
			else if (columnName.equals("Status"))
			{ // Status of XMLCombatant
				rowVector.add(getStatus());
			}
			else if (columnName.equals("+"))
			{ // Initiative bonus
				rowVector.add("" + init.getModifier());
			}
			else if (columnName.equals("Init"))
			{ // Initiative #
				rowVector.add("" + init.getCurrentInitiative());
			}
			else if (columnName.equals("Dur"))
			{ // Duration

				if (duration == 0)
				{
					rowVector.add("");
				}
				else
				{
					rowVector.add("" + getDuration());
				}
			}
			else if (columnName.equals("#"))
			{ // Number (for tokens)
				rowVector.add("" + number);
			}
			else if (columnName.equals("HP"))
			{ // Current Hit Points

				int hp = hitPoints.getCurrent();
				int sub = hitPoints.getSubdual();

				if (sub == 0)
				{
					rowVector.add("" + hp);
				}
				else if (sub > 0)
				{
					rowVector.add(hp + "/" + sub + "s");
				}
			}
			else if (columnName.equals("HP Max"))
			{ // Max Hit Points
				rowVector.add("" + hitPoints.getMax());
			}
			else if (columnName.equals("Type"))
			{ //PC, Enemy, Ally, Non-Com
				rowVector.add(comType);
			}
		}

		return rowVector;
	}

	/**
	 *  Sets the status of the Combatant
	 *
	 *@param  status  The new status value
	 */
    @Override
	public void setStatus(String status)
	{
		this.status = status;
	}

	/**
	 *  Gets the status of the Combatant
	 *
	 *@return    The status value
	 */
    @Override
	public String getStatus()
	{
		return status;
	}

	/**
	 * Set XP
	 * @param xp
	 */
	public abstract void setXP(int xp);

	/**
	 * Get XP
	 * @return XP
	 */
	public abstract int getXP();

	/**
	 * Add effect
	 * @param effect
	 */
	public void addEffect(Effect effect)
	{
		effects.add(effect);
	}

	/**  Causes the XMLCombatant to bleed for 1 point of damage */
	public void bleed()
	{
		setStatus(hitPoints.bleed());
	}

	/**
	 *  Does damage to the XMLCombatant
	 *
	 *@param  damage  number of points of damage to do
	 */
	public void damage(int damage)
	{
		setStatus(hitPoints.damage(damage));
	}

	/**
	 *  Decrements the duration
	 *
	 *@return    new duration
	 */
	public int decDuration()
	{
		if (duration > 0)
		{
			duration--;

			if (duration == 0)
			{
				setStatus(hitPoints.endDurationedStatus());
			}
		}

		return duration;
	}

    @Override
	public void endRound()
	{
		hitPoints.endRound();
	}

	/**
	 *  Heals the XMLCombatant
	 *
	 *@param  heal  amount of healing to do
	 */
	public void heal(int heal)
	{
		setStatus(hitPoints.heal(heal));
	}

	/**  Kills the XMLCombatant */
	public void kill()
	{
		setStatus(hitPoints.kill());
	}

	/**
	 * Does non lethal damage to the Combatant
	 *
	 * @param type
	 */
	public void nonLethalDamage(boolean type)
	{
		setStatus(hitPoints.nonLethalDamage(type));

		if (type)
		{
			setDuration(new Dice(4, 1).roll() + 1);
		}
		else
		{
			setDuration(1);
		}
	}

	/**  Raises a dead XMLCombatant */
	public void raise()
	{
		setStatus(hitPoints.raise());
	}

	/**  Stabilizes the XMLCombatant */
	public void stabilize()
	{
		setStatus(hitPoints.stabilize());
	}

	/**
	 *  Does subdual damage to the Combatant
	 *
	 *@param  damage  number of points of damage to do
	 */
	public void subdualDamage(int damage)
	{
		setStatus(hitPoints.subdualDamage(damage));
	}

	/**
	 * Returns a String representation of this combatant.
	 * @return String
	 */
	@Override
	public String toString()
	{
		return getName();
	}

	/**
	 * Return HTML String
	 * @return HTML String
	 */
	public abstract String toHtmlString();
}
