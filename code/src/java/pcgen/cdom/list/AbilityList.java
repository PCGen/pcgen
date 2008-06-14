package pcgen.cdom.list;

import pcgen.cdom.base.CDOMListObject;
import pcgen.core.Ability;

public class AbilityList extends CDOMListObject<Ability>
{

	public Class<Ability> getListClass()
	{
		return Ability.class;
	}

	@Override
	public boolean isType(String str)
	{
		return false;
	}

	// No additional Functionality :)

}
