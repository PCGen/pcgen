package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with BONUSFEATS Token
 */
public class BonusfeatsToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "BONUSFEATS";
	}

	 // number of additional feats to spend
	public boolean parse(PCTemplate template, String value) {
		try {
			template.setBonusInitialFeats(Integer.parseInt(value));
		}
		catch(NumberFormatException nfe) {
			return false;
		}
		return true;
	}
}
