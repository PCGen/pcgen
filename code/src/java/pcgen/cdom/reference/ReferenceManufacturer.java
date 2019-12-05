/*
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

import pcgen.base.util.FormatManager;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Loadable;

/**
 * A ReferenceManufacturer is an object capable of creating CDOMReferences of a
 * given "form". That "form" includes a specific Class of CDOMObject, or a
 * specific Class/Category for Categorized CDOMObjects.
 * <p>
 * The Class serves two purposes.
 * <p>
 * 1) This provides a universal interface, so it simplifies the LoadContext
 * class structure/code under categorized/non-categorized conditions
 * <p>
 * 2) This provides a method of constructing references after the class (or
 * class and context) have been established. By separating these actions,
 * references can be constructed at a time that is independent of the
 * class/context resolution. This allows for tokens which can refer to both
 * categorized and non-categorized objects (e.g. QUALIFY) to operate clearly and
 * with methods that are Generic-friendly.
 * <p>
 * Behavioral Notes: It is expected that various methods will interact with each
 * other. This is especially true with respect to buildDeferredObjects(). It is
 * expected that object identifiers passed to constructIfNecessary(String) not
 * result in objects being constructed (with appropriate consequences to methods
 * like containsObject(T) ) before buildDeferredObjects() is called, but will
 * result in objects being constructed (with appropriate consequences to methods
 * like containsObject(T) ) after buildDeferredObjects() is called.
 *
 * @param <T> The Class of object this ReferenceManufacturer can manufacture
 */
public interface ReferenceManufacturer<T extends Loadable> extends SelectionCreator<T>, FormatManager<T>
{
    /**
     * Constructs a new CDOMObject of the Class or Class/Category represented by
     * this ReferenceManufacturer
     * <p>
     * Implementation Note: At this point, the "key" provided is likely to be
     * the "display name" of an object, not the actual "KEY". This is due to the
     * need to construct an object at the time it is first encountered, which is
     * probably not the time at which the KEY is known (the intent is not to do
     * "lookahead", as it fails under .MOD conditions anyway). In order to
     * "rename" an object once a KEY is encountered, see renameObject(String, T)
     *
     * @param key The identifier of the CDOMObject to be constructed
     * @return The new CDOMObject of the Class or Class/Category represented by
     * this ReferenceManufacturer
     */
    T constructObject(String key);

    /**
     * Adds an object to the contents of this ReferenceManufacturer. This is
     * used in conditions where this ReferenceManufacturer was not used to
     * construct the object.
     * <p>
     * Implementation Note: There are various situations where this "external
     * construction" may happen - the primary one being loading of "game mode"
     * information like CDOMStat objects.
     *
     * @param item The object to be imported into this ReferenceManufacturer
     * @param key  The identifier of the object to be imported into this
     *             ReferenceManufacturer
     */
    void addObject(T item, String key);

    /**
     * Returns true if this ReferenceManufacturer contains an object of the
     * Class or Class/Category represented by this ReferenceManufacturer.
     * <p>
     * Note that this is testing *object* presence. This will not return true if
     * a reference for the given identifier has been requested; it will only
     * return true if an object with the given identifier has actually been
     * constructed by or imported into this ReferenceManufacturer.
     *
     * @param key The identifier of the object to be checked if it is present in
     *            this ReferenceManufacturer.
     * @return true if this ReferenceManufacturer contains an object of the
     * Class or Class/Category represented by this
     * ReferenceManufacturer; false otherwise.
     */
    boolean containsObjectKeyed(String key);

    /**
     * Gets the object represented by the given identifier. Will return null if
     * an object with the given identifier is not present in this
     * ReferenceManufacturer. Does not make a test to establish if the given
     * identifier is unique - must act silently.
     * <p>
     * Note that this is testing *object* presence. This will not return an
     * object if a reference for the given identifier has been requested; it
     * will only return true if an object with the given identifier has actually
     * been constructed by or imported into this ReferenceManufacturer.
     *
     * @param key identifier of the object to be returned
     * @return The object stored in this ReferenceManufacturer with the given
     * identifier, or null if this ReferenceManufacturer does not
     * contain an object with the given identifier.
     */
    T getActiveObject(String key);

