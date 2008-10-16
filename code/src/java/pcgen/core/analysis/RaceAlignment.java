package pcgen.core.analysis;

import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.prereq.Prerequisite;

public class RaceAlignment
{

	public static boolean canBeAlignment(Race r, final String aString)
	{
		if (r.hasPrerequisites())
		{
			for (Prerequisite prereq : r.getPrerequisiteList())
			{
				if ("ALIGN".equalsIgnoreCase(prereq.getKind()))
				{
					String alignStr = aString;
					final String[] aligns =
							SettingsHandler.getGame().getAlignmentListStrings(
								false);
					try
					{
						final int align = Integer.parseInt(alignStr);
						alignStr = aligns[align];
					}
					catch (NumberFormatException ex)
					{
						// Do Nothing
					}
					String desiredAlignment = prereq.getKey();
					try
					{
						final int align = Integer.parseInt(desiredAlignment);
						desiredAlignment = aligns[align];
					}
					catch (NumberFormatException ex)
					{
						// Do Nothing
					}
	
					return desiredAlignment.equalsIgnoreCase(alignStr);
				}
			}
		}
	
		return true;
	}

}
