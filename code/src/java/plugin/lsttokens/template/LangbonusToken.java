package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with LANGBONUS Token
 */
public class LangbonusToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "LANGBONUS";
	}

	public boolean parse(PCTemplate template, String value) {
		template.setLanguageBonus(value);
		return true;
	}
}
