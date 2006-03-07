package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with AGE Token
 */
public class AgeToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "AGE";
	}

	 // replaces racial age
	public boolean parse(PCTemplate template, String value) {
		template.setAgeString(value);
		return true;
	}
}
