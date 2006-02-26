package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

import java.net.URL;

/**
 * Class deals with ABILITY Token
 */
public class AbilityToken implements CampaignLstToken {

	public String getTokenName() {
		return "ABILITY";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl) {
		campaign.addLine("ABILITY:" + value);
		campaign.addAbilityFile(new CampaignSourceEntry(campaign, CampaignLoader.convertFilePath(sourceUrl, value)));
		return true;
	}
}
