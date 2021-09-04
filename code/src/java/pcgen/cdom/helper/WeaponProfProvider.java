/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.QualifyingObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.analysis.ChangeProfFacet;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.WeaponProf;

/**
 * A WeaponProfProvider is an object that contains the ability to contain
 * WeaponProficiencies, either by TYPE or direct WeaponProf references. Explicit
 * Storage of TYPE vs. primitive is necessary due to the ability of the
 * CHANGEPROF token to change types for a WeaponProf.
 * 
 * This is typically used for an AUTO:WEAPONPROF token to store the granted
 * proficiencies.
 */
public class WeaponProfProvider extends ConcretePrereqObject implements QualifyingObject
{

	private static final ChangeProfFacet CHANGE_PROF_FACET = FacetLibrary.getFacet(ChangeProfFacet.class);

	/**
	 * Contains the list of primitive WeaponProf objects that this
	 * WeaponProfProvider contains
	 */
	private List<CDOMSingleRef<WeaponProf>> direct;

	/**
	 * Contains the list of TYPEs of WeaponProf objects that this
	 * WeaponProfProvider contains
	 */
	private List<CDOMGroupRef<WeaponProf>> type;

	/**
	 * Contains the All WeaponProf reference if this WeaponProfProvider contains
	 * it
	 */
	private CDOMGroupRef<WeaponProf> all;

	/**
	 * Adds a primitive WeaponProf reference to this WeaponProfProvider.
	 * 
	 * @param ref
	 *            The primitive WeaponProf reference that should be added to
	 *            this WeaponProfProvider
	 */
	public void addWeaponProf(CDOMSingleRef<WeaponProf> ref)
	{
		if (direct == null)
		{
			direct = new ArrayList<>();
		}
		direct.add(ref);
	}

	/**
	 * Adds a WeaponProf TYPE reference to this WeaponProfProvider.
	 * 
	 * @param ref
	 *            The WeaponProf TYPE reference that should be added to this
	 *            WeaponProfProvider
	 */
	public void addWeaponProfType(CDOMGroupRef<WeaponProf> ref)
	{
		if (type == null)
		{
			type = new ArrayList<>();
		}
		type.add(ref);
	}

	/**
	 * Adds a All WeaponProf reference to this WeaponProfProvider.
	 * 
	 * @param ref
	 *            The WeaponProf ALL reference that should be added to this
	 *            WeaponProfProvider
	 */
	public void addWeaponProfAll(CDOMGroupRef<WeaponProf> ref)
	{
		all = ref;
	}

	/**
	 * Returns a collection of the WeaponProf objects that this
	 * WeaponProfProvider contains relative to a given PlayerCharacter. The
	 * PlayerCharacter must be known in order to resolve changes that may be
	 * introduced by any proficiency changes (CHANGEPROF).
	 * 
	 * Ownership of the Collection is transferred to the calling Object, no
	 * association is kept between the Collection and this WeaponProfProvider.
	 * (Thus, removal of a WeaponProf from the returned Collection will not
	 * remove that WeaponProf from this WeaponProfProvider)
	 * 
	 * @param id
	 *            The PlayerCharacter used to resolve the references in order to
	 *            account for any proficiency changes
	 * @return A Collection of the WeaponProf objects that this
	 *         WeaponProfProvider contains relative to the given PlayerCharacter
	 */
	public Collection<WeaponProf> getContainedProficiencies(CharID id)
	{
		List<WeaponProf> list = new ArrayList<>();
		if (all == null)
		{
			if (direct != null)
			{
				for (CDOMSingleRef<WeaponProf> ref : direct)
				{
					list.add(ref.get());
				}
			}
			if (type != null)
			{
				for (CDOMGroupRef<WeaponProf> ref : type)
				{
					list.addAll(getWeaponProfsInTarget(id, ref));
				}
			}
		}
		else
		{
			list.addAll(all.getContainedObjects());
		}
		return list;
	}

	/**
	 * Returns the LST format for this WeaponProfProvider. Provided primarily to
	 * allow the Token/Loader system to properly unparse the WeaponProfProvider.
	 * 
	 * @return The LST format of this WeaponProfProvider
	 */
	public String getLstFormat()
	{
		if (all != null)
		{
			return Constants.LST_ALL;
		}
		StringBuilder sb = new StringBuilder();
		boolean typeEmpty = type == null || type.isEmpty();
		if (direct != null && !direct.isEmpty())
		{
			sb.append(ReferenceUtilities.joinLstFormat(direct, Constants.PIPE));
			if (!typeEmpty)
			{
				sb.append(Constants.PIPE);
			}
		}
		if (!typeEmpty)
		{
			sb.append(ReferenceUtilities.joinLstFormat(type, Constants.PIPE));
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof WeaponProfProvider other)
		{
			if (direct == null)
			{
				if (other.direct != null)
				{
					return false;
				}
			}
			else
			{
				if (!direct.equals(other.direct))
				{
					return false;
				}
			}
			if (type == null)
			{
				if (other.type != null)
				{
					return false;
				}
			}
			else
			{
				if (!type.equals(other.type))
				{
					return false;
				}
			}
			if (all == null)
			{
				if (other.all != null)
				{
					return false;
				}
			}
			else
			{
				if (!all.equals(other.all))
				{
					return false;
				}
			}
			return this.equalsPrereqObject(other);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return (direct == null ? 0 : direct.hashCode() * 29) + (type == null ? 0 : type.hashCode());
	}

	/**
	 * Returns true if this WeaponProfProvider is empty, meaning it contains no
	 * direct references and no TYPE references.
	 * 
	 * @return true if this WeaponProfProvider is empty; false otherwise
	 */
	public boolean isEmpty()
	{
		return all == null && (direct == null || direct.isEmpty()) && (type == null || type.isEmpty());
	}

	public boolean isValid()
	{
		boolean hasDirect = (direct != null) && !direct.isEmpty();
		boolean hasType = (type != null) && !type.isEmpty();
		boolean hasIndividual = hasDirect || hasType;
		boolean hasAll = all != null;
		return hasAll ^ hasIndividual;
	}

	public List<WeaponProf> getWeaponProfsInTarget(CharID id, CDOMGroupRef<WeaponProf> master)
	{
		return CHANGE_PROF_FACET.getWeaponProfsInTarget(id, master);
	}
}
