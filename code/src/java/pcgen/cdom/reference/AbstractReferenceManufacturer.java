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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.event.EventListenerList;

import pcgen.base.lang.CaseInsensitiveString;
import pcgen.base.lang.UnreachableError;
import pcgen.base.util.FixedStringList;
import pcgen.base.util.HashMapToInstanceList;
import pcgen.base.util.KeyMap;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.AbilityUtilities;
import pcgen.core.PCClass;
import pcgen.util.Logging;

/**
 * An AbstractReferenceManufacturer is a concrete, but abstract object capable
 * of creating CDOMReferences of a given "form". That "form" includes a specific
 * Class of CDOMObject, or a specific Class/Category for Categorized CDOMObjects
 * (this class does not make distinction between the Class and Class/Categorized
 * cases)
 * 
 * The Class is designed to share significant common code between
 * implementations of the ReferenceManufacturer interface.
 * 
 * @param <T>
 *            The Class of object this AbstractReferenceManufacturer can
 *            manufacture
 * @param <RT>
 *            The Class of Single Reference that this
 *            AbstractReferenceManufacturer will produce
 * @param <TRT>
 *            The Class of Type Reference that this
 *            AbstractReferenceManufacturer will produce
 * @param <ART>
 *            The Class of All Reference that this AbstractReferenceManufacturer
 *            will produce
 */
