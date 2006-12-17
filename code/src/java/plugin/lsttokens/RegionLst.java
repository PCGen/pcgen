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
public class RegionLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "REGION";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (anInt > -9)
		{
			obj.setRegionString(anInt + "|" + value);
		}
		else
		{
			obj.setRegionString("0|" + value);
		}
		return true;
	}
}
