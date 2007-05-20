package plugin.lsttokens.deprecated;

import pcgen.core.Equipment;
import pcgen.core.PObject;
import pcgen.persistence.lst.EquipmentLstToken;
import pcgen.util.PropertyFactory;

/**
 * Deals with ALTCRITICAL token
 */
//514 deprecation cleanup
public class AltcriticalToken implements EquipmentLstToken //, DeprecatedToken
{

	public String getTokenName()
	{
		return "ALTCRITICAL";
	}

	public boolean parse(Equipment eq, String value)
	{
		if ((value.length() > 0) && (value.charAt(0) == 'x'))
		{
			try
			{
				eq.setAltCritMult(Integer.parseInt(value.substring(1)));
			}
			catch (NumberFormatException nfe)
			{
				return false;
			}
			return true;
		}
		else if (value.equals("-"))
		{
			eq.setAltCritMult(-1);
			return true;
		}
		return false;
	}

	/**
	 * @see pcgen.persistence.lst.DeprecatedToken#getMessage(pcgen.core.PObject, java.lang.String)
	 */
	public String getMessage(@SuppressWarnings("unused")
	PObject anObj, @SuppressWarnings("unused")
	String anValue)
	{
		return PropertyFactory.getString("Use ALTCRITMULT instead of ALTCRITICAL"); //$NON-NLS-1$
	}

}
