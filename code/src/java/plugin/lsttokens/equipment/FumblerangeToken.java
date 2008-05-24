package plugin.lsttokens.equipment;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Deals with FUMBLERANGE token
 */
public class FumblerangeToken implements CDOMPrimaryToken<Equipment>
{

	public String getTokenName()
	{
		return "FUMBLERANGE";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		if (value.length() == 0)
		{
			return false;
		}
		context.getObjectContext().put(eq, StringKey.FUMBLE_RANGE,
				Constants.LST_DOT_CLEAR.equals(value) ? null : value);
		return true;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		String range = context.getObjectContext().getString(eq,
				StringKey.FUMBLE_RANGE);
		if (range == null)
		{
			return null;
		}
		return new String[] { range };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}
