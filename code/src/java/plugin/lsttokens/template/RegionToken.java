package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with REGION Token
 */
public class RegionToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "REGION";
	}

	public boolean parse(PCTemplate template, String value) {
		if (value.toUpperCase().startsWith("Y")) {
			value = template.getName();
		}

		template.setRegion(value);
		return true;
	}
}
