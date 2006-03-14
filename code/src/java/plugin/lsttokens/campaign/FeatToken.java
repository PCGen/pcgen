package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

import java.net.URL;

/**
 * Class deals with FEAT Token
 */
public class FeatToken implements CampaignLstToken {

	public String getTokenName() {
		return "FEAT";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl) {
		campaign.addLine("FEAT:" + value);
		campaign.addFeatFile(new CampaignSourceEntry(campaign, CampaignLoader.convertFilePath(sourceUrl, value)));
		return true;
	}
}
