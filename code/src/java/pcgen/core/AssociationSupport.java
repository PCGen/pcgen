package pcgen.core;

import java.util.List;

import pcgen.base.util.HashMapToList;

public class AssociationSupport implements AssocStore
{

	private final HashMapToList<Object, Object> assocMap =
			new HashMapToList<Object, Object>();

	public void addAssoc(Object obj, Object o)
	{
		assocMap.addToListFor(obj, o);
	}

	public void removeAssoc(Object obj, Object o)
	{
		assocMap.removeFromListFor(obj, o);
	}

	public List<Object> removeAllAssocs(Object obj)
	{
		return assocMap.removeListFor(obj);
	}

	public int getAssocCount(Object obj)
	{
		return assocMap.sizeOfListFor(obj);
	}

	public boolean hasAssocs(Object obj)
	{
		return assocMap.containsListFor(obj);
	}

	public List<Object> getAssocList(Object obj)
	{
		return assocMap.getListFor(obj);
	}

	public boolean containsAssoc(Object obj, Object o)
	{
		return assocMap.containsInList(obj, o);
	}
}
