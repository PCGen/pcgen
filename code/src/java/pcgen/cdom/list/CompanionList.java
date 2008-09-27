package pcgen.cdom.list;

import pcgen.cdom.base.CDOMListObject;
import pcgen.core.Race;

public class CompanionList extends CDOMListObject<Race>
{

	public Class<Race> getListClass()
	{
		return Race.class;
	}

	/**
	 * Lists never have a Type, so this returns false
	 */
	@Override
	public boolean isType(String str)
	{
		return false;
	}

	// No additional Functionality :)

}
