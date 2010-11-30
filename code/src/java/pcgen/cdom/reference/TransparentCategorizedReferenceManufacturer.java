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

import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Loadable;

/**
 * A TransparentCategorizedReferenceManufacturer is a ReferenceManufacturer
 * capable of creating TransparentReferences of a given "form". That "form"
 * includes a specific Class of CDOMObject, or a specific Class/Category for
 * Categorized CDOMObjects (this class does not make distinction between the
 * Class and Class/Categorized cases)
 * 
 * @param <T>
 *            The Class of object this
 *            TransparentCategorizedReferenceManufacturer can reference
 */
public class TransparentCategorizedReferenceManufacturer<T extends Loadable & CategorizedCDOMObject<T>>
		extends TransparentReferenceManufacturer<T> implements
		CategorizedManufacturer<T>
{
	/**
	 * Stores the Category of the CategorizedCDOMObjects that this
	 * TransparentCategorizedReferenceManufacturer constructs and references.
	 */
	private final String category;
	
	private Category<T> finalCategory;

	/**
	 * Constructs a new TransparentCategorizedReferenceManufacturer for the
	 * given Class.
	 * 
	 * @param objClass
	 *            The Class of object this
	 *            TransparentCategorizedReferenceManufacturer will construct and
	 *            reference.
	 * @param cat
	 *            The Category of objects that this
	 *            TransparentCategorizedReferenceManufacturer will construct and
	 *            reference.
	 */
	public TransparentCategorizedReferenceManufacturer(Class<T> objClass,
			String cat)
	{
		super(objClass);
		if (cat == null)
		{
			throw new IllegalArgumentException(
					"Cannot built TransparentCategorizedReferenceManufacturer with null category");
		}
		category = cat;
	}

	/**
	 * Returns a CDOMTransparentSingleRef for the given identifier as defined by
	 * the Class provided when this TransparentCategorizedReferenceManufacturer
	 * was constructed. This is designed to be used ONLY by the
	 * AbstractReferenceManufacturer template Class and should not be called by
	 * other objects.
	 * 
	 * @param key
	 *            The identifier for which a CDOMTransparentSingleRef should be
	 *            returned.
	 * @return a CDOMTransparentSingleRef for the given identifier as defined by
	 *         the Class provided when this
	 *         TransparentCategorizedReferenceManufacturer was constructed.
	 */
	@Override
	protected CDOMTransparentCategorizedSingleRef<T> getLocalReference(
			String key)
	{
		return new CDOMTransparentCategorizedSingleRef<T>(getReferenceClass(),
				category, key);
	}

	/**
	 * Returns a CDOMTransparentTypeRef for the given types as defined by the
	 * Class provided when this TransparentCategorizedReferenceManufacturer was
	 * constructed. This is designed to be used ONLY by the
	 * AbstractReferenceManufacturer template Class and should not be called by
	 * other objects.
	 * 
	 * @param types
	 *            An array of the types of objects to which the returned
	 *            CDOMReference will refer.
	 * @return A CDOMTransparentTypeRef for the given types as defined by the
	 *         Class provided when this
	 *         TransparentCategorizedReferenceManufacturer was constructed.
	 */
	@Override
	protected CDOMTransparentTypeRef<T> getLocalTypeReference(String[] types)
	{
		return new CDOMTransparentTypeRef<T>(getReferenceClass(), types);
	}

	/**
	 * Returns a CDOMTransparentAllRef for all objects of the Class provided
	 * when this TransparentCategorizedReferenceManufacturer was constructed.
	 * This is designed to be used ONLY by the AbstractReferenceManufacturer
	 * template Class and should not be called by other objects.
	 * 
	 * @return A CDOMTransparentAllRef for all objects of the Class provided
	 *         when this TransparentCategorizedReferenceManufacturer was
	 *         constructed.
	 */
	@Override
	protected CDOMTransparentAllRef<T> getLocalAllReference()
	{
		return new CDOMTransparentAllRef<T>(getReferenceClass());
	}

	/**
	 * Resolves the TransparentReferences in this
	 * TransparentCategorizedReferenceManufacturer using the given
	 * ReferenceManufacturer.
	 * 
	 * This method may be called more than once; each time it is called it will
	 * overwrite the existing resolution of the TransparentReferences contained
	 * within this TransparentCategorizedReferenceManufacturer.
	 * 
	 * @param rm
	 *            The ReferenceManufacturer to be used to resolve the
	 *            TransparentReferences produced in this
	 *            TransparentCategorizedReferenceManufacturer
	 * @throws IllegalArgumentException
	 *             if the given ReferenceManufacturer is null
	 */
	@Override
	public void resolveUsing(ReferenceManufacturer<T> rm)
	{
		if (rm == null)
		{
			throw new IllegalArgumentException(
					"ReferenceManufacturer for resolveUsing cannot be null");
		}
		if (rm instanceof CategorizedManufacturer)
		{
			Category<T> rmCategory = ((CategorizedManufacturer<T>)rm).getCategory();
			if (finalCategory == null)
			{
				finalCategory = rmCategory;
			}
			else if (!finalCategory.equals(rmCategory))
			{
				throw new IllegalArgumentException(
						"ReferenceManufacturer for resolveUsing uses inconsistent Catefory with previous resolution");
			}
		}
		else
		{
			throw new IllegalArgumentException(
					"ReferenceManufacturer for resolveUsing must be a CategorizedManufacturer");
		}
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

	/**
	 * Returns a description of the type of Class this
	 * TransparentCategorizedReferenceManufacturer constructs or references.
	 * This is designed to be used ONLY by the AbstractReferenceManufacturer
	 * template Class and should not be called by other objects.
	 * 
	 * @return A String description of the Class that this
	 *         TransparentCategorizedReferenceManufacturer constructs or
	 *         references.
	 */
	@Override
	protected String getReferenceDescription()
	{
		return getReferenceClass().getSimpleName() + " " + category;
	}

	/**
	 * Returns true if the given String (an identifier) is permitted by the
	 * given UnconstructedValidator. Will always return false if the
	 * UnconstructedValidator is null.
	 * 
	 * @param validator
	 *            The UnconstructedValidator to use to determine if the given
	 *            String (an identifier) should be permitted as an unconstructed
	 *            reference.
	 * @param key
	 *            The identifier to be checked to see if the
	 *            UnconstructedValidator will permit it as an unconstructed
	 *            reference.
	 * @return true if the given String (an identifier) is permitted by the
	 *         given UnconstructedValidator; false otherwise.
	 */
	@Override
	protected boolean validate(UnconstructedValidator validator, String key)
	{
		return validator != null && validator.allow(getReferenceClass(), key);
	}

	/**
	 * Returns the Category of the object this
	 * TransparentCategorizedReferenceManufacturer manufactures
	 * 
	 * @return the Category of the object this
	 *         TransparentCategorizedReferenceManufacturer manufactures
	 */
	public String getCDOMCategory()
	{
		return category;
	}

	public Category<T> getCategory()
	{
		return finalCategory;
	}
}
