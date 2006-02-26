package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.persistence.lst.CampaignLstToken;

import java.net.URL;

/**
 * Class deals with BIOSET Token
 */
public class BiosetToken implements CampaignLstToken {

	public String getTokenName() {
		return "BIOSET";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl) {
		campaign.addLine("BIOSET:" + value);
		campaign.addBioSetFile(CampaignLoader.convertFilePath(sourceUrl, value));
		return true;
	}
}
