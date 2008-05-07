package plugin.lsttokens.pcclass;

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with DEITY Token
 */
public class DeityToken implements PCClassLstToken
{

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "DEITY";
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.PCClassLstToken#parse(pcgen.core.PCClass, java.lang.String, int)
	 */
	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.clearDeityList();

		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
		while (st.hasMoreTokens())
		{
			String name = st.nextToken();
			if (name.trim().length() > 0)
			{
				pcclass.addDeity(name);
			}
		}
		return true;
	}
}
