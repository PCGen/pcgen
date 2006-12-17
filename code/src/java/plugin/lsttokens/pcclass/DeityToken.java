package plugin.lsttokens.pcclass;

import java.util.StringTokenizer;

import pcgen.core.Constants;
import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with DEITY Token
 */
public class DeityToken implements PCClassLstToken
{

	public String getTokenName()
	{
		return "DEITY";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.clearDeityList();

		StringTokenizer st = new StringTokenizer(Constants.PIPE);
		while (st.hasMoreTokens())
		{
			pcclass.addDeity(st.nextToken());
		}
		return true;
	}
}
