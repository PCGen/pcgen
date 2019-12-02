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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

import javax.swing.event.EventListenerList;

import pcgen.base.lang.CaseInsensitiveString;
import pcgen.base.util.BasicIndirect;
import pcgen.base.util.FixedStringList;
import pcgen.base.util.FormatManager;
import pcgen.base.util.HashMapToInstanceList;
import pcgen.base.util.Indirect;
import pcgen.base.util.KeyMap;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ClassIdentity;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.content.RollMethod;
import pcgen.cdom.inst.Dynamic;
import pcgen.util.Logging;
import pcgen.util.StringPClassUtil;

/**
 * An AbstractReferenceManufacturer is a concrete, but abstract object capable
 * of creating CDOMReferences of a given "form". That "form" includes a specific
 * Class of Loadable, or a specific Class/Category for Categorized Loadable
 * (this class does not make distinction between the Class and Class/Categorized
 * cases)
 * 
 * The Class is designed to share significant common code between
 * implementations of the ReferenceManufacturer interface.
 * 
 * @param <T>
 *            The Class of object this AbstractReferenceManufacturer can
 *            manufacture
 */
public abstract class AbstractReferenceManufacturer<T extends Loadable> implements ReferenceManufacturer<T>
{

	private boolean isResolved = false;

	private final ManufacturableFactory<T> factory;

	/**
	 * The "ALL" reference, if it is ever referenced. This ensures that only one
	 * "ALL" reference is ever built (and allows it to be reused if the
	 * reference is requested a second time). This also stores the reference so
	 * that it can be appropriately resolved when resolveReferences() is called.
	 */
	private CDOMGroupRef<T> allRef;

	/**
	 * Storage for "TYPE" references. This ensures that only one "TYPE"
	 * reference is ever built for any combination of Types. (and allows those
	 * references to be reused if a combination of types reference is requested
	 * a second time). This also stores the reference so that it can be
	 * appropriately resolved when resolveReferences() is called.
	 * 
	 * It is expected that the String array used as a key to this map conforms
	 * to the following rules: (1) The array does not contain null values (2)
	 * The array does not contain redundant values (3) The array is sorted in
	 * alphabetical order, as defined by the natural ordering of String (for
	 * simplicity [and due to lack of user presentation of this value] this sort
	 * does not correct for internationalization)
	 */
	private final Map<FixedStringList, WeakReference<CDOMGroupRef<T>>> typeReferences =
			new TreeMap<>(FixedStringList.CASE_INSENSITIVE_ORDER);

	/**
	 * Storage for individual references. This ensures that only one reference
	 * is ever built for any identifier. (and allows those references to be
	 * reused if a reference to an identifier is requested a second time). This
	 * also stores the reference so that it can be appropriately resolved when
	 * resolveReferences() is called.
	 */
	private final Map<String, WeakReference<CDOMSingleRef<T>>> referenced =
			new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

	/**
	 * Stores the active objects for this AbstractReferenceManufacturer. These
	 * are objects that have been constructed or imported into the
	 * AbstractReferenceManufacturer.
	 */
	private final KeyMap<T> active = new KeyMap<>();

	/**
	 * Stores derivative objects (Those that are NOT created by this
	 * AbstractReferenceManufacturer and are NOT inserted into this
	 * AbstractReferenceManufacturer. However, these objects exist elsewhere,
	 * and need to be processed under certain conditions. The getAllObjects()
	 * method should NOT add this list to the items returned.
	 */
	private final List<T> derivatives = new ArrayList<>();

	/**
	 * Stores the duplicate objects for identifiers in this
	 * AbstractReferenceManufacturer. Identifiers will only be stored in this
	 * Map if an identical identifier already exists in the active map. Also, if
	 * the gating object in the active map is removed, then the first
	 * "duplicate" in this MapToList should be removed and moved to the "active"
	 * map. This map should never contain an identifier which is not in the
	 * active map.
	 * 
	 * Due to extremely weak .equals() rules in many PObjects, this Map MUST be
	 * a HashMapToInstanceList. In the future, it may be exchanged for a
	 * TreeMapToList that leverages String.CASE_INSENSITIVE_ORDER; however, the
	 * instance behavior may be too important to make that swap without
	 * developing a MapToList that is backed by a TreeMap and also an
	 * "InstanceList"
	 */
	private final HashMapToInstanceList<CaseInsensitiveString, T> duplicates = new HashMapToInstanceList<>();

	/**
	 * Contains a list of deferred objects. Identifiers for objects for which
	 * construction was deferred were inserted into the
	 * AbstractReferenceManufacturer using constructIfNecessary(String). Objects
	 * will be constructed when buildDeferredReferences() is called, if and only
	 * if no object with the matching identifier has been constructed or
	 * imported into this AbstractReferenceManufacturer.
	 */
	private final List<String> deferred = new ArrayList<>();

	/**
	 * Contains a list of manufactured objects (those that are built implicitly
	 * by tokens like NATURALATTACKS). These can be "displaced" by object which
	 * are later built explicitly in a WeaponProf LST file, for example.
	 */
	private final List<WeakReference<T>> manufactured = new ArrayList<>();

