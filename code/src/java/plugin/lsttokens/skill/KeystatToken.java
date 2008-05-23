package plugin.lsttokens.skill;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCStat;
import pcgen.core.Skill;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with KEYSTAT Token
 */
public class KeystatToken implements CDOMPrimaryToken<Skill>
{

	private static final Class<PCStat> PCSTAT_CLASS = PCStat.class;

	public String getTokenName()
	{
		return "KEYSTAT";
	}

	public boolean parse(LoadContext context, Skill skill, String value)
	{
		PCStat pcs = context.ref.getAbbreviatedObject(PCSTAT_CLASS, value);
		if (pcs == null)
		{
			Logging.errorPrint("Invalid Stat Abbreviation in Token "
					+ getTokenName() + ": " + value);
			return false;
		}
		context.getObjectContext().put(skill, ObjectKey.KEY_STAT, pcs);
		return true;
	}

	public String[] unparse(LoadContext context, Skill skill)
	{
		PCStat pcs = context.getObjectContext().getObject(skill,
				ObjectKey.KEY_STAT);
		if (pcs == null)
		{
			return null;
		}
		return new String[] { pcs.getLSTformat() };
	}

	public Class<Skill> getTokenClass()
	{
		return Skill.class;
	}
}
