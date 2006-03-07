package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with LEVELADJUSTMENT Token
 */
public class LeveladjustmentToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "LEVELADJUSTMENT";
	}

	public boolean parse(PCTemplate template, String value) {
		template.setLevelAdjustment(value);
		return true;
	}
}
