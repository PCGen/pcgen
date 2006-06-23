package plugin.lsttokens.sponsor;

import java.util.Map;

import pcgen.persistence.lst.SponsorLstToken;

/**
 * Class deals with NAME Token
 */
public class NameToken implements SponsorLstToken {

	public String getTokenName() {
		return "NAME";
	}

	public boolean parse(Map<String, String> sponsor, String value) {
		sponsor.put("NAME", value);
		return true;
	}
}
