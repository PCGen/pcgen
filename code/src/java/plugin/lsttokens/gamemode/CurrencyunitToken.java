package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with CURRENCYUNIT Token
 */
public class CurrencyunitToken implements GameModeLstToken
{

	@Override
	public String getTokenName()
	{
		return "CURRENCYUNIT";
	}

	@Override
	public boolean parse(GameMode gameMode, String value, URI source)
	{
		gameMode.setCurrencyUnit(value);
		return true;
	}
}
