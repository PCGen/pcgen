package plugin.lsttokens.pcclass;

import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with HASSUBCLASS Token
 */
public class HassubclassToken implements CDOMPrimaryToken<PCClass>
{

	public String getTokenName()
	{
		return "HASSUBCLASS";
	}

	public boolean parse(LoadContext context, PCClass obj, String value)
			throws PersistenceLayerException
	{
		return true;
	}

	public String[] unparse(LoadContext context, PCClass obj)
	{
		// Intentional
		// TODO Need to deprecate this token
		return null;
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}
