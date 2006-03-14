/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import pcgen.core.PObject;
import pcgen.persistence.lst.GlobalLstToken;

/**
 * @author djones4
 *
 */
public class SrLst implements GlobalLstToken {

	public String getTokenName() {
		return "SR";
	}

	public boolean parse(PObject obj, String value, int anInt) {
		if (anInt > -9) {
			obj.setSR(anInt + "|" + value);
		} else {
			obj.setSR(value);
		}
		return true;
	}
}

