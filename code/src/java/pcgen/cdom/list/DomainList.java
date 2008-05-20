package pcgen.cdom.list;

import pcgen.cdom.base.CDOMListObject;
import pcgen.core.Domain;

public class DomainList extends CDOMListObject<Domain>
{

	public Class<Domain> getListClass()
	{
		return Domain.class;
	}

	@Override
	public boolean isType(String str)
	{
		return false;
	}

	// No additional Functionality :)

}
