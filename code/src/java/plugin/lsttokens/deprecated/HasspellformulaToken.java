package plugin.lsttokens.deprecated;

import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with HASSPELLFORMULA Token
 */
public class HasspellformulaToken implements CDOMPrimaryToken<PCClass>
{

	public String getTokenName()
	{
		return "HASSPELLFORMULA";
	}

	public String[] unparse(LoadContext context, PCClass obj)
	{
		return null;
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}

	public boolean parse(LoadContext context, PCClass obj, String value)
			throws PersistenceLayerException
	{
		Logging.deprecationPrint("Ignoring HASSPELLFORMULA: "
				+ "No longer required in PCGen 5.15+ ");
		return true;
	}
}
