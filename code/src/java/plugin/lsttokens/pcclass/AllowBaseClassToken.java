package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * @author joe.frazier
 * 
 * Added for [ 1849571 ] New Class tag: ALLOWBASECLASS:x
 *
 */
public class AllowBaseClassToken implements PCClassLstToken
{
	public String getTokenName()
	{
		return "ALLOWBASECLASS";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.setAllowBaseClass(!value.equals("NO"));
		// this is negated since NO must set the allowbaseclass bool to false
		return true;
	}
}
