package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

import java.net.URL;

/**
 * Class deals with DEITY Token
 */
public class DeityToken implements CampaignLstToken {

	public String getTokenName() {
		return "DEITY";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl) {
		campaign.addLine("DEITY:" + value);
		campaign.addDeityFile(new CampaignSourceEntry(campaign, CampaignLoader.convertFilePath(sourceUrl, value)));
		return true;
	}
}

