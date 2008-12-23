package plugin.lsttokens.campaign;

import java.net.URI;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with PCC Token
 */
public class PccToken extends AbstractToken implements
		CDOMPrimaryToken<Campaign>
{

	@Override
	public String getTokenName()
	{
		return "PCC";
	}

	public boolean parse(LoadContext context, Campaign obj, String value)
			throws PersistenceLayerException
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}
		URI uri = context.getPathURI(value);
		if (uri == null)
		{
			// Error
			return false;
		}
		context.obj.addToList(obj, ListKey.FILE_PCC, uri);
		return true;
	}

	public String[] unparse(LoadContext context, Campaign obj)
	{
		Changes<URI> cseChanges = context.obj.getListChanges(obj,
				ListKey.FILE_PCC);
		Collection<URI> added = cseChanges.getAdded();
		if (added == null)
		{
			// empty indicates no token
			return null;
		}
		Set<String> set = new TreeSet<String>();
		for (URI uri : added)
		{
			set.add(uri.toString());
		}
		return set.toArray(new String[set.size()]);
	}

	public Class<Campaign> getTokenClass()
	{
		return Campaign.class;
	}
}
