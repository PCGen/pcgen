package plugin.lsttokens.sponsor;

import java.util.Map;

import pcgen.persistence.lst.SponsorLstToken;

/**
 * Class deals with TEXT Token
 */
public class TextToken implements SponsorLstToken {

	public String getTokenName() {
		return "TEXT";
	}

	public boolean parse(Map<String, String> sponsor, String value) {
		sponsor.put("TEXT", value);
		return true;
	}
}
