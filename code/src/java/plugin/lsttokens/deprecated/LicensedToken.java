package plugin.lsttokens.deprecated;

import pcgen.core.Campaign;
import pcgen.core.PObject;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.Deprecated;

import java.net.URL;

/**
 * Class deals with LICENSED Token
 */
public class LicensedToken implements CampaignLstToken, Deprecated {

	public String getTokenName() {
		return "LICENSED";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl) {
		campaign.setIsLicensed(value.startsWith("Y"));
		return true;
	}

	public String getMessage(PObject obj, String value) {
		return "Use ISLICENSED: instead.";
	}
}
