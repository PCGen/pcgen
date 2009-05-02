/*
 * This file is Open Game Content, covered by the OGL.
 */
package gmgen.plugin;

import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.StatList;

public class PcgSystemInitiative extends SystemInitiative
{
	protected PlayerCharacter pc;

	public PcgSystemInitiative(PlayerCharacter pc)
	{
		this.pc = pc;
		Globals.setCurrentPC(pc);

		StatList sl = pc.getStatList();
		PCStat stat = Globals.getContext().ref
				.getAbbreviatedObject(PCStat.class, "DEX");
		this.attribute = new SystemAttribute("Dexterity", sl.getTotalStatFor(stat));
		bonus = 0;
		die = new Dice(1, 20);
	}

	public SystemAttribute getAttribute()
	{
		Globals.setCurrentPC(pc);

		StatList sl = pc.getStatList();

		PCStat stat = Globals.getContext().ref
				.getAbbreviatedObject(PCStat.class, "DEX");
		return new SystemAttribute("Dexterity", sl.getTotalStatFor(stat));
	}

	public void setBonus(int bonus)
	{
		this.bonus = bonus - pc.initiativeMod();
		setCurrentInitiative(roll + getModifier() + mod);
	}

	public int getBonus()
	{
		Globals.setCurrentPC(pc);

		StatList sl = pc.getStatList();
		PCStat dex = Globals.getContext().ref.getAbbreviatedObject(
				PCStat.class, "DEX");
		return pc.initiativeMod() - sl.getStatModFor(dex) + bonus;
	}

	public int getModifier()
	{
		Globals.setCurrentPC(pc);

		return pc.initiativeMod() + bonus;
	}
}
