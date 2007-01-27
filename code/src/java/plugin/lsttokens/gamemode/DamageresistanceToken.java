package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with DAMAGERESISTANCE Token
 */
public class DamageresistanceToken implements GameModeLstToken
{

	public String getTokenName()
	{
		return "DAMAGERESISTANCE";
	}

	public boolean parse(GameMode gameMode, String value, URI source)
	{
		gameMode.setDamageResistanceText(value);
		return true;
	}
}
