package plugin.lsttokens.ability;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

/**
 * <code>VisibleToken</code> handles the processing of the VISIBLE tag in the
 * definition of an Ability.
 * 
 * Last Editor: $Author$ Last Edited: $Date: 2007-02-10 11:55:15 -0500
 * (Sat, 10 Feb 2007) $
 * 
 * @author Devon Jones
 * @version $Revision$
 */
public class VisibleToken implements CDOMPrimaryToken<Ability>
{

	/**
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	public String getTokenName()
	{
		return "VISIBLE";
	}

	public boolean parse(LoadContext context, Ability ability, String value)
	{
		Visibility vis;
		if (value.equals("YES"))
		{
			vis = Visibility.DEFAULT;
		}
		else if (value.equals("DISPLAY"))
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
		else
		{
			Logging.errorPrint("Unable to understand " + getTokenName()
					+ " tag: " + value);
			return false;
		}
		context.getObjectContext().put(ability, ObjectKey.VISIBILITY, vis);
		return true;
	}

	public String[] unparse(LoadContext context, Ability ability)
	{
		Visibility vis = context.getObjectContext().getObject(ability,
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
					+ " is not a valid Visibility for an Ability");
			return null;
		}
		return new String[] { visString };
	}

	public Class<Ability> getTokenClass()
	{
		return Ability.class;
	}
}
