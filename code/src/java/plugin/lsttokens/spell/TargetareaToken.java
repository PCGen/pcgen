package plugin.lsttokens.spell;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Globals;
import pcgen.core.spell.Spell;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with TARGETAREA Token
 */
public class TargetareaToken implements CDOMPrimaryToken<Spell>
{

	public String getTokenName()
	{
		return "TARGETAREA";
	}

	public boolean parse(LoadContext context, Spell spell, String value)
	{
		if (value.length() == 0)
		{
			Logging.errorPrint(getTokenName() + " arguments may not be empty");
			return false;
		}
		Globals.addSpellTargetSet(value);
		context.getObjectContext().put(spell, StringKey.TARGET_AREA, value);
		return true;
	}

	public String[] unparse(LoadContext context, Spell spell)
	{
		String target = context.getObjectContext().getString(spell,
				StringKey.TARGET_AREA);
		if (target == null)
		{
			return null;
		}
		return new String[] { target };
	}

	public Class<Spell> getTokenClass()
	{
		return Spell.class;
	}
}