	/**
	 * The EventListenerList which contains the listeners to this
	 * AbstractReferenceManufacturer.
	 */
	private final EventListenerList listenerList = new EventListenerList();

	/**
	 * Constructs a new AbstractReferenceManufacturer for the given Class.
	 * 
	 * @param fac
	 *            The ManufacturableFactory this AbstractReferenceManufacturer
	 *            will use to construct and reference objects
	 * @throws IllegalArgumentException
	 *             if the given Class is null or the given Class does not have a
	 *             public, zero argument constructor
	 * 
	 */
	public AbstractReferenceManufacturer(ManufacturableFactory<T> fac)
	{
		if (fac == null)
		{
			throw new IllegalArgumentException("Factory for " + getClass().getName() + " cannot be null");
		}
		factory = fac;
	}

	/**
	 * Gets a reference to the Class or Class/Context provided by this
	 * AbstractReferenceManufacturer. The reference will be a reference to the
	 * objects identified by the given types.
	 * 
	 * @param types
	 *            An array of the types of objects to which the returned
	 *            CDOMReference will refer.
	 * @return A CDOMGroupRef which is intended to contain objects of a given
	 *         Type for the Class or Class/Context this
	 *         AbstractReferenceManufacturer represents.
	 * @throws IllegalArgumentException
	 *             if any of the given Strings is null, empty (length is zero),
	 *             or contains a period (.), equals (=), comma (,) or pipe (|)
	 */
	@Override
	public CDOMGroupRef<T> getTypeReference(String... types)
	{
		for (String type : types)
		{
			if (type == null || type.isEmpty())
			{
				throw new IllegalArgumentException(
					"Attempt to acquire empty Type " + "(the type String contains a null or empty element)");
			}
			if (type.indexOf('.') != -1)
			{
				throw new IllegalArgumentException("Cannot build Reference with type conaining a period: " + type);
			}
			if (type.indexOf('=') != -1)
			{
				throw new IllegalArgumentException("Cannot build Reference with type conaining an equals: " + type);
			}
			if (type.indexOf(',') != -1)
			{
				throw new IllegalArgumentException("Cannot build Reference with type conaining a comma: " + type);
			}
			if (type.indexOf('|') != -1)
			{
				throw new IllegalArgumentException("Cannot build Reference with type conaining a pipe: " + type);
			}
		}
		Arrays.sort(types);
		FixedStringList typeList = new FixedStringList(types);
		WeakReference<CDOMGroupRef<T>> ref = typeReferences.get(typeList);
		if (ref != null)
		{
			CDOMGroupRef<T> trt = ref.get();
			if (trt != null)
			{
				return trt;
			}
		}
		// Didn't find the appropriate key, create new
		CDOMGroupRef<T> cgr = factory.getTypeReference(types);
		typeReferences.put(typeList, new WeakReference<>(cgr));
		return cgr;
	}

	/**
	 * Returns a CDOMGroupRef for the given Class or Class/Context provided by
	 * this AbstractReferenceManufacturer.
	 * 
	 * @return A CDOMGroupRef which is intended to contain all the objects of
	 *         the Class or Class/Context this AbstractReferenceManufacturer
	 *         represents.
	 */
	@Override
	public CDOMGroupRef<T> getAllReference()
	{
		if (allRef == null)
		{
			allRef = factory.getAllReference();
		}
		return allRef;
	}

	/**
	 * The class of object this AbstractReferenceManufacturer represents.
	 * 
	 * @return The class of object this AbstractReferenceManufacturer
	 *         represents.
	 */
	@Override
	public Class<T> getReferenceClass()
	{
		return factory.getReferenceClass();
	}

	/**
	 * Resolves the references that have been requested from this
	 * AbstractReferenceManufacturer, using the objects contained within this
	 * AbstractReferenceManufacturer.
	 * 
	 * This method guarantees that all references are resolved.
	 * 
	 * Note: Implementations of AbstractReferenceManufacturer may place limits
	 * on the number of times resolveReferences() can be called. The reason for
	 * this is that some references may only be resolved once, and the
	 * AbstractReferenceManufacturer is not required to maintain a list of
	 * references that have been resolved and those which have not been
	 * resolved.
	 */
	@Override
	public boolean resolveReferences(UnconstructedValidator validator)
	{
		boolean resolutionSuccessful = resolvePrimitiveReferences(validator);
		resolutionSuccessful &= resolveGroupReferences();
		for (WeakReference<CDOMGroupRef<T>> ref : typeReferences.values())
		{
			CDOMGroupRef<T> trt = ref.get();
			if (trt != null && trt.getObjectCount() == 0)
			{
				Logging.errorPrint("Error: No " + factory.getReferenceDescription() + " objects of "
					+ trt.getLSTformat(false) + " were loaded but were referred to in the data");
				fireUnconstuctedEvent(trt);
				resolutionSuccessful = false;
			}
		}
		isResolved = true;
		return resolutionSuccessful;
	}

