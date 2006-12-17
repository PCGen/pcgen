package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.util.Logging;

import java.net.URL;

/**
 * Class deals with RANK Token
 */
public class RankToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "RANK";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUl)
	{
		try
		{
			campaign.setRank(Integer.parseInt(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Bad RANK " + value);
		}
		return false;
	}
}
