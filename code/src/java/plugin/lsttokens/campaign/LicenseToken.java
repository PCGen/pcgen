package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLoader;
import pcgen.persistence.lst.CampaignLstToken;

import java.net.URL;

/**
 * Class deals with LICENSE Token
 */
public class LicenseToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "LICENSE";
	}

	public boolean parse(Campaign campaign, String value, URL sourceUrl)
	{
		if (value.startsWith("FILE="))
		{
			campaign.addLicenseFile(CampaignLoader.convertFilePath(sourceUrl,
				value.substring(5)));
		}
		else
		{
			campaign.addLicense(value);
		}
		return true;
	}
}
