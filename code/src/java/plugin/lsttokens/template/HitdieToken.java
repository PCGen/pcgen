package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with HITDIE Token
 */
public class HitdieToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "HITDIE";
	}

	public boolean parse(PCTemplate template, String value) {
		template.setHitDieLock(value);
		return true;
	}
}
