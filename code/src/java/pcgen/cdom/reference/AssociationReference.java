package pcgen.cdom.reference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.core.PObject;

public class AssociationReference<T extends CDOMObject> extends
		CDOMReference<T>
{
	private final CDOMGroupRef<T> all;
	private final PObject referenceObj;

	public AssociationReference(Class<T> cl, CDOMGroupRef<T> start, PObject ref)
	{
		super(cl, "LIST");
		if (start == null)
		{
			throw new IllegalArgumentException(
					"Starting Group cannot be null in PatternMatchingReference");
		}
		all = start;
		if (ref == null)
		{
			throw new IllegalArgumentException(
					"PObject in AssociationReference cannot be null");
		}
		referenceObj = ref;
	}

	@Override
	public void addResolution(T obj)
	{
		throw new IllegalStateException(
				"Cannot add resolution to PatternMatchingReference");
	}

	@Override
	public boolean contains(T obj)
	{
		if (!all.contains(obj))
		{
			return false;
		}
		int assocCount = referenceObj.getAssociatedCount();
		String key = obj.getKeyName();
		for (int e = 0; e < assocCount; ++e)
		{
			if (key.equalsIgnoreCase(referenceObj.getAssociated(e)))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public Collection<T> getContainedObjects()
	{
		List<T> list = new ArrayList<T>();
		int assocCount = referenceObj.getAssociatedCount();
		for (T obj : all.getContainedObjects())
		{
			String key = obj.getKeyName();
			for (int e = 0; e < assocCount; ++e)
			{
				if (key.equalsIgnoreCase(referenceObj.getAssociated(e)))
				{
					list.add(obj);
					break;
				}
			}
		}
		return list;
	}

	@Override
	public String getLSTformat()
	{
		return getName();
	}

	@Override
	public int getObjectCount()
	{
		return referenceObj.getAssociatedCount();
	}

	@Override
	public String getPrimitiveFormat()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof AssociationReference)
		{
			AssociationReference<?> other = (AssociationReference<?>) o;
			return getReferenceClass().equals(other.getReferenceClass())
					&& getName().equals(other.getName())
					&& all.equals(other.all)
					&& referenceObj.equals(other.referenceObj);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return getReferenceClass().hashCode() ^ referenceObj.hashCode();
	}

}
