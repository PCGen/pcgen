package pcgen.cdom.base;


public abstract class CDOMListObject<T extends CDOMObject> extends CDOMObject
		implements CDOMList<T>
{

	@Override
	public int hashCode()
	{
		return getClass().hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		return o != null && o.getClass().equals(getClass());
	}
}
