package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.core.GameMode;
import pcgen.persistence.lst.EqSizePenaltyLoader;
import pcgen.persistence.lst.GameModeLstToken;

/**
 * Class deals with EQSIZEPENALTY Token
 */
public class EqsizepenaltyToken implements GameModeLstToken
{

	public String getTokenName()
	{
		return "EQSIZEPENALTY";
	}

	public boolean parse(GameMode gameMode, String value, URI source)
	{
		try
		{
			EqSizePenaltyLoader penaltyDiceLoader = new EqSizePenaltyLoader();
			penaltyDiceLoader.parseLine(gameMode, "EQSIZEPENALTY:" + value, source);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
}
