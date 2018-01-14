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
package gmgen.plugin;

import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.display.CharacterDisplay;

import gmgen.plugin.dice.Dice;

public class PcgSystemInitiative extends SystemInitiative
{
	protected final PlayerCharacter pc;
	private final CharacterDisplay display;

	public PcgSystemInitiative(PlayerCharacter pc)
	{
		this.pc = pc;
		display = pc.getDisplay();
		PCStat stat = Globals.getContext().getReferenceContext()
				.silentlyGetConstructedCDOMObject(PCStat.class, "DEX");
		this.attribute = new SystemAttribute("Dexterity", pc.getTotalStatFor(stat));
		incrementalBonus = 0;
		die = new Dice(1, 20);
	}

    @Override
	public SystemAttribute getAttribute()
	{
		PCStat stat = Globals.getContext().getReferenceContext()
				.silentlyGetConstructedCDOMObject(PCStat.class, "DEX");
		return new SystemAttribute("Dexterity", pc.getTotalStatFor(stat));
	}

    @Override
	public void setBonus(int bonus)
	{
		String initiativeVar = ControlUtilities
			.getControlToken(Globals.getContext(), CControl.INITIATIVE);
		if (initiativeVar == null)
		{
			this.incrementalBonus = bonus - display.processOldInitiativeMod();
		}
		else
		{
			this.incrementalBonus =
					bonus - ((Number) pc.getGlobal(initiativeVar)).intValue();
		}
		setCurrentInitiative(roll + getModifier() + mod);
	}

    @Override
	public int getBonus()
	{
		String initiativeVar = ControlUtilities
			.getControlToken(Globals.getContext(), CControl.INITIATIVE);
		String initiativeStatVar = ControlUtilities
			.getControlToken(Globals.getContext(), CControl.INITIATIVESTAT);
		if (initiativeVar == null)
		{
			PCStat dex = Globals.getContext().getReferenceContext()
				.silentlyGetConstructedCDOMObject(PCStat.class, "DEX");
			return display.processOldInitiativeMod() - pc.getStatModFor(dex)
				+ incrementalBonus;
		}
		return ((Number) pc.getGlobal(initiativeVar)).intValue()
			- ((Number) pc.getGlobal(initiativeStatVar)).intValue()
			+ incrementalBonus;
	}

    @Override
	public int getModifier()
	{
		String initiativeVar = ControlUtilities
			.getControlToken(Globals.getContext(), CControl.INITIATIVE);
		if (initiativeVar == null)
		{
			return pc.getDisplay().processOldInitiativeMod() + incrementalBonus;
		}
		return ((Number) pc.getGlobal(initiativeVar)).intValue()
			+ incrementalBonus;
	}
}
