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
	 * FIXME This can only be converted after associations are no longer stored
	 * in the object, but instead are stored in the PC. This is due to the
	 * %CHOICE usage in EquipmentModifier not being handled by JEP due to use of %
	 * [which is the modulo function to JEP]
	 */
	/*
	 * Note: Don't need to wait for Template's LevelToken before this can be converted
	 * as there is no level support in templates for this token
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
