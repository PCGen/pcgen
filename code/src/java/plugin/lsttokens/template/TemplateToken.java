package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with TEMPLATE Token
 */
public class TemplateToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "TEMPLATE";
	}

	public boolean parse(PCTemplate template, String value) {
		template.addTemplate(value);
		return true;
	}
}
