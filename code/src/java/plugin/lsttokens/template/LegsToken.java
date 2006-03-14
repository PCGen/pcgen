package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;

/**
 * Class deals with LEGS Token
 */
public class LegsToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "LEGS";
	}

	public boolean parse(PCTemplate template, String value) {
		try {
			template.setLegs(Integer.parseInt(value));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}
