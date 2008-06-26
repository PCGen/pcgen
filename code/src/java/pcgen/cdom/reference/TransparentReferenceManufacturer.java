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

import pcgen.cdom.base.CDOMObject;

public class TransparentReferenceManufacturer<T extends CDOMObject>
		extends
		AbstractReferenceManufacturer<T, CDOMTransparentSingleRef<T>, CDOMTransparentTypeRef<T>, CDOMTransparentAllRef<T>>
		implements ReferenceManufacturer<T, CDOMTransparentSingleRef<T>>,
		Cloneable
{
	public TransparentReferenceManufacturer(Class<T> cl)
	{
		super(cl);
	}

	@Override
	protected CDOMTransparentSingleRef<T> getLocalReference(String val)
	{
		return new CDOMTransparentSingleRef<T>(getReferenceClass(), val);
	}

	@Override
	protected CDOMTransparentTypeRef<T> getLocalTypeReference(String[] val)
	{
		return new CDOMTransparentTypeRef<T>(getReferenceClass(), val);
	}

	@Override
	protected CDOMTransparentAllRef<T> getLocalAllReference()
	{
		return new CDOMTransparentAllRef<T>(getReferenceClass());
	}

	public void resolveUsing(ReferenceManufacturer<T, ?> rm)
	{
		CDOMTransparentAllRef<T> all = getAllRef();
		if (all != null)
		{
			all.resolve(rm);
		}
		for (CDOMTransparentTypeRef<T> ref : getTypeReferences())
		{
			ref.resolve(rm);
		}
		for (CDOMTransparentSingleRef<T> ref : getReferenced())
		{
			ref.resolve(rm);
		}
		injectConstructed(rm);
	}
	
	@Override
	protected String getReferenceDescription()
	{
		return getReferenceClass().getSimpleName();
	}
}