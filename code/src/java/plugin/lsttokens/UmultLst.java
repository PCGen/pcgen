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
public class UmultLst implements GlobalLstToken {

	public String getTokenName() {
		return "UMULT";
	}

	public boolean parse(PObject obj, String value, int anInt) {
		if (anInt > -9) {
			obj.addUmult(anInt + "|" + value);
		} else {
			obj.addUmult(value);
		}
		return true;
	}
}

