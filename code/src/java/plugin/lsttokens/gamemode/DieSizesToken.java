package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;



public class DieSizesToken implements GameModeLstToken
{

    @Override
    public boolean parse(final GameMode gameMode, final String value, final URI source)
	{
		gameMode.setDieSizes(value);
		return true;
	}

    @Override
	public String getTokenName()
	{
		return "DIESIZES";
	}

}
