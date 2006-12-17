package plugin.lsttokens.pcclass;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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

			List<String> knownList = new ArrayList<String>(st.countTokens());
			while (st.hasMoreTokens())
			{
				knownList.add(st.nextToken());
			}

			pcclass.setKnown(level, knownList);
			return true;
		}
		Logging.errorPrint("CAST tag without level not allowed!");
		return false;
	}
}
