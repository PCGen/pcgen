package plugin.lsttokens.sponsor;

import java.util.Map;

import pcgen.persistence.lst.SponsorLstToken;

/**
 * Class deals with IMAGESMALL Token
 */
public class ImagesmallToken implements SponsorLstToken {

	public String getTokenName() {
		return "IMAGESMALL";
	}

	public boolean parse(Map sponsor, String value) {
		sponsor.put("IMAGESMALL", value);
		return true;
	}
}
