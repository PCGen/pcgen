/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.helper;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;

public class Qualifier
{

	private final Class<? extends CDOMObject> qualClass;
	private final CDOMReference<? extends CDOMObject> qualRef;

	public Qualifier(Class<? extends CDOMObject> cl,
		CDOMReference<? extends CDOMObject> ref)
	{
		if (cl == null)
		{
			throw new IllegalArgumentException("Class cannot be null");
		}
		if (ref == null)
		{
			throw new IllegalArgumentException("Reference cannot be null");
		}
		qualClass = cl;
		qualRef = ref;
	}

	public Class<? extends CDOMObject> getQualifiedClass()
	{
		return qualClass;
	}

	public CDOMReference<? extends CDOMObject> getQualifiedReference()
	{
		return qualRef;
	}

	@Override
	public int hashCode()
	{
		return qualClass.hashCode() * 29 + qualRef.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Qualifier)
		{
			Qualifier other = (Qualifier) o;
			return qualClass.equals(other.qualClass) && qualRef.equals(other.qualRef);
		}
		return false;
	}
	
}
