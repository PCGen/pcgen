package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with WEIGHT Token
 */
public class WeightToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "WEIGHT";
	}

	 // replace racial weight
	public boolean parse(PCTemplate template, String value) {
		template.setWeightString(value);
		return true;
	}
}
