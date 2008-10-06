package plugin.lsttokens.deity;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Deity;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with APPEARANCE Token
 */
public class AppearanceToken extends AbstractToken implements
		CDOMPrimaryToken<Deity>
{

	@Override
	public String getTokenName()
	{
		return "APPEARANCE";
	}

	public boolean parse(LoadContext context, Deity deity, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		context.getObjectContext().put(deity, StringKey.APPEARANCE, value);
		return true;
	}

	public String[] unparse(LoadContext context, Deity deity)
	{
		String appearance = context.getObjectContext().getString(deity,
				StringKey.APPEARANCE);
		if (appearance == null)
		{
			return null;
		}
		return new String[] { appearance };
	}

	public Class<Deity> getTokenClass()
	{
		return Deity.class;
	}
}
