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
public class RemoveLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "REMOVE";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (anInt > -9)
		{
			obj.setRemoveString(anInt + "|" + value);
		}
		else
		{
			obj.setRemoveString("0|" + value);
		}
		return true;
	}
}
