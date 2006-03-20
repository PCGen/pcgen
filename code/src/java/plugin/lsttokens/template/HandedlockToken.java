package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with HANDEDLOCK Token
 * @deprecated To be removed in 5.10 Beta 1 (don't forget to remove entry in 
 * build.xml file when you remove this class)
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

