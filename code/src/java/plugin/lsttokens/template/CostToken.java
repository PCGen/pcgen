package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with COST Token
 */
public class CostToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "COST";
	}

	public boolean parse(PCTemplate template, String value) {
		template.setCost(value);
		return true;
	}
}
