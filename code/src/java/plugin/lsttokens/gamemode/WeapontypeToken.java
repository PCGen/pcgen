package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with SKILLCOST_CLASS Token
 */
public class WeapontypeToken implements GameModeLstToken
{

	public String getTokenName()
	{
		return "WEAPONTYPE";
	}

	public boolean parse(GameMode gameMode, String value)
	{
		gameMode.addWeaponType(value);
		return true;
	}
}
