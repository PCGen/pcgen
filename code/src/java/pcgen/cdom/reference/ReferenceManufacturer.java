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

import java.util.Collection;

import pcgen.cdom.base.PrereqObject;

public interface ReferenceManufacturer<T extends PrereqObject, RT extends CDOMSingleRef<T>>
{
	public RT getReference(String key);

	public CDOMGroupRef<T> getTypeReference(String... types);

	public CDOMGroupRef<T> getAllReference();

	public Class<T> getCDOMClass();

	public boolean validate();

	public void fillReferences();

	public void resolveReferences();

	public void buildDeferredObjects();

	public T constructCDOMObject(String val);

	public void constructIfNecessary(String value);

	public T silentlyGetConstructedCDOMObject(String val);

	public boolean forgetObject(T o);
	
	public boolean containsConstructedCDOMObject(String key);
	
	public Collection<T> getAllConstructedCDOMObjects();
	
	public void registerWithKey(T o, String key);
	
	public void reassociateKey(String key, T o);
}
