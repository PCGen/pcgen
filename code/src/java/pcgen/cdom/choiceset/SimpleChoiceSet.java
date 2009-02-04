/*
 * Copyright 2006 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * 
 * Created on October 29, 2006.
 * 
 * Current Ver: $Revision: 1111 $ Last Editor: $Author: boomer70 $ Last Edited:
 * $Date: 2006-06-22 21:22:44 -0400 (Thu, 22 Jun 2006) $
 */
package pcgen.cdom.choiceset;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.core.PlayerCharacter;

/**
 * A SimpleChoiceSet contains Objects
 * 
 * The contents of a SimpleChoiceSet is defined at construction of the
 * SimpleChoiceSet. The contents of a SimpleChoiceSet is fixed, and will not
 * vary by the PlayerCharacter used to resolve the SimpleChoiceSet.
 * 
 * @param <T>
 *            The class of object this SimpleChoiceSet contains.
 */
public class SimpleChoiceSet<T extends Comparable<T>> implements PrimitiveChoiceSet<T>
{

	/**
	 * The underlying Set of objects in this SimpleChoiceSet
	 */
	private final Set<T> set;

	/**
	 * Constructs a new SimpleChoiceSet which contains the Set of objects.
	 * 
	 * This constructor is both reference-semantic and value-semantic. Ownership
	 * of the Collection provided to this constructor is not transferred and
	 * this constructor will not modify the given Collection. Modification of
	 * the Collection (after this constructor completes) does not result in
	 * modifying the SimpleChoiceSet, and the SimpleChoiceSet will not modify
	 * the given Collection. However, this SimpleChoiceSet will maintain hard
	 * references to the objects contained within the given Collection.
	 * 
	 * @param col
	 *            A Collection of objects contained within the SimpleChoiceSet
	 * @throws IllegalArgumentException
	 *             if the given Collection is null or empty.
	 */
	public SimpleChoiceSet(Collection<? extends T> col)
	{
		super();
		if (col == null)
		{
			throw new IllegalArgumentException(
					"Choice Collection cannot be null");
		}
		if (col.isEmpty())
		{
			throw new IllegalArgumentException(
					"Choice Collection cannot be empty");
		}
		set = new HashSet<T>(col);
	}

	/**
	 * Returns a representation of this SimpleChoiceSet, suitable for storing in
	 * an LST file.
	 */
	public String getLSTformat(boolean useAny)
	{
		return StringUtil.join(new TreeSet<T>(set), Constants.COMMA);
	}

	/**
	 * The class of object this SimpleChoiceSet contains.
	 * 
	 * @return The class of object this SimpleChoiceSet contains.
	 */
	public Class<T> getChoiceClass()
	{
		return (Class<T>) (set == null ? null : set.iterator().next()
				.getClass());
	}

	/**
	 * Returns a Set containing the Objects which this SimpleChoiceSet contains.
	 * The contents of a SimpleChoiceSet is fixed, and will not vary by the
	 * PlayerCharacter used to resolve the SimpleChoiceSet.
	 * 
	 * Ownership of the Set returned by this method will be transferred to the
	 * calling object. Modification of the returned Set should not result in
	 * modifying the SimpleChoiceSet, and modifying the SimpleChoiceSet after
	 * the Set is returned should not modify the Set. However, the objects
	 * contained within the set are transferred by reference, so modification of
	 * the objects contained in the Set will result in modification of the
	 * objects within this SimpleChoiceSet.
	 * 
	 * @return A Set containing the Objects which this SimpleChoiceSet contains.
	 */
	public Set<T> getSet(PlayerCharacter pc)
	{
		return new HashSet<T>(set);
	}

	/**
	 * Returns the consistent-with-equals hashCode for this SimpleChoiceSet
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return set.size();
	}

	/**
	 * Returns true if this SimpleChoiceSet is equal to the given Object.
	 * Equality is defined as being another SimpleChoiceSet object with equal
	 * underlying contents.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (o instanceof SimpleChoiceSet)
		{
			SimpleChoiceSet<?> other = (SimpleChoiceSet<?>) o;
			return set.equals(other.set);
		}
		return false;
	}
}