	private boolean resolvePrimitiveReferences(UnconstructedValidator validator)
	{
		boolean resolutionSuccessful = true;
		for (Entry<String, WeakReference<CDOMSingleRef<T>>> me1 : referenced.entrySet())
		{
			CDOMSingleRef<T> value = me1.getValue().get();
			if (value != null)
			{
				resolutionSuccessful &= factory.resolve(this, me1.getKey(), value, validator);
			}
		}
		return resolutionSuccessful;
	}

	private boolean resolveGroupReferences()
	{
		for (T obj : getAllObjects())
		{
			if (allRef != null)
			{
				allRef.addResolution(obj);
			}
			for (Map.Entry<FixedStringList, WeakReference<CDOMGroupRef<T>>> me : typeReferences.entrySet())
			{
				CDOMGroupRef<T> trt = me.getValue().get();
				if (trt != null)
				{
					boolean typeOkay = true;
					for (String type : me.getKey())
					{
						if (!obj.isType(type))
						{
							typeOkay = false;
							break;
						}
					}
					if (typeOkay)
					{
						trt.addResolution(obj);
					}
				}
			}
		}
		if (allRef != null && allRef.getObjectCount() == 0)
		{
			Logging.errorPrint("Error: No " + factory.getReferenceDescription()
				+ " objects were loaded but were referred to in the data");
			fireUnconstuctedEvent(allRef);
			return false;
		}
		return true;
	}

	/**
	 * Adds an object to the contents of this AbstractReferenceManufacturer.
	 * This is used in conditions where this AbstractReferenceManufacturer was
	 * not used to construct the object.
	 * 
	 * Implementation Note: There are various situations where this "external
	 * construction" may happen - the primary one being loading of "game mode"
	 * information like CDOMStat objects.
	 * 
	 * @param item
	 *            The object to be imported into this
	 *            AbstractReferenceManufacturer
	 * @param key
	 *            The identifier of the object to be imported into this
	 *            AbstractReferenceManufacturer
	 * @throws IllegalArgumentException
	 *             if the given object is not of the Class that this
	 *             AbstractReferenceManufacturer constructs and references
	 */
	@Override
	public void addObject(T item, String key)
	{
		if (!factory.isMember(item))
		{
			throw new IllegalArgumentException(
				"Attempted to register a " + item.getClass().getName() + " in " + factory.getReferenceDescription());
		}
		T current = active.get(key);
		if (current == null)
		{
			active.put(key, item);
		}
		else
		{
			duplicates.addToListFor(new CaseInsensitiveString(key), item);
		}
	}

	/**
	 * Gets the object represented by the given identifier. Will return null if
	 * an object with the given identifier is not present in this
	 * AbstractReferenceManufacturer. Does not make any test to check if the
	 * given identifier has multiple matching objects.
	 * 
	 * Note that this is testing *object* presence. This will not return an
	 * object if a reference for the given identifier has been requested; it
	 * will only return true if an object with the given identifier has actually
	 * been constructed by or imported into this AbstractReferenceManufacturer.
	 * 
	 * @param key
	 *            identifier of the object to be returned
	 * @return The object stored in this AbstractReferenceManufacturer with the
	 *         given identifier, or null if this AbstractReferenceManufacturer
	 *         does not contain an object with the given identifier.
	 */
	@Override
	public T getActiveObject(String key)
	{
		return active.get(key);
	}

	/**
	 * Gets the object represented by the given identifier. Will return null if
	 * an object with the given identifier is not present in this
	 * AbstractReferenceManufacturer.
	 * 
	 * Note that this is testing *object* presence. This will not return an
	 * object if a reference for the given identifier has been requested; it
	 * will only return true if an object with the given identifier has actually
	 * been constructed by or imported into this AbstractReferenceManufacturer.
	 * 
	 * @param key
	 *            identifier of the object to be returned
	 * @return The object stored in this AbstractReferenceManufacturer with the
	 *         given identifier, or null if this AbstractReferenceManufacturer
	 *         does not contain an object with the given identifier.
	 */
	@Override
	public T getObject(String key)
	{
		T po = active.get(key);
		if (po != null)
		{
			List<T> list = duplicates.getListFor(new CaseInsensitiveString(key));
			if ((list != null) && !list.isEmpty())
			{
				Logging.errorPrint(
					"Reference to Constructed " + factory.getReferenceDescription() + " " + key + " is ambiguous");
				StringBuilder sb = new StringBuilder(1000);
				sb.append("Locations: ");
				sb.append(po.getSourceURI());
				for (T dupe : list)
				{
					sb.append(", ");
					sb.append(dupe.getSourceURI());
				}
				Logging.errorPrint(sb.toString());
			}
			return po;
		}
		return null;
	}

	/**
	 * Constructs a new Loadable of the Class or Class/Category represented by
	 * this AbstractReferenceManufacturer. This also adds the object to the list
	 * of constructed objects within this AbstractReferenceManufacturer.
	 * 
	 * Implementation Note: At this point, the "key" provided is likely to be
	 * the "display name" of an object, not the actual "KEY". This is due to the
	 * need to construct an object at the time it is first encountered, which is
	 * probably not the time at which the KEY is known (the intent is not to do
	 * "lookahead", as it fails under .MOD conditions anyway). In order to
	 * "rename" an object once a KEY is encountered, see renameObject(String, T)
	 * 
	 * @param key
	 *            The identifier of the Loadable to be constructed
	 * @return The new Loadable of the Class or Class/Category represented by
	 *         this AbstractReferenceManufacturer
	 * @throws IllegalArgumentException
	 *             if the given identifier is null or empty (length is zero)
	 */
	@Override
	public T constructObject(String key)
	{
		T obj = buildObject(key);
		addObject(obj, key);
		return obj;
	}

