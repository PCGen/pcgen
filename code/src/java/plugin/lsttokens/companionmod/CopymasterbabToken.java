package plugin.lsttokens.companionmod;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.character.CompanionMod;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with COPYMASTERBAB Token
 */
public class CopymasterbabToken extends AbstractToken implements
		CDOMPrimaryToken<CompanionMod>
{

	@Override
	public String getTokenName()
	{
		return "COPYMASTERBAB";
	}

	public boolean parse(LoadContext context, CompanionMod cMod, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		context.getObjectContext().put(cMod, StringKey.MASTER_BAB_FORMULA, value);
		return true;
	}

	public String[] unparse(LoadContext context, CompanionMod cMod)
	{
		String masterBAB =
				context.getObjectContext().getString(cMod, StringKey.MASTER_BAB_FORMULA);
		if (masterBAB == null)
		{
			return null;
		}
		return new String[]{masterBAB};
	}

	public Class<CompanionMod> getTokenClass()
	{
		return CompanionMod.class;
	}

}
