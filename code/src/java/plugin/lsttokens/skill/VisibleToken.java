package plugin.lsttokens.skill;

import pcgen.core.Skill;
import pcgen.persistence.lst.SkillLstToken;
import pcgen.util.Logging;
import pcgen.util.enumeration.Visibility;

/**
 * Class deals with VISIBLE Token
 */
public class VisibleToken implements SkillLstToken
{

	public String getTokenName()
	{
		return "VISIBLE";
	}

	public boolean parse(Skill skill, String value)
	{
		final String visType = value.toUpperCase();

		if (visType.equals("YES"))
		{
			skill.setVisibility(Visibility.DEFAULT);
		}
		else if (visType.equals("ALWAYS"))
		{
			skill.setVisibility(Visibility.DEFAULT);
		}
		else if (value.equals("DISPLAY"))
		{
			skill.setVisibility(Visibility.DISPLAY_ONLY);
		}
		else if (visType.equals("GUI"))
		{
		}
		else if (visType.equals("EXPORT") || visType.startsWith("EXPORT|"))
		{
			skill.setVisibility(Visibility.OUTPUT_ONLY);
		}
		else if (visType.equals("CSHEET"))
		{
			skill.setVisibility(Visibility.OUTPUT_ONLY);
		}
		else
		{
			Logging.errorPrint("Unexpected value used in "
				+ getTokenName() + " in Skill");
			Logging.errorPrint(" " + visType
				+ " is not a valid value for " + getTokenName());
			Logging
				.errorPrint(" Valid values in Skill are YES, ALWAYS, DISPLAY, GUI, EXPORT, CSHEET");
			return false;
		}

		String[] elements = value.split("\\|");

		if (elements.length > 1)
		{
			if (elements[1].equals("READONLY")
				&& !visType.startsWith("EXPORT"))
			{
				skill.setReadOnly(true);
			}
			else
			{
				Logging
					.errorPrint("Invalid Combination in Skill LST "
						+ getTokenName()
						+ ".  | must separate READONLY and cannot be used with EXPORT: "
						+ value);
				return false;
			}
		}
		return true;
	}
}
