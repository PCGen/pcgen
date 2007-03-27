package plugin.lsttokens.deprecated;

import pcgen.core.Deity;
import pcgen.core.PObject;
import pcgen.persistence.lst.DeityLstToken;
import pcgen.persistence.lst.DeprecatedToken;

/**
 * Class deals with FOLLOWERALIGN Token
 */
public class FolloweralignToken implements DeityLstToken, DeprecatedToken
{

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "FOLLOWERALIGN";
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.DeityLstToken#parse(pcgen.core.Deity, java.lang.String)
	 */
	public boolean parse(Deity deity, String value)
	{
		deity.setFollowerAlignments(value);
		return true;
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.DeprecatedToken#getMessage(pcgen.core.PObject, java.lang.String)
	 */
	public String getMessage(PObject obj, String value)
	{
		return "Use PREALIGN on the deity's domains instead.";
	}
}
