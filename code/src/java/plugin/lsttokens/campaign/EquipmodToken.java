package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

import java.net.URL;

/**
 * Class deals with EQUIPMOD Token
 */
public class EquipmodToken implements CampaignLstToken {

	public String getTokenName() {
		return "EQUIPMOD";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl) {
		campaign.addLine("EQUIPMOD:" + value);
		campaign.addEquipModFile(new CampaignSourceEntry(campaign, CampaignLoader.convertFilePath(sourceUrl, value)));
		return true;
	}
}
