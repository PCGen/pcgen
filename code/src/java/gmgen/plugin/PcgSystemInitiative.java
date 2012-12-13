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
 *  PcgSystemInitiative.java
 *
 *  Created on January 16, 2002, 12:27 PM
 */
package gmgen.plugin;

import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.StatAnalysis;
import pcgen.core.display.CharacterDisplay;

public class PcgSystemInitiative extends SystemInitiative
{
	protected final PlayerCharacter pc;
	private final CharacterDisplay display;

	public PcgSystemInitiative(PlayerCharacter pc)
	{
		this.pc = pc;
		display = pc.getDisplay();
		PCStat stat = Globals.getContext().ref
				.getAbbreviatedObject(PCStat.class, "DEX");
		this.attribute = new SystemAttribute("Dexterity", StatAnalysis.getTotalStatFor(pc, stat));
		bonus = 0;
		die = new Dice(1, 20);
	}

    @Override
	public SystemAttribute getAttribute()
	{
		PCStat stat = Globals.getContext().ref
				.getAbbreviatedObject(PCStat.class, "DEX");
		return new SystemAttribute("Dexterity", StatAnalysis.getTotalStatFor(pc, stat));
	}

    @Override
	public void setBonus(int bonus)
	{
		this.bonus = bonus - display.initiativeMod();
		setCurrentInitiative(roll + getModifier() + mod);
	}

    @Override
	public int getBonus()
	{
		PCStat dex = Globals.getContext().ref.getAbbreviatedObject(
				PCStat.class, "DEX");
		return display.initiativeMod() - StatAnalysis.getStatModFor(pc, dex) + bonus;
	}

    @Override
	public int getModifier()
	{
		return display.initiativeMod() + bonus;
	}
}
