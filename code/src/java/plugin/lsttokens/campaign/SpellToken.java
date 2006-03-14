package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

import java.net.URL;

/**
 * Class deals with SPELL Token
 */
public class SpellToken implements CampaignLstToken {

	public String getTokenName() {
		return "SPELL";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl) {
		campaign.addLine("SPELL:" + value);
		campaign.addSpellFile(new CampaignSourceEntry(campaign, CampaignLoader.convertFilePath(sourceUrl, value)));
		return true;
	}
}
