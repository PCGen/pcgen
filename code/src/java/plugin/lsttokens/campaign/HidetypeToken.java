package plugin.lsttokens.campaign;

import java.net.URI;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.core.Ability;
import pcgen.core.Campaign;
import pcgen.core.Equipment;
import pcgen.core.GameMode;
import pcgen.core.Skill;
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

			Class<?> cl = null;
			String types = null;
			if (value.startsWith("EQUIP|"))
			{
				cl = Equipment.class;
				types = value.substring(6);
			}
			else if (value.startsWith("FEAT|"))
			{
				cl = Ability.class;
				types = value.substring(5);
			}
			else if (value.startsWith("SKILL|"))
			{
				cl = Skill.class;
				types = value.substring(6);
			}
			if (cl == null)
			{
				Logging.errorPrint("Did not understand: " + value + " in "
						+ getTokenName());
				return false;
			}
			StringTokenizer st = new StringTokenizer(types, Constants.PIPE);
			while (st.hasMoreTokens())
			{
				gm.addHiddenType(cl, st.nextToken());
			}
		}
		return true;
	}
}
