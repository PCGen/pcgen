/*
 * Copyright (c) 2016 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.inst;

import java.net.URI;

import pcgen.cdom.base.Categorized;
import pcgen.cdom.base.Category;
import pcgen.cdom.reference.CDOMAllRef;
import pcgen.cdom.reference.CDOMCategorizedSingleRef;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.CDOMTypeRef;
import pcgen.cdom.reference.ManufacturableFactory;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.UnconstructedValidator;
import pcgen.util.Logging;

public abstract class AbstractCategory<T extends Categorized<T>> implements
		Category<T>
{

	private String categoryName;
	private URI sourceURI;

	@Override
	public String toString()
	{
		return categoryName;
	}

	@Override
	@SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
	public Category<T> getParentCategory()
	{
		return null;
	}

	@Override
	public String getKeyName()
	{
		return categoryName;
	}

	@Override
	public String getDisplayName()
	{
		return categoryName;
	}

	@Override
	public URI getSourceURI()
	{
		return sourceURI;
	}

	@Override
	public void setSourceURI(URI source)
	{
		sourceURI = source;
	}

	@Override
	public String getLSTformat()
	{
		return categoryName;
	}

	@Override
	public boolean isInternal()
	{
		return false;
	}

	@Override
	public boolean isType(String type)
	{
		return false;
	}

	@Override
	public void setName(String name)
	{
		categoryName = name;
	}

	@Override
	public CDOMGroupRef<T> getAllReference()
	{
		return new CDOMAllRef<>(getReferenceClass());
	}

	@Override
	public CDOMGroupRef<T> getTypeReference(String... types)
	{
		return new CDOMTypeRef<>(getReferenceClass(), types);
	}

	@Override
	public CDOMSingleRef<T> getReference(String ident)
	{
		return new CDOMCategorizedSingleRef<>(getReferenceClass(), this, ident);
	}

	@Override
	public boolean isMember(T item)
	{
		return (item != null) && this.equals(item.getCDOMCategory());
	}

	@Override
	public boolean resolve(ReferenceManufacturer<T> rm, String name,
		CDOMSingleRef<T> value, UnconstructedValidator validator)
	{
		boolean returnGood = true;
		T activeObj = rm.getObject(name);
		if (activeObj == null)
		{
			// Wasn't constructed!
			if (name.charAt(0) != '*' && !report(validator, name))
			{
				Logging.errorPrint("Unconstructed Reference: "
					+ getReferenceDescription() + " " + name);
				rm.fireUnconstuctedEvent(value);
				returnGood = false;
			}
			activeObj = rm.buildObject(name);
		}
		value.addResolution(activeObj);
		return returnGood;
	}

	protected boolean report(UnconstructedValidator validator, String key)
	{
		return (validator != null) && validator.allow(getReferenceClass(), this, key);
	}

	@Override
	public boolean populate(ReferenceManufacturer<T> parentCrm,
		ReferenceManufacturer<T> rm, UnconstructedValidator validator)
	{
		// Nothing to do (for now!)
		return true;
	}

	@Override
	@SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
	public ManufacturableFactory<T> getParent()
	{
		return null;
	}
}
