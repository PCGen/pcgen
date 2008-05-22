package plugin.lsttokens.template;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Region;
import pcgen.core.PCTemplate;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with REGION Token
 */
public class RegionToken extends AbstractToken implements
		CDOMPrimaryToken<PCTemplate>
{

	@Override
	public String getTokenName()
	{
		return "REGION";
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
					ObjectKey.USETEMPLATENAMEFORREGION, true);
			context.getObjectContext().put(template, ObjectKey.REGION, null);
		}
		else
		{
			context.getObjectContext().put(template,
					ObjectKey.USETEMPLATENAMEFORREGION, null);
			context.getObjectContext().put(template, ObjectKey.REGION,
					Region.getConstant(value));
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Boolean useName = context.getObjectContext().getObject(pct,
				ObjectKey.USETEMPLATENAMEFORREGION);
		Region region = context.getObjectContext().getObject(pct,
				ObjectKey.REGION);
		if (useName == null)
		{
			if (region == null)
			{
				// Okay, nothing set
				return null;
			}
			return new String[] { region.toString() };
		}
		if (region != null)
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
