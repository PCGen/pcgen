package plugin.lsttokens.equipmentmodifier;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.EquipmentModifier;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with FUMBLERANGE token
 */
public class FumblerangeToken implements CDOMPrimaryToken<EquipmentModifier>
{

	public String getTokenName()
	{
		return "FUMBLERANGE";
	}

	public boolean parse(LoadContext context, EquipmentModifier mod,
			String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		context.getObjectContext().put(mod, StringKey.FUMBLE_RANGE, value);
		return true;
	}

	public String[] unparse(LoadContext context, EquipmentModifier mod)
	{
		String range = context.getObjectContext().getString(mod,
				StringKey.FUMBLE_RANGE);
		if (range == null)
		{
			return null;
		}
		return new String[] { range };
	}

	public Class<EquipmentModifier> getTokenClass()
	{
		return EquipmentModifier.class;
	}
}
