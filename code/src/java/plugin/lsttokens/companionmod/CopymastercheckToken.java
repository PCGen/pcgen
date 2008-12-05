package plugin.lsttokens.companionmod;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.character.CompanionMod;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with COPYMASTERCHECK Token
 */
public class CopymastercheckToken extends AbstractToken implements
		CDOMPrimaryToken<CompanionMod>
{

	@Override
	public String getTokenName()
	{
		return "COPYMASTERCHECK";
	}

	public boolean parse(LoadContext context, CompanionMod cMod, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		context.getObjectContext().put(cMod, StringKey.MASTER_CHECK_FORMULA,
			value);
		return true;
	}

	public String[] unparse(LoadContext context, CompanionMod cMod)
	{
		String masterCheck =
				context.getObjectContext().getString(cMod,
					StringKey.MASTER_CHECK_FORMULA);
		if (masterCheck == null)
		{
			return null;
		}
		return new String[]{masterCheck};
	}

	public Class<CompanionMod> getTokenClass()
	{
		return CompanionMod.class;
	}

}