    /**
     * Gets the object represented by the given identifier. Will return null if
     * an object with the given identifier is not present in this
     * ReferenceManufacturer.
     * <p>
     * Note that this is testing *object* presence. This will not return an
     * object if a reference for the given identifier has been requested; it
     * will only return true if an object with the given identifier has actually
     * been constructed by or imported into this ReferenceManufacturer.
     *
     * @param key identifier of the object to be returned
     * @return The object stored in this ReferenceManufacturer with the given
     * identifier, or null if this ReferenceManufacturer does not
     * contain an object with the given identifier.
     */
    T getObject(String key);

    /**
     * Returns a Collection of all of the objects contained in this
     * ReferenceManufacturer sorted by their KeyName. This will not return null,
     * it will return an empty list if no objects have been constructed by or
     * imported into this ReferenceManufacturer.
     *
     * @return A Collection of all of the objects contained in this
     * ReferenceManufacturer
     */
    Collection<T> getAllObjects();

    /**
     * Changes the identifier for a given object, as stored in this
     * ReferenceManufacturer.
     *
     * @param key  The new identifier to be used for the given object
     * @param item The object for which the identifier in this
     *             ReferenceManufacturer should be changed
     */
    void renameObject(String key, T item);

    /**
     * Remove the given object from this ReferenceManufacturer. Returns true if
     * the object was removed from this ReferenceManufacturer; false otherwise.
     *
     * @param item The object to be removed from this ReferenceManufacturer.
     * @return true if the object was removed from this ReferenceManufacturer;
     * false otherwise.
     */
    boolean forgetObject(T item);

    /**
     * Resolves the references that have been requested from this
     * ReferenceManufacturer, using the objects contained within this
     * ReferenceManufacturer.
     * <p>
     * This method guarantees that all references are resolved.
     * <p>
     * Note: Implementations of ReferenceManufacturer may place limits on the
     * number of times resolveReferences() can be called. The reason for this is
     * that some references may only be resolved once, and the
     * ReferenceManufacturer is not required to maintain a list of references
     * that have been resolved and those which have not been resolved.
     */
    boolean resolveReferences(UnconstructedValidator validator);

    /**
     * Instructs the ReferenceManufacturer that the object with the given
     * identifier should be constructed automatically if it is necessary when
     * buildDeferredObjects() is called. The object will be constructed only if
     * no object with the matching identifier has been constructed or imported
     * into this ReferenceManufacturer.
     * <p>
     * Implementation Note: This is generally used for backwards compatibility
     * to previous versions of PCGen or to items that are built automatically
     * (such as Weapon Proficiencies for Natural Attacks)
     *
     * @param key The identifier of the CDOMObject to be built (if otherwise not
     *            constructed or imported into this ReferenceManufacturer) when
     *            buildDeferredObjects() is called.
     */
    void constructIfNecessary(String key);

    /**
     * Builds any objects whose construction was deferred. Identifiers for
     * objects for which construction was deferred were inserted into the
     * ReferenceManufacturer using constructIfNecessary(String). Objects will be
     * constructed only if no object with the matching identifier has been
     * constructed or imported into this ReferenceManufacturer.
     * <p>
     * Construction or import into the ReferenceManufacturer could occur at any
     * time before buildDeferredObjects() is called, either before or after
     * constructIfNecessary(String) was called with the relevant identifier.
     * However, construction or import of an object with an identical identifier
     * after buildDeferredObjects() is called will result in a duplicate object
     * being formed. ReferenceManufacturer is not responsible for deleting
     * automatically built objects under those conditions.
     */
    void buildDeferredObjects();

    /**
     * Returns true if this ReferenceManufacturer is "valid". A "valid"
     * ReferenceManufacturer is one where all of the following are true:
     * <p>
     * (1) Any object stored in the ReferenceManufacturer reports that it's KEY
     * (as defined by CDOMObject.getKeyName()) matches the identifier used to
     * store the object in the ReferenceManufacturer.
     * <p>
     * (2) All objects stored in the ReferenceManufacturer have valid names
     * (do not use illegal characters in the names)
     * <p>
     * (3) No two objects in the ReferenceManufacturer have a matching
     * identifier.
     *
     * @param validator UnconstructedValidator which can suppress unconstructed
     *                  reference warnings
     * @return true if the ReferenceManufacturer is "valid"; false otherwise.
     */
    boolean validate(UnconstructedValidator validator);

