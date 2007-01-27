package plugin.lsttokens.campaign;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;

import java.net.URI;

/**
 * Class deals with LICENSE Token
 */
public class LicenseToken implements CampaignLstToken
{

	public String getTokenName()
	{
		return "LICENSE";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		if (value.startsWith("FILE="))
		{
			campaign.addLicenseFile(CampaignSourceEntry.getPathURI(sourceUri,
				value.substring(5)));
		}
		else
		{
			campaign.addLicense(value);
		}
		return true;
	}
}
