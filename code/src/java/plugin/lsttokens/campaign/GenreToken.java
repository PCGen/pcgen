package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

/**
 * Class deals with GENRE Token
 */
public class GenreToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "GENRE";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.setGenre(value);
		return true;
	}
}
