package plugin.lsttokens.campaign;

import java.util.Collection;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with LSTEXCLUDE Token
 */
public class LstexcludeToken extends AbstractToken implements
		CDOMPrimaryToken<Campaign>
{

	public String getTokenName()
	{
		return "LSTEXCLUDE";
	}

	public boolean parse(LoadContext context, Campaign obj, String value)
		throws PersistenceLayerException
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}
		final StringTokenizer lstTok = new StringTokenizer(value, "|");

		while (lstTok.hasMoreTokens())
		{
			final String lstFilename = lstTok.nextToken();
			CampaignSourceEntry cse =
					context.getCampaignSourceEntry(obj, lstFilename);
			if (cse == null)
			{
				//Error
				return false;
			}
			if (!cse.getIncludeItems().isEmpty())
			{
				Logging.errorPrint(getTokenName() + " does not allow INCLUDE: "
					+ value);
				return false;
			}
			if (!cse.getExcludeItems().isEmpty())
			{
				Logging.errorPrint(getTokenName() + " does not allow EXCLUDE: "
					+ value);
				return false;
			}
			context.obj.addToList(obj, ListKey.FILE_LST_EXCLUDE, cse);
		}

		return true;
	}

	public String[] unparse(LoadContext context, Campaign obj)
	{
		Changes<CampaignSourceEntry> cseChanges =
				context.obj.getListChanges(obj, ListKey.FILE_LST_EXCLUDE);
		Collection<CampaignSourceEntry> added = cseChanges.getAdded();
		if (added == null)
		{
			//empty indicates no token
			return null;
		}
		Set<String> set = new TreeSet<String>();
		for (CampaignSourceEntry cse : added)
		{
			set.add(cse.getLSTformat());
		}
		return set.toArray(new String[set.size()]);
	}

	public Class<Campaign> getTokenClass()
	{
		return Campaign.class;
	}

}
