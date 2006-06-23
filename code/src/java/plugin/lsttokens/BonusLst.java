/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import pcgen.core.PObject;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.lst.GlobalLstToken;

/**
 * @author djones4
 */
public class BonusLst implements GlobalLstToken {

    /**
     * Returns token name
     * @return token name
     */
	public String getTokenName() {
		return "BONUS";
	}

    /**
     * Parse BONUS token
     * @param obj 
     * @param value 
     * @param anInt 
     * @return true or false
     */
	public boolean parse(PObject obj, String value, int anInt) {
		boolean result = false;
		value = CoreUtility.replaceAll(value, "<this>", obj.getKeyName());
		if (anInt > -9) {
			result = obj.addBonusList(anInt + "|" + value);
		}
		else {
			result = obj.addBonusList(value);
		}
		return result;
	}
}

