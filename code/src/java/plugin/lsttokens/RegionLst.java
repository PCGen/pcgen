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
	/*
	 * Note: Don't need to wait for Template's LevelToken before this can be converted
	 * as there is a REGION token in Template, so this is never hit by Templates
	 */

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
