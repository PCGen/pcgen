package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with HANDEDLOCK Token
 */
public class HandedlockToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "HANDEDLOCK";
	}

	 // set and lock character handedness, disabling pulldown menu in description section.
	public boolean parse(PCTemplate template, String value) {
		template.setHandedLock(value);
		return true;
	}
}

