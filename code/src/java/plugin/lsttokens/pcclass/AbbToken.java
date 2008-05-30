package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with ABB Token for PCC files
 */
public class AbbToken implements CDOMPrimaryToken<PCClass>
{

	/**
	 * Return token name
	 * 
	 * @return token name
	 */
	public String getTokenName()
	{
		return "ABB";
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		context.ref.registerAbbreviation(pcc, value);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		String abb = context.ref.getAbbreviation(pcc);
		if (abb == null)
		{
			return null;
		}
		return new String[] { abb };
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}
