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
public class SrLst implements GlobalLstToken
{

	/*
	 * FIXME Template's LevelToken needs adjustment before this can be converted
	 * to the new syntax, since this is level-dependent
	 */

	public String getTokenName()
	{
		return "SR";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (".CLEAR".equals(value))
		{
			obj.clearSRList();
		}
		else
		{
			obj.setSR(anInt, value);
		}
		return true;
	}
}
