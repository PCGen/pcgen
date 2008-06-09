package pcgen.cdom.reference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import pcgen.base.lang.CaseInsensitiveString;
import pcgen.base.util.HashMapToInstanceList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PCClass;
import pcgen.util.Logging;

public abstract class AbstractReferenceManufacturer<T extends CDOMObject, RT extends CDOMSingleRef<T>>
		implements ReferenceManufacturer<T, RT>
{
	private final Class<T> refClass;

	private Map<String[], CDOMTypeRef<T>> typeReferences = new HashMap<String[], CDOMTypeRef<T>>();

	private CDOMAllRef<T> allRef;
	
	public AbstractReferenceManufacturer(Class<T> cl)
	{
		refClass = cl;
	}

	public CDOMTypeRef<T> getTypeReference(String... types)
	{
		for (String type : types)
		{
			if (type.length() == 0)
			{
				Logging.errorPrint("Attempt to acquire empty Type "
						+ "(the type String contains an empty element)");
				return null;
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
		for (Entry<String[], CDOMTypeRef<T>> me : typeReferences.entrySet())
		{
			if (Arrays.deepEquals(me.getKey(), types))
			{
				return me.getValue();
			}
		}
		// Didn't find the appropriate key, create new
		CDOMTypeRef<T> cgr = new CDOMTypeRef<T>(refClass, types);
		typeReferences.put(types, cgr);
		return cgr;
	}

	public CDOMAllRef<T> getAllReference()
	{
		if (allRef == null)
		{
			allRef = new CDOMAllRef<T>(refClass);
		}
		return allRef;
	}

	public Class<T> getCDOMClass()
	{
		return refClass;
	}

	public void resolveReferences()
	{
		for (T obj : getAllConstructedCDOMObjects())
		{
			if (allRef != null)
			{
				allRef.addResolution(obj);
			}
			for (Map.Entry<String[], CDOMTypeRef<T>> me : typeReferences.entrySet())
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

	public void resetReferences()
	{
		if (allRef != null)
		{
			allRef.clearResolution();
		}
		for (CDOMTypeRef<T> ref : typeReferences.values())
		{
			ref.clearResolution();
		}
		for (RT ref : referenced.values())
		{
			ref.clearResolution();
		}
	}
	
	private HashMapToInstanceList<CaseInsensitiveString, T> duplicates = new HashMapToInstanceList<CaseInsensitiveString, T>();

	private Map<String, T> active = new TreeMap<String, T>(String.CASE_INSENSITIVE_ORDER);

	private List<String> deferred = new ArrayList<String>();

	private Map<String, RT> referenced = new TreeMap<String, RT>(String.CASE_INSENSITIVE_ORDER);

	public void registerWithKey(T obj, String key)
	{
		if (!refClass.isInstance(obj))
		{
			Logging.errorPrint("Attempted to register a "
					+ obj.getClass().getName() + " in " + refClass.getName()
					+ " ReferenceSupport");
			return;
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

	public T silentlyGetConstructedCDOMObject(String val)
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

	public T getConstructedCDOMObject(String val)
	{
		T obj = silentlyGetConstructedCDOMObject(val);
		if (obj == null)
		{
			Logging.errorPrint("Someone expected " + refClass.getSimpleName()
					+ " " + val + " to exist.");
		}
		return obj;
	}

	public T constructCDOMObject(String val)
	{
		if (val.equals(""))
		{
			throw new IllegalArgumentException("Cannot build empty name");
		}
		try
		{
			T obj = refClass.newInstance();
			obj.setName(val);
			registerWithKey(obj, val);
			return obj;
		}
		catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new IllegalArgumentException(refClass + " " + val);
	}

	public void reassociateKey(String value, T obj)
	{
		String oldKey = obj.getKeyName();
		if (oldKey.equalsIgnoreCase(value))
		{
			Logging.debugPrint("Worthless Key change encountered: "
					+ obj.getDisplayName() + " " + oldKey);
		}
		forgetObject(obj);
		registerWithKey(obj, value);
	}

	public boolean forgetObject(T obj) throws InternalError
	{
		if (!refClass.isInstance(obj))
		{
			// TODO Error
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
			throw new InternalError("Did not find " + obj + " under " + key);
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

	public boolean containsConstructedCDOMObject(String key)
	{
		return active.containsKey(key);
	}

	public CDOMSingleRef<T> getCDOMReference(String val)
	{
		// TODO Auto-generated method stub
		// TODO This is incorrect, but a hack for now :)
		if (val == null)
		{
			throw new IllegalArgumentException(val);
		}
		if (val.equals(""))
		{
			throw new IllegalArgumentException(val);
		}
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

		RT ref = referenced.get(val);
		if (ref == null)
		{
			ref = getReference(val);
			referenced.put(val, ref);
		}
		return ref;
	}

	public boolean validate()
	{
		boolean returnGood = true;
//		for (CaseInsensitiveString second : duplicates.getKeySet())
//		{
//			if (SettingsHandler.isAllowOverride())
//			{
//				List<T> list = duplicates.getListFor(second);
//				T good = active.get(second);
//				for (int i = 0; i < list.size(); i++)
//				{
//					T dupe = list.get(i);
//					// If the new object is more recent than the current
//					// one, use the new object
//					final Date origDate = good.getSourceEntry().getSourceBook()
//							.getDate();
//					final Date dupeDate = dupe.getSourceEntry().getSourceBook()
//							.getDate();
//					if ((dupeDate != null)
//							&& ((origDate == null) || ((dupeDate
//									.compareTo(origDate) > 0))))
//					{
//						duplicates.removeFromListFor(second, good);
//						good = dupe;
//					}
//					else
//					{
//						duplicates.removeFromListFor(second, dupe);
//					}
//				}
//				if (!good.equals(active.get(second)))
//				{
//					active.put(second, good);
//				}
//			}
//			else
//			{
//				Logging.errorPrint("More than one " + baseClass.getSimpleName()
//						+ " with key/name " + second + " was built");
//				returnGood = false;
//			}
//		}
		for (Object second : active.keySet())
		{
			T activeObj = active.get(second);
			String keyName = activeObj.getKeyName();
			if (keyName == null)
			{
				System.err.println(activeObj.getClass() + " "
						+ activeObj.get(StringKey.NAME));
			}
			else if (!keyName.equalsIgnoreCase(second.toString()))
			{
				 Logging.errorPrint("Magical Key Change: " + second + " to "
						+ keyName);
				returnGood = false;
			}
		}
		for (Object s : referenced.keySet())
		{
			if (!active.containsKey(s) && !deferred.contains(s))
			{
				if (!s.toString().startsWith("*"))
				{
					Logging.errorPrint("Unconstructed Reference: "
							+ refClass.getSimpleName() + " " + s);
					returnGood = false;
				}
				constructCDOMObject(s.toString());
			}
		}
		return returnGood;
	}

	public void constructIfNecessary(String value)
	{
		/*
		 * TODO FIXME Need to ensure that items that are built here are tagged
		 * as manufactured, so that they are not written out to LST files
		 */
		deferred.add(value);
	}

	public void clear()
	{
		duplicates.clear();
		active.clear();
		deferred.clear();
		referenced.clear();
	}

	public Collection<T> getAllConstructedCDOMObjects()
	{
		Set<T> set = new HashSet<T>();
		set.addAll(active.values());
		return set;
	}

	public void fillReferences()
	{
		for (Entry<String, RT> me : referenced.entrySet())
		{
			T activeObj = active.get(me.getKey());
			if (activeObj != null)
			{
				me.getValue().addResolution(activeObj);
			}
			else
			{
				System.err.println("Unable to Resolve: " + refClass + " " + me.getKey());
			}
		}
	}

	public void buildDeferredObjects()
	{
		for (Object cis : deferred)
		{
			if (!active.containsKey(cis))
			{
				constructCDOMObject(cis.toString());
			}
		}
	}

	@Override
	protected Object clone() throws CloneNotSupportedException
	{
		/*
		 * FUTURE This makes a strong (and limiting) assumption - that any Game
		 * Mode (and thus any instance of SimpleReferenceContext) will only be
		 * active at one time. This means that there cannot be two sets of
		 * campaigns loaded that both reference the same game mode. This is not
		 * limiting in 5.15/5.16 (at this time, anyway), but it does limit
		 * future expandability of PCGen. There is a significant reason for this
		 * limitation that I'm unsure how to work around without some really
		 * serious deep inspection of CDOMObjects. The problem is that
		 * references may be built in the game mode that reference objects NOT
		 * in the Game Mode. (Global tokens can be used in the Game Mode). The
		 * challenge with that is that the *resolution is already built* and we
		 * don't want to have to know everywhere a reference could be within a
		 * Game Mode PObject (e.g. a PCStat) in order to update all of those
		 * references each time a set of Campaigns is uploaded. Therefore, we
		 * keep one set of references, and allow the content of those references
		 * to be cleared/updated for each set of campaigns loaded. It is likely
		 * that the the solution to get around this limitation in the long term
		 * is to reload the Game Mode files once for each GameMode/Campaign Set
		 * that is loaded at the same time.
		 */
		AbstractReferenceManufacturer<T, RT> arm = (AbstractReferenceManufacturer<T, RT>) super.clone();
		if (arm.allRef != null)
		{
			arm.allRef.clearResolution();
		}
		arm.typeReferences = new HashMap<String[], CDOMTypeRef<T>>();
		for (Map.Entry<String[], CDOMTypeRef<T>> me : typeReferences.entrySet())
		{
			CDOMTypeRef<T> ref = me.getValue();
			ref.clearResolution();
			arm.typeReferences.put(me.getKey(), ref);
		}
		arm.referenced = new TreeMap<String, RT>(String.CASE_INSENSITIVE_ORDER);
		for (Map.Entry<String, RT> me : referenced.entrySet())
		{
			RT ref = me.getValue();
			ref.clearResolution();
			arm.referenced.put(me.getKey(), ref);
		}
		arm.duplicates = new HashMapToInstanceList<CaseInsensitiveString, T>();
		arm.duplicates.addAllLists(duplicates);
		arm.active = new TreeMap<String, T>(String.CASE_INSENSITIVE_ORDER);
		arm.active.putAll(active);
		arm.deferred = new ArrayList<String>();
		arm.deferred.addAll(deferred);
		return arm;
	}

	
}
