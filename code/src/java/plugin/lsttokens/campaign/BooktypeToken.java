package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;

import java.net.URL;

/**
 * Class deals with BOOKTYPE Token
 */
public class BooktypeToken implements CampaignLstToken {

	public String getTokenName() {
		return "BOOKTYPE";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUl) {
		campaign.setBookType(value);
		return true;
	}
}
