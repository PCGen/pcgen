package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;

/**
 * Class deals with HANDS Token
 */
public class HandsToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "HANDS";
	}

	public boolean parse(PCTemplate template, String value) {
		try {
			template.setHands(Integer.parseInt(value));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}
