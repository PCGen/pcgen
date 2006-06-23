package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

import java.net.URL;

/**
 * Class deals with ABILITY Token for campaign
 */
public class AbilityToken implements CampaignLstToken {

	/**
     * Get the token name
	 * @return token name
	 */
    public String getTokenName() {
		return "ABILITY";
	}

    /**
     * Parse the ability token
     * 
     * @param campaign 
     * @param value 
     * @param sourceUrl 
     * @return true
     */
	public boolean parse(Campaign campaign, String value, URL sourceUrl) {
		campaign.addLine("ABILITY:" + value);
		campaign.addAbilityFile(new CampaignSourceEntry(campaign, CampaignLoader.convertFilePath(sourceUrl, value)));
		return true;
	}
}
