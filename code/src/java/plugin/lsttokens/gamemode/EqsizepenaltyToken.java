package plugin.lsttokens.gamemode;

import java.net.URI;

import pcgen.cdom.inst.EqSizePenalty;
import pcgen.core.GameMode;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GameModeLstToken;
import pcgen.persistence.lst.SimpleLoader;
import pcgen.util.Logging;

/**
 * Class deals with EQSIZEPENALTY Token
 */
public class EqsizepenaltyToken implements GameModeLstToken
{

	@Override
	public String getTokenName()
	{
		return "EQSIZEPENALTY";
	}

	@Override
	public boolean parse(GameMode gameMode, String value, URI source)
	{
		try
		{
			SimpleLoader<EqSizePenalty> penaltyDiceLoader = new SimpleLoader<>(EqSizePenalty.class);
			penaltyDiceLoader.parseLine(gameMode.getModeContext(), value, source);
			return true;
		}
		catch (PersistenceLayerException e)
		{
			Logging.errorPrint(e.getMessage());
			return false;
		}
	}
}
