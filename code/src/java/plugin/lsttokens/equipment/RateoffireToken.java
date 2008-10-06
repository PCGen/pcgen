package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Deals with RATEOFFIRE token
 */
public class RateoffireToken extends AbstractToken implements
		CDOMPrimaryToken<Equipment>
{

	@Override
	public String getTokenName()
	{
		return "RATEOFFIRE";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		context.getObjectContext().put(eq, StringKey.RATE_OF_FIRE, value);
		return true;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		String rof = context.getObjectContext().getString(eq,
				StringKey.RATE_OF_FIRE);
		if (rof == null)
		{
			return null;
		}
		return new String[] { rof };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}
}
