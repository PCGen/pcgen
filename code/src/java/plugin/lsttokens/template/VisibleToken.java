package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with VISIBLE Token
 */
public class VisibleToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "VISIBLE";
	}

	public boolean parse(PCTemplate template, String value) {
		if (value.startsWith("DISPLAY")) {
			template.setVisible(PCTemplate.VISIBILITY_DISPLAY_ONLY);
		}
		else if (value.startsWith("EXPORT")) {
			template.setVisible(PCTemplate.VISIBILITY_OUTPUT_ONLY);
		}
		else if (value.startsWith("NO")) {
			template.setVisible(PCTemplate.VISIBILITY_HIDDEN);
		}
		else {
			template.setVisible(PCTemplate.VISIBILITY_DEFAULT);
		}
		return true;
	}
}
