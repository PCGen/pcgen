package plugin.lsttokens.deity;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.Ability;
import pcgen.core.Constants;
import pcgen.core.Deity;
import pcgen.core.PCClass;
import pcgen.core.QualifiedObject;
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

	public String getTokenName()
	{
		return "DOMAINS";
	}

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
