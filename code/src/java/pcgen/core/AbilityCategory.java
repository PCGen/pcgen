/*
 * AbilityCategory.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Current Ver: $Revision$
 * Last Editor: $Author: $
 * Last Edited: $Date$
 */
package pcgen.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import pcgen.util.PropertyFactory;

/**
 * This class stores and manages information about Ability categories.
 * 
 * <p>This is a higher level abstraction than the category specified by the 
 * ability object itself.  The low-level ability category defaults to the same
 * as this category key but this can be changed.  For example to specify an
 * <tt>AbilityCategory</tt> &quot;Fighter Bonus Feats&quot; you could specify
 * the ability category was &quot;FEAT&quot; and set the ability type to
 * &quot;Fighter&quot;. 
 * 
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 */
public class AbilityCategory implements KeyedObject
{
	private String theDisplayName;
	private String theKeyName;
	private String thePluralName;
	
	private String theAbilityCategory;
	private Set<String> theAbilityTypes = null;
	private String thePoolFormula = "0"; //$NON-NLS-1$
	
	private boolean theVisibleFlag = true;
	private boolean theEditableFlag = true;
	private boolean theModPoolFlag = true;
	private boolean theAllowFractionalPoolFlag = false;

	/** A constant used to refer to the &quot;Feat&quot; category. */
	public static final AbilityCategory FEAT = new AbilityCategory("FEAT", "in_feat"); //$NON-NLS-1$ //$NON-NLS-2$
	
	static
	{
		FEAT.thePluralName = PropertyFactory.getString("in_feats"); //$NON-NLS-1$
	}
	/**
	 * Constructs a new <tt>AbilityCategory</tt> with the specified key.
	 * 
	 * <p>This method sets the display and plural names to the same value as
	 * the key name.
	 * 
	 * @param aKeyName The name to use to reference this category.
	 */
	public AbilityCategory( final String aKeyName )
	{
		theKeyName = aKeyName;
		theDisplayName = aKeyName;
		thePluralName = aKeyName;
		
		theAbilityCategory = aKeyName;
	}
	
	/**
	 * Constructor takes a key name and display name for the category.
	 * 
	 * @param aKeyName The name to use to reference this category.
	 * @param aDisplayName The resource key to use for the display name
	 */
	public AbilityCategory( final String aKeyName, final String aDisplayName )
	{
		theKeyName = aKeyName;
		setName(aDisplayName);
		setPluralName(aDisplayName);

		theAbilityCategory = aKeyName;
	}

	/**
	 * Sets the low-level ability category this category refers to.
	 * 
	 * @param aCategory An ability category key string.
	 */
	public void setAbilityCategory(final String aCategory)
	{
		theAbilityCategory = aCategory;
	}
	
	/**
	 * Gets the low-level ability category this category refers to.
	 * 
	 * @return An ability category key string.
	 */
	public String getAbilityCategory()
	{
		return theAbilityCategory;
	}
	
	/**
	 * Sets the list of ability types to include in this category.
	 * 
	 * @param aTypeList A collection of type strings.
	 */
	public void setAbilityTypes(final Collection<String> aTypeList)
	{
		if ( theAbilityTypes == null )
		{
			theAbilityTypes = new TreeSet<String>();
		}
		theAbilityTypes.addAll(aTypeList);
	}
	
	/**
	 * Adds a new type to the list of types included in this category.
	 * 
	 * @param aType A type string.
	 */
	public void addAbilityType(final String aType)
	{
		if ( theAbilityTypes == null )
		{
			theAbilityTypes = new TreeSet<String>();
		}
		theAbilityTypes.add(aType);
	}
	
