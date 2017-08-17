package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with HPFORMULA Token
 */
public class HpformulaToken implements GameModeLstToken
{

    @Override
	public String getTokenName()
	{
		return "HPFORMULA";
	}

    @Override
	public boolean parse(GameMode gameMode, String value, URI source)
	{
		gameMode.setHPFormula(value);
		return true;
	}
}
