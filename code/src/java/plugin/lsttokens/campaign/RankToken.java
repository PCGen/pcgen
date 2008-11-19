package plugin.lsttokens.campaign;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with RANK Token
 */
public class RankToken implements CDOMPrimaryToken<Campaign>
{

	public String getTokenName()
	{
		return "RANK";
	}

	public boolean parse(LoadContext context, Campaign obj, String value)
		throws PersistenceLayerException
	{
		try
		{
			context.obj.put(obj, IntegerKey.CAMPAIGN_RANK, Integer
				.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.log(Logging.LST_ERROR, "Bad " + getTokenName()
				+ " value, expected an integer: " + value);
		}
		return false;
	}

	public String[] unparse(LoadContext context, Campaign obj)
	{
		Integer rank =
				context.getObjectContext().getInteger(obj,
					IntegerKey.CAMPAIGN_RANK);
		if (rank == null)
		{
			return null;
		}
		return new String[]{rank.toString()};
	}

	public Class<Campaign> getTokenClass()
	{
		return Campaign.class;
	}

}
