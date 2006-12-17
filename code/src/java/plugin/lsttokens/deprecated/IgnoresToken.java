package plugin.lsttokens.deprecated;

import pcgen.core.EquipmentModifier;
import pcgen.core.PObject;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.persistence.lst.EquipmentModifierLstToken;

/**
 * Deals with IGNORES token 
 */
public class IgnoresToken implements EquipmentModifierLstToken, DeprecatedToken
{

	public String getTokenName()
	{
		return "IGNORES";
	}

	public boolean parse(EquipmentModifier mod, String value)
	{
		return true;
	}

	public String getMessage(PObject obj, String value)
	{
		return "This was a non-working token, use REPLACES and PRETYPE instead";
	}
}
