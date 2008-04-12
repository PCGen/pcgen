/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import pcgen.core.PObject;
import pcgen.persistence.lst.GlobalLstToken;

/**
 * @author djones4
 * 
 */
public class UdamLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "UDAM";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (".CLEAR".equals(value))
		{
			obj.clearUdamList();
		}
		else if (anInt <= 0)
		{
			obj.setUdamItem(value, 0);
		}
		else
		{
			obj.setUdamItem(value, anInt);
		}
		return true;
	}
}
