package plugin.lsttokens.deprecated;

import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.enumeration.DefaultTriState;

/**
 * Class deals with XPPENALTY Token
 */
public class XppenaltyToken implements PCClassLstToken, DeprecatedToken
{

	/**
	 * Get token name
	 * @return token name
	 */
	public String getTokenName()
	{
		return "XPPENALTY";
	}

	/**
	 * Parse XPPENALTY token
	 * 
	 * @param pcclass 
	 * @param value 
	 * @param level 
	 * @return true
	 */
	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.setXPPenalty(DefaultTriState.valueOf(value));
		return true;
	}

	public String getMessage(PObject obj, String value)
	{
		return "XPPENALTY is not a valid Token; the XPPENALTY is set in the Game Mode";
	}
}
