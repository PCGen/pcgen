package plugin.lsttokens.template;

import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCTemplate;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with GENDERLOCK Token
 */
public class GenderlockToken implements CDOMPrimaryToken<PCTemplate>
{

	public String getTokenName()
	{
		return "GENDERLOCK";
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		try
		{
			context.getObjectContext().put(template, ObjectKey.GENDER_LOCK,
					Gender.valueOf(value));
			return true;
		}
		catch (IllegalArgumentException iae)
		{
			Logging.errorPrint("Invalid Gender provided in " + getTokenName()
					+ ": " + value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Gender g = context.getObjectContext().getObject(pct,
				ObjectKey.GENDER_LOCK);
		if (g == null)
		{
			return null;
		}
		return new String[] { g.toString() };
	}

	public Class<PCTemplate> getTokenClass()
	{
		return PCTemplate.class;
	}
}
