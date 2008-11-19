package plugin.lsttokens.campaign;

import java.net.URI;
import java.util.Collection;
import java.util.TreeSet;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with LICENSE Token
 */
public class LicenseToken extends AbstractToken implements
		CDOMPrimaryToken<Campaign>
{

	public String getTokenName()
	{
		return "LICENSE";
	}

	public boolean parse(LoadContext context, Campaign obj, String value)
		throws PersistenceLayerException
	{
		if (isEmpty(value))
		{
			return false;
		}
		if (value.startsWith("FILE="))
		{
			URI uri = context.getPathURI(value.substring(5));
			if (uri == null)
			{
				//Error
				return false;
			}
			context.obj.addToList(obj, ListKey.LICENSE_FILE, uri);
		}
		else
		{
			context.obj.addToList(obj, ListKey.LICENSE, value);
		}
		return true;
	}

	public String[] unparse(LoadContext context, Campaign obj)
	{
		Changes<String> changes =
				context.getObjectContext().getListChanges(obj, ListKey.LICENSE);
		Changes<URI> filechanges =
				context.getObjectContext().getListChanges(obj,
					ListKey.LICENSE_FILE);
		TreeSet<String> set = new TreeSet<String>();
		Collection<String> added = changes.getAdded();
		if (added != null && !added.isEmpty())
		{
			set.addAll(added);
		}
		Collection<URI> addeduri = filechanges.getAdded();
		if (addeduri != null && !addeduri.isEmpty())
		{
			for (URI uri : addeduri)
			{
				set.add("FILE=" + uri);
			}
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
