package pcgen.core.analysis;

import java.util.List;

import pcgen.core.PCStat;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;

public class BonusCalc
{

	/**
	 * gets the bonuses to a stat based on the stat Index
	 * @param statIdx
	 * @param aPC
	 * @return stat mod
	 */
	public static int getStatMod(PObject po, final int statIdx, final PlayerCharacter aPC)
	{
		final List<PCStat> statList = SettingsHandler.getGame().getUnmodifiableStatList();
	
		if ((statIdx < 0) || (statIdx >= statList.size()))
		{
			return 0;
		}
	
		final String aStat = statList.get(statIdx).getAbb();
	
		return (int) po.bonusTo("STAT", aStat, aPC, aPC);
	}

}
