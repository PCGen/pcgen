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

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;

/**
 * A ClassReferenceChoiceSet contains references to PCClass Objects.
 * 
 * The contents of a ClassReferenceChoiceSet is defined at construction of the
 * ClassReferenceChoiceSet. The contents of a ClassReferenceChoiceSet is fixed,
 * and will not vary by the PlayerCharacter used to resolve the
 * ClassReferenceChoiceSet.
 * 
 * @param <T>
 *            The class of object this ReferenceChoiceSet contains.
 */
public class ClassReferenceChoiceSet implements PrimitiveChoiceSet<PCClass>
{

	/**
	 * The underlying Set of CDOMReferences that contain the objects in this
	 * ClassReferenceChoiceSet
	 */
	private final Set<CDOMReference<? extends PCClass>> set;

	/**
	 * Constructs a new ClassReferenceChoiceSet which contains the Set of
	 * objects contained within the given CDOMReferences. The CDOMReferences do
	 * not need to be resolved at the time of construction of the
	 * ClassReferenceChoiceSet.
	 * 
	 * This constructor is reference-semantic, meaning that ownership of the
	 * Collection provided to this constructor is not transferred. Modification
	 * of the Collection (after this constructor completes) does not result in
	 * modifying the ClassReferenceChoiceSet, and the ClassReferenceChoiceSet
	 * will not modify the given Collection.
	 * 
	 * @param col
	 *            A Collection of CDOMReferences which define the Set of objects
	 *            contained within the ClassReferenceChoiceSet
	 * @throws IllegalArgumentException
	 *             if the given Collection is null or empty.
	 */
	public ClassReferenceChoiceSet(
			Collection<? extends CDOMReference<? extends PCClass>> col)
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
		set = new HashSet<CDOMReference<? extends PCClass>>(col);
	}

	/**
	 * Returns a representation of this ClassReferenceChoiceSet, suitable for
	 * storing in an LST file.
	 */
	public String getLSTformat()
	{
		Set<CDOMReference<?>> sortedSet = new TreeSet<CDOMReference<?>>(
				ReferenceUtilities.REFERENCE_SORTER);
		sortedSet.addAll(set);
		return ReferenceUtilities.joinLstFormat(sortedSet, Constants.COMMA);
	}

	/**
	 * The class of object this ClassReferenceChoiceSet contains.
	 * 
	 * The behavior of this method is undefined if the CDOMReference objects
	 * provided during the construction of this ClassReferenceChoiceSet are not
	 * yet resolved.
	 * 
	 * @return The class of object this ClassReferenceChoiceSet contains.
	 */
	public Class<PCClass> getChoiceClass()
	{
		return PCClass.class;
	}

	/**
	 * Returns a Set containing the Objects which this ClassReferenceChoiceSet
	 * contains. The contents of a ClassReferenceChoiceSet is fixed, and will
	 * not vary by the PlayerCharacter used to resolve the
	 * ClassReferenceChoiceSet.
	 * 
	 * The behavior of this method is undefined if the CDOMReference objects
	 * provided during the construction of this ClassReferenceChoiceSet are not
	 * yet resolved.
	 * 
	 * This method is reference-semantic, meaning that ownership of the Set
	 * returned by this method will be transferred to the calling object.
	 * Modification of the returned Set should not result in modifying the
	 * ClassReferenceChoiceSet, and modifying the ClassReferenceChoiceSet after
	 * the Set is returned should not modify the Set.
	 * 
	 * @return A Set containing the Objects which this ClassReferenceChoiceSet
	 *         contains.
	 */
	public Set<PCClass> getSet(PlayerCharacter pc)
	{
		Set<PCClass> returnSet = new HashSet<PCClass>();
		for (CDOMReference<? extends PCClass> ref : set)
		{
			returnSet.addAll(ref.getContainedObjects());
		}
		return returnSet;
	}

	/**
	 * Returns the consistent-with-equals hashCode for this
	 * ClassReferenceChoiceSet
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return set.size();
	}

	/**
	 * Returns true if this ClassReferenceChoiceSet is equal to the given
	 * Object. Equality is defined as being another ClassReferenceChoiceSet
	 * object with equal underlying contents.
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
		if (o instanceof ClassReferenceChoiceSet)
		{
			ClassReferenceChoiceSet other = (ClassReferenceChoiceSet) o;
			return set.equals(other.set);
		}
		return false;
	}
}
