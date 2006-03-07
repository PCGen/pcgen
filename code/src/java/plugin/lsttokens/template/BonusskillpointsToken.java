package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with BONUSSKILLPOINTS Token
 */
public class BonusskillpointsToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "BONUSSKILLPOINTS";
	}

	// additional skill points per level
	public boolean parse(PCTemplate template, String value) {
		try {
			template.setBonusSkillsPerLevel(Integer.parseInt(value));
		}
		catch(NumberFormatException nfe) {
			return false;
		}
		return true;
	}
}