public abstract class AbstractReferenceManufacturer<T extends CDOMObject, SRT extends CDOMSingleRef<T>, TRT extends CDOMGroupRef<T>, ART extends CDOMGroupRef<T>>
		implements ReferenceManufacturer<T>
{
	
	private boolean isResolved = false;
	
	/**
	 * The class of object this AbstractReferenceManufacturer constructs or
	 * builds references to.
	 */
	private final Class<T> refClass;

	/**
	 * The "ALL" reference, if it is ever referenced. This ensures that only one
	 * "ALL" reference is ever built (and allows it to be reused if the
	 * reference is requested a second time). This also stores the reference so
	 * that it can be appropriately resolved when resolveReferences() is called.
	 */
	private ART allRef;

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
	private final Map<FixedStringList, WeakReference<TRT>> typeReferences = new TreeMap<FixedStringList, WeakReference<TRT>>(
			FixedStringList.CASE_INSENSITIVE_ORDER);

	/**
	 * Storage for individual references. This ensures that only one reference
	 * is ever built for any identifier. (and allows those references to be
	 * reused if a reference to an identifier is requested a second time). This
	 * also stores the reference so that it can be appropriately resolved when
	 * resolveReferences() is called.
	 */
	private final Map<String, WeakReference<SRT>> referenced = new TreeMap<String, WeakReference<SRT>>(
			String.CASE_INSENSITIVE_ORDER);

	/**
	 * Stores the active objects for this AbstractReferenceManufacturer. These
	 * are objects that have been constructed or imported into the
	 * AbstractReferenceManufacturer.
	 */
	private final KeyMap<T> active = new KeyMap<T>();

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
	private final HashMapToInstanceList<CaseInsensitiveString, T> duplicates = new HashMapToInstanceList<CaseInsensitiveString, T>();

	/**
	 * Contains a list of deferred objects. Identifiers for objects for which
	 * construction was deferred were inserted into the
	 * AbstractReferenceManufacturer using constructIfNecessary(String). Objects
	 * will be constructed when buildDeferredReferences() is called, if and only
	 * if no object with the matching identifier has been constructed or
	 * imported into this AbstractReferenceManufacturer.
	 */
	private final List<String> deferred = new ArrayList<String>();

	/**
	 * Contains a list of manufactured objects (those that are built implicitly
	 * by tokens like NATURALATTACKS). These can be "displaced" by object which
	 * are later built explicitly in a WeaponProf LST file, for example.
	 */
	private final List<WeakReference<T>> manufactured = new ArrayList<WeakReference<T>>();

	/**
	 * Contains a list of unconstructed objects (those that are caught during
	 * validate as not having been built. These are queued in order to avoid
	 * building a "real" object, but instead knowing inside of "resolve
	 * references" when it is safe to populate the reference with an otherwise
	 * "empty" object. This insertion of a reference is done to avoid null
	 * pointer exceptions at runtime (and to avoid constant checking of whether
	 * a CDOMSingleRef resolves to null or a real object)
	 */
	private final Set<String> unconstructed = new HashSet<String>();

	/**
	 * The EventListenerList which contains the listeners to this
	 * AbstractReferenceManufacturer.
	 */
	private final transient EventListenerList listenerList = new EventListenerList();

	/**
	 * Constructs a new AbstractReferenceManufacturer for the given Class.
	 * 
	 * @param cl
	 *            The Class of object this AbstractReferenceManufacturer will
	 *            construct and reference.
	 * @throws IllegalArgumentException
	 *             if the given Class is null or the given Class does not have a
	 *             public, zero argument constructor
	 * 
	 */
	public AbstractReferenceManufacturer(Class<T> cl)
	{
		if (cl == null)
		{
			throw new IllegalArgumentException("Reference Class for "
					+ getClass().getName() + " cannot be null");
		}
		try
		{
			cl.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new IllegalArgumentException("Class for "
					+ getClass().getName()
					+ " must possess a zero-argument constructor", e);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalArgumentException("Class for "
					+ getClass().getName()
					+ " must possess a public zero-argument constructor", e);
		}
		refClass = cl;
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
	public CDOMGroupRef<T> getTypeReference(String... types)
	{
		for (String type : types)
		{
			if (type == null || type.length() == 0)
			{
				throw new IllegalArgumentException(
						"Attempt to acquire empty Type "
								+ "(the type String contains a null or empty element)");
			}
			if (type.indexOf('.') != -1)
			{
				throw new IllegalArgumentException(
						"Cannot build Reference with type conaining a period: "
								+ type);
			}
			if (type.indexOf('=') != -1)
			{
				throw new IllegalArgumentException(
						"Cannot build Reference with type conaining an equals: "
								+ type);
			}
			if (type.indexOf(',') != -1)
			{
				throw new IllegalArgumentException(
						"Cannot build Reference with type conaining a comma: "
								+ type);
			}
			if (type.indexOf('|') != -1)
			{
				throw new IllegalArgumentException(
						"Cannot build Reference with type conaining a pipe: "
								+ type);
			}
		}
		Arrays.sort(types);
		FixedStringList typeList = new FixedStringList(types);
		WeakReference<TRT> ref = typeReferences.get(typeList);
		if (ref != null)
		{
			TRT trt = ref.get();
			if (trt != null)
			{
				return trt;
			}
		}
		// Didn't find the appropriate key, create new
		TRT cgr = getLocalTypeReference(types);
		typeReferences.put(typeList, new WeakReference<TRT>(cgr));
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
	public CDOMGroupRef<T> getAllReference()
	{
		if (allRef == null)
		{
			allRef = getLocalAllReference();
		}
		return allRef;
	}

	/**
	 * The class of object this AbstractReferenceManufacturer represents.
	 * 
	 * @return The class of object this AbstractReferenceManufacturer
	 *         represents.
	 */
	public Class<T> getReferenceClass()
	{
		return refClass;
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
	public void resolveReferences()
	{
		resolvePrimitiveReferences();
		resolveAllReference();
		for (WeakReference<TRT> ref : typeReferences.values())
		{
			TRT trt = ref.get();
			if (trt != null && trt.getObjectCount() == 0)
			{
				Logging.errorPrint("Error: No " + getReferenceDescription()
						+ " objects of " + trt.getLSTformat()
						+ " were loaded but were referred to in the data");
				fireUnconstuctedEvent(trt);
			}
		}
		isResolved = true;
	}

	private void resolvePrimitiveReferences()
	{
		List<String> throwaway = new ArrayList<String>();
		for (Entry<String, WeakReference<SRT>> me1 : referenced.entrySet())
		{
			SRT value = me1.getValue().get();
			if (value != null)
			{
				T activeObj = active.get(me1.getKey());
				if (activeObj == null)
				{
					String reduced = AbilityUtilities.getUndecoratedName(me1
							.getKey(), throwaway);
					activeObj = active.get(reduced);
					if (activeObj == null
							&& (unconstructed.contains(me1.getKey()) || unconstructed
									.contains(reduced)))
					{
						activeObj = buildObject(me1.getKey());
					}
					if (activeObj == null)
					{
						Logging.errorPrint("Unable to Resolve: " + refClass + " "
								+ me1.getKey());
					}
					else
					{
						value.addResolution(activeObj);
					}
				}
				else
				{
					value.addResolution(activeObj);
				}
			}
		}
	}

	private void resolveAllReference()
	{
		for (T obj : getAllObjects())
		{
			if (allRef != null)
			{
				allRef.addResolution(obj);
			}
			for (Map.Entry<FixedStringList, WeakReference<TRT>> me : typeReferences
					.entrySet())
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
					TRT trt = me.getValue().get();
					if (trt != null)
					{
						trt.addResolution(obj);
					}
				}
			}
		}
		if (allRef != null && allRef.getObjectCount() == 0)
		{
			Logging.errorPrint("Error: No " + getReferenceDescription()
					+ " objects were loaded but were referred to in the data");
			fireUnconstuctedEvent(allRef);
		}
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
	 * @param o
	 *            The object to be imported into this
	 *            AbstractReferenceManufacturer
	 * @param key
	 *            The identifier of the object to be imported into this
	 *            AbstractReferenceManufacturer
	 * @throws IllegalArgumentException
	 *             if the given object is not of the Class that this
	 *             AbstractReferenceManufacturer constructs and references
	 */
	public void addObject(T obj, String key)
	{
		if (!refClass.isInstance(obj))
		{
			throw new IllegalArgumentException("Attempted to register a "
					+ obj.getClass().getName() + " in " + refClass.getName()
					+ " ReferenceSupport");
		}
		T current = active.get(key);
		if (current == null)
		{
			active.put(key, obj);
		}
		else
		{
			duplicates.addToListFor(new CaseInsensitiveString(key), obj);
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
	 * @param val
	 *            identifier of the object to be returned
	 * @return The object stored in this AbstractReferenceManufacturer with the
	 *         given identifier, or null if this AbstractReferenceManufacturer
	 *         does not contain an object with the given identifier.
	 */
	public T getActiveObject(String val)
	{
		return active.get(val);
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
	 * @param val
	 *            identifier of the object to be returned
	 * @return The object stored in this AbstractReferenceManufacturer with the
	 *         given identifier, or null if this AbstractReferenceManufacturer
	 *         does not contain an object with the given identifier.
	 */
	public T getObject(String val)
	{
		T po = active.get(val);
		if (po != null)
		{
			if (duplicates.containsListFor(new CaseInsensitiveString(val)))
			{
				Logging.errorPrint("Reference to Constructed "
						+ refClass.getSimpleName() + " " + val
						+ " is ambiguous");
			}
			return po;
		}
		return null;
	}

	/**
	 * Constructs a new CDOMObject of the Class or Class/Category represented by
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
	 *            The identifier of the CDOMObject to be constructed
	 * @return The new CDOMObject of the Class or Class/Category represented by
	 *         this AbstractReferenceManufacturer
	 * @throws IllegalArgumentException
	 *             if the given identifier is null or empty (length is zero)
	 */
	public T constructObject(String val)
	{
		T obj = buildObject(val);
		addObject(obj, val);
		return obj;
	}

	/**
	 * Constructs a new CDOMObject of the Class or Class/Category represented by
	 * this AbstractReferenceManufacturer
	 * 
	 * This should remain protected (vs. public) as it is for "internal use
	 * only"; it serves as a convenience method to wrap the .newInstance call
	 * and the possible Exceptions. Other classes should use
	 * constructObject(String)
	 * 
	 * @param key
	 *            The identifier of the CDOMObject to be constructed
	 * @return The new CDOMObject of the Class or Class/Category represented by
	 *         this AbstractReferenceManufacturer
	 * @throws IllegalArgumentException
	 *             if the given identifier is null or empty (length is zero)
	 */
	protected T buildObject(String val)
	{
		if (val == null || val.equals(""))
		{
			throw new IllegalArgumentException("Cannot build empty name");
		}
		try
		{
			T obj = refClass.newInstance();
			obj.setName(val);
			return obj;
		}
		catch (InstantiationException e)
		{
			throw new UnreachableError(
					"Class was tested at AbstractReferenceManufacturer "
							+ "construction to ensure it had a public, zero-argument constructor");
		}
		catch (IllegalAccessException e)
		{
			throw new UnreachableError(
					"Class was tested at AbstractReferenceManufacturer "
							+ "construction to ensure it had a public, zero-argument constructor");
		}
	}

	/**
	 * Changes the identifier for a given object, as stored in this
	 * AbstractReferenceManufacturer.
	 * 
	 * @param key
	 *            The new identifier to be used for the given object
	 * @param o
	 *            The object for which the identifier in this
	 *            AbstractReferenceManufacturer should be changed
	 */
	public void renameObject(String value, T obj)
	{
		String oldKey = obj.getKeyName();
		if (oldKey.equalsIgnoreCase(value))
		{
			Logging.debugPrint("Worthless Key change encountered: "
					+ obj.getDisplayName() + " " + oldKey);
		}
		forgetObject(obj);
		addObject(obj, value);
	}

	/**
	 * Remove the given object from this AbstractReferenceManufacturer. Returns
	 * true if the object was removed from this AbstractReferenceManufacturer;
	 * false otherwise.
	 * 
	 * @param o
	 *            The object to be removed from this
	 *            AbstractReferenceManufacturer.
	 * @return true if the object was removed from this
	 *         AbstractReferenceManufacturer; false otherwise.
	 */
	public boolean forgetObject(T obj) throws InternalError
	{
		if (!refClass.isInstance(obj))
		{
			throw new IllegalArgumentException(
					"Object to be forgotten does not match Class of this AbstractReferenceManufacturer");
		}
		String key = active.getKeyFor(obj);
		if (key == null)
		{
			/*
			 * TODO This is a bug - the key name is not necessarily loaded into
			 * the object, it may have been consumed by the object context... :P
			 */
			CaseInsensitiveString ocik = new CaseInsensitiveString(obj
					.getKeyName());
			duplicates.removeFromListFor(ocik, obj);
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
	public boolean containsObject(String key)
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
	public CDOMSingleRef<T> getReference(String val)
	{
		/*
		 * TODO This is incorrect, but a hack for now :)
		 * 
		 * Mainly this throws around IllegalArgumentException in order to catch
		 * bad parsing issues (design flaws in the code). Not sure if we want to
		 * continue that long term? Once tokens are truly tested this may not be
		 * necessary or desireable.
		 */
		if (val == null)
		{
			throw new IllegalArgumentException(
					"Cannot request a reference to null identifier");
		}
		if (val.length() == 0)
		{
			throw new IllegalArgumentException(
					"Cannot request a reference to an empty identifier");
		}
		/*
		 * Items thrown below this point are for protection from coding errors
		 * in LST files, not part of the public interface of this method
		 */
		try
		{
			Integer.parseInt(val);
			throw new IllegalArgumentException(val);
		}
		catch (NumberFormatException nfe)
		{
			// ok
		}
		if (val.startsWith("TYPE"))
		{
			throw new IllegalArgumentException(val);
		}
		if (val.equalsIgnoreCase("ANY"))
		{
			throw new IllegalArgumentException(val);
		}
		if (val.equalsIgnoreCase("ALL"))
		{
			throw new IllegalArgumentException(val);
		}
		if (val.startsWith("PRE"))
		{
			throw new IllegalArgumentException(val);
		}
		if (val.startsWith("CHOOSE"))
		{
			throw new IllegalArgumentException(val);
		}
		if (val.startsWith("TIMES="))
		{
			throw new IllegalArgumentException(val);
		}
		if (val.startsWith("TIMEUNIT="))
		{
			throw new IllegalArgumentException(val);
		}
		if (val.startsWith("CASTERLEVEL="))
		{
			throw new IllegalArgumentException(val);
		}
		if (refClass.equals(PCClass.class))
		{
			if (val.startsWith("CLASS"))
			{
				throw new IllegalArgumentException(val);
			}
			else if (val.startsWith("SUB"))
			{
				throw new IllegalArgumentException(val);
			}
			else
			{
				try
				{
					Integer.parseInt(val);
					throw new IllegalArgumentException(val);
				}
				catch (NumberFormatException nfe)
				{
					// Want this!
				}
			}
		}

		WeakReference<SRT> wr = referenced.get(val);
		if (wr != null)
		{
			SRT ref = wr.get();
			if (ref != null)
			{
				return ref;
			}
		}
		CDOMSingleRef<T> ref;
		if (isResolved)
		{
			T current = active.get(val);
			if (current == null)
			{
				throw new IllegalArgumentException(val
						+ " is not valid post-resolution "
						+ "because it was never constructed");
			}
			ref = CDOMDirectSingleRef.getRef(current);
		}
		else
		{
			SRT lr = getLocalReference(val);
			referenced.put(val, new WeakReference<SRT>(lr));
			ref = lr;
		}
		return ref;
	}

	/**
	 * Returns a CDOMSingleRef for the given identifier as defined by the class
	 * that extends AbstractReferenceManufacturer. This is designed to be used
	 * ONLY within AbstractReferenceManufacturer and should not be called by
	 * other objects.
	 * 
	 * @param ident
	 *            The identifier for which a CDOMTransparentSingleRef should be
	 *            returned.
	 * @return a CDOMSingleRef for the given identifier as defined by the class
	 *         that extends AbstractReferenceManufacturer.
	 */
	protected abstract SRT getLocalReference(String ident);

	/**
	 * Returns a CDOMGroupRef for the given types as defined by the class that
	 * extends AbstractReferenceManufacturer. This is designed to be used ONLY
	 * within AbstractReferenceManufacturer and should not be called by other
	 * objects.
	 * 
	 * @param types
	 *            An array of the types of objects to which the returned
	 *            CDOMReference will refer.
	 * @return A CDOMGroupRef for the given types as defined by the class that
	 *         extends AbstractReferenceManufacturer.
	 */
	protected abstract TRT getLocalTypeReference(String[] types);

	/**
	 * Returns a CDOMGroupRef for all objects of the class that extends
	 * AbstractReferenceManufacturer. This is designed to be used ONLY within
	 * AbstractReferenceManufacturer and should not be called by other objects.
	 * 
	 * @return A CDOMGroupRef for all objects of the class that extends
	 *         AbstractReferenceManufacturer.
	 */
	protected abstract ART getLocalAllReference();

	/**
	 * Returns true if this AbstractReferenceManufacturer is "valid". A "valid"
	 * AbstractReferenceManufacturer is one where all of the following are true:
	 * 
	 * (1) Any object stored in the AbstractReferenceManufacturer reports that
	 * it's KEY (as defined by CDOMObject.getKeyName()) matches the identifier
	 * used to store the object in the AbstractReferenceManufacturer.
	 * 
	 * (2) Any identifier to which a reference was made has a constructed or
	 * imported object with that identifier present in the
	 * AbstractReferenceManufacturer.
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
	public boolean validate(UnconstructedValidator validator)
	{
		boolean returnGood = true;
		if (validator == null
				|| !validator.allowDuplicates(getReferenceClass()))
		{
			returnGood = validateDuplicates();
		}
		returnGood &= validateNames();
		returnGood &= validateActive();
		returnGood &= validateUnconstructed(validator);
		return returnGood;
	}

	private boolean validateNames()
	{
		for (String key : active.getKeySet())
		{
			/*
			 * http://wiki.pcgen.org/index.php?title=Data_LST_Standards
			 * 
			 * Characters which should never be used in object names are Commas
			 * (,), Pipes (|), Backslashes (\), Colons (:), Semicolons (;),
			 * Periods (.), Brackets ([]), Percent (%), Asterisk (*) and Equals
			 * (=).
			 */
			if (key.indexOf(',') != -1)
			{
				Logging.log(Logging.LST_WARNING, "Found "
						+ getReferenceDescription() + " with KEY: " + key
						+ " which contains a comma "
						+ "(prohibited character in a key)");
			}
			if (key.indexOf('|') != -1)
			{
				Logging.log(Logging.LST_WARNING, "Found "
						+ getReferenceDescription() + " with KEY: " + key
						+ " which contains a pipe "
						+ "(prohibited character in a key)");
			}
			if (key.indexOf('\\') != -1)
			{
				Logging.log(Logging.LST_WARNING, "Found "
						+ getReferenceDescription() + " with KEY: " + key
						+ " which contains a backslash "
						+ "(prohibited character in a key)");
			}
			if (key.indexOf(':') != -1)
			{
				Logging.log(Logging.LST_WARNING, "Found "
						+ getReferenceDescription() + " with KEY: " + key
						+ " which contains a colon "
						+ "(prohibited character in a key)");
			}
			if (key.indexOf(';') != -1)
			{
				Logging.log(Logging.LST_WARNING, "Found "
						+ getReferenceDescription() + " with KEY: " + key
						+ " which contains a semicolon "
						+ "(prohibited character in a key)");
			}
			if (key.indexOf('.') != -1)
			{
				Logging.log(Logging.LST_WARNING, "Found "
						+ getReferenceDescription() + " with KEY: " + key
						+ " which contains a period "
						+ "(prohibited character in a key)");
			}
			if (key.indexOf('%') != -1)
			{
				Logging.log(Logging.LST_WARNING, "Found "
						+ getReferenceDescription() + " with KEY: " + key
						+ " which contains a percent sign "
						+ "(prohibited character in a key)");
			}
			if (key.indexOf('*') != -1)
			{
				Logging.log(Logging.LST_WARNING, "Found "
						+ getReferenceDescription() + " with KEY: " + key
						+ " which contains an asterisk "
						+ "(prohibited character in a key)");
			}
			if (key.indexOf('=') != -1)
			{
				Logging.log(Logging.LST_WARNING, "Found "
						+ getReferenceDescription() + " with KEY: " + key
						+ " which contains an equals sign "
						+ "(prohibited character in a key)");
			}
			if ((key.indexOf('[') != -1) || (key.indexOf(']') != -1))
			{
				Logging.log(Logging.LST_WARNING, "Found "
						+ getReferenceDescription() + " with KEY: " + key
						+ " which contains a bracket  "
						+ "(prohibited character in a key)");
			}
		}
		return true;
	}

	private boolean validateUnconstructed(UnconstructedValidator validator)
	{
		boolean returnGood = true;
		List<String> throwaway = new ArrayList<String>();
		for (Iterator<Entry<String, WeakReference<SRT>>> it = referenced
				.entrySet().iterator(); it.hasNext();)
		{
			Entry<String, WeakReference<SRT>> me = it.next();
			SRT value = me.getValue().get();
			if (value == null)
			{
				it.remove();
			}
			else
			{
				String s = me.getKey();
				if (!active.containsKey(s) && !deferred.contains(s))
				{
					String undec = AbilityUtilities.getUndecoratedName(s,
							throwaway);
					if (!active.containsKey(undec) && !deferred.contains(undec))
					{
						if (s.charAt(0) != '*' && !validate(validator, s))
						{
							Logging.errorPrint("Unconstructed Reference: "
									+ getReferenceDescription() + " " + s);
							fireUnconstuctedEvent(value);
							returnGood = false;
						}
						unconstructed.add(s);
					}
				}
			}
		}
		return returnGood;
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
				Logging
						.errorPrint(activeObj.getClass() + " "
								+ activeObj.get(StringKey.NAME)
								+ " has a null KeyName");
			}
			else if (!keyName.equalsIgnoreCase(second.toString()))
			{
				Logging.errorPrint("Magical Key Change: " + second + " to "
						+ keyName);
				returnGood = false;
			}
		}
		return returnGood;
	}

	private boolean validateDuplicates() throws InternalError
	{
		boolean returnGood = true;
		for (CaseInsensitiveString second : duplicates.getKeySet())
		{
			List<T> list = duplicates.getListFor(second);
			T good = active.get(second.toString());
			for (int i = 0; i < list.size(); i++)
			{
				T dupe = list.get(i);
				if (dupe.isCDOMEqual(good))
				{
					for (Iterator<WeakReference<T>> it = manufactured
							.iterator(); it.hasNext();)
					{
						WeakReference<T> wr = it.next();
						T mfg = wr.get();
						if (mfg == null)
						{
							it.remove();
						}
						//Yes this is instance equality, not .equals
						else if (mfg == good)
						{
							forgetObject(good);
							break;
						}
					}
				}
			}
			if (duplicates.containsListFor(second))
			{
				Logging.errorPrint("More than one "
						+ refClass.getSimpleName() + " with key/name "
						+ second + " was built");
				returnGood = false;
			}
		}
		return returnGood;
	}

	/**
	 * Returns true if the given String (a reference name) is permitted by the
	 * given UnconstructedValidator. Will always return false if the
	 * UnconstructedValidator is null.
	 * 
	 * @param validator
	 *            The UnconstructedValidator to use to determine if the given
	 *            String (a reference name) should be permitted as an
	 *            unconstructed reference.
	 * @param s
	 *            The reference name to be checked to see if the
	 *            UnconstructedValidator will permit it as an unconstructed
	 *            reference.
	 * @return true if the given String (a reference name) is permitted by the
	 *         given UnconstructedValidator; false otherwise.
	 */
	protected abstract boolean validate(UnconstructedValidator validator,
			String s);

	/**
	 * Returns a description of the type of Class or Class/Category that this
	 * AbstractReferenceManufacturer constructs or references. This is designed
	 * to be overridden by classes that extend AbstractReferenceManufacturer so
	 * that AbstractReferenceManufacturer can output error messages that are
	 * useful for users.
	 * 
	 * @return A String description of the type of Class or Class/Category that
	 *         this AbstractReferenceManufacturer constructs or references.
	 */
	protected abstract String getReferenceDescription();

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
	 * @param value
	 *            The identifier of the CDOMObject to be built (if otherwise not
	 *            constructed or imported into this
	 *            AbstractReferenceManufacturer) when buildDeferredObjects() is
	 *            called.
	 */
	public void constructIfNecessary(String value)
	{
		/*
		 * TODO FIXME Need to ensure that items that are built here are tagged
		 * as manufactured, so that they are not written out to LST files
		 */
		deferred.add(value);
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
	public Collection<T> getAllObjects()
	{
		return active.keySortedValues();
	}

	/**
	 * Returns a List of all of the objects contained in this
	 * AbstractReferenceManufacturer in the original order in which they were
	 * imported into this AbstractReferenceManufacturer. This will not return
	 * null, it will return an empty list if no objects have been constructed by
	 * or imported into this AbstractReferenceManufacturer.
	 * 
	 * @return A List of all of the objects contained in this
	 *         AbstractReferenceManufacturer
	 */
	public List<T> getOrderSortedObjects()
	{
		return active.insertOrderValues();
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
	public void buildDeferredObjects()
	{
		for (Object cis : deferred)
		{
			if (!active.containsKey(cis))
			{
				constructObject(cis.toString());
			}
		}
	}

	/**
	 * Returns the "ALL" reference for this AbstractReferenceManufacturer. May
	 * be null if the "ALL" reference was never retrieved through the
	 * getAllReference() method.
	 * 
	 * @return The "ALL" reference for this AbstractReferenceManufacturer.
	 */
	protected ART getAllRef()
	{
		return allRef;
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
	protected Collection<TRT> getTypeReferences()
	{
		List<TRT> list = new ArrayList<TRT>(typeReferences.size());
		for (Iterator<WeakReference<TRT>> it = typeReferences.values()
				.iterator(); it.hasNext();)
		{
			WeakReference<TRT> wr = it.next();
			TRT trt = wr.get();
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
	protected Collection<SRT> getReferenced()
	{
		List<SRT> list = new ArrayList<SRT>();
		for (WeakReference<SRT> wr : referenced.values())
		{
			SRT ref = wr.get();
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
	protected void injectConstructed(ReferenceManufacturer<T> arm)
	{
		//Must maintain order
		for (T value : active.insertOrderValues())
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
	 *            constructed or imported into this
	 *            AbstractReferenceManufacturer), or if an object with that
	 *            identifier already exists, the identifier of the object to be
	 *            returned.
	 * @return The previously existing or new CDOMObject with the given
	 *         identifier.
	 */
	public T constructNowIfNecessary(String name)
	{
		T obj = active.get(name);
		if (obj == null)
		{
			obj = constructObject(name);
			manufactured.add(new WeakReference<T>(obj));
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
	private void fireUnconstuctedEvent(CDOMReference<?> ref)
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
				((UnconstructedListener) listeners[i + 1])
						.unconstructedReferenceFound(uEvent);
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
	public int getConstructedObjectCount()
	{
		return active.size();
	}

	public T getItemInOrder(int item)
	{
		return active.getItemInOrder(item);
	}
}
