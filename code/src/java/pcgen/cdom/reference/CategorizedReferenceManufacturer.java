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
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;

/**
 * A CategorizedReferenceManufacturer is a ReferenceManufacturer that will
 * construct or reference Categorized CDOMObjects.
 * 
 * @see pcgen.cdom.reference.ReferenceManufacturer
 * @see pcgen.cdom.base.Category
 * 
 * @param <T>
 *            The Class of object this CategorizedReferenceManufacturer can
 *            manufacture
 */
public class CategorizedReferenceManufacturer<T extends CDOMObject & CategorizedCDOMObject<T>>
		extends
		AbstractReferenceManufacturer<T, CDOMCategorizedSingleRef<T>, CDOMTypeRef<T>, CDOMAllRef<T>>
		implements ReferenceManufacturer<T, CDOMCategorizedSingleRef<T>>
{

	/**
	 * Stores the Category of the CategorizedCDOMObjects that this
	 * CategorizedReferenceManufacturer constructs and references.
	 */
	private final Category<T> category;

	/**
	 * Constructs a new SimpleReferenceManufacturer that will construct or
	 * reference non-categorized CDOMObjects of the given Class.
	 * 
	 * @param cl
	 *            The Class of object this AbstractReferenceManufacturer will
	 *            construct and reference.
	 */
	public CategorizedReferenceManufacturer(Class<T> cl, Category<T> cat)
	{
		super(cl);
		if (cat == null)
		{
			throw new IllegalArgumentException("Category for "
					+ getClass().getName() + " cannot be null");
		}
		category = cat;
	}

	/**
	 * Returns a CDOMCategorizedSingleRef for the given identifier as defined by
	 * the Class and Category provided when this
	 * CategorizedReferenceManufacturer was constructed. This is designed to be
	 * used ONLY by the AbstractReferenceManufacturer template Class and should
	 * not be called by other objects.
	 * 
	 * @return a CDOMCategorizedSingleRef for the given identifier as defined by
	 *         the Class and Category provided when this
	 *         CategorizedReferenceManufacturer was constructed.
	 */
	@Override
	protected CDOMCategorizedSingleRef<T> getLocalReference(String val)
	{
		return new CDOMCategorizedSingleRef<T>(getReferenceClass(), category,
				val);
	}

	/**
	 * Returns a CDOMTypeRef for the given types as defined by the Class and
	 * Category provided when this CategorizedReferenceManufacturer was
	 * constructed. This is designed to be used ONLY by the
	 * AbstractReferenceManufacturer template Class and should not be called by
	 * other objects.
	 * 
	 * @return A CDOMTypeRef for the given types as defined by the Class and
	 *         Category provided when this CategorizedReferenceManufacturer was
	 *         constructed.
	 */
	@Override
	protected CDOMTypeRef<T> getLocalTypeReference(String[] val)
	{
		return new CDOMTypeRef<T>(getReferenceClass(), val);
	}

	/**
	 * Returns a CDOMAllRef for all objects of the Class and Category provided
	 * when this CategorizedReferenceManufacturer was constructed. This is
	 * designed to be used ONLY by the AbstractReferenceManufacturer template
	 * Class and should not be called by other objects.
	 * 
	 * @return A CDOMAllRef for all objects of the Class and Category provided
	 *         when this CategorizedReferenceManufacturer was constructed.
	 */
	@Override
	protected CDOMAllRef<T> getLocalAllReference()
	{
		return new CDOMAllRef<T>(getReferenceClass());
	}

	/**
	 * Returns a description of the type of Class and Category this
	 * CategorizedReferenceManufacturer constructs or references. This is
	 * designed to be used ONLY by the AbstractReferenceManufacturer template
	 * Class and should not be called by other objects.
	 * 
	 * @return A String description of the Class and Category that this
	 *         CategorizedReferenceManufacturer constructs or references.
	 */
	@Override
	protected String getReferenceDescription()
	{
		return getReferenceClass().getSimpleName() + " " + category;
	}

	/**
	 * Constructs a new CDOMObject of the Class and Category this
	 * CategorizedReferenceManufacturer constructs.
	 * 
	 * @see pcgen.cdom.reference.AbstractReferenceManufacturer#constructObject(java.lang.String)
	 */
	@Override
	public T constructObject(String val)
	{
		T obj = super.constructObject(val);
		obj.setCDOMCategory(category);
		return obj;
	}

}