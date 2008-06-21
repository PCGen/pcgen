package pcgen.cdom.inst;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;

public class ObjectCache extends CDOMObject
{

	@Override
	public boolean isType(String str)
	{
		return false;
	}

	public void initializeListFor(ListKey<?> lk)
	{
		listChar.initializeListFor(lk);
	}

}
