package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

/**
 * Class deals with CLASSSKILL Token
 */
public class ClassskillToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "CLASSSKILL";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.addLine("CLASSSKILL:" + value);
		campaign.addClassSkillFile(CampaignSourceEntry.getNewCSE(campaign,
				sourceUri, value));
		return true;
	}
}
