package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Equipment;
import pcgen.core.character.WieldCategory;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with WIELD token
 */
public class WieldToken implements CDOMPrimaryToken<Equipment>
{

	/**
	 * Get token name
	 * 
	 * @return token name
	 */
	public String getTokenName()
	{
		return "WIELD";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		try
		{
			WieldCategory wc = WieldCategory.findByName(value);
			if (wc.equals(WieldCategory.DEFAULT_UNUSABLE))
			{
				Logging.errorPrint("In " + getTokenName()
						+ " unable to find WieldCategory for " + value);
				return false;
			}
			context.getObjectContext().put(eq, ObjectKey.WIELD, wc);
		}
		catch (IllegalArgumentException iae)
		{
			Logging.errorPrint("Invalid Wield provided in " + getTokenName()
					+ ": " + value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		WieldCategory w = context.getObjectContext().getObject(eq,
				ObjectKey.WIELD);
		if (w == null)
		{
			return null;
		}
		return new String[] { w.getName() };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}
