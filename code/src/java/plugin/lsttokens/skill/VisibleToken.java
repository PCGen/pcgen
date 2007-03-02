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
				Logging.errorPrint("In " + getTokenName()
					+ " Use of abbrevations is deprecated in " + getTokenName()
					+ ".  Please use 'YES' (in all CAPS)");
			}
			skill.setVisibility(Visibility.DEFAULT);
		}
		else if (visType.startsWith("ALWAYS"))
		{
			if (!value.equals("ALWAYS"))
			{
				Logging.errorPrint("In " + getTokenName()
					+ " Use of abbrevations is deprecated in " + getTokenName()
					+ ".  Please use 'YES' or 'ALWAYS' (in all CAPS)");
				skill.setVisibility(Visibility.DEFAULT);
			}
		}
		else if (value.equals("DISPLAY"))
		{
			skill.setVisibility(Visibility.DISPLAY_ONLY);
		}
		else if (visType.startsWith("GUI"))
		{
			if (!value.equals("GUI"))
			{
				Logging.errorPrint("In " + getTokenName()
					+ " Use of abbrevations is deprecated in " + getTokenName()
					+ ".  Please use 'GUI' (in all CAPS)");
			}
			skill.setVisibility(Visibility.DISPLAY_ONLY);
		}
		else if (visType.startsWith("EXPORT"))
		{
			if (!value.equals("EXPORT"))
			{
				Logging.errorPrint("In " + getTokenName()
					+ " Use of abbrevations is deprecated in " + getTokenName()
					+ ".  Please use 'EXPORT' (in all CAPS)");
			}
			skill.setVisibility(Visibility.OUTPUT_ONLY);
		}
		else if (visType.startsWith("CSHEET"))
		{
			if (!value.equals("CSHEET"))
			{
				Logging.errorPrint("In " + getTokenName()
					+ " Use of abbrevations is deprecated in " + getTokenName()
					+ ".  Please use 'CSHEET' or 'EXPORT' (in all CAPS)");
			}
			skill.setVisibility(Visibility.OUTPUT_ONLY);
		}
		else
		{
			Logging.errorPrint("Invalid Visibility: " + value);
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
					Logging.errorPrint("In " + getTokenName()
						+ " Use of lower case is deprecated in "
						+ getTokenName()
						+ ".  Please use 'READONLY' (in all CAPS): " + value);
				}
				skill.setReadOnly(true);
			}
			else
			{
				Logging
					.errorPrint("Invalid Combination in "
						+ getTokenName()
						+ ".  | must separate READONLY and cannot be used with EXPORT: "
						+ value);
				return false;
			}
		}
		return true;
	}
}
