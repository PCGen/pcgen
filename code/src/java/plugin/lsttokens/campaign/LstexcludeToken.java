package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

import java.net.URL;
import java.util.StringTokenizer;

/**
 * Class deals with LSTEXCLUDE Token
 */
public class LstexcludeToken implements CampaignLstToken {

	public String getTokenName() {
		return "LSTEXCLUDE";
	}

	//check here for LST files to exclude from any further loading
	public boolean parse(Campaign campaign, String value, URL sourceUrl) {
		campaign.addLine("LSTEXCLUDE:" + value);
		final StringTokenizer lstTok = new StringTokenizer(value, "|");

		while (lstTok.hasMoreTokens()) {
			final String lstFilename = lstTok.nextToken();
			campaign.addLstExcludeFile(new CampaignSourceEntry(campaign, CampaignLoader.convertFilePath(sourceUrl, lstFilename)));
		}
		return true;
	}
}
