package pcgen.cdom.list;

import pcgen.cdom.base.CDOMListObject;
import pcgen.core.PCTemplate;

public class PCTemplateList extends CDOMListObject<PCTemplate>
{

	public Class<PCTemplate> getListClass()
	{
		return PCTemplate.class;
	}

	@Override
	public boolean isType(String str)
	{
		return false;
	}

	// No additional Functionality :)

}
