package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with FAVOREDCLASS Token
 */
public class FavoredclassToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "FAVOREDCLASS";
	}

	public boolean parse(PCTemplate template, String value) {
		template.setFavoredClass(value);
		return true;
	}
}
