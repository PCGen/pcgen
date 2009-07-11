package plugin.lsttokens.statsandchecks.check;

import pcgen.core.PCCheck;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with CHECKNAME Token
 */
public class ChecknameToken extends AbstractToken implements
		CDOMPrimaryToken<PCCheck>
{

	@Override
	public String getTokenName()
	{
		return "CHECKNAME";
	}

	public boolean parse(LoadContext context, PCCheck check, String value)
			throws PersistenceLayerException
	{
		if (isEmpty(value))
		{
			return false;
		}
		/*
		 * Warning: setName is not editor friendly, and this is a gate to
		 * additional checks being added in Campaigns (vs. Game Modes)
		 */
		check.setName(value);
		return true;
	}

	public String[] unparse(LoadContext context, PCCheck check)
	{
		String name = check.getDisplayName();
		if (name == null)
		{
			return null;
		}
		return new String[] { name };
	}

	public Class<PCCheck> getTokenClass()
	{
		return PCCheck.class;
	}

}
