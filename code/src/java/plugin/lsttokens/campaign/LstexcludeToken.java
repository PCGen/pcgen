package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

import java.net.URI;
import java.util.StringTokenizer;

/**
 * Class deals with LSTEXCLUDE Token
 */
public class LstexcludeToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "LSTEXCLUDE";
	}

	//check here for LST files to exclude from any further loading
	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.addLine("LSTEXCLUDE:" + value);
		final StringTokenizer lstTok = new StringTokenizer(value, "|");

		while (lstTok.hasMoreTokens())
		{
			final String lstFilename = lstTok.nextToken();
			//Call constructor directly, as this doesn't allow INCLUDE/EXCLUDE
			campaign.addLstExcludeFile(new CampaignSourceEntry(campaign,
					CampaignSourceEntry.getPathURI(sourceUri, lstFilename)));
		}
		return true;
	}
}
