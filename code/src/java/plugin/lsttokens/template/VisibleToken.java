package plugin.lsttokens.template;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCTemplate;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

/**
 * Class deals with VISIBLE Token
 */
public class VisibleToken implements CDOMPrimaryToken<PCTemplate>
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "VISIBLE";
	}

	public boolean parse(LoadContext context, PCTemplate template, String value)
	{
		Visibility vis;
		if (value.equals("DISPLAY"))
		{
			vis = Visibility.DISPLAY_ONLY;
		}
		else if (value.equals("EXPORT"))
		{
			vis = Visibility.OUTPUT_ONLY;
		}
		else if (value.equals("NO"))
		{
			vis = Visibility.HIDDEN;
		}
		else if (value.equals("YES"))
		{
			vis = Visibility.DEFAULT;
		}
		else
		{
			Logging.errorPrint("Can't understand Visibility: " + value);
			return false;
		}
		context.getObjectContext().put(template, ObjectKey.VISIBILITY, vis);
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate template)
	{
		Visibility vis =
				context.getObjectContext().getObject(template,
					ObjectKey.VISIBILITY);
		if (vis == null)
		{
			return null;
		}
		String visString;
		if (vis.equals(Visibility.DEFAULT))
		{
			visString = "YES";
		}
		else if (vis.equals(Visibility.DISPLAY_ONLY))
		{
			visString = "DISPLAY";
		}
		else if (vis.equals(Visibility.OUTPUT_ONLY))
		{
			visString = "EXPORT";
		}
		else if (vis.equals(Visibility.HIDDEN))
		{
			visString = "NO";
		}
		else
		{
			context.addWriteMessage("Visibility " + vis
				+ " is not a valid Visibility for a PCTemplate");
			return null;
		}
		return new String[]{visString};
	}

	public Class<PCTemplate> getTokenClass()
	{
		return PCTemplate.class;
	}
}
