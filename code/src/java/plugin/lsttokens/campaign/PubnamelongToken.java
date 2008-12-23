package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.persistence.lst.InstallLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with PUBNAMELONG Token
 */
public class PubnamelongToken extends AbstractToken implements
		CDOMPrimaryToken<Campaign>, InstallLstToken
{

	@Override
	public String getTokenName()
	{
		return "PUBNAMELONG";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.put(StringKey.PUB_NAME_LONG, value);
		return true;
	}

	public boolean parse(LoadContext context, Campaign campaign, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		context.getObjectContext()
				.put(campaign, StringKey.PUB_NAME_LONG, value);
		return true;
	}

	public String[] unparse(LoadContext context, Campaign campaign)
	{
		String title = context.getObjectContext().getString(campaign,
				StringKey.PUB_NAME_LONG);
		if (title == null)
		{
			return null;
		}
		return new String[] { title };
	}

	public Class<Campaign> getTokenClass()
	{
		return Campaign.class;
	}
}
