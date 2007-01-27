package plugin.lsttokens.deprecated;

import pcgen.core.Equipment;
import pcgen.core.PObject;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.persistence.lst.EquipmentLstToken;

/**
 * Deals with BONUSTYPE token
 */
public class BonustypeToken implements EquipmentLstToken, DeprecatedToken
{

	public String getTokenName()
	{
		return "BONUSTYPE";
	}

	public boolean parse(Equipment eq, String value)
	{
		//		eq.setBonusType(value);
		return true;
	}

	public String getMessage(PObject obj, String value) {
		return "BONUSTYPE is ignored by PCGen - please remove this tag";
	}
}
