package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with HEIGHT Token
 */
public class HeightToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "HEIGHT";
	}

	public boolean parse(PCTemplate template, String value) {
		template.setHeightString(value);
		return true;
	}
}
