/*
 * Copyright 2007, 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
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
