package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with MENUTOOLTIP Token
 */
public class MenutooltipToken implements GameModeLstToken
{

	@Override
	public String getTokenName()
	{
		return "MENUTOOLTIP";
	}

	@Override
	public boolean parse(GameMode gameMode, String value, URI source)
	{
		return true;
	}
}
