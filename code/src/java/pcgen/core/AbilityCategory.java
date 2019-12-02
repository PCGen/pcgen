/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
 */
package pcgen.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.BasicClassIdentity;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.ClassIdentity;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.enumeration.DisplayLocation;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.reference.CDOMAllRef;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.CDOMSimpleSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.CDOMTypeRef;
import pcgen.cdom.reference.ManufacturableFactory;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.UnconstructedValidator;
import pcgen.core.utils.LastGroupSeparator.GroupingMismatchException;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;
import pcgen.util.enumeration.View;
import pcgen.util.enumeration.Visibility;

/**
 * This class stores and manages information about Ability categories.
 * 
 * <p>This is a higher level abstraction than the category specified by the 
 * ability object itself.  The low-level AbilityCategory defaults to the same
 * as this category key but this can be changed.  For example to specify an
 * <tt>AbilityCategory</tt> &quot;Fighter Bonus Feats&quot; you could specify
 * the AbilityCategory was &quot;FEAT&quot; and set the ability type to
 * &quot;Fighter&quot;. 
 * 
 * 
 */
public class AbilityCategory
		implements Category<Ability>, Loadable, ManufacturableFactory<Ability>
{
	private static final ClassIdentity<AbilityCategory> IDENTITY =
			BasicClassIdentity.getIdentity(AbilityCategory.class);

	private URI sourceURI;

	private String keyName;
	private String displayName;
	private String pluralName;

	private CDOMSingleRef<AbilityCategory> parentCategory;
	private Set<CDOMSingleRef<Ability>> containedAbilities = null;
	private DisplayLocation displayLocation;
	private boolean isAllAbilityTypes = false;
	private Set<Type> types = null;
	private Formula poolFormula = FormulaFactory.ZERO;

	private Visibility visibility = Visibility.DEFAULT;
	private boolean isEditable = true;
	private boolean isPoolModifiable = true;
	private boolean isPoolFractional = false;
	private boolean isInternal = false;

	/** A constant used to refer to the &quot;Feat&quot; category. */
	public static final AbilityCategory FEAT = new AbilityCategory("FEAT", "in_feat"); //$NON-NLS-1$ //$NON-NLS-2$
	public static final AbilityCategory LANGBONUS = new AbilityCategory("*LANGBONUS"); //$NON-NLS-1$
	public static final AbilityCategory ANY = new AbilityCategory("ANY"); //$NON-NLS-1$

	static
	{
		FEAT.pluralName = LanguageBundle.getString("in_feats"); //$NON-NLS-1$
		FEAT.displayLocation = DisplayLocation.getConstant(LanguageBundle.getString("in_feats")); //$NON-NLS-1$
		FEAT.setInternal(true);
		LANGBONUS.setPoolFormula(FormulaFactory.getFormulaFor("BONUSLANG"));
		LANGBONUS.setInternal(true);
	}

	/**
	 * Constructs a new <tt>AbilityCategory</tt> with the specified key.
	 * 
	 * <p>This method sets the display and plural names to the same value as
	 * the key name.
	 */
	public AbilityCategory()
	{
		//For fooling other things
		keyName = "";
		//Self until proven otherwise
		parentCategory = CDOMDirectSingleRef.getRef(this);
	}

	/**
	 * Update this ability category using the values from the supplied 
	 * ability category. 
	 * @param srcCat The category to be copied.
	 */
	public void copyFields(AbilityCategory srcCat)
	{
		sourceURI = srcCat.sourceURI;
		keyName = srcCat.keyName;
		displayName = srcCat.displayName;
		pluralName = srcCat.pluralName;
		if (srcCat.getParentCategory() == srcCat)
		{
			parentCategory = CDOMDirectSingleRef.getRef(this);
		}
		else
		{
			parentCategory = srcCat.parentCategory;
		}
		displayLocation = srcCat.displayLocation;
		isAllAbilityTypes = srcCat.isAllAbilityTypes;
		types = (srcCat.types == null) ? null : new HashSet<>(srcCat.types);
		poolFormula = srcCat.poolFormula;
		visibility = srcCat.visibility;
		isEditable = srcCat.isEditable;
		isPoolModifiable = srcCat.isPoolModifiable;
		isPoolFractional = srcCat.isPoolFractional;
		isInternal = srcCat.isInternal;
	}

	/**
	 * Constructor takes a key name and display name for the category.
	 * 
	 * @param aKeyName The name to use to reference this category.
	 * @param aDisplayName The resource key to use for the display name
	 */
	public AbilityCategory(final String aKeyName, final String aDisplayName)
	{
		keyName = aKeyName;
		setName(aDisplayName);
		setPluralName(aDisplayName);

		parentCategory = CDOMDirectSingleRef.getRef(this);
		displayLocation = DisplayLocation.getConstant(aDisplayName);
	}

	/**
	 * Constructor takes a name for the category.
	 * 
	 * @param aKeyName The name to use to reference this category.
	 */
	public AbilityCategory(String aKeyName)
	{
		this(aKeyName, aKeyName);
	}

	/**
	 * Sets the parent AbilityCategory this category is part of.
	 * 
	 * @param category A Reference to an AbilityCategory.
	 */
	public void setAbilityCategory(CDOMSingleRef<AbilityCategory> category)
	{
		/*
		 * Note: This makes an assumption that keyName will not change. We
		 * should not enable a KEY token for AbilityCategory
		 */
		if (isInternal)
		{
			if (!category.getLSTformat(false).equals(this.getKeyName()))
			{
				throw new IllegalArgumentException("Cannot set CATEGORY on an internal AbilityCategory");
			}
		}
		else
		{
			parentCategory = category;
		}
	}

	/**
	 * Gets the parent AbilityCategory this category is part of.
	 * 
	 * @return A reference to the AbilityCategory.
	 */
	public CDOMSingleRef<AbilityCategory> getAbilityCatRef()
	{
		return parentCategory;
	}

	/**
	 * Adds a new type to the list of types included in this category.
	 * 
	 * @param type A type string.
	 */
	public void addAbilityType(final Type type)
	{
		if (types == null)
		{
			types = new TreeSet<>();
		}
		types.add(type);
	}

	/**
	 * Gets the <tt>Set</tt> of all the ability types to be included in this
	 * category.
	 * 
	 * @return An unmodifiable <tt>Set</tt> of type strings.
	 */
	public Set<Type> getTypes()
	{
		if (types == null)
		{
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(types);
	}

	/**
	 * Should all ability types be included in this category?
	 * @return true if all types should be included, 
	 *         false if only those listed should be.
	 */
	public boolean isAllAbilityTypes()
	{
		return isAllAbilityTypes;
	}

	/**
	 * Configure whether all ability types be included in this category?
	 * @param allAbilityTypes true if all types should be included, 
	 *         false if only those listed should be.
	 */
	public void setAllAbilityTypes(boolean allAbilityTypes)
	{
		this.isAllAbilityTypes = allAbilityTypes;
	}

	/**
	 * @param key the Ability Key to add to the set
	 */
	public void addAbilityKey(CDOMSingleRef<Ability> key)
	{
		if (containedAbilities == null)
		{
			containedAbilities = new HashSet<>();
		}
		containedAbilities.add(key);
	}

	/**
	 * Gets the formula to use for calculating the base pool size for this
	 * category of ability.
	 * 
	 * @return A formula
	 */
	public Formula getPoolFormula()
	{
		return poolFormula;
	}

	/**
	 * Sets the formula to use to calculate the base pool size for this category
	 * of ability.
	 * 
	 * @param formula A valid formula or variable.
	 */
	public void setPoolFormula(Formula formula)
	{
		poolFormula = formula;
	}

	/**
	 * Sets the internationalized plural name for this category.
	 * 
	 * @param aName A plural name.
	 */
	public void setPluralName(final String aName)
	{
		pluralName = aName;
	}

	/**
	 * Returns an internationalized plural version of the category name.
	 * 
	 * @return The pluralized name
	 */
	public String getPluralName()
	{
		String name = pluralName;
		if (name == null)
		{
			name = displayName;
		}
		if (name.startsWith("in_"))
		{
			return LanguageBundle.getString(name);
		}
		else
		{
			return name;
		}
	}

	public String getRawPluralName()
	{
		return pluralName;
	}

	/**
	 * Returns the location on which the AbilityCategory should be displayed.
	 * 
	 * @return The display location.
	 */
	public DisplayLocation getDisplayLocation()
	{
		if (displayLocation == null)
		{
			displayLocation = DisplayLocation.getConstant(getPluralName());
		}
		return displayLocation;
	}

	/**
	 * Sets the location where the AbilityCategory should be displayed.
	 * 
	 * @param location
	 *            The new displayLocation
	 */
	public void setDisplayLocation(DisplayLocation location)
	{
		displayLocation = location;
	}

	/**
	 * Sets if abilities of this category should be displayed in the UI.
	 * 
	 * @param visible the visibility for abilities, i.e. hidden, visible, etc. 
	 */
	public void setVisible(Visibility visible)
	{
		visibility = visible;
	}

	/**
	 * Checks if this category of ability should be displayed in the UI.
	 * 
	 * @return <tt>true</tt> if these abilities should be displayed.
	 */
	public boolean isVisibleTo(View v)
	{
		return isVisibleTo(null, v);
	}

	/**
	 * Checks if this category of ability should be displayed in the 
	 * UI for this PC.
	 * 
	 * @param pc The character to be tested.
	 * @return <tt>true</tt> if these abilities should be displayed.
	 */
	public boolean isVisibleTo(PlayerCharacter pc, View v)
	{
		if (visibility.equals(Visibility.QUALIFY))
		{
			/*
			 * Note that hasAbilityVisibleTo is apparently not how data is
			 * designed - the problem (in my opinion) being that either an
			 * undocumented design change was made or a bug was being taken
			 * advantage of and the data is now dependent upon that bug. This
			 * reintroduces a wider behavior, but - again in my opinion -
			 * decreases clarity over the definition of QUALIFIED and whether
			 * the actual behavior aligns with the dictionary definition of the
			 * word. - thpr Apr 5 '14
			 */
			return (pc == null) || (pc.getTotalAbilityPool(this).floatValue() != 0.0) || pc.hasAbilityInPool(this);
			//|| pc.hasAbilityVisibleTo(this, v);
		}
		return visibility.isVisibleTo(v);
	}

	/**
	 * Sets if abilities in this category should be user-editable
	 * 
	 * @param yesNo <tt>true</tt> if the user should be able to add and remove
	 * abilities of this category.
	 */
	public void setEditable(final boolean yesNo)
	{
		isEditable = yesNo;
	}

	/**
	 * Checks if this category of abilities is user-editable.
	 * 
	 * @return <tt>true</tt> if these abilities are editable.
	 */
	public boolean isEditable()
	{
		return isEditable;
	}

	/**
	 * Sets the flag to allow/disallow user editing of the pool.
	 * 
	 * @param yesNo Set to <tt>true</tt> to allow user editing.
	 */
	public void setModPool(final boolean yesNo)
	{
		isPoolModifiable = yesNo;
	}

	/**
	 * Checks if this category allows user editing of the pool.
	 * 
	 * @return <tt>true</tt> to allow user editing.
	 */
	public boolean allowPoolMod()
	{
		return isPoolModifiable;
	}

	/**
	 * Sets if the pool can use fractional amounts.
	 * 
	 * @param yesNo <tt>true</tt> to allow fractions.
	 */
	public void setAllowFractionalPool(final boolean yesNo)
	{
		isPoolFractional = yesNo;
	}

	/**
	 * Checks if the pool should use whole numbers only.
	 * 
	 * @return <tt>true</tt> if fractional pool amounts are valid.
	 */
	public boolean allowFractionalPool()
	{
		return isPoolFractional;
	}

	// -------------------------------------------
	// KeyedObject Support
	// -------------------------------------------
	@Override
	public String getDisplayName()
	{
		if (displayName.startsWith("in_"))
		{
			return LanguageBundle.getString(displayName);
		}
		else
		{
			return displayName;
		}
	}

	public String getRawDisplayName()
	{
		return displayName;
	}

	@Override
	public String getKeyName()
	{
		return keyName;
	}

	@Override
	public void setName(final String aName)
	{
		if ("".equals(keyName))
		{
			keyName = aName;
		}
		displayName = aName;
	}

	@Override
	public String toString()
	{
		return getDisplayName();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((keyName == null) ? 0 : keyName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final AbilityCategory other = (AbilityCategory) obj;
		if (keyName == null)
		{
            return other.keyName == null;
		}
		else return keyName.equals(other.keyName);
    }

	@Override
	public Category<Ability> getParentCategory()
	{
		return parentCategory.get();
	}

	/**
	 * Return the collection of references for abilities that will be directly
	 * included in the category.
	 * 
	 * @return the collection of references
	 */
	public Collection<CDOMSingleRef<Ability>> getAbilityRefs()
	{
		if (containedAbilities == null)
		{
			return Collections.emptySet();
		}
		return Collections.unmodifiableCollection(containedAbilities);
	}

	public boolean hasDirectReferences()
	{
		return (containedAbilities != null) && !containedAbilities.isEmpty();
	}

	public Visibility getVisibility()
	{
		return visibility;
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

	public void setInternal(boolean internal)
	{
		isInternal = internal;
	}

	@Override
	public boolean isInternal()
	{
		return isInternal;
	}

	@Override
	public boolean isType(String type)
	{
		return false;
	}

	@Override
	public CDOMGroupRef<Ability> getAllReference()
	{
		return new CDOMAllRef<>(this);
	}

	@Override
	public CDOMGroupRef<Ability> getTypeReference(String... types)
	{
		return new CDOMTypeRef<>(this, types);
	}

	@Override
	public CDOMSingleRef<Ability> getReference(String ident)
	{
		return new CDOMSimpleSingleRef<>(this, ident);
	}

	@Override
	public Ability newInstance()
	{
		Ability a = new Ability();
		a.setCDOMCategory(this);
		return a;
	}

	@Override
	public boolean isMember(Ability item)
	{
		if (item == null)
		{
			return false;
		}
		Category<Ability> itemCategory = item.getCDOMCategory();
		return this.equals(itemCategory) || getParentCategory().equals(itemCategory);
	}

	@Override
	public Class<Ability> getReferenceClass()
	{
		return Ability.class;
	}

	@Override
	public String getReferenceDescription()
	{
		return "Ability Category " + this.getKeyName();
	}

	@Override
	public boolean resolve(ReferenceManufacturer<Ability> rm, String name, CDOMSingleRef<Ability> reference,
		UnconstructedValidator validator)
	{
		if ((containedAbilities != null) && (containedAbilities.contains(reference)))
		{
			return true;
		}
		return doResolve(rm, name, reference, validator);
	}

	private boolean doResolve(ReferenceManufacturer<Ability> rm, String name, CDOMSingleRef<Ability> reference,
		UnconstructedValidator validator)
	{
		boolean returnGood = true;
		Ability activeObj = rm.getObject(name);
		if (activeObj == null)
		{
			List<String> choices = new ArrayList<>();
			try
			{
				String reduced = AbilityUtilities.getUndecoratedName(name, choices);
				activeObj = rm.getObject(reduced);
			}
			catch (GroupingMismatchException e)
			{
				Logging.log(Logging.LST_ERROR, e.getMessage());
			}
			if (activeObj == null)
			{
				// Really not constructed...
				// Wasn't constructed!
				if ((name.charAt(0) != '*') && !report(validator, name))
				{
					Logging.errorPrint("Unconstructed Reference: " + getReferenceDescription() + " " + name);
					rm.fireUnconstuctedEvent(reference);
					returnGood = false;
				}
				activeObj = rm.buildObject(name);
			}
			else
			{
				// Successful on reduced
				reference.addResolution(activeObj);
				if (choices.size() == 1)
				{
					reference.setChoice(choices.get(0));
				}
				else if (choices.size() > 1)
				{
					Logging.errorPrint("Invalid use of multiple items " + "in parenthesis (comma prohibited) in "
						+ activeObj + " " + choices.toString());
					returnGood = false;
				}
			}
		}
		else
		{
			reference.addResolution(activeObj);
			if (reference.requiresTarget() && activeObj.getSafe(ObjectKey.MULTIPLE_ALLOWED))
			{
				ChooseInformation<?> ci = activeObj.get(ObjectKey.CHOOSE_INFO);
				// Is MULT:YES.... and not CHOOSE:NOCHOICE
				// Null check (unfortunately) required to protect vs. bad data
				// No error message though, that is caught by MULT token
				if ((ci != null) && !"No Choice".equals(ci.getName()))
				{
					Logging.errorPrint(
						"Invalid use of MULT:YES Ability " + activeObj + " where a target [parens] is required");
					Logging.errorPrint("PLEASE TAKE NOTE: " + "If usage locations are reported, "
						+ "not all usages are necessary illegal " + "(at least one is)");
					rm.fireUnconstuctedEvent(reference);
					returnGood = false;
				}
			}
		}
		return returnGood;
	}

	private boolean report(UnconstructedValidator validator, String key)
	{
		return (validator != null) && validator.allowUnconstructed(getReferenceIdentity(), key);
	}

	@Override
	public boolean populate(ReferenceManufacturer<Ability> parentCrm, ReferenceManufacturer<Ability> rm,
		UnconstructedValidator validator)
	{
		if (parentCrm == null)
		{
			return true;
		}
		Collection<Ability> allObjects = parentCrm.getAllObjects();
		// Don't add things twice or we'll get dupe messages :)
		Set<Ability> added = Collections.newSetFromMap(new IdentityHashMap<>());
		/*
		 * Pull in all the base objects... note this skips containsDirectly
		 * because items haven't been resolved
		 */
		for (final Ability ability : allObjects)
		{
			boolean use = isAllAbilityTypes;
			if (!use && (types != null))
			{
				for (Type type : types)
				{
					if (ability.isType(type.toString()))
					{
						use = true;
						break;
					}
				}
			}
			if (use)
			{
				added.add(ability);
				rm.addObject(ability, ability.getKeyName());
			}
		}
		boolean returnGood = true;
		if (containedAbilities != null)
		{
			for (CDOMSingleRef<Ability> ref : containedAbilities)
			{
				boolean res = doResolve(parentCrm, ref.getLSTformat(false), ref, validator);
				if (res)
				{
					Ability ability = ref.get();
					if (added.add(ability))
					{
						rm.addObject(ability, ability.getKeyName());
					}
				}
				returnGood &= res;
			}
		}
		return returnGood;
	}

	@Override
	public ManufacturableFactory<Ability> getParent()
	{
		AbilityCategory parent = parentCategory.get();
		if (this.equals(parent))
		{
			return null;
		}
		return parent;
	}

	@Override
	public String getName()
	{
		return getDisplayName();
	}

	public String getType()
	{
		return String.valueOf(getDisplayLocation());
	}

	@Override
	public ClassIdentity<Ability> getReferenceIdentity()
	{
		return this;
	}

	@Override
	public ClassIdentity<? extends Loadable> getClassIdentity()
	{
		return IDENTITY;
	}

	@Override
	public String getPersistentFormat()
	{
		return "ABILITY=" + getKeyName();
	}
}
