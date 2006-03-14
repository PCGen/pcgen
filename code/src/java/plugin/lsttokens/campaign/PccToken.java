package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.persistence.lst.CampaignLstToken;

import java.net.URL;

/**
 * Class deals with PCC Token
 */
public class PccToken implements CampaignLstToken {

	public String getTokenName() {
		return "PCC";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl) {
		campaign.addLine("PCC:" + value);
		campaign.addPccFile(CampaignLoader.convertFilePath(sourceUrl, value));
		return true;
	}
}
