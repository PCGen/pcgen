package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with SUBRACE Token
 */
public class SubraceToken implements PCTemplateLstToken {

	public String getTokenName() {
		return "SUBRACE";
	}

	public boolean parse(PCTemplate template, String value) {
		String subrace = value;
		//CONSIDER This prohibits any SubRace that starts with Y ... too general? - thpr 10/27/06
		if (value.toUpperCase().startsWith("Y")) {
			subrace = template.getDisplayName();
		}

		template.setSubRace(subrace);
		return true;
	}
}
