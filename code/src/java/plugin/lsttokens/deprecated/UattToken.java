package plugin.lsttokens.deprecated;

import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.util.PropertyFactory;

/**
 * Class deals with UATT Token
 */
public class UattToken implements PCClassLstToken, DeprecatedToken
{

	public String getTokenName()
	{
		return "UATT";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.addUatt(value);
		return true;
	}

	/**
	 * @see pcgen.persistence.lst.DeprecatedToken#getMessage(pcgen.core.PObject, java.lang.String)
	 */
	public String getMessage(@SuppressWarnings("unused")
	PObject anObj, @SuppressWarnings("unused")
	String anValue)
	{
		return PropertyFactory.getString("Use ATTACKCYCLE:UAB and BONUS:COMBAT|BAB instead of UATT"); //$NON-NLS-1$
	}
}
