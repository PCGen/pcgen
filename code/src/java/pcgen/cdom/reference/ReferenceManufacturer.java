/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.reference;

import java.util.Collection;

import pcgen.cdom.base.PrereqObject;

/**
 * A ReferenceManufacturer is an object capable of creating CDOMReferences of a
 * given "form". That "form" includes a specific Class of CDOMObject, or a
 * specific Class/Category for Categorized CDOMObjects.
 * 
 * The Class serves two purposes.
 * 
 * 1) This provides a universal interface, so it simplifies the LoadContext
 * class structure/code under categorized/non-categorized conditions
 * 
 * 2) This provides a method of constructing references after the class (or
 * class and context) have been established. By separating these actions,
 * references can be constructed at a time that is independent of the
 * class/context resolution. This allows for tokens which can refer to both
 * categorized and non-categorized objects (e.g. QUALIFY) to operate clearly and
 * with methods that are Generic-friendly.
 * 
 * Behavioral Notes: It is expected that various methods will interact with each
 * other. This is especially true with respect to buildDeferredObjects(). It is
 * expected that object identifiers passed to constructIfNecessary(String) not
 * result in objects being constructed (with appropriate consequences to methods
 * like containsObject(T) ) before buildDeferredObjects() is called, but will
 * result in objects being constructed (with appropriate consequences to methods
 * like containsObject(T) ) after buildDeferredObjects() is called.
 * 
 * @param <T>
 *            The Class of object this ReferenceManufacturer can manufacture
 * @param <RT>
 *            The Class of Single Reference that this ReferenceManufacturer will
 *            produce
 */
public interface ReferenceManufacturer<T extends PrereqObject, RT extends CDOMSingleRef<T>>
{
	/**
	 * Constructs a new CDOMObject of the Class or Class/Category represented by
	 * this ReferenceManufacturer
	 * 
	 * Implementation Note: At this point, the "key" provided is likely to be
	 * the "display name" of an object, not the actual "KEY". This is due to the
	 * need to construct an object at the time it is first encountered, which is
	 * probably not the time at which the KEY is known (the intent is not to do
	 * "lookahead", as it fails under .MOD conditions anyway). In order to
	 * "rename" an object once a KEY is encountered, see renameObject(String, T)
	 * 
	 * @param key
	 *            The identifier of the CDOMObject to be constructed
	 * @return The new CDOMObject of the Class or Class/Category represented by
	 *         this ReferenceManufacturer
	 */
	public T constructObject(String key);

	/**
	 * Adds an object to the contents of this ReferenceManufacturer. This is
	 * used in conditions where this ReferenceManufacturer was not used to
	 * construct the object.
	 * 
	 * Implementation Note: There are various situations where this "external
	 * construction" may happen - the primary one being loading of "game mode"
	 * information like CDOMStat objects.
	 * 
	 * @param o
	 *            The object to be imported into this ReferenceManufacturer
	 * @param key
	 *            The identifier of the object to be imported into this
	 *            ReferenceManufacturer
	 */
	public void addObject(T o, String key);

	/**
	 * Returns true if this ReferenceManufacturer contains an object of the
	 * Class or Class/Category represented by this ReferenceManufacturer.
	 * 
	 * Note that this is testing *object* presence. This will not return true if
	 * a reference for the given identifier has been requested; it will only
	 * return true if an object with the given identifier has actually been
	 * constructed by or imported into this ReferenceManufacturer.
	 * 
	 * @param key
	 *            The identifier of the object to be checked if it is present in
	 *            this ReferenceManufacturer.
	 * @return true if this ReferenceManufacturer contains an object of the
	 *         Class or Class/Category represented by this
	 *         ReferenceManufacturer; false otherwise.
	 */
	public boolean containsObject(String key);

	/**
	 * Gets the object represented by the given identifier. Will return null if
	 * an object with the given identifier is not present in this
	 * ReferenceManufacturer.
	 * 
	 * Note that this is testing *object* presence. This will not return an
	 * object if a reference for the given identifier has been requested; it
	 * will only return true if an object with the given identifier has actually
	 * been constructed by or imported into this ReferenceManufacturer.
	 * 
	 * @param key
	 *            identifier of the object to be returned
	 * @return The object stored in this ReferenceManufacturer with the given
	 *         identifier, or null if this ReferenceManufacturer does not
	 *         contain an object with the given identifier.
	 */
	public T getObject(String key);

	/**
	 * Returns a Collection of all of the objects contained in this
	 * ReferenceManufacturer. This will not return null, it will return an empty
	 * list if no objects have been constructed by or imported into this
	 * ReferenceManufacturer.
	 * 
	 * @return A Collection of all of the objects contained in this
	 *         ReferenceManufacturer
	 */
	public Collection<T> getAllObjects();

	/**
	 * Changes the identifier for a given object, as stored in this
	 * ReferenceManufacturer.
	 * 
	 * @param key
	 *            The new identifier to be used for the given object
	 * @param o
	 *            The object for which the identifier in this
	 *            ReferenceManufacturer should be changed
	 */
	public void renameObject(String key, T o);

	/**
	 * Remove the given object from this ReferenceManufacturer. Returns true if
	 * the object was removed from this ReferenceManufacturer; false otherwise.
	 * 
	 * @param o
	 *            The object to be removed from this ReferenceManufacturer.
	 * @return true if the object was removed from this ReferenceManufacturer;
	 *         false otherwise.
	 */
	public boolean forgetObject(T o);