	/**
	 * Constructs a new Loadable of the Class or Class/Category represented by
	 * this AbstractReferenceManufacturer
	 * 
	 * This should remain protected (vs. public) as it is for "internal use
	 * only"; it serves as a convenience method to wrap the .newInstance call
	 * and the possible Exceptions. Other classes should use
	 * constructObject(String)
	 * 
	 * @param key
	 *            The identifier of the Loadable to be constructed
	 * @return The new Loadable of the Class or Class/Category represented by
	 *         this AbstractReferenceManufacturer
	 * @throws IllegalArgumentException
	 *             if the given identifier is null or empty (length is zero)
	 */
	@Override
	public T buildObject(String key)
	{
		if (key == null || key.equals(""))
		{
			throw new IllegalArgumentException("Cannot build empty name");
		}
		T obj = factory.newInstance();
		obj.setName(key);
		return obj;
	}

	/**
	 * Changes the identifier for a given object, as stored in this
	 * AbstractReferenceManufacturer.
	 * 
	 * @param key
	 *            The new identifier to be used for the given object
	 * @param item
	 *            The object for which the identifier in this
	 *            AbstractReferenceManufacturer should be changed
	 */
	@Override
	public void renameObject(String key, T item)
	{
		String oldKey = item.getKeyName();
		if (oldKey.equalsIgnoreCase(key))
		{
			if (Logging.isDebugMode())
			{
				Logging.debugPrint("Worthless Key change encountered: " + item.getDisplayName() + " " + oldKey);
				Logging.reportSource(Logging.DEBUG, item.getSourceURI());
			}
		}
		forgetObject(item);
		addObject(item, key);
	}

	/**
	 * Remove the given object from this AbstractReferenceManufacturer. Returns
	 * true if the object was removed from this AbstractReferenceManufacturer;
	 * false otherwise.
	 * 
	 * @param item
	 *            The object to be removed from this
	 *            AbstractReferenceManufacturer.
	 * @return true if the object was removed from this
	 *         AbstractReferenceManufacturer; false otherwise.
	 */
	@Override
	public boolean forgetObject(T item)
	{
		if (!factory.isMember(item))
		{
			throw new IllegalArgumentException(
				"Object to be forgotten does not match Class " + "of this AbstractReferenceManufacturer");
		}
		String key = active.getKeyFor(item);
		if (key == null)
		{
			/*
			 * TODO This is a bug - the key name is not necessarily loaded into
			 * the object, it may have been consumed by the object context... :P
			 */
			CaseInsensitiveString ocik = new CaseInsensitiveString(item.getKeyName());
			duplicates.removeFromListFor(ocik, item);
		}
		else
		{
			CaseInsensitiveString ocik = new CaseInsensitiveString(key);
			List<T> list = duplicates.getListFor(ocik);
			if (list == null)
			{
				// No replacement
				active.remove(key);
			}
			else
			{
				T newActive = duplicates.getElementInList(ocik, 0);
				duplicates.removeFromListFor(ocik, newActive);
				active.put(key, newActive);
			}
		}
		return true;
	}

	/**
	 * Returns true if this AbstractReferenceManufacturer contains an object of
	 * the Class or Class/Category represented by this
	 * AbstractReferenceManufacturer.
	 * 
	 * Note that this is testing *object* presence. This will not return true if
	 * a reference for the given identifier has been requested; it will only
	 * return true if an object with the given identifier has actually been
	 * constructed by or imported into this AbstractReferenceManufacturer.
	 * 
	 * @param key
	 *            The identifier of the object to be checked if it is present in
	 *            this AbstractReferenceManufacturer.
	 * @return true if this AbstractReferenceManufacturer contains an object of
	 *         the Class or Class/Category represented by this
	 *         AbstractReferenceManufacturer; false otherwise.
	 */
	@Override
	public boolean containsObjectKeyed(String key)
	{
		return active.containsKey(key);
	}

