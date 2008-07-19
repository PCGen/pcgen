package pcgen.core;

import java.util.List;

import pcgen.base.util.HashMapToList;

public class AssociationSupport implements AssociationStore
{

	private final HashMapToList<Object, Object> assocMap =
			new HashMapToList<Object, Object>();

	public void addAssociation(Object obj, Object o)
	{
		assocMap.addToListFor(obj, o);
	}

	public void removeAssociation(Object obj, Object o)
	{
		assocMap.removeFromListFor(obj, o);
	}

	public List<Object> removeAllAssociations(Object obj)
	{
		return assocMap.removeListFor(obj);
	}

	public int getAssociationCount(Object obj)
	{
		return assocMap.sizeOfListFor(obj);
	}

	public boolean hasAssociations(Object obj)
	{
		return assocMap.containsListFor(obj);
	}

	public List<Object> getAssociationList(Object obj)
	{
		return assocMap.getListFor(obj);
	}

	public boolean containsAssociated(Object obj, Object o)
	{
		return assocMap.containsInList(obj, o);
	}
}
