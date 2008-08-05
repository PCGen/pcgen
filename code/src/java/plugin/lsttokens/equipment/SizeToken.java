package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Equipment;
import pcgen.core.SizeAdjustment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with SIZE token 
 */
public class SizeToken implements CDOMPrimaryToken<Equipment>
{

	public String getTokenName()
	{
		return "SIZE";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		SizeAdjustment size =
				context.ref.getAbbreviatedObject(SizeAdjustment.class,
					value);
		if (size == null)
		{
			Logging.errorPrint("Unable to find Size: " + value);
			return false;
		}
		context.getObjectContext().put(eq, ObjectKey.BASESIZE, size);
		context.getObjectContext().put(eq, ObjectKey.SIZE, size);
		return true;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		SizeAdjustment res = context.getObjectContext().getObject(eq,
				ObjectKey.BASESIZE);
		if (res == null)
		{
			return null;
		}
		return new String[]{res.getAbbreviation()};
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}

}
