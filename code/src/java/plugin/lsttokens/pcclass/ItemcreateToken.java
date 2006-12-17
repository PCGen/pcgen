package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.lst.PCClassLstToken;

/**
 * Class deals with ITEMCREATE Token
 */
public class ItemcreateToken implements PCClassLstToken
{

	public String getTokenName()
	{
		return "ITEMCREATE";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		pcclass.setItemCreationMultiplier(value);
		return true;
	}
}
