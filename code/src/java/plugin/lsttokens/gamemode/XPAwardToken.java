package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with SKILLCOST_CLASS Token
 */
public class XPAwardToken implements GameModeLstToken
{

    @Override
	public String getTokenName()
	{
		return "XPAWARD";
	}

    @Override
	public boolean parse(GameMode gameMode, String value, URI source)
	{
		gameMode.setXPAwards(value);
		return true;
	}
}
