package plugin.lsttokens.campaign;

import java.net.URI;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Campaign;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with SHOWINMENU Token
 */
public class ShowinmenuToken extends AbstractToken implements
		CDOMPrimaryToken<Campaign>
{

	@Override
	public String getTokenName()
	{
		return "SHOWINMENU";
	}

	public boolean parse(Campaign campaign, String value, URI sourceUri)
	{
		boolean show = value.startsWith("Y")
				|| Boolean.valueOf(value).booleanValue();
		campaign.put(ObjectKey.SHOW_IN_MENU, show);
		return true;
	}

	public boolean parse(LoadContext context, Campaign pcc, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		Boolean set;
		char firstChar = value.charAt(0);
		if (firstChar == 'y' || firstChar == 'Y')
		{
			if (value.length() > 1 && !value.equalsIgnoreCase("YES"))
			{
				Logging.errorPrint("You should use 'YES' as the "
						+ getTokenName() + ": " + value);
				return false;
			}
			set = Boolean.TRUE;
		}
		else if (Boolean.valueOf(value).booleanValue())
		{
			// TODO Need to deprecate this behavior - inconsistent with ALL
			// other tokens
			set = Boolean.TRUE;
		}
		else
		{
			if (firstChar != 'N' && firstChar != 'n')
			{
				Logging.errorPrint("You should use 'YES' or 'NO' as the "
						+ getTokenName() + ": " + value);
				return false;
			}
			if (value.length() > 1 && !value.equalsIgnoreCase("NO"))
			{
				Logging.errorPrint("You should use 'YES' or 'NO' as the "
						+ getTokenName() + ": " + value);
				return false;
			}
			set = Boolean.FALSE;
		}
		context.getObjectContext().put(pcc, ObjectKey.SHOW_IN_MENU, set);
		return true;
	}

	public String[] unparse(LoadContext context, Campaign pcc)
	{
		Boolean isM = context.getObjectContext().getObject(pcc,
				ObjectKey.SHOW_IN_MENU);
		if (isM == null)
		{
			return null;
		}
		return new String[] { isM.booleanValue() ? "YES" : "NO" };
	}

	public Class<Campaign> getTokenClass()
	{
		return Campaign.class;
	}
}
