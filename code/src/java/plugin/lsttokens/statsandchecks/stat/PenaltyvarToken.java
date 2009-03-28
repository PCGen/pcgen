package plugin.lsttokens.statsandchecks.stat;

import pcgen.core.PCStat;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with PENALTYVAR Token
 */
public class PenaltyvarToken implements CDOMPrimaryToken<PCStat>
{

	public String getTokenName()
	{
		return "PENALTYVAR";
	}

	public boolean parse(LoadContext context, PCStat obj, String value)
			throws PersistenceLayerException
	{
		// TODO Need to figure out what to do here...
		return true;
	}

	public String[] unparse(LoadContext context, PCStat obj)
	{
		// TODO Need to figure out what to do here...
		return null;
	}

	public Class<PCStat> getTokenClass()
	{
		return PCStat.class;
	}
}
