package plugin.lsttokens.campaign;

import java.net.URI;
import java.util.Collection;
import java.util.TreeSet;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.InstallLstToken;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with COPYRIGHT Token
 */
public class CopyrightToken extends AbstractToken implements
		CDOMPrimaryToken<Campaign>, InstallLstToken
{

	public String getTokenName()
	{
		return "COPYRIGHT";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.addToListFor(ListKey.SECTION_15, value);
		return true;
	}

	public boolean parse(LoadContext context, Campaign obj, String value)
		throws PersistenceLayerException
	{
		if (isEmpty(value))
		{
			return false;
		}
		context.obj.addToList(obj, ListKey.SECTION_15, value);
		return true;
	}

	public String[] unparse(LoadContext context, Campaign obj)
	{
		Changes<String> changes =
				context.getObjectContext().getListChanges(obj,
					ListKey.SECTION_15);
		TreeSet<String> set = new TreeSet<String>();
		Collection<String> added = changes.getAdded();
		if (added != null && !added.isEmpty())
		{
			set.addAll(added);
		}
		if (set.isEmpty())
		{
			context.addWriteMessage(getTokenName()
				+ " was expecting non-empty changes to include "
				+ "added items or global clear");
			return null;
		}
		return set.toArray(new String[set.size()]);
	}

	public Class<Campaign> getTokenClass()
	{
		return Campaign.class;
	}

}
