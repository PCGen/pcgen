package plugin.lsttokens.ability;

import java.math.BigDecimal;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deal with COST Token
 */
public class CostToken implements CDOMPrimaryToken<Ability>
{

	public String getTokenName()
	{
		return "COST";
	}

	public boolean parse(LoadContext context, Ability ability, String value)
	{
		try
		{
			context.getObjectContext().put(ability, ObjectKey.SELECTION_COST,
					new BigDecimal(value));
			return true;
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint(getTokenName() + " expected a number: " + value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, Ability ability)
	{
		BigDecimal bd = context.getObjectContext().getObject(ability,
				ObjectKey.SELECTION_COST);
		if (bd == null)
		{
			return null;
		}
		return new String[] { bd.toString() };
	}

	public Class<Ability> getTokenClass()
	{
		return Ability.class;
	}
}
