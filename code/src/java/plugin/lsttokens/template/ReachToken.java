package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;

/**
 * Class deals with REACH Token
 */
public class ReachToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "REACH";
	}

	public boolean parse(PCTemplate template, String value) {
		try {
			template.setReach(Integer.parseInt(value));
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}
}
