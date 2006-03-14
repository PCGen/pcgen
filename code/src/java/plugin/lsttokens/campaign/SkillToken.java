package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

import java.net.URL;

/**
 * Class deals with SKILL Token
 */
public class SkillToken implements CampaignLstToken {

	public String getTokenName() {
		return "SKILL";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl) {
		campaign.addLine("SKILL:" + value);
		campaign.addSkillFile(new CampaignSourceEntry(campaign, CampaignLoader.convertFilePath(sourceUrl, value)));
		return true;
	}
}
