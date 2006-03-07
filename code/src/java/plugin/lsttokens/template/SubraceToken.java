package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with REGION Token
 */
public class SubraceToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "REGION";
	}

	public boolean parse(PCTemplate template, String value) {
		if (value.toUpperCase().startsWith("Y")) {
			value = template.getName();
		}

		template.setSubRace(value);
		return true;
	}
}
