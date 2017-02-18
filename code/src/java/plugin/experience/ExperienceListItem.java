/*
 * Copyright 2003 (C) Devon Jones
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
 */
 package plugin.experience;

import gmgen.plugin.Combatant;

/**
 * The {@code ExperienceAdjusterController} handles the functionality of
 * the Adjusting of experience.  This class is called by the {@code GMGenSystem
 * } and will have it's own model and view.<br>
 */
public class ExperienceListItem
{
	/** Combatant for the List Item */
	protected Combatant cbt;

	/**
	 * Creates a new instance of ExperienceListItem taking in a
	 * Combatant
	 *@param cbt Combatant this item represents
	 */
	public ExperienceListItem(Combatant cbt)
	{
		this.cbt = cbt;
	}

	/**
	 * Get combatant
	 * @return combatant
	 */
	public Combatant getCombatant()
	{
		return cbt;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(cbt.getName() + " (" + cbt.getCR() + ") ");

		if (cbt.getXP() != 0)
		{
			sb.append(cbt.getXP());
		}

		return sb.toString();
	}
}
