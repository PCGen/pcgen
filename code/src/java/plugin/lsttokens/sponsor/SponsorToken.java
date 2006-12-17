package plugin.lsttokens.sponsor;

import java.util.Map;

import pcgen.persistence.lst.SponsorLstToken;

/**
 * Class deals with SPONSOR Token
 */
public class SponsorToken implements SponsorLstToken
{

	public String getTokenName()
	{
		return "SPONSOR";
	}

	public boolean parse(Map<String, String> sponsor, String value)
	{
		sponsor.put("SPONSOR", value);
		return true;
	}
}
