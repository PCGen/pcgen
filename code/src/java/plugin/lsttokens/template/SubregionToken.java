package plugin.lsttokens.template;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SubRegion;
import pcgen.core.PCTemplate;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with SUBREGION Token
 */
public class SubregionToken extends AbstractToken implements
		CDOMPrimaryToken<PCTemplate>
{

	@Override
	public String getTokenName()
	{
		return "SUBREGION";
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		if (value.equalsIgnoreCase("YES"))
		{
			context.getObjectContext().put(template, ObjectKey.USETEMPLATENAMEFORSUBREGION,
					true);
			context.getObjectContext().put(template, ObjectKey.SUBREGION,
					null);
		}
		else
		{
			context.getObjectContext().put(template, ObjectKey.USETEMPLATENAMEFORSUBREGION,
					null);
			context.getObjectContext().put(template, ObjectKey.SUBREGION,
					SubRegion.getConstant(value));
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Boolean useName = context.getObjectContext().getObject(pct,
				ObjectKey.USETEMPLATENAMEFORSUBREGION);
		SubRegion subregion = context.getObjectContext().getObject(pct,
				ObjectKey.SUBREGION);
		if (useName == null)
		{
			if (subregion == null)
			{
				// Okay, nothing set
				return null;
			}
			return new String[] { subregion.toString() };
		}
		if (subregion != null)
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
