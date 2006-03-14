package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

import java.net.URL;

/**
 * Class deals with GENRE Token
 */
public class GenreToken implements CampaignLstToken {

	public String getTokenName() {
		return "GENRE";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl) {
		campaign.setGenre(value);
		return true;
	}
}
