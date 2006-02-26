package plugin.lsttokens.equipmentmodifier;

import pcgen.core.EquipmentModifier;
import pcgen.persistence.lst.EquipmentModifierLstToken;

/**
 * <code>VisibleToken</code> handles the processing of the VISIBLE tag
 * in the definition of an Equipment Modifier.
 *
 * Last Editor: $Author: jdempsey $
 * Last Edited: $Date: 2006/01/29 00:08:08 $
 *
 * @author Devon Jones
 * @version $Revision: 1.2 $
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
		mod.setVisible(value.toUpperCase());
		return true;
	}
}
