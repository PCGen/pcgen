/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import java.util.StringTokenizer;

import pcgen.core.PObject;
import pcgen.persistence.lst.GlobalLstToken;

/**
 * @author djones4
 *
 */
public class CcskillLst implements GlobalLstToken {

	public String getTokenName() {
		return "CCSKILL";
	}

	public boolean parse(PObject obj, String value, int anInt) {
		final StringTokenizer tok = new StringTokenizer(value, "|");
		while (tok.hasMoreTokens()) {
			obj.addCcSkill(tok.nextToken());
		}
		return true;
	}
}

