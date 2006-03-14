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
public class KeyLst implements GlobalLstToken {

	public String getTokenName() {
		return "KEY";
	}

	public boolean parse(PObject obj, String value, int anInt) {
		obj.setKeyName(value);
		return true;
	}
}

