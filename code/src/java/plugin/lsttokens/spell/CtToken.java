package plugin.lsttokens.spell;

import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with CT Token
 */
public class CtToken implements CDOMPrimaryToken<Spell>
{

	public String getTokenName()
	{
		return "CT";
	}

	public String[] unparse(LoadContext context, Spell obj)
	{
		return null;
	}

	public Class<Spell> getTokenClass()
	{
		return Spell.class;
	}

	public boolean parse(LoadContext context, Spell obj, String value)
			throws PersistenceLayerException
	{
		return false;
	}
}