	/**
	 * Gets a reference to the Class or Class/Context provided by this
	 * AbstractReferenceManufacturer. The reference will be a reference to the
	 * object identified by the given key.
	 * 
	 * @param key
	 *            The key used to identify the object to which the returned
	 *            CDOMReference will refer.
	 * @return A CDOMReference that refers to the object identified by the given
	 *         key
	 * @throws IllegalArgumentException
	 *             if the given key is null or empty
	 */
	@Override
	public CDOMSingleRef<T> getReference(String key)
	{
		/*
		 * TODO This is incorrect, but a hack for now :)
		 * 
		 * Mainly this throws around IllegalArgumentException in order to catch
		 * bad parsing issues (design flaws in the code). Not sure if we want to
		 * continue that long term? Once tokens are truly tested this may not be
		 * necessary or desirable.
		 */
		Objects.requireNonNull(key, "Cannot request a reference to null identifier");
		if (key.isEmpty())
		{
			throw new IllegalArgumentException("Cannot request a reference to an empty identifier");
		}
		/*
		 * Items thrown below this point are for protection from coding errors
		 * in LST files, not part of the public interface of this method
		 */
		try
		{
			Integer.parseInt(key);
			throw new IllegalArgumentException("A number cannot be a valid single item: " + key);
		}
		catch (NumberFormatException nfe)
		{
			// ok
		}
		if (key.contains("="))
		{
			throw new IllegalArgumentException("= cannot be a in valid single item (perhaps something like TYPE= "
				+ "is not supported in this token?): " + key);
		}
		if (key.equalsIgnoreCase("ANY"))
		{
			throw new IllegalArgumentException("Any cannot be a valid single item (not supported in this token?)");
		}
		if (key.equalsIgnoreCase("ALL"))
		{
			throw new IllegalArgumentException("All cannot be a valid single item (not supported in this token?)");
		}
		if (key.contains(":"))
		{
			throw new IllegalArgumentException(": cannot exist in a valid single item (did you try to use a "
				+ "PRE where it is not supported?) " + key);
		}
		if (key.equalsIgnoreCase("%LIST"))
		{
			throw new IllegalArgumentException("%LIST cannot be a valid single item (not supported in this token?)");
		}

		WeakReference<CDOMSingleRef<T>> wr = referenced.get(key);
		if (wr != null)
		{
			CDOMSingleRef<T> ref = wr.get();
			if (ref != null)
			{
				return ref;
			}
		}
		CDOMSingleRef<T> ref;
		if (isResolved)
		{
			T current = active.get(key);
			if (current == null)
			{
				throw new IllegalArgumentException(
					key + " is not valid post-resolution " + "because it was never constructed");
			}
			ref = CDOMDirectSingleRef.getRef(current);
		}
		else
		{
			CDOMSingleRef<T> lr = factory.getReference(key);
			referenced.put(key, new WeakReference<>(lr));
			ref = lr;
		}
		return ref;
	}

	/**
	 * Returns true if this AbstractReferenceManufacturer is "valid". A "valid"
	 * AbstractReferenceManufacturer is one where all of the following are true:
	 * 
	 * (1) Any object stored in the AbstractReferenceManufacturer reports that
	 * it's KEY (as defined by Loadable.getKeyName()) matches the identifier
	 * used to store the object in the AbstractReferenceManufacturer.
	 * 
	 * (2) All objects stored in the ReferenceManufacturer have valid names
	 * (do not use illegal characters in the names)
	 * 
	 * (3) No two objects in the AbstractReferenceManufacturer have a matching
	 * identifier.
	 * 
	 * @param validator
	 *            UnconstructedValidator which can suppress unconstructed
	 *            reference warnings
	 * 
	 * @return true if the AbstractReferenceManufacturer is "valid"; false
	 *         otherwise.
	 */
	@Override
	public boolean validate(UnconstructedValidator validator)
	{
		boolean returnGood = true;
		if (validator == null || !validator.allowDuplicates(getReferenceClass()))
		{
			returnGood = validateDuplicates();
		}
		returnGood &= validateNames();
		returnGood &= validateActive();
		return returnGood;
	}

	private boolean validateNames()
	{
		if (!Logging.isLoggable(Logging.LST_WARNING))
		{
			return true;
		}
		for (String key : active.keySet())
		{
			T value = active.get(key);
			if (value.isInternal())
			{
				continue;
			}
			/*
			 * http://wiki.pcgen.org/index.php?title=Data_LST_Standards
			 * 
			 * Characters which should never be used in object names are Commas
			 * (,), Pipes (|), Backslashes (\), Colons (:), Semicolons (;),
			 * Periods (.), Brackets ([]), Percent (%), Asterisk (*) and Equals
			 * (=).
			 */
			if (key.indexOf(',') != -1 && factory.getReferenceClass() != RollMethod.class)
			{
				Logging.log(Logging.LST_WARNING, "Found " + factory.getReferenceDescription() + " with KEY: " + key
					+ " which contains a comma " + "(prohibited character in a key)");
			}
			if (key.indexOf('|') != -1)
			{
				Logging.log(Logging.LST_WARNING, "Found " + factory.getReferenceDescription() + " with KEY: " + key
					+ " which contains a pipe " + "(prohibited character in a key)");
			}
			if (key.indexOf('\\') != -1)
			{
				Logging.log(Logging.LST_WARNING, "Found " + factory.getReferenceDescription() + " with KEY: " + key
					+ " which contains a backslash " + "(prohibited character in a key)");
			}
			if (key.indexOf(':') != -1)
			{
				Logging.log(Logging.LST_WARNING, "Found " + factory.getReferenceDescription() + " with KEY: " + key
					+ " which contains a colon " + "(prohibited character in a key)");
			}
			if (key.indexOf(';') != -1)
			{
				Logging.log(Logging.LST_WARNING, "Found " + factory.getReferenceDescription() + " with KEY: " + key
					+ " which contains a semicolon " + "(prohibited character in a key)");
			}
			// if (key.indexOf('.') != -1)
			// {
			// Logging.log(Logging.LST_WARNING, "Found "
			// + getReferenceDescription() + " with KEY: " + key
			// + " which contains a period "
			// + "(prohibited character in a key)");
			// }
			if (key.indexOf('%') != -1)
			{
				Logging.log(Logging.LST_WARNING, "Found " + factory.getReferenceDescription() + " with KEY: " + key
					+ " which contains a percent sign " + "(prohibited character in a key)");
			}
			if (key.indexOf('*') != -1)
			{
				Logging.log(Logging.LST_WARNING, "Found " + factory.getReferenceDescription() + " with KEY: " + key
					+ " which contains an asterisk " + "(prohibited character in a key)");
			}
			if (key.indexOf('=') != -1)
			{
				Logging.log(Logging.LST_WARNING, "Found " + factory.getReferenceDescription() + " with KEY: " + key
					+ " which contains an equals sign " + "(prohibited character in a key)");
			}
			if ((key.indexOf('[') != -1) || (key.indexOf(']') != -1))
			{
				Logging.log(Logging.LST_WARNING, "Found " + factory.getReferenceDescription() + " with KEY: " + key
					+ " which contains a bracket  " + "(prohibited character in a key)");
			}
		}
		return true;
	}