	/**
	 * Gets a reference to the Class or Class/Context provided by this
	 * ReferenceManufacturer. The reference will be a reference to the object
	 * identified by the given key.
	 * 
	 * @param key
	 *            The key used to identify the object to which the returned
	 *            CDOMReference will refer.
	 * @return A CDOMReference that refers to the object identified by the given
	 *         key
	 */
	public RT getReference(String key);

	/**
	 * Gets a reference to the Class or Class/Context provided by this
	 * ReferenceManufacturer. The reference will be a reference to the objects
	 * identified by the given types.
	 * 
	 * @param types
	 *            An array of the types of objects to which the returned
	 *            CDOMReference will refer.
	 * @return A CDOMReference which is intended to contain objects of a given
	 *         Type for the Class or Class/Context this ReferenceManufacturer
	 *         represents.
	 */
	public CDOMGroupRef<T> getTypeReference(String... types);

	/**
	 * Returns a CDOMReference for the given Class or Class/Context provided by
	 * this ReferenceManufacturer.
	 * 
	 * @return A CDOMReference which is intended to contain all the objects of
	 *         the Class or Class/Context this ReferenceManufacturer represents.
	 */
	public CDOMGroupRef<T> getAllReference();

	/**
	 * Resolves the references that have been requested from this
	 * ReferenceManufacturer, using the objects contained within this
	 * ReferenceManufacturer.
	 * 
	 * This method guarantees that all references are resolved.
	 * 
	 * Note: Implementations of ReferenceManufacturer may place limits on the
	 * number of times resolveReferences() can be called. The reason for this is
	 * that some references may only be resolved once, and the
	 * ReferenceManufacturer is not required to maintain a list of references
	 * that have been resolved and those which have not been resolved.
	 */
	public void resolveReferences();

	/**
	 * Instructs the ReferenceManufacturer that the object with the given
	 * identifer should be constructed automatically if it is necessary when
	 * buildDeferredObjects() is called. The object will be constructed only if
	 * no object with the matching identifier has been constructed or imported
	 * into this ReferenceManufacturer.
	 * 
	 * Implementation Note: This is generally used for backwards compatibility
	 * to previous versions of PCGen or to items that are built automatically
	 * (such as Weapon Proficiencies for Natural Attacks)
	 * 
	 * @param value
	 *            The identifier of the CDOMObject to be built (if otherwise not
	 *            constructed or imported into this ReferenceManufacturer) when
	 *            buildDeferredObjects() is called.
	 */
	public void constructIfNecessary(String value);

	/**
	 * Builds any objects whose construction was deferred. Identifiers for
	 * objects for which construction was deferred were inserted into the
	 * ReferenceManufacturer using constructIfNecessary(String). Objects will be
	 * constructed only if no object with the matching identifier has been
	 * constructed or imported into this ReferenceManufacturer.
	 * 
	 * Construction or import into the ReferenceManufacturer could occur at any
	 * time before buildDeferredObjects() is called, either before or after
	 * constructIfNecessary(String) was called with the relevant identifier.
	 * However, construction or import of an object with an identical identifier
	 * after buildDeferredObjects() is called will result in a duplicate object
	 * being formed. ReferenceManufacturer is not responsible for deleting
	 * automatically built objects under those conditions.
	 */
	public void buildDeferredObjects();

	/**
	 * The class of object this ReferenceManufacturer represents.
	 * 
	 * @return The class of object this ReferenceManufacturer represents.
	 */
	public Class<T> getReferenceClass();

	/**
	 * Returns true if this ReferenceManufacturer is "valid". A "valid"
	 * ReferenceManufacturer is one where all of the following are true:
	 * 
	 * (1) Any object stored in the ReferenceManufacturer reports that it's KEY
	 * (as defined by CDOMObject.getKeyName()) matches the identifier used to
	 * store the object in the ReferenceManufacturer.
	 * 
	 * (2) Any identifier to which a reference was made has a constructed or
	 * imported object with that identifier present in the
	 * ReferenceManufacturer.
	 * 
	 * (3) No two objects in the ReferenceManufacturer have a matching
	 * identifier.
	 * 
	 * @return true if the ReferenceManufacturer is "valid"; false otherwise.
	 */
	public boolean validate();

	/**
	 * Triggers immediate construction of the object with the given identifier
	 * if it does not exist. This is an alternative to constructIfNecessary that
	 * should be used sparingly (generally direct access like this is higher
	 * risk, but necessary in some cases)
	 * 
	 * Note that use of this method is inherently risky when taken in context to
	 * .MOD and .COPY. Changes to keys may change the object to which an
	 * identifier refers. Therefore, any resolution that should take place at
	 * runtime should use getReference and resovle the reference.
	 * 
	 * The object will be constructed only if no object with the matching
	 * identifier has been constructed or imported into this
	 * ReferenceManufacturer. If the object has already been constructed, then
	 * the previously constructed object is returned.
	 * 
	 * This method is effectively a convenience method that wraps
	 * containsObject, getObject, and constructObject into a single method call
	 * (and avoids the contains-triggered branch)
	 * 
	 * @param name
	 *            The identifier of the CDOMObject to be built (if otherwise not
	 *            constructed or imported into this ReferenceManufacturer), or
	 *            if an object with that identifier already exists, the
	 *            identifier of the object to be returned.
	 * @return The previously existing or new CDOMObject with the given
	 *         identifier.
	 */
	public T constructNowIfNecessary(String name);
}
