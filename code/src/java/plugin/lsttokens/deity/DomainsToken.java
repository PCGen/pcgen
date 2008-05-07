package plugin.lsttokens.deity;

import java.util.ArrayList;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.core.Deity;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.DeityLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.util.PropertyFactory;

/**
 * Class deals with DOMAINS Token
 */
public class DomainsToken implements DeityLstToken
{

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "DOMAINS";
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.DeityLstToken#parse(pcgen.core.Deity, java.lang.String)
	 */
	public boolean parse(Deity deity, String value) throws PersistenceLayerException
	{
		if (value.length() == 0)
		{
			return false;
		}
		
		final StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		String[] domains = tok.nextToken().split(",");

		ArrayList<Prerequisite> preReqs =
			new ArrayList<Prerequisite>();
		if (tok.hasMoreTokens())
		{
			while (tok.hasMoreTokens())
			{
				final String key = tok.nextToken();
				if (PreParserFactory.isPreReqString(key))
				{
					final PreParserFactory factory =
							PreParserFactory.getInstance();
					final Prerequisite r = factory.parse(key);
					preReqs.add(r);
				}
				else
				{
					throw new PersistenceLayerException(PropertyFactory.getFormattedString(
						"Errors.LstTokens.InvalidTokenFormat", //$NON-NLS-1$
						getClass().getName(), value));
				}
			}
		}

		deity.setDomainNameList(CoreUtility.arrayToList(domains), preReqs);
		return true;
	}
}
