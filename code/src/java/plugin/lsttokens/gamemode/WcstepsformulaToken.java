package plugin.lsttokens.gamemode;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with WCSTEPSFORMULA Token
 */
public class WcstepsformulaToken implements GameModeLstToken
{

	public String getTokenName()
	{
		return "WCSTEPSFORMULA";
	}

	public boolean parse(GameMode gameMode, String value)
	{
		gameMode.setWCStepsFormula(value);
		return true;
	}
}
