package plugin.lsttokens.pcclass;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.FormulaFactory;
import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with KNOWN Token
 */
public class KnownToken implements PCClassLstToken
{

	public String getTokenName()
	{
		return "KNOWN";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		if (level > 0)
		{
			StringTokenizer st = new StringTokenizer(value, ",");

			List<Formula> knownList = new ArrayList<Formula>();
			while (st.hasMoreTokens())
			{
				String nextToken = st.nextToken();
				if (nextToken.endsWith("+d")) {
					Logging.errorPrint("+d use in KNOWN has been deprecated.  " +
							"Use SPECIALTYKNOWN instead");
					return false;
				}
				knownList.add(FormulaFactory.getFormulaFor(nextToken));
			}

			pcclass.setKnown(level, knownList);
			return true;
		}
		Logging.errorPrint("KNOWN tag without level not allowed!");
		return false;
	}
}
