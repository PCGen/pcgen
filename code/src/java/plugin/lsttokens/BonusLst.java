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
 *
 */
public class BonusLst implements GlobalLstToken {

	public String getTokenName() {
		return "BONUS";
	}

	public boolean parse(PObject obj, String value, int anInt) {
		boolean result = false;
		value = CoreUtility.replaceAll(value, "<this>", obj.getName());
		if (anInt > -9) {
			result = obj.addBonusList(anInt + "|" + value);
		}
		else {
			result = obj.addBonusList(value);
		}
		return result;
	}
}

