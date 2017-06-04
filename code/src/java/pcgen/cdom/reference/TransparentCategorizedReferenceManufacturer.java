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

import pcgen.cdom.base.Categorized;
import pcgen.cdom.base.Category;

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
public class TransparentCategorizedReferenceManufacturer<T extends Categorized<T>>
		extends AbstractReferenceManufacturer<T>
{
	/**
	 * Stores the Category of the CategorizedCDOMObjects that this
	 * TransparentCategorizedReferenceManufacturer constructs and references.
	 */
	private final String category;
	
	private final Class<? extends Category<T>> categoryClass;

	/**
	 * Constructs a new TransparentCategorizedReferenceManufacturer for the
	 * given Class.
	 * 
	 * @param factory
	 *            The Class of object this
	 *            TransparentCategorizedReferenceManufacturer will construct and
	 *            reference.
	 * @param cat
	 *            The Category of objects that this
	 *            TransparentCategorizedReferenceManufacturer will construct and
	 *            reference.
	 */
	public TransparentCategorizedReferenceManufacturer(ManufacturableFactory<T> factory, 
			Class<? extends Category<T>> catClass, String cat)
	{
		super(factory);
		if (cat == null)
		{
			throw new IllegalArgumentException(
				"Cannot build TransparentCategorizedReferenceManufacturer "
					+ "with null category");
		}
		if (catClass == null)
		{
			throw new IllegalArgumentException(
				"Cannot build TransparentCategorizedReferenceManufacturer "
					+ "with null category Class");
		}
		category = cat;
		categoryClass = catClass;
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

	public Class<? extends Category<T>> getCategoryClass()
	{
		return categoryClass;
	}
}
