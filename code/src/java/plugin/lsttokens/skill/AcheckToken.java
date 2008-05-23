package plugin.lsttokens.skill;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillArmorCheck;
import pcgen.core.Skill;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with ACHECK Token
 */
public class AcheckToken implements CDOMPrimaryToken<Skill>
{

	public String getTokenName()
	{
		return "ACHECK";
	}

	public boolean parse(LoadContext context, Skill skill, String value)
	{
		SkillArmorCheck aCheck;
		try
		{
			aCheck = SkillArmorCheck.valueOf(value);
		}
		catch (IllegalArgumentException iae)
		{
			/*
			 * TODO516 turn on deprecation
			 */
			// Logging.deprecationPrint("Misunderstood " + getTokenName() + ": "
			// + value + " is not an abbreviation");
			char first = value.charAt(0);
			if (first == 'N')
			{
				// Logging.deprecationPrint(" please use NONE");
				aCheck = SkillArmorCheck.NONE;
			}
			else if (first == 'Y')
			{
				// Logging.deprecationPrint(" please use YES");
				aCheck = SkillArmorCheck.YES;
			}
			else if (first == 'P')
			{
				// Logging.deprecationPrint(" please use NONPROF");
				aCheck = SkillArmorCheck.NONPROF;
			}
			else if (first == 'D')
			{
				// Logging.deprecationPrint(" please use DOUBLE");
				aCheck = SkillArmorCheck.DOUBLE;
			}
			else if (first == 'W')
			{
				// Logging.deprecationPrint(" please use WEIGHT");
				aCheck = SkillArmorCheck.WEIGHT;
			}
			else
			{
				Logging.addParseMessage(Logging.LST_ERROR, "Skill "
						+ getTokenName() + " Did not understand: " + value);
				return false;
			}
		}

		context.getObjectContext().put(skill, ObjectKey.ARMOR_CHECK, aCheck);
		return true;
	}

	public String[] unparse(LoadContext context, Skill skill)
	{
		SkillArmorCheck sac = context.getObjectContext().getObject(skill,
				ObjectKey.ARMOR_CHECK);
		if (sac == null)
		{
			return null;
		}
		return new String[] { sac.toString() };
	}

	public Class<Skill> getTokenClass()
	{
		return Skill.class;
	}
}
