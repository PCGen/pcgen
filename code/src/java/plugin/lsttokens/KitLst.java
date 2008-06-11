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
public class KitLst implements GlobalLstToken
{
	/*
	 * FIXME Template's LevelToken needs adjustment before this can be converted
	 * to the new syntax, since this is level-dependent
	 */

	public String getTokenName()
	{
		return "KIT";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (anInt > -9)
		{
			obj.setKitString(anInt + "|" + value);
		}
		else
		{
			obj.setKitString("0|" + value);
		}
		return true;
	}
}
