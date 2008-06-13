package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.util.Logging;

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
		if (value == null)
		{
			Logging.errorPrint("Empty " + getTokenName()
					+ " in campaign file: " + sourceUri.toString());
			return false;
		}
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
