package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with WCSTEPSFORMULA Token
 */
public class WcstepsformulaToken implements GameModeLstToken
{

	@Override
	public String getTokenName()
	{
		return "WCSTEPSFORMULA";
	}

	@Override
	public boolean parse(GameMode gameMode, String value, URI source)
	{
		gameMode.setWCStepsFormula(value);
		return true;
	}
}
