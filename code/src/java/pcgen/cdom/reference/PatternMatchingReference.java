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
import java.util.Objects;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.enumeration.GroupingState;

/**
 * A PatternMatchingReference is a CDOMReference that matches objects based on a
 * pattern of their key.
 * 
 * An underlying start list is provided during construction of the
 * PatternMatchingReference. Generally, this will be the CDOMAllRef for the
 * class of object underlying this PatternMatchingReference.
 * 
 * @param <T>
 *            The class of object underlying this PatternMatchingReference.
 */
public class PatternMatchingReference<T extends Loadable> extends CDOMReference<T>
{

	/**
	 * The CDOMGroupRef containing the underlying list of objects from which
	 * this PatternMatchingReference will draw.
	 */
	private final CDOMGroupRef<T> all;

	/**
	 * The pattern used to match against the key of objects from the underlying
	 * CDOMGroupRef
	 */
	private final String pattern;

	/*
	 * CONSIDER is it necessary/useful to cache the results of the pattern
	 * match? If that is done, under what conditions does the cache need to be
	 * invalidated (how can the underlying CDOMGroupRef be known to not have
	 * been modified)?
	 */

	/**
	 * Constructs a new PatternMatchingReference
	 * 
	 * @param startingGroup
	 *            The underlying list of objects from which this
	 *            PatternMatchingReference will draw.
	 * @param patternText
	 *            The pattern used to identify items which this
	 *            PatternMatchingReference will contain. Note that this pattern
	 *            must end with the PCGen pattern characters (defined by
	 *            Constants.PERCENT)
	 * @throws IllegalArgumentException
	 *             if the starting group is null or the provided pattern does
	 *             not end with the PCGen pattern characters
	 */
	public PatternMatchingReference(CDOMGroupRef<T> startingGroup, String patternText)
	{
		super(patternText);
		Objects.requireNonNull(startingGroup, "Starting Group cannot be null in PatternMatchingReference");
		all = startingGroup;
		String lstPattern = Constants.PERCENT;
		int patternchar = patternText.length() - lstPattern.length();
		if (patternText.indexOf(lstPattern) != patternchar)
		{
			throw new IllegalArgumentException("Pattern for PatternMatchingReference must end with " + lstPattern);
		}
		pattern = patternText.substring(0, patternchar);
	}

	/**
	 * Throws an exception. This method may not be called because a
	 * PatternMatchingReference is resolved based on the pattern provided at
	 * construction.
	 * 
	 * @param item
	 *            ignored
	 * @throws IllegalStateException
	 *             because a PatternMatchingReference is resolved based on the
	 *             pattern provided at construction.
	 */
	@Override
	public void addResolution(T item)
	{
		throw new IllegalStateException("Cannot add resolution to PatternMatchingReference");
	}

	/**
	 * Returns true if the given Object is included in the Collection of Objects
	 * to which this PatternMatchingReference refers.
	 * 
	 * Note that the behavior of this class is undefined if the CDOMGroupRef
	 * underlying this PatternMatchingReference has not yet been resolved.
	 * 
	 * @param item
	 *            The object to be tested to see if it is referred to by this
	 *            PatternMatchingReference.
	 * @return true if the given Object is included in the Collection of Objects
	 *         to which this PatternMatchingReference refers; false otherwise.
	 */
	@Override
	public boolean contains(T item)
	{
		return all.contains(item) && item.getKeyName().startsWith(pattern);
	}

	/**
	 * Returns a Collection containing the Objects to which this
	 * PatternMatchingReference refers.
	 * 
	 * This method is reference-semantic, meaning that ownership of the
	 * Collection returned by this method is transferred to the calling object.
	 * Modification of the returned Collection should not result in modifying
	 * the PatternMatchingReference, and modifying the PatternMatchingReference
	 * after the Collection is returned should not modify the Collection.
	 * 
	 * Note that the behavior of this class is undefined if the CDOMGroupRef
	 * underlying this PatternMatchingReference has not yet been resolved.
	 * 
	 * @return A Collection containing the Objects to which this
	 *         PatternMatchingReference refers.
	 */
	@Override
	public Collection<T> getContainedObjects()
	{
		List<T> list = new ArrayList<>();
		for (T obj : all.getContainedObjects())
		{
			if (obj.getKeyName().startsWith(pattern))
			{
				list.add(obj);
			}
		}
		return list;
	}

	/**
	 * Returns a representation of this PatternMatchingReference, suitable for
	 * storing in an LST file.
	 * 
	 * Note that this will return the pattern String provided during
	 * construction of the PatternMatchingReference.
	 * 
	 * @return A representation of this PatternMatchingReference, suitable for
	 *         storing in an LST file.
	 */
	@Override
	public String getLSTformat(boolean useAny)
	{
		return getName();
	}

	/**
	 * Returns the count of the number of objects included in the Collection of
	 * Objects to which this PatternMatchingReference refers.
	 * 
	 * Note that the behavior of this class is undefined if the CDOMGroupRef
	 * underlying this PatternMatchingReference has not yet been resolved.
	 * 
	 * @return The count of the number of objects included in the Collection of
	 *         Objects to which this PatternMatchingReference refers.
	 */
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
	public boolean equals(Object obj)
	{
		if (obj instanceof PatternMatchingReference<?> other)
		{
			return getReferenceClass().equals(other.getReferenceClass()) && all.equals(other.all)
				&& pattern.equals(other.pattern);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return getReferenceClass().hashCode() ^ pattern.hashCode();
	}

	/**
	 * Returns the GroupingState for this PatternMatchingReference. The
	 * GroupingState indicates how this PatternMatchingReference can be combined
	 * with other PrimitiveChoiceFilters.
	 * 
	 * @return The GroupingState for this PatternMatchingReference.
	 */
	@Override
	public GroupingState getGroupingState()
	{
		return GroupingState.ANY;
	}

	@Override
	public String getChoice()
	{
		return null;
	}

	@Override
	public Class<T> getReferenceClass()
	{
		return all.getReferenceClass();
	}

	@Override
	public String getReferenceDescription()
	{
		return all.getReferenceDescription() + " (Pattern " + pattern + ")";
	}

	@Override
	public String getPersistentFormat()
	{
		return all.getPersistentFormat();
	}
}
