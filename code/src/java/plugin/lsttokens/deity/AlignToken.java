package plugin.lsttokens.deity;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Deity;
import pcgen.core.PCAlignment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with ALIGN Token
 */
public class AlignToken implements CDOMPrimaryToken<Deity>
{

	public String getTokenName()
	{
		return "ALIGN";
	}

	public boolean parse(LoadContext context, Deity deity, String value)
	{
		PCAlignment al =
				context.ref.getAbbreviatedObject(PCAlignment.class, value);
		if (al == null)
		{
			Logging.errorPrint("In " + getTokenName() + " " + value
				+ " is not an Alignment");
			return false;
		}
		context.getObjectContext().put(deity, ObjectKey.ALIGNMENT, al);
		return true;
	}

	public String[] unparse(LoadContext context, Deity deity)
	{
		PCAlignment at =
				context.getObjectContext()
					.getObject(deity, ObjectKey.ALIGNMENT);
		if (at == null)
		{
			return null;
		}
		return new String[]{at.getLSTformat()};
	}

	public Class<Deity> getTokenClass()
	{
		return Deity.class;
	}
}
