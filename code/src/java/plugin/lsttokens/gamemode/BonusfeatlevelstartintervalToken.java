package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with BONUSFEATLEVELSTARTINTERVAL Token
 */
public class BonusfeatlevelstartintervalToken implements GameModeLstToken
{

	public String getTokenName()
	{
		return "BONUSFEATLEVELSTARTINTERVAL";
	}

	public boolean parse(GameMode gameMode, String value)
	{
		gameMode.setBonusFeatLevels(value);
		return true;
	}
}