    /**
     * Triggers immediate construction of the object with the given identifier
     * if it does not exist. This is an alternative to constructIfNecessary that
     * should be used sparingly (generally direct access like this is higher
     * risk, but necessary in some cases)
     * <p>
     * Note that use of this method is inherently risky when taken in context to
     * .MOD and .COPY. Changes to keys may change the object to which an
     * identifier refers. Therefore, any resolution that should take place at
     * runtime should use getReference and resolve the reference.
     * <p>
     * The object will be constructed only if no object with the matching
     * identifier has been constructed or imported into this
     * ReferenceManufacturer. If the object has already been constructed, then
     * the previously constructed object is returned.
     * <p>
     * This method is effectively a convenience method that wraps
     * containsObject, getObject, and constructObject into a single method call
     * (and avoids the contains-triggered branch)
     *
     * @param key The identifier of the CDOMObject to be built (if otherwise not
     *            constructed or imported into this ReferenceManufacturer), or
     *            if an object with that identifier already exists, the
     *            identifier of the object to be returned.
     * @return The previously existing or new CDOMObject with the given
     * identifier.
     */
    T constructNowIfNecessary(String key);

    /**
     * Adds an UnconstructedListener to this ReferenceManufacturer, that will
     * receive UnconstructedEvents if the validate method of this
     * ReferenceManufacturer is called and the UnconstructedValidator given to
     * the validate method does not report that the unconstructed reference is
     * permitted.
     *
     * @param listener The UnconstructedListener to be registered with this
     *                 ReferenceManufacturer
     */
    void addUnconstructedListener(UnconstructedListener listener);

    /**
     * Returns an array of UnconstructedListeners that are registered with this
     * ReferenceManufacturer.
     *
     * @return An array of UnconstructedListeners that are registered with this
     * ReferenceManufacturer.
     */
    UnconstructedListener[] getUnconstructedListeners();

    /**
     * Removes an UnconstructedListener from this ReferenceManufacturer, so that
     * it will no longer receive UnconstructedEvents from this
     * ReferenceManufacturer
     *
     * @param listener The UnconstructedListener to be removed from registration with
     *                 this ReferenceManufacturer
     */
    void removeUnconstructedListener(UnconstructedListener listener);

    /**
     * Returns the number of objects that are constructed in this
     * ReferenceManufacturer (These could be natively constructed or imported
     * objects and does not count duplicates)
     *
     * @return The number of objects that are constructed in this
     * ReferenceManufacturer
     */
    int getConstructedObjectCount();

    T buildObject(String name);

    /**
     * Fires an Unconstructed Event for this ReferenceManufacturer, based on the given
     * reference.
     *
     * @param reference The reference to indicate that the underlying requested object was not
     *                  constructed
     */
    void fireUnconstuctedEvent(CDOMReference<?> reference);

    /**
     * Returns a Collection of the CDOMSingleRef objects that had references requested
     * from this ReferenceManufacturer.
     *
     * @return A Collection of the CDOMSingleRef objects that had references requested
     * from this ReferenceManufacturer
     */
    Collection<CDOMSingleRef<T>> getReferenced();

    /**
     * Returns the ManufacturableFactory for this ReferenceManufacturer.
     *
     * @return The ManufacturableFactory for this ReferenceManufacturer
     */
    ManufacturableFactory<T> getFactory();

    /**
     * Returns a Collection of all of the references requested from this
     * ReferenceManufacturer.
     *
     * @return A Collection of the references that have been requested from this
     * ReferenceManufacturer
     */
    Collection<CDOMReference<T>> getAllReferences();

    /**
     * Injects constructed items from this ReferenceManufacturer into the given
     * ReferenceManufacturer.
     *
     * @param rm The ReferenceManufacturer into which objects already constructed in this
     *           ReferenceManufacturer should be injected
     */
    void injectConstructed(ReferenceManufacturer<T> rm);

    /**
     * Adds a derivative object for the given object to this ReferenceManufacturer.
     * Derivative objects are objects which are not named, but which represent sub-objects
     * within a given object. (This is used for certain parts of templates, for example).
     * These are tracked so that certain sets of validation that need to occur on tokens
     * (post load) will also occur on derivative objects.
     *
     * @param obj The derivative object to be added to this ReferenceManufacturer
     */
    void addDerivativeObject(T obj);

    /**
     * Returns a Collection of the derivative objects added to this ReferenceManufacturer.
     *
     * @return A Collection of the derivative objects added to this ReferenceManufacturer
     */
    Collection<T> getDerivativeObjects();

    /**
     * Returns the persistent format of the ClassIdentity returned by
     * getReferenceIdentity().
     *
     * @return The persistent format of the ClassIdentity managed by this
     * ReferenceManufacturer
     */
    String getPersistentFormat();
}
