package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

import java.net.URL;

/**
 * Class deals with CAMPAIGN Token
 */
public class CampaignToken implements CampaignLstToken {

	public String getTokenName() {
		return "CAMPAIGN";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl) {
		campaign.setName(value);
		campaign.setSourceFile(sourceUrl.toString());
		return true;
	}
}
