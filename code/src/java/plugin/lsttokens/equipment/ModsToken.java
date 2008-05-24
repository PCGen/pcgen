package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.EqModControl;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with MODS token
 */
public class ModsToken implements CDOMPrimaryToken<Equipment>
{

	public String getTokenName()
	{
		return "MODS";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		EqModControl ctrl;
		try
		{
			ctrl = EqModControl.valueOf(value);
		}
		catch (IllegalArgumentException iae)
		{
			Logging.errorPrint("Invalid Mod Control provided in "
					+ getTokenName() + ": " + value);
			if (value.length() == 0)
			{
				return false;
			}
			switch (value.charAt(0))
			{
			case 'R':
			case 'r':
				ctrl = EqModControl.REQUIRED;
				break;

			case 'Y':
			case 'y':
				ctrl = EqModControl.YES;
				break;

			case 'N':
			case 'n':
				ctrl = EqModControl.NO;
				break;

			default:
				return false;
			}
		}
		context.getObjectContext().put(eq, ObjectKey.MOD_CONTROL, ctrl);
		return true;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		EqModControl control = context.getObjectContext().getObject(eq,
				ObjectKey.MOD_CONTROL);
		if (control == null)
		{
			return null;
		}
		return new String[] { control.toString() };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}
