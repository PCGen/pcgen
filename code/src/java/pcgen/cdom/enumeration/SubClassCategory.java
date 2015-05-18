/*
 * Copyright (c) 2006 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.enumeration;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;

import pcgen.base.enumeration.TypeSafeConstant;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.cdom.base.Category;
import pcgen.cdom.reference.CDOMAllRef;
import pcgen.cdom.reference.CDOMCategorizedSingleRef;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.CDOMTypeRef;
import pcgen.cdom.reference.ManufacturableFactory;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.UnconstructedValidator;
import pcgen.core.SubClass;
import pcgen.util.Logging;

/**
 * @author Tom Parker (thpr [at] yahoo.com)
 * 
 * This Class is a Type Safe Constant.
 */
public final class SubClassCategory implements TypeSafeConstant,
		Category<SubClass>, ManufacturableFactory<SubClass>
{

	/**
	 * This Map contains the mappings from Strings to the Type Safe Constant
	 */
	private static CaseInsensitiveMap<SubClassCategory> typeMap;

	/**
	 * This is used to provide a unique ordinal to each constant in this class
	 */
	private static int ordinalCount = 0;

	/**
	 * The name of this Constant
	 */
	private final String fieldName;

	/**
	 * The ordinal of this Constant
	 */
	private final transient int ordinal;

	private boolean defined = false;
	private URI sourceURI;

	private SubClassCategory(String name)
	{
		ordinal = ordinalCount++;
		fieldName = name;
	}

	/**
	 * Converts this Constant to a String (returns the name of this Constant)
	 * 
	 * @return The string representation (name) of this Constant
	 */
	@Override
	public String toString()
	{
		return fieldName;
	}

	/**
	 * Gets the ordinal of this Constant
	 */
	@Override
	public int getOrdinal()
	{
		return ordinal;
	}

	public void define()
	{
		defined = true;
	}

	public boolean isDefined()
	{
		return defined;
	}

	/**
	 * Returns the constant for the given String (the search for the constant is
	 * case insensitive). If the constant does not already exist, a new Constant
	 * is created with the given String as the name of the Constant.
	 * 
	 * @param name
	 *            The name of the constant to be returned
	 * @return The Constant for the given name
	 */
	public static SubClassCategory getConstant(String name)
	{
		initializeTypeMap();
		String lookup = name.replace('_', ' ');
		SubClassCategory category = typeMap.get(lookup);
		if (category == null)
		{
			/*
			 * TODO FIXME Should .,| or other stuff be banned here? (probably)
			 */
			if (name.length() == 0)
			{
				throw new IllegalArgumentException(
						"Type Name cannot be zero length");
			}
			category = new SubClassCategory(lookup);
			typeMap.put(lookup, category);
		}
		return category;
	}

	private static synchronized void initializeTypeMap()
	{
		if (typeMap == null)
		{
			typeMap = new CaseInsensitiveMap<SubClassCategory>();
		}
	}

	/**
	 * Returns the constant for the given String (the search for the constant is
	 * case insensitive). If the constant does not already exist, an
	 * IllegalArgumentException is thrown.
	 * 
	 * @param name
	 *            The name of the constant to be returned
	 * @return The Constant for the given name
	 */
	public static SubClassCategory valueOf(String name)
	{
		initializeTypeMap();
		SubClassCategory category = typeMap.get(name);
		if (category == null)
		{
			throw new IllegalArgumentException(name);
		}
		return category;
	}

	/**
	 * Returns a Collection of all of the Constants in this Class.
	 * 
	 * This collection maintains a reference to the Constants in this Class, so
	 * if a new Constant is created, the Collection returned by this method will
	 * be modified. (Beware of ConcurrentModificationExceptions)
	 * 
	 * @return a Collection of all of the Constants in this Class.
	 */
	public static Collection<SubClassCategory> getAllConstants()
	{
		if (typeMap == null)
		{
			return null;
		}
		return Collections.unmodifiableCollection(typeMap.values());
	}

	/**
	 * Clears all of the Constants in this Class (forgetting the mapping from
	 * the String to the Constant).
	 */
	/*
	 * CONSIDER Need to consider the ramifications of this on TypeSafeMap, since
	 * this does not (and really cannot) reset the ordinal count... Does this
	 * method need to be renamed, such that it is clearConstantMap? - Tom
	 * Parker, Feb 28, 2007
	 */
	public static void clearConstants()
	{
		if (typeMap != null)
		{
			typeMap.clear();
		}
	}

	@Override
	public Category<SubClass> getParentCategory()
	{
		return null;
	}

	@Override
	public String getKeyName()
	{
		return fieldName;
	}

	@Override
	public String getDisplayName()
	{
		return fieldName;
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
		return fieldName;
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
		throw new UnsupportedOperationException(
				"Cannot set name in SubClassCategory");
	}

	@Override
	public CDOMGroupRef<SubClass> getAllReference()
	{
		return new CDOMAllRef<SubClass>(SubClass.class);
	}

	@Override
	public CDOMGroupRef<SubClass> getTypeReference(String... types)
	{
		return new CDOMTypeRef<SubClass>(SubClass.class, types);
	}

	@Override
	public CDOMSingleRef<SubClass> getReference(String ident)
	{
		return new CDOMCategorizedSingleRef<SubClass>(SubClass.class, this,
				ident);
	}

	@Override
	public SubClass newInstance()
	{
		SubClass sc = new SubClass();
		sc.setCDOMCategory(this);
		return sc;
	}

	@Override
	public boolean isMember(SubClass item)
	{
		return (item != null) && this.equals(item.getCDOMCategory());
	}

	@Override
	public Class<SubClass> getReferenceClass()
	{
		return SubClass.class;
	}

	@Override
	public String getReferenceDescription()
	{
		return "SubClass Category " + getKeyName();
	}

	@Override
	public boolean resolve(ReferenceManufacturer<SubClass> rm, String name,
			CDOMSingleRef<SubClass> value, UnconstructedValidator validator)
	{
		boolean returnGood = true;
		SubClass activeObj = rm.getObject(name);
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

	private boolean report(UnconstructedValidator validator, String key)
	{
		return validator != null && validator.allow(getReferenceClass(), key);
	}

	@Override
	public boolean populate(ReferenceManufacturer<SubClass> parentCrm,
			ReferenceManufacturer<SubClass> rm, UnconstructedValidator validator)
	{
		// Nothing to do (for now!)
		return true;
	}

	@Override
	public ManufacturableFactory<SubClass> getParent()
	{
		return null;
	}
}
