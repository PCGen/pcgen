package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

import java.net.URL;

/**
 * Class deals with PUBNAMESHORT Token
 */
public class PubnameshortToken implements CampaignLstToken {

	public String getTokenName() {
		return "PUBNAMESHORT";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUl) {
		campaign.setPubNameShort(value);
		return true;
	}
}
