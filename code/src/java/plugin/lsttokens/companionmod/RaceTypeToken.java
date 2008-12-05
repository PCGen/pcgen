package plugin.lsttokens.companionmod;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceType;
import pcgen.core.character.CompanionMod;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with RACETYPE: Token
 */
public class RaceTypeToken extends AbstractToken implements
		CDOMPrimaryToken<CompanionMod>
{

	@Override
	public String getTokenName()
	{
		return "RACETYPE";
	}

	public boolean parse(LoadContext context, CompanionMod cMod, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		context.getObjectContext().put(cMod, ObjectKey.RACETYPE,
			RaceType.getConstant(value));
		return true;
	}

	public String[] unparse(LoadContext context, CompanionMod cMod)
	{
		RaceType raceType =
				context.getObjectContext().getObject(cMod, ObjectKey.RACETYPE);
		if (raceType == null)
		{
			return null;
		}
		return new String[]{raceType.toString()};
	}

	public Class<CompanionMod> getTokenClass()
	{
		return CompanionMod.class;
	}

}
