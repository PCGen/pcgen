package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with BONUS_ACTOUCH Token
 */
public class Bonus_actouchToken implements GameModeLstToken
{

	public String getTokenName()
	{
		return "BONUS_ACTOUCH";
	}

	public boolean parse(GameMode gameMode, String value, URI source)
	{
		gameMode.setAcTouchBonus(value);
		return true;
	}
}
