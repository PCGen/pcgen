package plugin.lsttokens.deprecated;

import pcgen.core.Campaign;
import pcgen.core.PObject;
import pcgen.persistence.lst.CampaignLstToken;
import pcgen.persistence.lst.DeprecatedToken;

import java.net.URI;

/**
 * Class deals with LICENSED Token
 */
public class LicensedToken implements CampaignLstToken, DeprecatedToken
{

	public String getTokenName()
	{
		return "LICENSED";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		campaign.setIsLicensed(value.startsWith("Y"));
		return true;
	}

	public String getMessage(PObject obj, String value)
	{
		return "Use ISLICENSED: instead.";
	}
}
