package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with WEAPONCATEGORY Token
 */
public class WeaponcategoryToken implements GameModeLstToken
{

	@Override
	public String getTokenName()
	{
		return "WEAPONCATEGORY";
	}

	@Override
	public boolean parse(GameMode gameMode, String value, URI source)
	{
		gameMode.addWeaponCategory(value);
		return true;
	}
}
