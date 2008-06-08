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
package pcgen.cdom.base;

import java.util.Collection;

public abstract class CDOMReference<T extends PrereqObject> implements
		LSTWriteable //, PrimitiveChoiceFilter<T>
{

	private final String name;

	private final Class<T> clazz;

	public CDOMReference(Class<T> cl, String nm)
	{
		clazz = cl;
		name = nm;
	}

	public String getName()
	{
		return name;
	}

	public Class<T> getReferenceClass()
	{
		return clazz;
	}

	public abstract void addResolution(T obj);

	public abstract void clearResolution();

	public abstract boolean contains(T obj);
	
//	public boolean allow(CharacterDataStore pc, T obj)
//	{
//		return contains(obj);
//	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " " + clazz.getSimpleName() + " "
			+ name;
	}

	public abstract String getPrimitiveFormat();

	public abstract String getLSTformat();

	public abstract int getObjectCount();
	
	public abstract Collection<T> getContainedObjects();
}
