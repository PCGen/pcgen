package plugin.lsttokens.ability;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Ability;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Delta;
import pcgen.util.Logging;

/**
 * Class deals with ADDSPELLLEVEL Token
 */
public class AddspelllevelToken implements CDOMPrimaryToken<Ability>
{

	public String getTokenName()
	{
		return "ADDSPELLLEVEL";
	}

	public boolean parse(LoadContext context, Ability ability, String value)
	{
		try
		{
			Integer i = Delta.parseInt(value);
			if (i.intValue() < 0)
			{
				Logging.errorPrint(getTokenName() + " must be an integer >= 0");
				return false;
			}
			context.getObjectContext().put(ability, IntegerKey.ADD_SPELL_LEVEL,
				i);
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint(getTokenName()
				+ " expected an integer.  Tag must be of the form: "
				+ getTokenName() + ":<int>");
			return false;
		}
	}

	public String[] unparse(LoadContext context, Ability ability)
	{
		Integer lvl =
				context.getObjectContext().getInteger(ability,
					IntegerKey.ADD_SPELL_LEVEL);
		if (lvl == null)
		{
			return null;
		}
		if (lvl.intValue() < 0)
		{
			context
				.addWriteMessage(getTokenName() + " must be an integer >= 0");
			return null;
		}
		return new String[]{lvl.toString()};
	}

	public Class<Ability> getTokenClass()
	{
		return Ability.class;
	}

}
