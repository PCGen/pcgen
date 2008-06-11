package pcgen.cdom.reference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;

public class PatternMatchingReference<T extends CDOMObject> extends
		CDOMReference<T>
{

	private final CDOMGroupRef<T> all;
	private final String pattern;

	public PatternMatchingReference(Class<T> cl, CDOMGroupRef<T> start,
			String tokText)
	{
		super(cl, tokText);
		if (start == null)
		{
			throw new IllegalArgumentException(
					"Starting Group cannot be null in PatternMatchingReference");
		}
		all = start;
		String lstPattern = Constants.LST_PATTERN;
		int patternchar = tokText.length() - lstPattern.length();
		if (tokText.indexOf(lstPattern) != patternchar)
		{
			throw new IllegalArgumentException(
					"Pattern for PatternMatchingReference must end with "
							+ lstPattern);
		}
		pattern = tokText.substring(0, patternchar);
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
		return all.contains(obj) && obj.getKeyName().startsWith(pattern);
	}

	@Override
	public Collection<T> getContainedObjects()
	{
		List<T> list = new ArrayList<T>();
		for (T obj : all.getContainedObjects())
		{
			if (obj.getKeyName().startsWith(pattern))
			{
				list.add(obj);
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
		int count = 0;
		for (T obj : all.getContainedObjects())
		{
			if (obj.getKeyName().startsWith(pattern))
			{
				count++;
			}
		}
		return count;
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
		if (o instanceof PatternMatchingReference)
		{
			PatternMatchingReference<?> other = (PatternMatchingReference<?>) o;
			return getReferenceClass().equals(other.getReferenceClass())
					&& getName().equals(other.getName())
					&& all.equals(other.all) && pattern.equals(other.pattern);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return getReferenceClass().hashCode() ^ pattern.hashCode();
	}
}
