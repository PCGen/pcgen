package plugin.lsttokens.deprecated;

import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.PropertyFactory;

/**
 * Class deals with CASTAS Token
 * 
 * Deprecated prior to 5.11.1 Alpha (to be removed after 5.12 release)
 */
public class CastasToken implements PCClassLstToken, DeprecatedToken
{

	public String getTokenName()
	{
		return "CASTAS";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.setCastAs(value);
		return true;
	}

	/**
	 * @see pcgen.persistence.lst.DeprecatedToken#getMessage(pcgen.core.PObject, java.lang.String)
	 */
	public String getMessage(@SuppressWarnings("unused")
	PObject anObj, @SuppressWarnings("unused")
	String anValue)
	{
		return PropertyFactory.getString("Use SPELLLIST instead of CASTAS"); //$NON-NLS-1$
	}
}
