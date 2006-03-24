package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with ABILITY Token
 */
public class AbilityToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "ABILITY";
	}

	public boolean parse(PCTemplate template, String value) {
		template.addAbilityString(value);
		return true;
	}
}
