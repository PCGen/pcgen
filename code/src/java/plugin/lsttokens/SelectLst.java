package plugin.lsttokens;

import pcgen.core.PObject;
import pcgen.persistence.lst.GlobalLstToken;

/**
 * Class deals with SELECT Token
 */
public class SelectLst implements GlobalLstToken
{
	
	/*
	 * FIXME Can't do this until Formula objects can be used in Equipment
	 */

	public String getTokenName()
	{
		return "SELECT";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		obj.setSelect(value);
		return true;
	}
}
