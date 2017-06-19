package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.GameModeLstToken;



public class DieSizesToken implements GameModeLstToken
{

	/**
	 * @see pcgen.persistence.lst.GameModeLstToken#parse(pcgen.core.GameMode, java.lang.String, java.net.URI)
	 */
    @Override
    public boolean parse(final GameMode gameMode, final String value, final URI source)
	{
		gameMode.setDieSizes(value);
		return true;
	}

	/**
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
    @Override
	public String getTokenName()
	{
		return "DIESIZES";
	}

}
