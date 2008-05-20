package plugin.lsttokens.deity;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Deity;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with WORSHIPPERS Token
 */
public class WorshippersToken implements CDOMPrimaryToken<Deity>
{

	/**
	 * Get token name
	 * @return token name 
	 */
	public String getTokenName()
	{
		return "WORSHIPPERS";
	}

	public boolean parse(LoadContext context, Deity deity, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		context.getObjectContext().put(deity, StringKey.WORSHIPPERS, value);
		return true;
	}

	public String[] unparse(LoadContext context, Deity deity)
	{
		String worshippers =
				context.getObjectContext().getString(deity,
					StringKey.WORSHIPPERS);
		if (worshippers == null)
		{
			return null;
		}
		return new String[]{worshippers};
	}

	public Class<Deity> getTokenClass()
	{
		return Deity.class;
	}
}
