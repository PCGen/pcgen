package plugin.lsttokens.equipmentmodifier;

import pcgen.core.EquipmentModifier;
import pcgen.persistence.lst.EquipmentModifierLstToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

/**
 * <code>VisibleToken</code> handles the processing of the VISIBLE tag
 * in the definition of an Equipment Modifier.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author Devon Jones
 * @version $Revision$
 */
public class VisibleToken implements EquipmentModifierLstToken
{

	/**
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "VISIBLE";
	}

	/**
	 * @see pcgen.persistence.lst.EquipmentModifierLstToken#parse(pcgen.core.EquipmentModifier, java.lang.String)
	 */
	public boolean parse(EquipmentModifier mod, String value)
	{
		if (value.equals("YES"))
		{
			mod.setVisibility(Visibility.DEFAULT);
		}
		else if (value.equals("QUALIFY"))
		{
			mod.setVisibility(Visibility.QUALIFY);
		}
		else if (value.equals("NO"))
		{
			mod.setVisibility(Visibility.HIDDEN);
		}
		else
		{
			Logging.errorPrint("Unexpected value used in "
					+ getTokenName() + " in Skill");
				Logging.errorPrint(" " + value
					+ " is not a valid value for " + getTokenName());
				Logging
					.errorPrint(" Valid values in Skill are YES, QUALIFY, NO");
				return false;
		}
		return true;
	}
}
