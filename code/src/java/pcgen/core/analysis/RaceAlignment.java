package pcgen.core.analysis;

import pcgen.core.PCAlignment;
import pcgen.core.PObject;
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
					return align.equals(AlignmentConverter
							.getPCAlignment(prereq.getKey()));
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