	/**
	 * Gets the <tt>Set</tt> of all the ability types to be included in this
	 * category.
	 * 
	 * @return An unmodifiable <tt>Set</tt> of type strings.
	 */
	public Set<String> getAbilityTypes()
	{
		if ( theAbilityTypes == null )
		{
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(theAbilityTypes);
	}
	
	/**
	 * Gets the formula to use for calculating the base pool size for this
	 * category of ability.
	 * 
	 * @return A formula
	 */
	public String getPoolFormula()
	{
		return thePoolFormula;
	}
	
	/**
	 * Sets the formula to use to calculate the base pool size for this category
	 * of ability.
	 * 
	 * @param aFormula A valid formula or variable.
	 */
	public void setPoolFormula( final String aFormula )
	{
		thePoolFormula = aFormula;
	}
	
	/**
	 * Sets the internationalized plural name for this category.
	 * 
	 * @param aName A plural name.
	 */
	public void setPluralName(final String aName)
	{
		if (aName.startsWith("in_"))
		{
			thePluralName = PropertyFactory.getString(aName);
		}
		else
		{
			thePluralName = aName;
		}
	}
	
	/**
	 * Returns an internationalized plural version of the category name.
	 * 
	 * @return The pluralized name
	 */
	public String getPluralName()
	{
		return thePluralName;
	}
	
	/**
	 * Sets if abilities of this category should be displayed in the UI.
	 * 
	 * @param yesNo <tt>true</tt> if these abilities should be displayed.
	 */
	public void setVisible(final boolean yesNo)
	{
		theVisibleFlag = yesNo;
	}
	
	/**
	 * Checks if this category of ability should be displayed in the UI.
	 * 
	 * @return <tt>true</tt> if these abilities should be displayed.
	 */
	public boolean isVisible()
	{
		return theVisibleFlag;
	}
	
	/**
	 * Sets if abilities in this category should be user-editable
	 * 
	 * @param yesNo <tt>true</tt> if the user should be able to add and remove
	 * abilities of this category.
	 */
	public void setEditable(final boolean yesNo)
	{
		theEditableFlag = yesNo;
	}
	
	/**
	 * Checks if this category of abilities is user-editable.
	 * 
	 * @return <tt>true</tt> if these abilities are editable.
	 */
	public boolean isEditable()
	{
		return theEditableFlag;
	}

	/**
	 * Sets the flag to allow/disallow user editing of the pool.
	 * 
	 * @param yesNo Set to <tt>true</tt> to allow user editing.
	 */
	public void setModPool(final boolean yesNo)
	{
		theModPoolFlag = yesNo;
	}
	
	/**
	 * Checks if this category allows user editing of the pool.
	 * 
	 * @return <tt>true</tt> to allow user editing.
	 */
	public boolean allowPoolMod()
	{
		return theModPoolFlag;
	}
	
	/**
	 * Sets if the pool can use fractional amounts.
	 * 
	 * @param yesNo <tt>true</tt> to allow fractions.
	 */
	public void setAllowFractionalPool(final boolean yesNo)
	{
		theAllowFractionalPoolFlag = yesNo;
	}
	
	/**
	 * Checks if the pool should use whole numbers only.
	 * 
	 * @return <tt>true</tt> if fractional pool amounts are valid.
	 */
	public boolean allowFractionalPool()
	{
		return theAllowFractionalPoolFlag;
	}
	
	// -------------------------------------------
	// KeyedObject Support
	// -------------------------------------------
	/**
	 * @see pcgen.core.KeyedObject#getDisplayName()
	 */
	public String getDisplayName()
	{
		return theDisplayName;
	}

	/**
	 * @see pcgen.core.KeyedObject#getKeyName()
	 */
	public String getKeyName()
	{
		return theKeyName;
	}

	/**
	 * @see pcgen.core.KeyedObject#setKeyName(java.lang.String)
	 */
	public void setKeyName(final String aKey)
	{
		theKeyName = aKey;
	}

	/**
	 * @see pcgen.core.KeyedObject#setName(java.lang.String)
	 */
	public void setName(final String aName)
	{
		if (aName.startsWith("in_"))
		{
			theDisplayName = PropertyFactory.getString(aName);
		}
		else
		{
			theDisplayName = aName;
		}
	}

	/**
	 * Returns the display name for this category.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return theDisplayName;
	}

	/**
	 * Generates a hash code using the key, category and types.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((theKeyName == null) ? 0 : theKeyName.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AbilityCategory other = (AbilityCategory) obj;
		if (theKeyName == null)
		{
			if (other.theKeyName != null)
				return false;
		}
		else if (!theKeyName.equals(other.theKeyName))
			return false;
		return true;
	}
}
