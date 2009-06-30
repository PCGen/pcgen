package pcgen.core.analysis;

import pcgen.core.PCAlignment;
import pcgen.core.PObject;
import pcgen.core.SettingsHandler;
import pcgen.core.prereq.Prerequisite;

public class RaceAlignment
{

	public static boolean canBeAlignment(PObject r, PCAlignment align)
	{
		if (r.hasPrerequisites())
		{
			for (Prerequisite prereq : r.getPrerequisiteList())
			{
				if ("ALIGN".equalsIgnoreCase(prereq.getKind()))
				{
					PCAlignment desiredAlignment;
					String prereqKey = prereq.getKey();
					try
					{
						final int index = Integer.parseInt(prereqKey);
						desiredAlignment = SettingsHandler.getGame()
								.getAlignmentAtIndex(index);
					}
					catch (NumberFormatException ex)
					{
						desiredAlignment = SettingsHandler.getGame()
								.getAlignment(prereqKey);
					}
	
					return desiredAlignment.equals(align);
				}
			}
		}
	
		return true;
	}

	public static boolean hasAlignmentRestriction(PObject r)
	{
		if (r.hasPrerequisites())
		{
			for (Prerequisite prereq : r.getPrerequisiteList())
			{
				if ("ALIGN".equalsIgnoreCase(prereq.getKind()))
				{
					return true;
				}
			}
		}
	
		return false;
	}

}
