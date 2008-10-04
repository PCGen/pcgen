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
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;

/**
 * An AssociationReference is a CDOMReference that points to associations on a
 * PObject.
 * 
 * An Association (in PCGen terms) is something used to store choices made on an
 * object. Thus, a Feat like Martial Weapon Proficiency which allows the
 * selection of Weapon Proficiencies stores the identifier of each selected
 * Weapon Proficiency as an association on the Feat. Other items may refer to
 * this association (such as a selection of choices providing input into CSKILL:
 * through the LIST entry)
 * 
 * @param <T>
 *            The Class of the underlying objects contained by this
 *            AssociationReference
 */
public class AssociationReference<T extends CDOMObject> extends
		CDOMReference<T>
{
	/**
	 * The CDOMGroupRef used to establish the potential objects that could be
	 * stored in associations of the given PObject
	 */
	private final CDOMGroupRef<T> all;

	/**
	 * The PObject from which identifiers should be extracted to establish which
	 * of the objects in the potential object CDOMGroupRef this
	 * AssociationReference actually references.
	 */
	private final PObject referenceObj;

	/**
	 * Constructs a new AssociationReference, which will reference the
	 * associations on the given object, with the association used as an
	 * identifier to extract objects from the given starting CDOMGroupRef of
	 * objects.
	 * 
	 * This AssociationReference will reference the same Class of object as the
	 * Class of object referenced by the given CDOMGroupRef.
	 * 
	 * @param start
	 *            The starting CDOMGroupRef, which references the potential
	 *            objects that could be stored in associations of the given
	 *            PObject
	 * @param ref
	 *            The PObject from which identifiers should be extracted to
	 *            establish which of the objects in the given CDOMGroupRef this
	 *            AssociationReference actually references.
	 * @throws NullPointerException
	 *             if the starting reference provided is null
	 * @throws IllegalArgumentException
	 *             if the given PObject is null
	 */
	public AssociationReference(CDOMGroupRef<T> start, PObject ref)
	{
		super(start.getReferenceClass(), "LIST");
		all = start;
		if (ref == null)
		{
			throw new IllegalArgumentException(
					"PObject in AssociationReference cannot be null");
		}
		referenceObj = ref;
	}

	/**
	 * Throws an exception. This method may not be called because a
	 * AssociationReference is resolved by association.
	 * 
	 * @param obj
	 *            ignored
	 * @throws IllegalStateException
	 *             because a AssociationReference is resolved by association.
	 */
	@Override
	public void addResolution(T obj)
	{
		throw new IllegalStateException(
				"Cannot add resolution to AssociationReference");
	}

	/**
	 * Returns true if the given Object matches one of the objects to which this
	 * AssociationReference refers.
	 * 
	 * @param obj
	 *            The object to be tested to see if it matches one of the
	 *            objects to which this AssociationReference refers.
	 * @return true if the given Object is one of the objects to which this
	 *         AssociationReference refers; false otherwise.
	 */
	@Override
	public boolean contains(T obj)
	{
		if (!all.contains(obj))
		{
			return false;
		}
		//CONSIDER once getActiveEquivalent goes away, this should be AssocationStore
		PlayerCharacter as = Globals.getCurrentPC();
		String key = obj.getKeyName();
		PObject active = referenceObj.getActiveEquivalent(as);
		for (String assoc : as.getAssociationList(active))
		{
			if (key.equalsIgnoreCase(assoc))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a Collection containing the Objects to which this
	 * AssociationReference refers.
	 * 
	 * This method is reference-semantic, meaning that ownership of the
	 * Collection returned by this method is transferred to the calling object.
	 * Modification of the returned Collection should not result in modifying
	 * the AssociationReference, and modifying the AssociationReference after
	 * the Collection is returned should not modify the Collection.
	 * 
	 * @return A Collection containing the Objects to which this
	 *         AssociationReference refers.
	 */
	@Override
	public Collection<T> getContainedObjects()
	{
		List<T> list = new ArrayList<T>();
		PlayerCharacter as = Globals.getCurrentPC();
		PObject active = referenceObj.getActiveEquivalent(as);
		List<String> associationList = as.getAssociationList(active);
		for (T obj : all.getContainedObjects())
		{
			String key = obj.getKeyName();
			for (String assoc : associationList)
			{
				if (key.equalsIgnoreCase(assoc))
				{
					list.add(obj);
					break;
				}
			}
		}
		return list;
	}

	/**
	 * Returns a representation of this AssociationReference, suitable for
	 * storing in an LST file.
	 * 
	 * @see pcgen.cdom.base.CDOMReference#getLSTformat()
	 */
	@Override
	public String getLSTformat()
	{
		return getName();
	}

	/**
	 * Returns the count of the number of objects included in the Collection of
	 * Objects to which this AssociationReference refers.
	 * 
	 * Note that the behavior of this class is undefined if the
	 * AssociationReference has not yet been resolved.
	 * 
	 * @return The count of the number of objects included in the Collection of
	 *         Objects to which this AssociationReference refers.
	 */
	@Override
	public int getObjectCount()
	{
		PlayerCharacter as = Globals.getCurrentPC();
		PObject active = referenceObj.getActiveEquivalent(as);
		return as.getDetailedAssociationCount(active);
	}

	/**
	 * Returns true if this AssociationReference is equal to the given Object.
	 * Equality is defined as being another AssociationReference object with
	 * equal starting Reference Set and equal reference object. This is NOT a
	 * deep .equals, in that the actual contents of this AssociationReference
	 * are not tested.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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

	/**
	 * Returns the consistent-with-equals hashCode for this AssociationReference
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return getReferenceClass().hashCode() ^ referenceObj.hashCode();
	}

}
