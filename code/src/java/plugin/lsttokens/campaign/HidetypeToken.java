package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.core.SystemCollections;
import pcgen.persistence.lst.CampaignLstToken;

import java.net.URL;

/**
 * Class deals with HIDETYPE Token
 */
public class HidetypeToken implements CampaignLstToken {

	public String getTokenName() {
		return "HIDETYPE";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl) {
		for (String gmName : campaign.getGameModes()) {
			if (value.startsWith("EQUIP|")) {
				SystemCollections.getGameModeNamed(gmName).setHiddenEquipmentTypes(value.substring(6));
			}
			else if (value.startsWith("FEAT|")) {
				SystemCollections.getGameModeNamed(gmName).setHiddenAbilityTypes(value.substring(5));
			}
			else if (value.startsWith("SKILL|")) {
				SystemCollections.getGameModeNamed(gmName).setHiddenSkillTypes(value.substring(6));
			}
		}
		return true;
	}
}
