package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

import java.net.URL;

/**
 * Class deals with COPYRIGHT Token
 */
public class CopyrightToken implements CampaignLstToken {

	public String getTokenName() {
		return "COPYRIGHT";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl) {
		campaign.addSection15(value);
		return true;
	}
}
