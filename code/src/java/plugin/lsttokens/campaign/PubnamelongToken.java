package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

import java.net.URL;

/**
 * Class deals with PUBNAMELONG Token
 */
public class PubnamelongToken implements CampaignLstToken {

	public String getTokenName() {
		return "PUBNAMELONG";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUl) {
		campaign.setPubNameLong(value);
		return true;
	}
}
