package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

import java.net.URL;

/**
 * Class deals with CLASSSPELL Token
 */
public class ClassspellToken implements CampaignLstToken {

	public String getTokenName() {
		return "CLASSSPELL";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl) {
		campaign.addLine("CLASSSPELL:" + value);
		campaign.addClassSpellFile(new CampaignSourceEntry(campaign, CampaignLoader.convertFilePath(sourceUrl, value)));
		return true;
	}
}
