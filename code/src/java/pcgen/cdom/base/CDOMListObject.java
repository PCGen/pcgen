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
		if (o == this)
		{
			return true;
		}
		if (o instanceof CDOMListObject)
		{
			CDOMListObject<?> other = (CDOMListObject<?>) o;
			return o.getClass().equals(getClass())
					&& other.getListClass().equals(getListClass())
					&& getKeyName().equals(other.getKeyName());
		}
		return false;
	}
}
