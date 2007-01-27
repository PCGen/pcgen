package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

/**
 * Class deals with BOOKTYPE Token
 */
public class BooktypeToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "BOOKTYPE";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.setBookType(value);
		return true;
	}
}
