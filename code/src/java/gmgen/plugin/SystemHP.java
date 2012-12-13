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
 *  SystemHP.java
 */
package gmgen.plugin;

import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.analysis.StatAnalysis;

/**
 * Deals with the HP part of the gmgen plugin system
 */
public class SystemHP
{
	private String state = "";
	private SystemAttribute attribute;
	private boolean firstround;
	private int current;
	private int max;

	private int subdual;

	/**
	 * Constructor
	 * @param attribute
	 * @param hpmax
	 * @param current
	 */
	public SystemHP(SystemAttribute attribute, int hpmax, int current)
	{
		this.attribute = attribute;
		this.max = hpmax;
		this.current = current;
	}

	/**
	 * Constructor
	 * @param attribute
	 * @param hpmax
	 */
	public SystemHP(SystemAttribute attribute, int hpmax)
	{
		this(attribute, hpmax, hpmax);
	}

	/**
	 * Constructor
	 * @param hpmax
	 */
	public SystemHP(int hpmax)
	{
		this(new SystemAttribute("Attribute", 10), hpmax, hpmax);
	}

	/**
	 * Set Attribute
	 * @param attribute
	 */
	public void setAttribute(SystemAttribute attribute)
	{
		this.attribute = attribute;
	}

	/**
	 * Get attribute
	 * @return attribute
	 */
	public SystemAttribute getAttribute()
	{
		return attribute;
	}

	/**
	 * Set the current HP
	 * @param current
	 */
	public void setCurrent(int current)
	{
		int currentHP = current;
		
		if (currentHP > max)
		{
			currentHP = max;
		}

		if (currentHP > this.current)
		{
			heal(currentHP - this.current);
		}
		else if (currentHP < this.current)
		{
			damage(this.current - currentHP);
		}
	}

	/**
	 * Get the current HP
	 * @return the current HP
	 */
	public int getCurrent()
	{
		return current;
	}

