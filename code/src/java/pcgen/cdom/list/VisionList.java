package pcgen.cdom.list;

import pcgen.cdom.base.CDOMListObject;
import pcgen.core.Vision;

public class VisionList extends CDOMListObject<Vision>
{

	public Class<Vision> getListClass()
	{
		return Vision.class;
	}

	@Override
	public boolean isType(String str)
	{
		return false;
	}

	// No additional Functionality :)

}
