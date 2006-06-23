package plugin.lsttokens.deity;

import pcgen.core.Deity;
import pcgen.persistence.lst.DeityLstToken;

/**
 * Class deals with WORSHIPPERS Token
 */
public class WorshippersToken implements DeityLstToken{

	/**
     * Get token name
     * @return token name 
	 */
    public String getTokenName() {
		return "WORSHIPPERS";
	}

    /**
     * Parse WORSHIPPERS token
     * 
     * @param deity 
     * @param value 
     * @return true
     */
	public boolean parse(Deity deity, String value) {
		deity.setWorshippers(value);
		return true;
	}
}
