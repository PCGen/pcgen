package plugin.lsttokens.sponsor;

import java.util.Map;

import pcgen.persistence.lst.SponsorLstToken;

/**
 * Class deals with IMAGEBANNER Token
 */
public class ImagebannerToken implements SponsorLstToken {

	public String getTokenName() {
		return "IMAGEBANNER";
	}

	public boolean parse(Map sponsor, String value) {
		sponsor.put("IMAGEBANNER", value);
		return true;
	}
}
