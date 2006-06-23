package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with ABB Token for PCC files
 */
public class AbbToken implements PCClassLstToken {

    /**
     * Return token name
     * @return token name
     */
	public String getTokenName() {
		return "ABB";
	}

    /**
     * Parse the ABB token
     * 
     * @param pcclass 
     * @param value 
     * @param level 
     * @return true
     */
	public boolean parse(PCClass pcclass, String value, int level) {
		pcclass.setAbbrev(value);
		return true;
	}
}
