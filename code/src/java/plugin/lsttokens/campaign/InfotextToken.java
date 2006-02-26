package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

import java.net.URL;

/**
 * Class deals with INFOTEXT Token
 */
public class InfotextToken implements CampaignLstToken {

	public String getTokenName() {
		return "INFOTEXT";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl) {
		campaign.setInfoText(value);
		return true;
	}
}
