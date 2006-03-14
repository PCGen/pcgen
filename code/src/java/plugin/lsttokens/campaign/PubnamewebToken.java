package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

import java.net.URL;

/**
 * Class deals with PUBNAMEWEB Token
 */
public class PubnamewebToken implements CampaignLstToken {

	public String getTokenName() {
		return "PUBNAMEWEB";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUl) {
		campaign.setPubNameWeb(value);
		return true;
	}
}
