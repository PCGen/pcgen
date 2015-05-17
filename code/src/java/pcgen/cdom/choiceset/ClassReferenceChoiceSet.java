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
import pcgen.cdom.enumeration.GroupingState;
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
 * Note: This is a transition class for a feature to be removed after 5.16 is
 * released. It is used only in the special case of a FAVOREDCLASS:CHOOSE in a
 * Race LST file.
 */
public class ClassReferenceChoiceSet implements PrimitiveChoiceSet<PCClass>
{

	/**
	 * The underlying Set of CDOMReferences that contain the objects in this
	 * ClassReferenceChoiceSet
	 */
	private final Set<CDOMReference<? extends PCClass>> classRefSet;

	/**
	 * Constructs a new ClassReferenceChoiceSet which contains the Set of
	 * objects contained within the given CDOMReferences. The CDOMReferences do
	 * not need to be resolved at the time of construction of the
	 * ClassReferenceChoiceSet.
	 * 
	 * This constructor is reference-semantic and value-semantic. Ownership of
	 * the Collection provided to this constructor is not transferred.
	 * Modification of the Collection (after this constructor completes) does
	 * not result in modifying the ClassReferenceChoiceSet, and the
	 * ClassReferenceChoiceSet will not modify the given Collection. However,
	 * strong references are maintained to the CDOMReference objects contained
	 * within the given Collection.
	 * 
	 * @param classRefCollection
	 *            A Collection of CDOMReferences which define the Set of objects
	 *            contained within the ClassReferenceChoiceSet
	 * @throws IllegalArgumentException
	 *             if the given Collection is null or empty.
	 */
	public ClassReferenceChoiceSet(
			Collection<? extends CDOMReference<? extends PCClass>> classRefCollection)
	{
		super();
		if (classRefCollection == null)
		{
			throw new IllegalArgumentException(
					"Choice Collection cannot be null");
		}
		if (classRefCollection.isEmpty())
		{
			throw new IllegalArgumentException(
					"Choice Collection cannot be empty");
		}
		classRefSet = new HashSet<CDOMReference<? extends PCClass>>(
				classRefCollection);
	}

	/**
	 * Returns a representation of this ClassReferenceChoiceSet, suitable for
	 * storing in an LST file.
	 * 
	 * @param useAny
	 *            use "ANY" for the global "ALL" reference when creating the LST
	 *            format
	 * @return A representation of this ClassReferenceChoiceSet, suitable for
	 *         storing in an LST file.
	 */
	@Override
	public String getLSTformat(boolean useAny)
	{
		Set<CDOMReference<?>> sortedSet = new TreeSet<CDOMReference<?>>(
				ReferenceUtilities.REFERENCE_SORTER);
		sortedSet.addAll(classRefSet);
		return ReferenceUtilities.joinLstFormat(sortedSet, Constants.COMMA,
				useAny);
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
	@Override
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
	 * Ownership of the Set returned by this method will be transferred to the
	 * calling object. Modification of the returned Set should not result in
	 * modifying the ClassReferenceChoiceSet, and modifying the
	 * ClassReferenceChoiceSet after the Set is returned should not modify the
	 * Set. However, modification of the PCClass objects contained within the
	 * returned set will result in modification of the PCClass objects contained
	 * within this ClassReferenceChoiceSet.
	 * 
	 * @param pc
	 *            The PlayerCharacter for which the choices in this
	 *            ClassReferenceChoiceSet should be returned.
	 * @return A Set containing the Objects which this ClassReferenceChoiceSet
	 *         contains.
	 */
	@Override
	public Set<PCClass> getSet(PlayerCharacter pc)
	{
		Set<PCClass> returnSet = new HashSet<PCClass>();
		for (CDOMReference<? extends PCClass> ref : classRefSet)
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
		return classRefSet.size();
	}

	/**
	 * Returns true if this ClassReferenceChoiceSet is equal to the given
	 * Object. Equality is defined as being another ClassReferenceChoiceSet
	 * object with equal underlying contents.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		if (obj instanceof ClassReferenceChoiceSet)
		{
			ClassReferenceChoiceSet other = (ClassReferenceChoiceSet) obj;
			return classRefSet.equals(other.classRefSet);
		}
		return false;
	}

	/**
	 * Returns the GroupingState for this ClassReferenceChoiceSet. The
	 * GroupingState indicates how this ClassReferenceChoiceSet can be combined
	 * with other PrimitiveChoiceSets.
	 * 
	 * @return The GroupingState for this ClassReferenceChoiceSet.
	 */
	@Override
	public GroupingState getGroupingState()
	{
		GroupingState state = GroupingState.EMPTY;
		for (CDOMReference<? extends PCClass> classRef : classRefSet)
		{
			state = state.add(classRef.getGroupingState());
		}
		return state.compound(GroupingState.ALLOWS_UNION);
	}
}
