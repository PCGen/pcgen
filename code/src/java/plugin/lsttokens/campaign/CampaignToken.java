package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.InstallLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with CAMPAIGN Token
 */
public class CampaignToken extends AbstractToken implements
		CDOMPrimaryToken<Campaign>, InstallLstToken
{

	@Override
	public String getTokenName()
	{
		return "CAMPAIGN";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.setName(value);
		return true;
	}

	public boolean parse(LoadContext context, Campaign campaign, String value)
			throws PersistenceLayerException
	{
		if (isEmpty(value))
		{
			return false;
		}
		/*
		 * TODO This violates the CDOM direct-access token rules, but given how
		 * complicated NAME is, this is much easier to cheat here, and fix
		 * later. This really has no major negative effect since campaigns can't
		 * be .MODed or .COPYed, so direct access isn't a critical problem. -
		 * thpr 12/23/08
		 */
		campaign.setName(value);
		return true;
	}

	public String[] unparse(LoadContext context, Campaign campaign)
	{
		return new String[] { campaign.getDisplayName() };
	}

	public Class<Campaign> getTokenClass()
	{
		return Campaign.class;
	}

}
