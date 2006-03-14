package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

import java.net.URL;

/**
 * Class deals with ISD20 Token
 */
public class Isd20Token implements CampaignLstToken {

	public String getTokenName() {
		return "ISD20";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl) {
		campaign.setIsD20(value.startsWith("Y"));
		return true;
	}
}
