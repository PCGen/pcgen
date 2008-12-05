package plugin.lsttokens.companionmod;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.character.CompanionMod;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with USEMASTERSKILL Token
 */
public class UsemasterskillToken extends AbstractToken implements
		CDOMPrimaryToken<CompanionMod>
{

	@Override
	public String getTokenName()
	{
		return "USEMASTERSKILL";
	}

	public boolean parse(LoadContext context, CompanionMod cMod, String value)
		throws PersistenceLayerException
	{
		if (isEmpty(value))
		{
			return false;
		}
		boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				Logging.errorPrint("You should use 'YES' or 'NO' as the "
					+ getTokenName());
				return false;
			}
			set = true;
		}
		else
		{
			if (firstChar != 'N' && firstChar != 'n'
				&& !value.equalsIgnoreCase("NO"))
			{
				Logging.errorPrint("You should use 'YES' or 'NO' as the "
					+ getTokenName());
				return false;
			}
			set = false;
		}
		context.getObjectContext().put(cMod, ObjectKey.USE_MASTER_SKILL, set);
		return true;
	}

	public String[] unparse(LoadContext context, CompanionMod cMod)
	{
		Boolean ums =
				context.getObjectContext().getObject(cMod,
					ObjectKey.USE_MASTER_SKILL);
		if (ums == null)
		{
			return null;
		}
		return new String[]{ums.booleanValue() ? "YES" : "NO"};
	}

	public Class<CompanionMod> getTokenClass()
	{
		return CompanionMod.class;
	}
}
