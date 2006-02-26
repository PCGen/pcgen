package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

import java.net.URL;

/**
 * Class deals with LANGUAGE Token
 */
public class LanguageToken implements CampaignLstToken {

	public String getTokenName() {
		return "LANGUAGE";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl) {
		campaign.addLine("LANGUAGE:" + value);
		campaign.addLanguageFile(new CampaignSourceEntry(campaign, CampaignLoader.convertFilePath(sourceUrl, value)));
		return true;
	}
}
