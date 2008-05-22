package plugin.lsttokens.template;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SubRace;
import pcgen.core.PCTemplate;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with SUBRACE Token
 */
public class SubraceToken extends AbstractToken implements
		CDOMPrimaryToken<PCTemplate>
{

	@Override
	public String getTokenName()
	{
		return "SUBRACE";
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		if (value.equalsIgnoreCase("YES"))
		{
			context.getObjectContext().put(template,
					ObjectKey.USETEMPLATENAMEFORSUBRACE, true);
			context.getObjectContext().put(template, ObjectKey.SUBRACE, null);
		}
		else
		{
			context.getObjectContext().put(template,
					ObjectKey.USETEMPLATENAMEFORSUBRACE, null);
			context.getObjectContext().put(template, ObjectKey.SUBRACE,
					SubRace.getConstant(value));
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Boolean useName = context.getObjectContext().getObject(pct,
				ObjectKey.USETEMPLATENAMEFORSUBRACE);
		SubRace subrace = context.getObjectContext().getObject(pct,
				ObjectKey.SUBRACE);
		if (useName == null)
		{
			if (subrace == null)
			{
				// Okay, nothing set
				return null;
			}
			return new String[] { subrace.toString() };
		}
		if (subrace != null)
		{
			context.addWriteMessage("Cannot have Template with "
					+ getTokenName() + " YES and specific value");
		}
		return new String[] { "YES" };
	}

	public Class<PCTemplate> getTokenClass()
	{
		return PCTemplate.class;
	}
}
