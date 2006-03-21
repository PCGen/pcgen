/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import pcgen.core.PObject;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.core.DamageReduction;
import pcgen.persistence.PersistenceLayerException;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Iterator;

/**
 * @author djones4
 *
 */

public class DrLst implements GlobalLstToken {

	public String getTokenName() {
		return "DR";
	}

	public boolean parse(PObject obj, String value, int anInt) {
		ArrayList preReqs = new ArrayList();
		if (anInt > -9) {
			try
			{
				PreParserFactory factory = PreParserFactory.getInstance();
				Prerequisite r = factory.parse("PRELEVEL:" + anInt);
				preReqs.add(r);
			}
			catch (PersistenceLayerException notUsed)
			{
				// Should never happen
			}
		}

		if (".CLEAR".equals(value))
		{
			obj.clearDR();
			return true;
		 }

		StringTokenizer tok = new StringTokenizer(value, "|");
		String[] values = tok.nextToken().split("/");
		if (values.length != 2)
		{
			return false;
		}
		DamageReduction dr = new DamageReduction(values[0], values[1]);

		if (tok.hasMoreTokens())
		{
			try
			{
				PreParserFactory factory = PreParserFactory.getInstance();
				Prerequisite r = factory.parse(tok.nextToken());
				preReqs.add(r);
			}
			catch (PersistenceLayerException notUsed)
			{
				return false;
			}
		}
		if (preReqs.size() > 0)
		{
			for (Iterator i = preReqs.iterator(); i.hasNext(); )
			{
				dr.addPreReq((Prerequisite)i.next());
			}
		}

		obj.addDR(dr);
		return true;
	}
}

