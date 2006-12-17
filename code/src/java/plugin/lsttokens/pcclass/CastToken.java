package plugin.lsttokens.pcclass;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.Logging;

/**
 * Class deals with CAST Token
 */
public class CastToken implements PCClassLstToken
{

	public String getTokenName()
	{
		return "CAST";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		if (level > 0)
		{
			StringTokenizer st = new StringTokenizer(value, ",");

			List<String> castList = new ArrayList<String>(st.countTokens());
			while (st.hasMoreTokens())
			{
				castList.add(st.nextToken());
			}

			pcclass.setCast(level, castList);
			return true;
		}
		Logging.errorPrint("CAST tag without level not allowed!");
		return false;
	}
}
