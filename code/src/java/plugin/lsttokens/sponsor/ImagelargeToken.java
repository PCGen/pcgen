package plugin.lsttokens.sponsor;

import java.util.Map;

import pcgen.persistence.lst.SponsorLstToken;

/**
 * Class deals with IMAGELARGE Token
 */
public class ImagelargeToken implements SponsorLstToken {

	public String getTokenName() {
		return "IMAGELARGE";
	}

	public boolean parse(Map<String, String> sponsor, String value) {
		sponsor.put("IMAGELARGE", value);
		return true;
	}
}
