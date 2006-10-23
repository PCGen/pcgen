/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.core.DamageReduction;
import pcgen.persistence.PersistenceLayerException;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * @author djones4
 *
 */

public class DrLst implements GlobalLstToken {

	public String getTokenName() {
		return "DR";
	}

	public boolean parse(PObject obj, String value, int anInt) {
		ArrayList<Prerequisite> preReqs = new ArrayList<Prerequisite>();
		if (anInt > -9) {
			try {
				PreParserFactory factory = PreParserFactory.getInstance();
				String preLevelString = "PRELEVEL:" + anInt;
				if (obj instanceof PCClass)
				{
					// Classes handle this differently
					preLevelString = "PRECLASS:1," + obj.getKeyName() + "=" + anInt;
				}
				Prerequisite r = factory.parse(preLevelString);
				preReqs.add(r);
			}
			catch (PersistenceLayerException notUsed) {
				return false;
			}
		}

		if (".CLEAR".equals(value)) {
			obj.clearDR();
			return true;
		}

		StringTokenizer tok = new StringTokenizer(value, "|");
		String[] values = tok.nextToken().split("/");
		if (values.length != 2) {
			return false;
		}
		DamageReduction dr = new DamageReduction(values[0], values[1]);

		if (tok.hasMoreTokens()) {
			try {
				PreParserFactory factory = PreParserFactory.getInstance();
				Prerequisite r = factory.parse(tok.nextToken());
				preReqs.add(r);
			}
			catch (PersistenceLayerException notUsed) {
				return false;
			}
		}
		for ( Prerequisite prereq : preReqs )
		{
			dr.addPreReq(prereq);
		}

		obj.addDR(dr);
		return true;
	}
}

