package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

import java.net.URL;

/**
 * Class deals with ISLICENSED Token
 */
public class IslicensedToken implements CampaignLstToken {

	public String getTokenName() {
		return "ISLICENSED";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl) {
		campaign.setIsLicensed(value.startsWith("Y"));
		return true;
	}
}