	/**
	 * Returns true if the damage done is Massive damage under the 
	 * d20 Modern system
	 * 
	 * @param cbt
	 * @param damage
	 * @return true if the damage done is Massive damage under the 
	 * d20 Modern system
	 */
	public static boolean isD20ModernMassive(Combatant cbt, int damage)
	{
		if (cbt instanceof PcgCombatant)
		{
			PcgCombatant pcgcbt = (PcgCombatant) cbt;
			PlayerCharacter pc = pcgcbt.getPC();

			PCStat stat = Globals.getContext().ref
					.getAbbreviatedObject(PCStat.class, "CON");
			if (damage > StatAnalysis.getTotalStatFor(pc, stat))
			{
				return true;
			}
		}
		else
		{
			if (damage > cbt.getHP().getAttribute().getValue())
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns true if the damage done is Massive damage under the 
	 * d20 DnD system
	 * 
	 * @param cbt
	 * @param damage
	 * @return true if the damage done is Massive damage under the 
	 * d20 DnD system
	 */
	public static boolean isDndMassive(Combatant cbt, int damage)
	{
		int damageThreshold = 50;

		if (SettingsHandler.getGMGenOption("Initiative.Damage.Massive.SizeMod", true))
		{
			if (cbt instanceof PcgCombatant)
			{
				PcgCombatant pcgcbt = (PcgCombatant) cbt;
				PlayerCharacter pc = pcgcbt.getPC();
				String size = pc.getDisplay().getSize();

				//FIX: This needs to be moved to pcgen's sizeAdjustment.lst
				if (size.equals("Fine"))
				{
					damageThreshold = 10;
				}

				if (size.equals("Diminutive"))
				{
					damageThreshold = 20;
				}

				if (size.equals("Tiny"))
				{
					damageThreshold = 30;
				}

				if (size.equals("Small"))
				{
					damageThreshold = 40;
				}

				//Medium 50
				if (size.equals("Large"))
				{
					damageThreshold = 60;
				}

				if (size.equals("Huge"))
				{
					damageThreshold = 70;
				}

				if (size.equals("Gargantuan"))
				{
					damageThreshold = 80;
				}

				if (size.equals("Colossal"))
				{
					damageThreshold = 90;
				}
			}
		}

		if (damage >= damageThreshold)
		{
			return true;
		}

		return false;
	}

	/**
	 * Returns true if the damage done is Massive damage under the 
	 * the 1/2 of max hitpoints house rule
	 * 
	 * @param cbt
	 * @param damage
	 * @return true if the damage done is Massive damage under the 
	 * the 1/2 of max hitpoints house rule
	 */
	public static boolean isHouseHalfMassive(Combatant cbt, int damage)
	{
		SystemHP hp = cbt.getHP();

		if (damage > hp.getMax())
		{
			return true;
		}

		return false;
	}

	/**
	 * Set the max HP
	 * @param hpmax
	 */
	public void setMax(int hpmax)
	{
		this.max = hpmax;

		if (max > current)
		{
			current = max;
		}
	}

	/**
	 * Get the max HP
	 * @return max HP
	 */
	public int getMax()
	{
		return max;
	}

	/**
	 * Set the state
	 * @param state
	 */
	public void setState(String state)
	{
		this.state = state;
	}

	/**
	 * Get the state
	 * @return state
	 */
	public String getState()
	{
		return state;
	}

	/**
	 * Set the subdual damage
	 * @param subdual
	 */
	public void setSubdual(int subdual)
	{
		subdualDamage(subdual - getSubdual());
	}

	/**
	 * Get the subdual damage
	 * @return subdual damage
	 */
	public int getSubdual()
	{
		return subdual;
	}

	/**
	 * Apply damage because of the Bleeding state
	 * @return the state
	 */
	public String bleed()
	{
		if (state.equals("Bleeding") && !firstround)
		{
			damage(1);
		}

		return state;
	}

	/**
	 * Apply damage
	 * @param damage
	 * @return state
	 */
	public String damage(int damage)
	{
		if ((current > -1) && ((current - damage) < 0))
		{
			boolean dyingStart = SettingsHandler.getGMGenOption("Initiative.Damage.Dying.Start", true);

			if (!dyingStart)
			{
				firstround = true;
			}
		}

		current -= damage;

		//TODO: Make it so that we can use static finals from somewhere here
		//      and not "1" or "2"
		//		Should we also set up static constants for "Initiative.Damage.Death" . . .
		int disabledType =
			SettingsHandler.getGMGenOption(
				"Initiative.Damage.Disabled",
				1);
		
		int disabledLowRange = 0;
		if (disabledType == 2)
		{
			disabledLowRange = -1 * Math.max(0,attribute.getModifier());
		}
		
		if (current <= 0 && current >= disabledLowRange)
		{
			state = "Disabled";
		}
		else if (current < disabledLowRange)
		{
			state = "Bleeding";
		}

		//TODO: Make it so that we can use static finals from somewhere here
		//      and not "1" or "2"
		//		Should we also set up static constants for "Initiative.Damage.Death" . . .
		int deathType =
			SettingsHandler.getGMGenOption(
				"Initiative.Damage.Death",
				1);

		if (deathType == 1)
		{
			if (current <= -10)
			{
				state = "Dead";
				current = 0;
			}
		}
		else if (deathType == 2)
		{
			if (current <= (-1 * attribute.getValue()))
			{
				state = "Dead";
				current = 0;
			}
		}

		checkSubdual();

		return state;
	}

	/**
	 * End status that has a duration, e.g. Dazed
	 * @return state
	 */
	public String endDurationedStatus()
	{
		if (state.equals("Unconsious") || state.equals("Dazed"))
		{
			state = "";
		}

		return state;
	}

	/**
	 * End the round
	 */
	public void endRound()
	{
		firstround = false;
	}

	/**
	 * Heal 
	 * @param heal
	 * @return the state
	 */
	public String heal(int heal)
	{
		if (!state.equals("Dead"))
		{
			current += heal;
			subdual -= heal;

			if (current > max)
			{
				current = max;
			}

			if (subdual < 0)
			{
				subdual = 0;
			}

			if (state.equals("Bleeding"))
			{
				state = "Stable";
			}

			if (current > 0)
			{
				state = "";
			}

			checkSubdual();
		}

		return state;
	}

	/**
	 * Kill the PC
	 * @return the state
	 */
	public String kill()
	{
		state = "Dead";
		current = 0;

		return state;
	}

	/**
	 * Apply non lethal damage
	 * @param type
	 * @return the state
	 */
	public String nonLethalDamage(boolean type)
	{
		if (state.equals(""))
		{
			if (type)
			{
				state = "Unconsious";
			}
			else
			{
				state = "Dazed";
			}
		}

		return state;
	}

	/**
	 * Raise the PC from the dead
	 * @return the state
	 */
	public String raise()
	{
		if (state.equals("Dead"))
		{
			state = "";
			current = 1;
		}

		return state;
	}

	/**
	 * Stabalize a bleeding PC
	 * @return the state
	 */
	public String stabilize()
	{
		if (state.equals("Bleeding"))
		{
			state = "Stable";
		}

		return state;
	}

	/**
	 * Apply subdual damage
	 * @param damage
	 * @return the state
	 */
	public String subdualDamage(int damage)
	{
		subdual += damage;

		return checkSubdual();
	}

	private String checkSubdual()
	{
		//TODO: Make it so that we can use static finals from somewhere here
		//      and not "1" or "2"
		//		Should we also set up static constants for "Initiative.Damage.Death" . . .
		int disabledType =
			SettingsHandler.getGMGenOption(
				"Initiative.Damage.Disabled",
				1);
		
		int disabledBonus = 0;
		if (disabledType == 2)
		{
			disabledBonus = Math.max(0,attribute.getModifier());
		}
		
		if ((state.equals("") || state.equals("Staggered") || state.equals("Unconsious")) && (subdual > 0))
		{
			if (subdual >= current && subdual <= (current + disabledBonus))
			{
				state = "Staggered";
			}
			else if (subdual > (current + disabledBonus))
			{
				state = "Unconsious";
			}
		}

		return state;
	}
}
