/*
 * Copyright 2010 (C) Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.cdom.base.ClassIdentity;
import pcgen.cdom.base.Loadable;

/**
 * A ManufacturableFactory is a Factory for Loadable objects.
 * 
 * @param <T>
 *            The Format (class) of object that this ManufacturableFactory creates. Note
 *            that this is NOT sufficient to uniquely identify the SelectionCreator... to
 *            do that please inspect getReferenceIdentity, not the just the plain Generics
 *            on the ManufacturableFactory.
 */
public interface ManufacturableFactory<T extends Loadable> extends SelectionCreator<T>
{
	/**
	 * Constructs a new instance of the object that this ManufacturableFactory creates.
	 * Any other initialization as necessary by the ClassIdentity of this
	 * ManufacturableFactory should be performed by this method as well.
	 * 
	 * @return A new instance of the object that this ManufacturableFactory creates
	 */
    T newInstance();

	/**
	 * Returns true if the given item is a member of items potentially created by this
	 * ManufacturableFactory.
	 * 
	 * Note that this does NOT imply that the given object was actually constructed by
	 * this ManufacturableFactory; this method does NOT require the ManufacturableFactory
	 * to maintain a list of all items that have been constructed.
	 * 
	 * @param item
	 *            The item to be checked to see if is potentially created by this
	 *            ManufacturableFactory
	 * @return true if the given item is a member of items potentially created by this
	 *         ManufacturableFactory; false otherwise
	 */
    boolean isMember(T item);

	/**
	 * Resolves the given CDOMSingleRef with the object of the given name in the given
	 * ReferenceManufacturer. Any issues will be checked with the UnconstructedValidator
	 * to see if they are permissible errors.
	 * 
	 * @param rm
	 *            The ReferenceManufacturer that should be used to resolve the given
	 *            CDOMSingleRef
	 * @param name
	 *            The name of the object to be loaded into the CDOMSingleRef
	 * @param reference
	 *            The CDOMSingleRef that will be resolved during this method call
	 * @param validator
	 *            The UnconstructedValidator that will be checked to determine if the
	 *            given object is permitted to be unconstructed
	 * @return true if the given CDOMSingleRef was successfully resolved; false otherwise
	 */
    boolean resolve(ReferenceManufacturer<T> rm, String name, CDOMSingleRef<T> reference,
                    UnconstructedValidator validator);

	/**
	 * Populates the given ReferenceManufacturer with information from the parent
	 * ReferenceManufacturer (for ManuacturableFactory objects that serve items with a
	 * hierarchical Category). Any issues will be checked with the UnconstructedValidator
	 * to see if they are permissible errors.
	 * 
	 * @param parent
	 *            The parent ReferenceManufacturer from which references can be pulled
	 * @param rm
	 *            The ReferenceManufacturer for which references will be populated
	 * @param validator
	 *            The UnconstructedValidator that will be checked to determine if the
	 *            given object is permitted to be unconstructed
	 * @return true if this ManufactorableFactory was successfully populated; false
	 *         otherwise
	 */
    boolean populate(ReferenceManufacturer<T> parent, ReferenceManufacturer<T> rm,
                     UnconstructedValidator validator);

	/**
	 * Returns the parent ManufacturableFactory of this ManufacturableFactory (for
	 * ManuacturableFactory objects that serve items with a hierarchical Category).
	 * 
	 * null is a legal return value for ManuacturableFactory objects that do not have a
	 * parent.
	 * 
	 * @return The parent ManufacturableFactory of this ManufacturableFactory; null if no
	 *         parent
	 */
    ManufacturableFactory<T> getParent();

	/**
	 * Returns the persistent String that represents the Format of object constructed by
	 * this ManufacturableFactory. This must be consistent with the response to
	 * getReferenceIdentity()... but getReferenceIdentity() may only be available after a
	 * load is complete; whereas this will always be available.
	 * 
	 * @return The persistent String that represents the Format of object constructed by
	 *         this ManufacturableFactory
	 */
    String getPersistentFormat();

	/**
	 * Returns the ClassIdentity for this ManufacturableFactory. This is more specific
	 * than the ReferenceClass, since this ClassIdentity will also contain information
	 * about the Category for a Categorized object.
	 * 
	 * WARNING: For a ManufacturableFactory, this is not guaranteed to be valid until
	 * after load is complete.
	 * 
	 * @return The ClassIdentity for this ManufacturableFactory
	 */
	@Override
    ClassIdentity<T> getReferenceIdentity();
}
