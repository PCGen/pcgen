package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.core.SystemCollections;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.util.Logging;

/**
 * Class deals with HIDETYPE Token
 */
public class HidetypeToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "HIDETYPE";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		for (String gmName : campaign.getGameModes())
		{
			GameMode gm = SystemCollections.getGameModeNamed(gmName);
			if (gm == null)
			{
				Logging.errorPrint("Unknown game mode '" + gmName
					+ "' in campaign: " + campaign.getDisplayName());
				continue;
			}

			if (value.startsWith("EQUIP|"))
			{
				gm.setHiddenEquipmentTypes(value.substring(6));
			}
			else if (value.startsWith("FEAT|"))
			{
				gm.setHiddenAbilityTypes(value.substring(5));
			}
			else if (value.startsWith("SKILL|"))
			{
				gm.setHiddenSkillTypes(value.substring(6));
			}
		}
		return true;
	}
}
