package pcgen.cdom.content;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.SizeAdjustment;

public class DeferredResolution
{
	
	CDOMObject baseObject;
	ObjectKey<SizeAdjustment> key;

	public DeferredResolution(CDOMObject obj, ObjectKey<SizeAdjustment> size)
	{
		baseObject = obj;
		key = size;
	}
	
	public SizeAdjustment resolve()
	{
		return baseObject.getSafe(key);
	}

}