	private boolean validateActive()
	{
		boolean returnGood = true;
		for (Object second : active.keySet())
		{
			T activeObj = active.get(second);
			String keyName = activeObj.getKeyName();
			if (keyName == null)
			{
				Logging.errorPrint(activeObj.getClass() + " " + activeObj.getDisplayName() + " has a null KeyName");
			}
			else if (!keyName.equalsIgnoreCase(second.toString()))
			{
				Logging.errorPrint(getReferenceDescription() + " Magical Key Change: " + second + " to " + keyName);
				returnGood = false;
			}
		}
		return returnGood;
	}

	private boolean validateDuplicates()
	{
		boolean returnGood = true;
		for (CaseInsensitiveString second : duplicates.getKeySet())
		{
			List<T> list = duplicates.getListFor(second);
			T good = active.get(second.toString());
			/*
			 * CONSIDER Should get CDOMObject reference out of here :(
			 */
			if (good instanceof CDOMObject)
			{
				CDOMObject cdo = (CDOMObject) good;
                for (T dupe : list) {
                    if (cdo.isCDOMEqual((CDOMObject) dupe)) {
                        for (Iterator<WeakReference<T>> it = manufactured.iterator(); it.hasNext(); ) {
                            WeakReference<T> wr = it.next();
                            T mfg = wr.get();
                            if (mfg == null) {
                                it.remove();
                            }
                            // Yes this is instance equality, not .equals
                            else if (mfg == good) {
                                forgetObject(good);
                                break;
                            }
                        }
                    }
                }
			}
			if (duplicates.containsListFor(second))
			{
				Logging.errorPrint("More than one " + factory.getReferenceDescription() + " with key/name "
					+ good.getKeyName() + " was built");
				List<T> dupes = duplicates.getListFor(second);
				StringBuilder sb = new StringBuilder(1000);
				sb.append("Sources: ");
				sb.append(good.isInternal() ? "<internal>" : good.getSourceURI());
				for (T dupe : dupes)
				{
					sb.append(", ").append(dupe.isInternal() ? "<internal>" : dupe.getSourceURI());
					if (!dupe.getKeyName().equals(good.getKeyName()))
					{
						Logging.errorPrint("Key case differed for " + dupe.getKeyName());
					}
				}
				Logging.errorPrint(sb.toString());
				returnGood = false;
			}
		}
		return returnGood;
	}

	/**
	 * Instructs the AbstractReferenceManufacturer that the object with the
	 * given identifer should be constructed automatically if it is necessary
	 * when buildDeferredObjects() is called. The object will be constructed
	 * only if no object with the matching identifier has been constructed or
	 * imported into this AbstractReferenceManufacturer.
	 * 
	 * Implementation Note: This is generally used for backwards compatibility
	 * to previous versions of PCGen or to items that are built automatically
	 * (such as Weapon Proficiencies for Natural Attacks)
	 * 
	 * @param key
	 *            The identifier of the Loadable to be built (if otherwise not
	 *            constructed or imported into this
	 *            AbstractReferenceManufacturer) when buildDeferredObjects() is
	 *            called.
	 */
	@Override
	public void constructIfNecessary(String key)
	{
		/*
		 * TODO FIXME Need to ensure that items that are built here are tagged
		 * as manufactured, so that they are not written out to LST files
		 */
		deferred.add(key);
	}

	/**
	 * Returns a Collection of all of the objects contained in this
	 * AbstractReferenceManufacturer, sorted by their Key Name. This will not
	 * return null, it will return an empty list if no objects have been
	 * constructed by or imported into this AbstractReferenceManufacturer.
	 * 
	 * @return A sorted Collection of all of the objects contained in this
	 *         AbstractReferenceManufacturer
	 */
	@Override
	public Collection<T> getAllObjects()
	{
		return active.keySortedValues();
	}

