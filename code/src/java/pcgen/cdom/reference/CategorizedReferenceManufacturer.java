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

import java.util.ArrayList;
import java.util.Collection;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.core.AbilityUtilities;
import pcgen.util.Logging;

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
		implements ReferenceManufacturer<T>
{

	/**
	 * Stores the Category of the CategorizedCDOMObjects that this
	 * CategorizedReferenceManufacturer constructs and references.
	 */
	private final Category<T> category;

	/**
	 * Stores the reference manager that is dealing with this category's parent
	 * category.
	 */
	private CategorizedReferenceManufacturer<T> parentCrm = null;

	/**
	 * Constructs a new SimpleReferenceManufacturer that will construct or
	 * reference non-categorized CDOMObjects of the given Class.
	 * 
	 * @param cl
	 *            The Class of object this AbstractReferenceManufacturer will
	 *            construct and reference.
	 * @param cat
	 *            The Category of objects that this
	 *            AbstractReferenceManufacturer will construct and reference.
	 */
	public CategorizedReferenceManufacturer(Class<T> cl, Category<T> cat)
	{
		super(cl);
		/*
		 * Note: null must be a legal value here, as Categorized objects (e.g.
		 * Ability) are constructed with the "null" Category and then reassigned
		 * once the CATEGORY: token is struck.
		 */
		category = cat;
	}

	/**
	 * Sets the reference manager that is dealing with this category's parent
	 * category.
	 * 
	 * @param crm
	 *            the reference manager for the parent category
	 */
	public void setParentCRM(CategorizedReferenceManufacturer<T> crm)
	{
		this.parentCrm = crm;
	}

	/**
	 * This is a specialisation of the validate function to cope with categories
	 * that have parents (i.e Fighter feats being a child of feats). It checks
	 * for active matches in the parent before doing the normal validation. Any
	 * matches in the parent for unconstructed references in this class are
	 * registered as if they had been made in the child class.
	 * 
	 * @param validator
	 *            UnconstructedValidator which can suppress unconstructed
	 *            reference warnings
	 * 
	 * @return true if the CategorizedReferenceManufacturer is "valid"; false
	 *         otherwise.
	 * @see pcgen.cdom.reference.AbstractReferenceManufacturer#validate(List<Campaign>)
	 */
	@Override
	public boolean validate(UnconstructedValidator validator)
	{
		if (parentCrm != null)
		{
			Collection<CDOMCategorizedSingleRef<T>> childRefs = getReferenced();
			for (CDOMCategorizedSingleRef<T> ref : childRefs)
			{
				String name = ref.getName();
				if (parentCrm.containsObject(name) && !containsObject(name))
				{
					Logging.debugPrint("Found match in parent for " + category
							+ " - " + name);
					addObject(parentCrm.getObject(name), name);
				}
				else
				{
					Collection<String> specifics = new ArrayList<String>();
					String undecName = AbilityUtilities.getUndecoratedName(
							name, specifics);
					if (parentCrm.containsObject(undecName)
							&& !containsObject(undecName))
					{
						Logging.debugPrint("Found match in parent for "
								+ category + " - " + undecName + " - "
								+ specifics);
						addObject(parentCrm.getObject(undecName), undecName);
					}
				}
			}
		}
		return super.validate(validator);
	}

	/**
	 * Returns a CDOMCategorizedSingleRef for the given identifier as defined by
	 * the Class and Category provided when this
	 * CategorizedReferenceManufacturer was constructed. This is designed to be
	 * used ONLY by the AbstractReferenceManufacturer template Class and should
	 * not be called by other objects.
	 * 
	 * @param ident
	 *            The identifier for which a CDOMTransparentSingleRef should be
	 *            returned.
	 * @return a CDOMCategorizedSingleRef for the given identifier as defined by
	 *         the Class and Category provided when this
	 *         CategorizedReferenceManufacturer was constructed.
	 */
	@Override
	protected CDOMCategorizedSingleRef<T> getLocalReference(String ident)
	{
		return new CDOMCategorizedSingleRef<T>(getReferenceClass(), category,
				ident);
	}

	/**
	 * Returns a CDOMTypeRef for the given types as defined by the Class and
	 * Category provided when this CategorizedReferenceManufacturer was
	 * constructed. This is designed to be used ONLY by the
	 * AbstractReferenceManufacturer template Class and should not be called by
	 * other objects.
	 * 
	 * @param types
	 *            An array of the types of objects to which the returned
	 *            CDOMReference will refer.
	 * @return A CDOMTypeRef for the given types as defined by the Class and
	 *         Category provided when this CategorizedReferenceManufacturer was
	 *         constructed.
	 */
	@Override
	protected CDOMTypeRef<T> getLocalTypeReference(String[] types)
	{
		return new CDOMTypeRef<T>(getReferenceClass(), types);
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
	 * Builds a new CDOMObject of the Class and Category this
	 * CategorizedReferenceManufacturer constructs.
	 * 
	 * @param val
	 *            The identifier of the CDOMObject to be constructed
	 * @return The new CDOMObject of the Class or Class/Category represented by
	 *         this AbstractReferenceManufacturer
	 * @see pcgen.cdom.reference.AbstractReferenceManufacturer#constructObject(java.lang.String)
	 */
	@Override
	protected T buildObject(String val)
	{
		T obj = super.buildObject(val);
		obj.setCDOMCategory(category);
		return obj;
	}

	/**
	 * Returns a String representation of this CategorizedReferenceManufacturer
	 * 
	 * @return A String representation of this CategorizedReferenceManufacturer
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.getClass().getName() + " [" + getReferenceClass() + " "
				+ category + "]";
	}

	/**
	 * Returns true if the given String (a reference name) is permitted by the
	 * given UnconstructedValidator. Will always return false if the
	 * UnconstructedValidator is null.
	 * 
	 * @param validator
	 *            The UnconstructedValidator to use to determine if the given
	 *            String (a reference name) should be permitted as an
	 *            unconstructed reference.
	 * @param s
	 *            The reference name to be checked to see if the
	 *            UnconstructedValidator will permit it as an unconstructed
	 *            reference.
	 * @return true if the given String (a reference name) is permitted by the
	 *         given UnconstructedValidator; false otherwise.
	 */
	@Override
	protected boolean validate(UnconstructedValidator validator, String s)
	{
		return validator != null
				&& validator.allow(getReferenceClass(), category, s);
	}

}