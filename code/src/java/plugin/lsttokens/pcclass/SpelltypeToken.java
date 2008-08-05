package plugin.lsttokens.pcclass;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with SPELLTYPE Token
 */
public class SpelltypeToken implements CDOMPrimaryToken<PCClass>
{

	public String getTokenName()
	{
		return "SPELLTYPE";
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		if (value == null || value.length() == 0)
		{
			// CONSIDER Deprecate this behavior
			return true;
		}
		if (value.equalsIgnoreCase(Constants.LST_NONE))
		{
			// CONSIDER Deprecate this behavior
			return true;
		}
		context.getObjectContext().put(pcc, StringKey.SPELLTYPE, value);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		String target = context.getObjectContext().getString(pcc,
				StringKey.SPELLTYPE);
		if (target == null)
		{
			return null;
		}
		return new String[] { target };
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}

}
