package plugin.lsttokens.template;

import pcgen.core.PCTemplate;
import pcgen.persistence.lst.PCTemplateLstToken;


/**
 * Class deals with ABILITY Token
 */
public class AbilityToken implements PCTemplateLstToken {

	/**
     * Get token name
     * @return token name 
	 */
    public String getTokenName() {
		return "ABILITY";
	}

    /**
     * Parse ability token for template
     * @param template 
     * @param value 
     * @return true
     */
	public boolean parse(PCTemplate template, String value) {
		template.addAbilityString(value);
		return true;
	}
}
