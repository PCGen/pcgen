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
package pcgen.rules.context;

import java.util.Collection;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.CategorizedManufacturer;
import pcgen.cdom.reference.UnconstructedListener;
import pcgen.cdom.reference.UnconstructedValidator;

public class CategorizedTrackingManufacturer<T extends CDOMObject & CategorizedCDOMObject<T>> implements
		CategorizedManufacturer<T>
{

	private final CategorizedManufacturer<T> rm;
	private final TrackingReferenceContext context;

	public CategorizedTrackingManufacturer(TrackingReferenceContext trc,
			CategorizedManufacturer<T> mfg)
	{
		context = trc;
		rm = mfg;
	}

	public void addObject(T o, String key)
	{
		rm.addObject(o, key);
	}

	public void addUnconstructedListener(UnconstructedListener listener)
	{
		rm.addUnconstructedListener(listener);
	}

	public void buildDeferredObjects()
	{
		rm.buildDeferredObjects();
	}

	public void constructIfNecessary(String value)
	{
		rm.constructIfNecessary(value);
	}

	public T constructNowIfNecessary(String name)
	{
		return rm.constructNowIfNecessary(name);
	}

	public T constructObject(String key)
	{
		return rm.constructObject(key);
	}

	public boolean containsObject(String key)
	{
		return rm.containsObject(key);
	}

	public boolean forgetObject(T o)
	{
		return rm.forgetObject(o);
	}

	public T getActiveObject(String key)
	{
		return rm.getActiveObject(key);
	}

	public Collection<T> getAllObjects()
	{
		return rm.getAllObjects();
	}

	public CDOMGroupRef<T> getAllReference()
	{
		CDOMGroupRef<T> ref = rm.getAllReference();
		context.track(ref);
		return ref;
	}

	public int getConstructedObjectCount()
	{
		return rm.getConstructedObjectCount();
	}

	public T getItemInOrder(int item)
	{
		return rm.getItemInOrder(item);
	}

	public T getObject(String key)
	{
		return rm.getObject(key);
	}

	public List<T> getOrderSortedObjects()
	{
		return rm.getOrderSortedObjects();
	}

	public CDOMSingleRef<T> getReference(String key)
	{
		CDOMSingleRef<T> ref = rm.getReference(key);
		context.track(ref);
		return ref;
	}

	public Class<T> getReferenceClass()
	{
		return rm.getReferenceClass();
	}

	public CDOMGroupRef<T> getTypeReference(String... types)
	{
		CDOMGroupRef<T> ref = rm.getTypeReference(types);
		context.track(ref);
		return ref;
	}

	public UnconstructedListener[] getUnconstructedListeners()
	{
		return rm.getUnconstructedListeners();
	}

	public void removeUnconstructedListener(UnconstructedListener listener)
	{
		rm.removeUnconstructedListener(listener);
	}

	public void renameObject(String key, T o)
	{
		rm.renameObject(key, o);
	}

	public void resolveReferences()
	{
		rm.resolveReferences();
	}

	public boolean validate(UnconstructedValidator validator)
	{
		return rm.validate(validator);
	}

	public Category<T> getCategory()
	{
		return null;
	}

}
