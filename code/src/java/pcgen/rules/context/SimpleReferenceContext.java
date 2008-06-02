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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.reference.CDOMSimpleSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.SimpleReferenceManufacturer;

public class SimpleReferenceContext
{

	private Map<Class<?>, SimpleReferenceManufacturer<?>> map = 
		new HashMap<Class<?>, SimpleReferenceManufacturer<?>>();
	
	public <T extends CDOMObject> SimpleReferenceManufacturer<T> getManufacturer(Class<T> cl)
	{
		SimpleReferenceManufacturer<T> mfg = (SimpleReferenceManufacturer<T>) map.get(cl);
		if (mfg == null)
		{
			mfg = new SimpleReferenceManufacturer<T>(cl);
			map.put(cl, mfg);
		}
		return mfg;
	}


	private Map<Class<?>, ReferenceSupport<?, ?>> refMap = new HashMap<Class<?>, ReferenceSupport<?, ?>>();

//	private DoubleKeyMap<CDOMObject, Class, CDOMAddressedSingleRef> addressed = new DoubleKeyMap<CDOMObject, Class, CDOMAddressedSingleRef>();

	public SimpleReferenceContext()
	{
		initialize();
	}

	private void initialize()
	{
		// TODO Need to reproduce this effect...
		// GameMode game = SettingsHandler.getGame();
		// List<CDOMStat> statList = game.getUnmodifiableStatList();
		// for (CDOMStat stat : statList)
		// {
		// active.put(CDOMStat.class, new CaseInsensitiveString(stat
		// .get(StringKey.ABB)), stat);
		// }
		// for (int i = 0; i < game.getSizeAdjustmentListSize(); i++)
		// {
		// CDOMSizeAdjustment sa = game.getSizeAdjustmentAtIndex(i);
		// active.put(CDOMSizeAdjustment.class, new CaseInsensitiveString(sa
		// .getAbbreviation()), sa);
		// }
	}

	private <T extends CDOMObject> ReferenceSupport<T, CDOMSimpleSingleRef<T>> getRefSupport(
			Class<T> cl)
	{
		ReferenceSupport ref = refMap.get(cl);
		if (ref == null)
		{
			ref = new ReferenceSupport<T, CDOMSimpleSingleRef<T>>(
					getManufacturer(cl));
			refMap.put(cl, ref);
		}
		return ref;
	}

	public <T extends CDOMObject> void importObject(T obj)
	{
		getRefSupport((Class<T>) obj.getClass()).registerWithKey(obj,
				obj.getKeyName());
	}

	public <T extends CDOMObject> void reassociateKey(T obj, String key)
	{
		getRefSupport((Class<T>) obj.getClass()).reassociateKey(key, obj);
	}

	public <T extends CDOMObject> T silentlyGetConstructedCDOMObject(
			Class<T> c, String val)
	{
		return getRefSupport(c).silentlyGetConstructedCDOMObject(val);
	}

	public <T extends CDOMObject> T getConstructedCDOMObject(Class<T> c,
			String val)
	{
		return getRefSupport(c).getConstructedCDOMObject(val);
	}

	public <T extends CDOMObject> T constructCDOMObject(Class<T> c, String val)
	{
		return getRefSupport(c).constructCDOMObject(val);
	}

	public <T extends CDOMObject> void forgetCDOMObjectKeyed(Class<T> cl,
			String forgetKey)
	{
		getRefSupport(cl).forgetCDOMObjectKeyed(forgetKey);
	}

	public <T extends CDOMObject> Collection<T> getConstructedCDOMObjects(
			Class<T> name)
	{
		return getRefSupport(name).getAllConstructedCDOMObjects();
	}

	public <T extends CDOMObject> boolean containsConstructedCDOMObject(
			Class<T> name, String key)
	{
		return getRefSupport(name).containsConstructedCDOMObject(key);
	}

	public <T extends CDOMObject> CDOMSingleRef<T> getCDOMReference(Class<T> c,
			String val)
	{
		return getRefSupport(c).getCDOMReference(val);
	}

	public boolean validate()
	{
		boolean returnGood = true;
		for (ReferenceSupport<?, ?> ref : refMap.values())
		{
			returnGood &= ref.validate();
		}
		return returnGood;
	}

	public <T extends CDOMObject> void constructIfNecessary(Class<T> cl,
			String value)
	{
		getRefSupport(cl).constructIfNecessary(value);
	}

	public void clear()
	{
		refMap.clear();
	}

//	public <T extends CDOMObject> CDOMAddressedSingleRef<T> getAddressedReference(
//			CDOMObject obj, Class<T> name, String addressName)
//	{
//		CDOMAddressedSingleRef<T> addr = addressed.get(obj, name);
//		if (addr == null)
//		{
//			addr = new CDOMAddressedSingleRef<T>(obj, name, addressName);
//			addressed.put(obj, name, addr);
//		}
//		return addr;
//	}

	public Collection<CDOMObject> getAllConstructedCDOMObjects()
	{
		Set<CDOMObject> set = new HashSet<CDOMObject>();
		for (ReferenceSupport<?, ?> ref : refMap.values())
		{
			set.addAll(ref.getAllConstructedCDOMObjects());
		}
		return set;
	}

	public void resolveReferences()
	{
		for (ReferenceSupport<?, ?> rs : refMap.values())
		{
			rs.fillReferences();
		}
		for (Class cl : refMap.keySet())
		{
			resolve(cl);
		}
	}

	private <T extends CDOMObject> void resolve(Class<T> cl)
	{
		getManufacturer(cl).resolveReferences(getRefSupport(cl));
	}

	public void buildDeferredObjects()
	{
		for (ReferenceSupport<?, ?> rs : refMap.values())
		{
			rs.buildDeferredObjects();
		}
	}
}