	/**
	 * Builds any objects whose construction was deferred. Identifiers for
	 * objects for which construction was deferred were inserted into the
	 * AbstractReferenceManufacturer using constructIfNecessary(String). Objects
	 * will be constructed only if no object with the matching identifier has
	 * been constructed or imported into this AbstractReferenceManufacturer.
	 * 
	 * Construction or import into the AbstractReferenceManufacturer could occur
	 * at any time before buildDeferredObjects() is called, either before or
	 * after constructIfNecessary(String) was called with the relevant
	 * identifier. However, construction or import of an object with an
	 * identical identifier after buildDeferredObjects() is called will result
	 * in a duplicate object being formed. AbstractReferenceManufacturer is not
	 * responsible for deleting automatically built objects under those
	 * conditions.
	 */
	@Override
	public void buildDeferredObjects()
	{
		for (String cis : deferred)
		{
			if (!active.containsKey(cis))
			{
				constructObject(cis);
			}
		}
	}

	/**
	 * Returns a Collection of the "TYPE" references for this
	 * AbstractReferenceManufacturer.
	 * 
	 * This method is value-semantic in that ownership of the returned
	 * Collection is transferred to the class calling this method. Modification
	 * of the returned Collection will not modify the "TYPE" references for this
	 * AbstractReferenceManufacturer and modification of the "TYPE" references
	 * for this AbstractReferenceManufacturer through subsequent calls of
	 * getTypeReference(String...) will not modify the returned Collection.
	 * 
	 * This method will not return null, even if getTypeReference(String...)
	 * method was never called.
	 * 
	 * @return A Collection of the "TYPE" references for this
	 *         AbstractReferenceManufacturer.
	 */
	protected Collection<CDOMGroupRef<T>> getTypeReferences()
	{
		List<CDOMGroupRef<T>> list = new ArrayList<>(typeReferences.size());
		for (Iterator<WeakReference<CDOMGroupRef<T>>> it = typeReferences.values().iterator(); it.hasNext();)
		{
			WeakReference<CDOMGroupRef<T>> wr = it.next();
			CDOMGroupRef<T> trt = wr.get();
			if (trt == null)
			{
				it.remove();
			}
			else
			{
				list.add(trt);
			}
		}
		return list;
	}

	/**
	 * Returns a Collection of the primitive references for this
	 * AbstractReferenceManufacturer.
	 * 
	 * This method is value-semantic in that ownership of the returned
	 * Collection is transferred to the class calling this method. Modification
	 * of the returned Collection will not modify the primitive references for
	 * this AbstractReferenceManufacturer and modification of the primitive
	 * references for this AbstractReferenceManufacturer through subsequent
	 * calls of getReference(String) will not modify the returned Collection.
	 * 
	 * This method will not return null, even if getReference(String) method was
	 * never called.
	 * 
	 * @return A Collection of the primitive references for this
	 *         AbstractReferenceManufacturer.
	 */
	@Override
	public Collection<CDOMSingleRef<T>> getReferenced()
	{
		List<CDOMSingleRef<T>> list = new ArrayList<>();
		for (WeakReference<CDOMSingleRef<T>> wr : referenced.values())
		{
			CDOMSingleRef<T> ref = wr.get();
			if (ref != null)
			{
				list.add(ref);
			}
		}
		return list;
	}

	/**
	 * Injects all objects from the given ReferenceManufacturer into this
	 * AbstractReferenceManufacturer. Effectively this is a bulk addObject for
	 * all of the objects contained in the given ReferenceManufacturer.
	 * 
	 * Note that this imports only the objects, and NOT references. This
	 * AbstractReferenceManufacturer does inherit any deferred objects
	 * (triggered through constructIfNecessary) from the given
	 * ReferenceManufacturer.
	 * 
	 * @param arm
	 *            The ReferenceManufacturer from which the objects should be
	 *            imported into this AbstractReferenceManufacturer
	 */
	@Override
	public void injectConstructed(ReferenceManufacturer<T> arm)
	{
		for (T value : active.keySortedValues())
		{
			arm.addObject(value, active.getKeyFor(value));
		}
		for (CaseInsensitiveString cis : duplicates.getKeySet())
		{
			for (T obj : duplicates.getListFor(cis))
			{
				arm.addObject(obj, cis.toString());
			}
		}
		for (String s : deferred)
		{
			arm.constructIfNecessary(s);
		}
	}

	/**
	 * Triggers immediate construction of the object with the given identifier
	 * if it does not exist. This is an alternative to constructIfNecessary that
	 * should be used sparingly (generally direct access like this is higher
	 * risk, but necessary in some cases)
	 * 
	 * Note that use of this method is inherently risky when taken in context to
	 * .MOD and .COPY. Changes to keys may change the object to which an
	 * identifier refers. Therefore, any resolution that should take place at
	 * runtime should use getReference and resolve the reference.
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
	 * @param key
	 *            The identifier of the Loadable to be built (if otherwise not
	 *            constructed or imported into this
	 *            AbstractReferenceManufacturer), or if an object with that
	 *            identifier already exists, the identifier of the object to be
	 *            returned.
	 * @return The previously existing or new Loadable with the given
	 *         identifier.
	 */
	@Override
	public T constructNowIfNecessary(String key)
	{
		T obj = active.get(key);
		if (obj == null)
		{
			obj = constructObject(key);
			manufactured.add(new WeakReference<>(obj));
		}
		return obj;
	}

