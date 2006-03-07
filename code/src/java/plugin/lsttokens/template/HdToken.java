package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with HD Token
 */
public class HdToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "HD";
	}

	public boolean parse(PCTemplate template, String value) {
		template.addHitDiceString(value);
		return true;
	}
}
