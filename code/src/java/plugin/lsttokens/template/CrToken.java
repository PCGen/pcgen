package plugin.lsttokens.template;

import java.math.BigDecimal;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCTemplate;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with CR Token
 */
public class CrToken implements CDOMPrimaryToken<PCTemplate>
{

	public String getTokenName()
	{
		return "CR";
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		try
		{
			context.getObjectContext().put(template, ObjectKey.CR_MODIFIER,
					new BigDecimal(value));
			return true;
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Misunderstood Double in Tag: " + value);
			return false;
		}
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		BigDecimal mod = context.getObjectContext().getObject(pct,
				ObjectKey.CR_MODIFIER);
		if (mod == null)
		{
			return null;
		}
		return new String[] { mod.toString() };
	}

	public Class<PCTemplate> getTokenClass()
	{
		return PCTemplate.class;
	}
}