	/**
	 * Adds an UnconstructedListener to this AbstractReferenceManufacturer, that
	 * will receive UnconstructedEvents if the validate method of this
	 * AbstractReferenceManufacturer is called and the UnconstructedValidator
	 * given to the validate method does not report that the unconstructed
	 * reference is permitted.
	 * 
	 * @param listener
	 *            The UnconstructedListener to be registered with this
	 *            AbstractReferenceManufacturer
	 */
	@Override
	public void addUnconstructedListener(UnconstructedListener listener)
	{
		listenerList.add(UnconstructedListener.class, listener);
	}

	/**
	 * Returns an array of UnconstructedListeners that are registered with this
	 * AbstractReferenceManufacturer.
	 * 
	 * @return An array of UnconstructedListeners that are registered with this
	 *         AbstractReferenceManufacturer.
	 */
	@Override
	public synchronized UnconstructedListener[] getUnconstructedListeners()
	{
		return listenerList.getListeners(UnconstructedListener.class);
	}

	/**
	 * Removes an UnconstructedListener from this AbstractReferenceManufacturer,
	 * so that it will no longer receive UnconstructedEvents from this
	 * AbstractReferenceManufacturer
	 * 
	 * @param listener
	 *            The UnconstructedListener to be removed from registration with
	 *            this AbstractReferenceManufacturer
	 */
	@Override
	public void removeUnconstructedListener(UnconstructedListener listener)
	{
		listenerList.remove(UnconstructedListener.class, listener);
	}

	/**
	 * Fires a new UnconstructedEvent for the given CDOMReference to any
	 * UnconstructedListener objects registered with this
	 * AbstractReferenceManufacturer
	 * 
	 * @param ref
	 *            The CDOMReference to which the UnconstructedEvent should
	 *            refer.
	 */
	@Override
	public void fireUnconstuctedEvent(CDOMReference<?> ref)
	{
		Object[] listeners = listenerList.getListenerList();
		/*
		 * This list is decremented from the end of the list to the beginning in
		 * order to maintain consistent operation with how Java AWT and Swing
		 * listeners are notified of Events (they are in reverse order to how
		 * they were added to the Event-owning object).
		 */
		UnconstructedEvent uEvent = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == UnconstructedListener.class)
			{
				// Lazily create event
				if (uEvent == null)
				{
					uEvent = new UnconstructedEvent(this, ref); // NOPMD
				}
				((UnconstructedListener) listeners[i + 1]).unconstructedReferenceFound(uEvent);
			}
		}
	}

	/**
	 * Returns the number of objects that are constructed in this
	 * AbstractReferenceManufacturer (These could be natively constructed or
	 * imported objects and does not count duplicates)
	 * 
	 * @return The number of objects that are constructed in this
	 *         AbstractReferenceManufacturer
	 */
	@Override
	public int getConstructedObjectCount()
	{
		return active.size();
	}

	@Override
	public ManufacturableFactory<T> getFactory()
	{
		return factory;
	}

	@Override
	public String getReferenceDescription()
	{
		return factory.getReferenceDescription();
	}

	@Override
	public Collection<CDOMReference<T>> getAllReferences()
	{
		List<CDOMReference<T>> list = new ArrayList<>();
		if (allRef != null)
		{
			list.add(allRef);
		}
		list.addAll(getTypeReferences());
		list.addAll(getReferenced());
		return list;
	}

	@Override
	public void addDerivativeObject(T obj)
	{
		Objects.requireNonNull(obj, "Derivative Object cannot be null");
		derivatives.add(obj);
	}

	@Override
	public Collection<T> getDerivativeObjects()
	{
		return new ArrayList<>(derivatives);
	}

	@Override
	public T convert(String key)
	{
		return getActiveObject(key);
	}

	@Override
	public Indirect<T> convertIndirect(String key)
	{
		return isResolved ? new BasicIndirect<>(this, getActiveObject(key)) : getReference(key);
	}

	@Override
	public String getIdentifierType()
	{
		if (Dynamic.class.equals(getManagedClass()))
		{
			return factory.getPersistentFormat();
		}
		return StringPClassUtil.getStringFor(getManagedClass());
	}

	@Override
	public Class<T> getManagedClass()
	{
		return factory.getReferenceClass();
	}

	@Override
	public String unconvert(T arg0)
	{
		return arg0.getKeyName();
	}

	@Override
	public Optional<FormatManager<?>> getComponentManager()
	{
		return Optional.empty();
	}

	@Override
	public boolean isDirect()
	{
		return false;
	}

	@Override
	public ClassIdentity<T> getReferenceIdentity()
	{
		return factory.getReferenceIdentity();
	}

	@Override
	public String getPersistentFormat()
	{
		return factory.getPersistentFormat();
	}
}
