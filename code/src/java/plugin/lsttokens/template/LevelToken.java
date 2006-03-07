package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with LEVEL Token
 */
public class LevelToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "LEVEL";
	}

	public boolean parse(PCTemplate template, String value) {
		template.addLevelString(value);
		return true;
	}
}
