package plugin.lsttokens.companionmod;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.character.CompanionMod;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with COPYMASTERHP Token
 */
public class CopymasterhdToken extends AbstractToken implements
		CDOMPrimaryToken<CompanionMod>
{

	@Override
	public String getTokenName()
	{
		return "COPYMASTERHP";
	}

	public boolean parse(LoadContext context, CompanionMod cMod, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		context.getObjectContext()
			.put(cMod, StringKey.MASTER_HP_FORMULA, value);
		return true;
	}

	public String[] unparse(LoadContext context, CompanionMod cMod)
	{
		String masterHP =
				context.getObjectContext().getString(cMod,
					StringKey.MASTER_HP_FORMULA);
		if (masterHP == null)
		{
			return null;
		}
		return new String[]{masterHP};
	}

	public Class<CompanionMod> getTokenClass()
	{
		return CompanionMod.class;
	}

}
