package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

/**
 * Class deals with REQSKILL Token
 */
public class ReqskillToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "REQSKILL";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.addLine("REQSKILL:" + value);
		String[] names = value.split("\\|");
		for (int i = 0; i < names.length; i++)
		{
			campaign.getReqSkillFiles().add(names[i]);
		}
		return true;
	}
}
