package plugin.lsttokens.spell;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCStat;
import pcgen.core.spell.Spell;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with STAT Token
 */
public class StatToken implements CDOMPrimaryToken<Spell>
{

	private static final Class<PCStat> PCSTAT_CLASS = PCStat.class;

	public String getTokenName()
	{
		return "STAT";
	}

	public boolean parse(LoadContext context, Spell spell, String value)
	{
		PCStat pcs = context.ref.getAbbreviatedObject(PCSTAT_CLASS, value);
		if (pcs == null)
		{
			Logging.errorPrint("Invalid Stat Abbreviation in Token "
					+ getTokenName() + ": " + value);
			return false;
		}
		context.getObjectContext().put(spell, ObjectKey.SPELL_STAT, pcs);
		return true;
	}

	public String[] unparse(LoadContext context, Spell spell)
	{
		PCStat pcs = context.getObjectContext().getObject(spell,
				ObjectKey.SPELL_STAT);
		if (pcs == null)
		{
			return null;
		}
		return new String[] { pcs.getAbb() };
	}

	public Class<Spell> getTokenClass()
	{
		return Spell.class;
	}
}
