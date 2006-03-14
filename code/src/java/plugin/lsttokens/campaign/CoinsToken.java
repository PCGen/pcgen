package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

import java.net.URL;

/**
 * Class deals with COINS Token
 */
public class CoinsToken implements CampaignLstToken {

	public String getTokenName() {
		return "COINS";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl) {
		campaign.addLine("COINS:" + value);
		campaign.addCoinFile(new CampaignSourceEntry(campaign, CampaignLoader.convertFilePath(sourceUrl, value)));
		return true;
	}
}
