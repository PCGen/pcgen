/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.rules.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import pcgen.base.lang.CaseInsensitiveString;
import pcgen.base.util.HashMapToInstanceList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.core.PCClass;
import pcgen.util.Logging;

public class ReferenceSupport<T extends CDOMObject, RT extends CDOMSingleRef<T>>
{

	private HashMapToInstanceList<CaseInsensitiveString, T> duplicates = new HashMapToInstanceList<CaseInsensitiveString, T>();

	private Map<CaseInsensitiveString, T> active = new HashMap<CaseInsensitiveString, T>();

	private List<CaseInsensitiveString> deferred = new ArrayList<CaseInsensitiveString>();

	private Map<CaseInsensitiveString, RT> referenced = new HashMap<CaseInsensitiveString, RT>();

	private final Class<T> baseClass;

	private final ReferenceManufacturer<T, RT> referenceMfg;

	public ReferenceSupport(ReferenceManufacturer<T, RT> mfg)
	{
		referenceMfg = mfg;
		baseClass = mfg.getCDOMClass();
	}

	public void registerWithKey(T obj, String key)
	{
		if (!baseClass.isInstance(obj))
		{
			Logging.errorPrint("Attempted to register a "
					+ obj.getClass().getName() + " in " + baseClass.getName()
					+ " ReferenceSupport");
			return;
		}
		CaseInsensitiveString cik = new CaseInsensitiveString(key);
		if (active.containsKey(cik))
		{
			duplicates.addToListFor(cik, obj);
		}
		else
		{
			active.put(cik, obj);
		}
	}

	public T silentlyGetConstructedCDOMObject(String val)
	{
		CaseInsensitiveString civ = new CaseInsensitiveString(val);
		T po = active.get(civ);
		if (po != null)
		{
			if (duplicates.containsListFor(civ))
			{
				Logging.errorPrint("Reference to Constructed "
						+ baseClass.getSimpleName() + " " + val
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
			Logging.errorPrint("Someone expected " + baseClass.getSimpleName()
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
			T obj = baseClass.newInstance();
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
		throw new IllegalArgumentException(baseClass + " " + val);
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

	public void forgetObject(T obj) throws InternalError
	{
		if (!baseClass.isInstance(obj))
		{
			// TODO Error
		}
		/*
		 * TODO This is a bug - the key name is not necessarily loaded into the
		 * object, it may have been consumed by the object context... :P
		 */
		String key = obj.getKeyName();
		CaseInsensitiveString ocik = new CaseInsensitiveString(key);
		CDOMObject act = active.get(ocik);
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
				active.remove(ocik);
			}
			else
			{
				T newActive = duplicates.getElementInList(ocik, 0);
				duplicates.removeFromListFor(ocik, newActive);
				active.put(ocik, newActive);
			}
		}
		else
		{
			duplicates.removeFromListFor(ocik, obj);
		}
	}

	public void forgetCDOMObjectKeyed(String forgetKey)
	{
		CaseInsensitiveString cis = new CaseInsensitiveString(forgetKey);
		active.remove(cis);
		duplicates.removeListFor(cis);
	}

	public boolean containsConstructedCDOMObject(String key)
	{
		return active.containsKey(new CaseInsensitiveString(key));
	}

	public CDOMSingleRef<T> getCDOMReference(String val)
	{
		// TODO Auto-generated method stub
		// TODO This is incorrect, but a hack for now :)
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
		if (baseClass.equals(PCClass.class))
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

		CaseInsensitiveString cis = new CaseInsensitiveString(val);
		RT ref = referenced.get(cis);
		if (ref == null)
		{
			ref = referenceMfg.getReference(val);
			referenced.put(cis, ref);
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
		for (CaseInsensitiveString second : active.keySet())
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
		for (CaseInsensitiveString s : referenced.keySet())
		{
			if (!active.containsKey(s) && !deferred.contains(s)
					&& !s.toString().startsWith("*"))
			{
				Logging.errorPrint("Unconstructed Reference: "
						+ baseClass.getSimpleName() + " " + s);
				returnGood = false;
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
		deferred.add(new CaseInsensitiveString(value));
	}

	public void clear()
	{
		duplicates.clear();
		active.clear();
		deferred.clear();
		referenced.clear();
	}

	public ReferenceManufacturer<T, RT> getReferenceManufacturer()
	{
		return referenceMfg;
	}

	public Collection<T> getAllConstructedCDOMObjects()
	{
		Set<T> set = new HashSet<T>();
		set.addAll(active.values());
		return set;
	}

	public void fillReferences()
	{
		for (Entry<CaseInsensitiveString, RT> me : referenced.entrySet())
		{
			T activeObj = active.get(me.getKey());
			if (activeObj != null)
			{
				me.getValue().addResolution(activeObj);
			}
		}
	}
}
