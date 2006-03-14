package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

import java.net.URL;

/**
 * Class deals with ISOGL Token
 */
public class IsoglToken implements CampaignLstToken {

	public String getTokenName() {
		return "ISOGL";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl) {
		campaign.setIsOGL(value.startsWith("Y"));
		return true;
	}
}
