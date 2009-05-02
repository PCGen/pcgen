package pcgen.core.analysis;

import pcgen.core.PCStat;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

public class BonusCalc
{

	/**
	 * gets the bonuses to a stat based on the stat Index
	 * @param statIdx
	 * @param aPC
	 * @return stat mod
	 */
	public static int getStatMod(PObject po, PCStat stat, final PlayerCharacter aPC)
	{
		return (int) po.bonusTo("STAT", stat.getAbb(), aPC, aPC);
	}

}
