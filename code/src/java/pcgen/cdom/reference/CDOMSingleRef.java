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
package pcgen.cdom.reference;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.PrereqObject;

public abstract class CDOMSingleRef<T extends PrereqObject> extends
		CDOMReference<T>
{

	public CDOMSingleRef(Class<T> cl, String nm)
	{
		super(cl, nm);
	}

	@Override
	public int getObjectCount()
	{
		return 1;
	}

	public abstract T resolvesTo();
}
