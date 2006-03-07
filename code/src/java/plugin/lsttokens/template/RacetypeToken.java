package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with RACETYPE Token
 */
public class RacetypeToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "RACETYPE";
	}

	public boolean parse(PCTemplate template, String value) {
		template.setRaceType(value);
		return true;
	}
}
