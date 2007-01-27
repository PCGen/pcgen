package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

/**
 * Class deals with CLASSSPELL Token
 */
public class ClassspellToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "CLASSSPELL";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.addLine("CLASSSPELL:" + value);
		campaign.addClassSpellFile(CampaignSourceEntry.getNewCSE(campaign,
				sourceUri, value));
		return true;
	}
}
