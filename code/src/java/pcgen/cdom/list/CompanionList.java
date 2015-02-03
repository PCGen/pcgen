/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.list;

import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.reference.CDOMAllRef;
import pcgen.cdom.reference.CDOMCategorizedSingleRef;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.CDOMTypeRef;
import pcgen.cdom.reference.ManufacturableFactory;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.UnconstructedValidator;
import pcgen.core.Race;
import pcgen.core.character.CompanionMod;
import pcgen.util.Logging;

/**
 * CompanionList is a CDOMListObject designed to reference a List of Race
 * objects available as companions to a PlayerCharacter objects.
 */
public class CompanionList extends CDOMListObject<Race> implements
		Category<CompanionMod>
{

	/**
	 * Returns the Race Class object (Race.class)
	 * 
	 * @return the Race Class object (Race.class)
	 */
	@Override
	public Class<Race> getListClass()
	{
		return Race.class;
	}

	/**
	 * Lists never have a Type, so this returns false
	 */
	@Override
	public boolean isType(String type)
	{
		return false;
	}

	@Override
	public CompanionMod newInstance()
	{
		CompanionMod mod = new CompanionMod();
		mod.setCDOMCategory(this);
		return mod;
	}

	@Override
	public boolean isMember(CompanionMod item)
	{
		return (item != null) && equals(item.getCDOMCategory());
	}

	@Override
	public String getReferenceDescription()
	{
		return "CompanionMod of TYPE " + getKeyName();
	}

	@Override
	public boolean resolve(ReferenceManufacturer<CompanionMod> rm, String name,
		CDOMSingleRef<CompanionMod> reference, UnconstructedValidator validator)
	{
		boolean returnGood = true;
		CompanionMod activeObj = rm.getObject(name);
		if (activeObj == null)
		{
			// Wasn't constructed!
			if (name.charAt(0) != '*' && !report(validator, name))
			{
				Logging.errorPrint("Unconstructed Reference: "
					+ getReferenceDescription() + " " + name);
				rm.fireUnconstuctedEvent(reference);
				returnGood = false;
			}
			activeObj = rm.buildObject(name);
		}
		reference.addResolution(activeObj);
		return returnGood;
	}

	private boolean report(UnconstructedValidator validator, String key)
	{
		return validator != null
			&& validator.allow(getReferenceClass(), this, key);
	}

	@Override
	public boolean populate(ReferenceManufacturer<CompanionMod> parentCrm,
		ReferenceManufacturer<CompanionMod> rm, UnconstructedValidator validator)
	{
		//Never hierarchical
		return true;
	}

	@Override
	public ManufacturableFactory<CompanionMod> getParent()
	{
		//Never hierarchical
		return null;
	}

	@Override
	public CDOMSingleRef<CompanionMod> getReference(String key)
	{
		return new CDOMCategorizedSingleRef<CompanionMod>(CompanionMod.class,
			this, key);
	}

	@Override
	public CDOMGroupRef<CompanionMod> getTypeReference(String... types)
	{
		return new CDOMTypeRef<CompanionMod>(CompanionMod.class, types);
	}

	@Override
	public CDOMGroupRef<CompanionMod> getAllReference()
	{
		return new CDOMAllRef<CompanionMod>(CompanionMod.class);
	}

	@Override
	public Class<CompanionMod> getReferenceClass()
	{
		return CompanionMod.class;
	}

	@Override
	public Category<CompanionMod> getParentCategory()
	{
		//Never hierarchical
		return null;
	}

}
