package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with CR Token
 */
public class CrToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "CR";
	}

	public boolean parse(PCTemplate template, String value) {
		try {
			template.setCR(Integer.parseInt(value));
		}
		catch(NumberFormatException nfe) {
			return false;
		}
		return true;
	}
}
