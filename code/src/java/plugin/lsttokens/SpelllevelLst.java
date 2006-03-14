/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import pcgen.core.Campaign;
import pcgen.core.PObject;
import pcgen.persistence.lst.GlobalLstToken;

/**
 * @author djones4
 *
 */
public class SpelllevelLst implements GlobalLstToken {

	public String getTokenName() {
		return "SPELLLEVEL";
	}

	public boolean parse(PObject obj, String value, int anInt) {
		if (!(obj instanceof Campaign)) {
			obj.getSpellSupport().addSpellLevel(value);
			return true;
		}
		return false;
	}
}

