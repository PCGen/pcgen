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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import pcgen.base.lang.CaseInsensitiveString;
import pcgen.base.lang.UnreachableError;
import pcgen.base.util.HashMapToInstanceList;
import pcgen.cdom.base.CDOMObject;
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
		implements ReferenceManufacturer<T, SRT>
{
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
	/*
	 * TODO Should there be (4) all types should be upper case? That isn't
	 * enforced here, and that may be a problem in terms of duplication. It's
	 * probably not too problematic, in the sense that a few extra CDOMReference
	 * objects really isn't that big of a deal. But it's still imperfect...
	 */
	private final Map<String[], TRT> typeReferences = new HashMap<String[], TRT>();

	/**
	 * Storage for individual references. This ensures that only one reference
	 * is ever built for any identifier. (and allows those references to be
	 * reused if a refernce to an identifier is requested a second time). This
	 * also stores the reference so that it can be appropriately resolved when
	 * resolveReferences() is called.
	 */
	private final Map<String, SRT> referenced = new TreeMap<String, SRT>(
			String.CASE_INSENSITIVE_ORDER);

	/**
	 * Stores the active objects for this AbstractReferenceManufacturer. These
	 * are objects that have been constructed or imported into the
	 * AbstractReferenceManufacturer.
	 */
	private final Map<String, T> active = new TreeMap<String, T>(
			String.CASE_INSENSITIVE_ORDER);

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
		/*
		 * TODO FIXME This is the SLOW method - better to actually use Jakarta
		 * Commons Collections and create a map that does the lookup based on
		 * deepEquals of an Array...
		 */
		for (Entry<String[], TRT> me : typeReferences.entrySet())
		{
			if (Arrays.deepEquals(me.getKey(), types))
			{
				return me.getValue();
			}
		}
		// Didn't find the appropriate key, create new
		TRT cgr = getLocalTypeReference(types);
		typeReferences.put(types, cgr);
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
		List<String> throwaway = new ArrayList<String>();
		for (Entry<String, SRT> me1 : referenced.entrySet())
		{
			T activeObj = active.get(me1.getKey());
			if (activeObj == null)
			{
				String reduced =
						AbilityUtilities.getUndecoratedName(me1.getKey(),
							throwaway);
				activeObj = active.get(reduced);
				if (activeObj == null)
				{
					Logging.errorPrint("Unable to Resolve: " + refClass + " "
						+ me1.getKey());
				}
				else
				{
					me1.getValue().addResolution(activeObj);
				}
			}
			else
			{
				me1.getValue().addResolution(activeObj);
			}
		}
		for (T obj : getAllObjects())
		{
			if (allRef != null)
			{
				allRef.addResolution(obj);
			}
			for (Map.Entry<String[], TRT> me : typeReferences.entrySet())
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
					me.getValue().addResolution(obj);
				}
			}
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
		if (active.containsKey(key))
		{
			duplicates.addToListFor(new CaseInsensitiveString(key), obj);
		}
		else
		{
			active.put(key, obj);
		}
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
	 * this AbstractReferenceManufacturer
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
		if (val == null || val.equals(""))
		{
			throw new IllegalArgumentException("Cannot build empty name");
		}
		try
		{
			T obj = refClass.newInstance();
			obj.setName(val);
			addObject(obj, val);
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
		/*
		 * TODO This is a bug - the key name is not necessarily loaded into the
		 * object, it may have been consumed by the object context... :P
		 */
		String key = obj.getKeyName();
		CaseInsensitiveString ocik = new CaseInsensitiveString(key);
		CDOMObject act = active.get(key);
		if (act == null)
		{
			throw new UnreachableError("Did not find " + obj + " under " + key);
		}
		if (act.equals(obj))
		{
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
		else
		{
			duplicates.removeFromListFor(ocik, obj);
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
	public SRT getReference(String val)
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

		SRT ref = referenced.get(val);
		if (ref == null)
		{
			ref = getLocalReference(val);
			referenced.put(val, ref);
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
	 * TODO Condition (3) is not enforced presently, due to issues with some
	 * classes allowing duplicates (e.g. Languages)
	 * 
	 * @return true if the AbstractReferenceManufacturer is "valid"; false
	 *         otherwise.
	 */
	public boolean validate()
	{
		boolean returnGood = true;
		/*
		 * Commented out stuff is the case 3 to-do
		 */
		// for (CaseInsensitiveString second : duplicates.getKeySet())
		// {
		// if (SettingsHandler.isAllowOverride())
		// {
		// List<T> list = duplicates.getListFor(second);
		// T good = active.get(second);
		// for (int i = 0; i < list.size(); i++)
		// {
		// T dupe = list.get(i);
		// // If the new object is more recent than the current
		// // one, use the new object
		// final Date origDate = good.getSourceEntry().getSourceBook()
		// .getDate();
		// final Date dupeDate = dupe.getSourceEntry().getSourceBook()
		// .getDate();
		// if ((dupeDate != null)
		// && ((origDate == null) || ((dupeDate
		// .compareTo(origDate) > 0))))
		// {
		// duplicates.removeFromListFor(second, good);
		// good = dupe;
		// }
		// else
		// {
		// duplicates.removeFromListFor(second, dupe);
		// }
		// }
		// if (!good.equals(active.get(second)))
		// {
		// active.put(second, good);
		// }
		// }
		// else
		// {
		// Logging.errorPrint("More than one " + baseClass.getSimpleName()
		// + " with key/name " + second + " was built");
		// returnGood = false;
		// }
		// }
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
		List<String> throwaway = new ArrayList<String>();
		for (String s : referenced.keySet())
		{
			if (!active.containsKey(s) && !deferred.contains(s))
			{
				String undec = AbilityUtilities
						.getUndecoratedName(s, throwaway);
				if (!active.containsKey(undec) && !deferred.contains(undec))
				{
					if (s.charAt(0) != '*')
					{
						Logging.errorPrint("Unconstructed Reference: "
								+ getReferenceDescription() + " " + s);
						returnGood = false;
					}
					constructObject(s);
				}
			}
		}
		return returnGood;
	}

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
	 * AbstractReferenceManufacturer. This will not return null, it will return
	 * an empty list if no objects have been constructed by or imported into
	 * this AbstractReferenceManufacturer.
	 * 
	 * @return A Collection of all of the objects contained in this
	 *         AbstractReferenceManufacturer
	 */
	public Collection<T> getAllObjects()
	{
		List<T> list = new ArrayList<T>(active.size());
		list.addAll(active.values());
		return list;
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

	protected ART getAllRef()
	{
		return allRef;
	}

	protected Collection<TRT> getTypeReferences()
	{
		return typeReferences.values();
	}

	protected Collection<SRT> getReferenced()
	{
		return referenced.values();
	}

	protected void injectConstructed(ReferenceManufacturer<T, ?> arm)
	{
		for (Map.Entry<String, T> me : active.entrySet())
		{
			arm.addObject(me.getValue(), me.getKey());
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
}
