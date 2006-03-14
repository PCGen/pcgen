package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

import java.net.URL;

/**
 * Class deals with CLASSSKILL Token
 */
public class ClassskillToken implements CampaignLstToken {

	public String getTokenName() {
		return "CLASSSKILL";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl) {
		campaign.addLine("CLASSSKILL:" + value);
		campaign.addClassSkillFile(new CampaignSourceEntry(campaign, CampaignLoader.convertFilePath(sourceUrl, value)));
		return true;
	}
}
