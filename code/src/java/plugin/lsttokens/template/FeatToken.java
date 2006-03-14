package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with FEAT Token
 */
public class FeatToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "FEAT";
	}

	public boolean parse(PCTemplate template, String value) {
		template.addFeatString(value);
		return true;
	}
}
