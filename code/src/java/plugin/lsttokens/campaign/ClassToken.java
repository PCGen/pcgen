package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

import java.net.URL;

/**
 * Class deals with CLASS Token
 */
public class ClassToken implements CampaignLstToken {

	public String getTokenName() {
		return "CLASS";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl) {
		campaign.addLine("CLASS:" + value);
		campaign.addClassFile(new CampaignSourceEntry(campaign, CampaignLoader.convertFilePath(sourceUrl, value)));
		return true;
	}
}
