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

	/*
	 * FIXME Classes must be resolved/in context before this is converted due to 
	 * undocumented "features"
	 */
	/*
	 * Note: Don't need to wait for Template's LevelToken before this can be converted
	 * as there is no level support in templates for this token
	 */

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
