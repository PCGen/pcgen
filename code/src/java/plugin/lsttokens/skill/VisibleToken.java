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

		if (visType.startsWith("YES"))
		{
			if (!value.equals("YES"))
			{
				Logging.errorPrint("Abbreviation used in " + getTokenName()
					+ " in Skill");
				Logging.errorPrint(" " + value + " is not a valid value for "
					+ getTokenName());
				Logging
					.errorPrint(" Valid values in Skill are YES, ALWAYS, DISPLAY, GUI, EXPORT, CSHEET");
				Logging
					.errorPrint(" assuming you meant YES, please use YES (exact String, upper case) in the LST file");
				Logging.errorPrint(" This will break after PCGen 5.12");
			}
			skill.setVisibility(Visibility.DEFAULT);
		}
		else if (visType.startsWith("ALWAYS"))
		{
			if (!value.equals("ALWAYS"))
			{
				Logging.errorPrint("Abbreviation used in " + getTokenName()
					+ " in Skill");
				Logging.errorPrint(" " + value + " is not a valid value for "
					+ getTokenName());
				Logging
					.errorPrint(" Valid values in Skill are YES, ALWAYS, DISPLAY, GUI, EXPORT, CSHEET");
				Logging
					.errorPrint(" assuming you meant ALWAYS, please use ALWAYS (exact String, upper case) in the LST file");
				Logging.errorPrint(" This will break after PCGen 5.12");
			}
			skill.setVisibility(Visibility.DEFAULT);
		}
		else if (value.equals("DISPLAY"))
		{
			skill.setVisibility(Visibility.DISPLAY_ONLY);
		}
		else if (visType.startsWith("GUI"))
		{
			if (!value.equals("GUI"))
			{
				Logging.errorPrint("Abbreviation used in " + getTokenName()
					+ " in Skill");
				Logging.errorPrint(" " + value + " is not a valid value for "
					+ getTokenName());
				Logging
					.errorPrint(" Valid values in Skill are YES, ALWAYS, DISPLAY, GUI, EXPORT, CSHEET");
				Logging
					.errorPrint(" assuming you meant GUI, please use GUI or DISPLAY (exact String, upper case) in the LST file");
				Logging.errorPrint(" This will break after PCGen 5.12");
			}
			skill.setVisibility(Visibility.DISPLAY_ONLY);
		}
		else if (visType.startsWith("EXPORT"))
		{
			if (!value.equals("EXPORT"))
			{
				Logging.errorPrint("Abbreviation used in " + getTokenName()
					+ " in Skill");
				Logging.errorPrint(" " + value + " is not a valid value for "
					+ getTokenName());
				Logging
					.errorPrint(" Valid values in Skill are YES, ALWAYS, DISPLAY, GUI, EXPORT, CSHEET");
				Logging
					.errorPrint(" assuming you meant EXPORT, please use EXPORT or CSHEET (exact String, upper case) in the LST file");
				Logging.errorPrint(" This will break after PCGen 5.12");
			}
			skill.setVisibility(Visibility.OUTPUT_ONLY);
		}
		else if (visType.startsWith("CSHEET"))
		{
			if (!value.equals("CSHEET"))
			{
				Logging.errorPrint("Abbreviation used in " + getTokenName()
					+ " in Skill");
				Logging.errorPrint(" " + value + " is not a valid value for "
					+ getTokenName());
				Logging
					.errorPrint(" Valid values in Skill are YES, ALWAYS, DISPLAY, GUI, EXPORT, CSHEET");
				Logging
					.errorPrint(" assuming you meant CSHEET, please use EXPORT or CSHEET (exact String, upper case) in the LST file");
				Logging.errorPrint(" This will break after PCGen 5.12");
			}
			skill.setVisibility(Visibility.OUTPUT_ONLY);
		}
		else
		{
			Logging.errorPrint("Unexpected value used in " + getTokenName()
				+ " in Skill");
			Logging.errorPrint(" " + visType + " is not a valid value for "
				+ getTokenName());
			Logging
				.errorPrint(" Valid values in Skill are YES, ALWAYS, DISPLAY, GUI, EXPORT, CSHEET");
			Logging
				.errorPrint(" assuming you meant YES, please use YES (exact String, upper case) in the LST file");
			skill.setVisibility(Visibility.DEFAULT);
			return false;
		}

		String[] elements = value.split("\\|");

		if (elements.length > 1)
		{
			if (elements[1].equalsIgnoreCase("READONLY")
				&& !visType.startsWith("EXPORT"))
			{
				if (!elements[1].equals("READONLY"))
				{
					Logging
						.errorPrint("In Skill "
							+ getTokenName()
							+ " Use of lower case is deprecated in "
							+ getTokenName()
							+ ".  Please use 'READONLY' (exact String, upper case): "
							+ value);
				}
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
