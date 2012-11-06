/*
 * This file is Open Game Content, covered by the OGL.
 */
package gmgen.plugin;

import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.StatAnalysis;

public class PcgSystemInitiative extends SystemInitiative
{
	protected PlayerCharacter pc;

	public PcgSystemInitiative(PlayerCharacter pc)
	{
		this.pc = pc;
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
		this.bonus = bonus - pc.initiativeMod();
		setCurrentInitiative(roll + getModifier() + mod);
	}

    @Override
	public int getBonus()
	{
		PCStat dex = Globals.getContext().ref.getAbbreviatedObject(
				PCStat.class, "DEX");
		return pc.initiativeMod() - StatAnalysis.getStatModFor(pc, dex) + bonus;
	}

    @Override
	public int getModifier()
	{
		return pc.initiativeMod() + bonus;
	}
}
