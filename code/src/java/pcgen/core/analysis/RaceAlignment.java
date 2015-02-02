package pcgen.core.analysis;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PCAlignment;
import pcgen.core.prereq.Prerequisite;

public class RaceAlignment
{

	public static boolean canBeAlignment(CDOMObject r, PCAlignment align)
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

}
