package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with SKILLCOST_CLASS Token
 */
public class CRStepsToken implements GameModeLstToken
{

    @Override
	public String getTokenName()
	{
		return "CRSTEPS";
	}

    @Override
	public boolean parse(GameMode gameMode, String value, URI source)
	{
		gameMode.setCRSteps(value);
		return true;
	}
}
