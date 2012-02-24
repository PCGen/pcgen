/*
 * Copyright (c) Thomas Parker, 2009.
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
package pcgen.cdom.facet;

import java.util.IdentityHashMap;
import java.util.Set;

import pcgen.base.util.WrappedMapSet;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Equipment;
import pcgen.core.WeaponProf;

/**
 * WeaponProfFacet is a Facet that tracks the WeaponProfs that have been granted
 * to a Player Character.
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class WeaponProfFacet extends AbstractSourcedListFacet<WeaponProf>
		implements DataFacetChangeListener<WeaponProf>
{

	private AutoWeaponProfFacet autoWeaponProfFacet;
	private HasDeityWeaponProfFacet hasDeityWeaponProfFacet;
	private DeityWeaponProfFacet deityWeaponProfFacet;

	/**
	 * Consolidates WeaponProf objects into this facet when a WeaponProf is
	 * added to a Player Character.
	 * 
	 * Triggered when one of the Facets to which WeaponProfFacet listens fires a
	 * DataFacetChangeEvent to indicate a WeaponProf was added to a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataAdded(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<WeaponProf> dfce)
	{
		add(dfce.getCharID(), dfce.getCDOMObject(), dfce.getSource());
	}

	/**
	 * Removes WeaponProf objects into this facet when a WeaponProf is removed
	 * from a Player Character.
	 * 
	 * Triggered when one of the Facets to which WeaponProfFacet listens fires a
	 * DataFacetChangeEvent to indicate a WeaponProf was removed from a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataRemoved(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	@Override
	public void dataRemoved(DataFacetChangeEvent<WeaponProf> dfce)
	{
		remove(dfce.getCharID(), dfce.getCDOMObject(), dfce.getSource());
	}

	/*
	 * TODO The following methods violate the "facet does one thing" rule - they
	 * are consolidating this facet and other facets into one answer...
	 */

	/**
	 * Returns a non-null copy of the Set of WeaponProfs in this WeaponProfFacet
	 * for the Player Character represented by the given CharID. This method
	 * returns an empty set if no WeaponProfs are in this WeaponProfFacet for
	 * the Player Character identified by the given CharID.
	 * 
	 * This method is value-semantic in that ownership of the returned Set is
	 * transferred to the class calling this method. Modification of the
	 * returned Set will not modify this WeaponProfFacet and modification of
	 * this WeaponProfFacet will not modify the returned Set. Modifications to
	 * the returned Set will also not modify any future or previous objects
	 * returned by this (or other) methods on WeaponProfFacet. If you wish to
	 * modify the information stored in this WeaponProfFacet, you must use the
	 * add*() and remove*() methods of WeaponProfFacet.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            WeaponProfs in this WeaponProfFacet should be returned.
	 * @return A non-null copy of the Set of WeaponProfs in this WeaponProfFacet
	 *         for the Player Character represented by the given CharID
	 */
	public Set<WeaponProf> getProfs(CharID id)
	{
		final Set<WeaponProf> ret = new WrappedMapSet<WeaponProf>(IdentityHashMap.class);
		ret.addAll(getSet(id));
		ret.addAll(autoWeaponProfFacet.getWeaponProfs(id));
		if (hasDeityWeaponProfFacet.hasDeityWeaponProf(id))
		{
			ret.addAll(deityWeaponProfFacet.getSet(id));
		}
		return ret;
	}

	/**
	 * Returns true if this WeaponProfFacet contains the given WeaponProf in the
	 * list of WeaponProfs for the Player Character represented by the given
	 * CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character used for testing
	 * @param wp
	 *            The WeaponProf to test if this WeaponProfFacet contains that
	 *            WeaponProf for the Player Character represented by the given
	 *            CharID
	 * @return true if this WeaponProfFacet contains the given WeaponProf for
	 *         the Player Character represented by the given CharID; false
	 *         otherwise
	 */
	public boolean containsProf(CharID id, WeaponProf wp)
	{
		return contains(id, wp)
			|| autoWeaponProfFacet.getWeaponProfs(id).contains(wp)
			|| hasDeityWeaponProfFacet.hasDeityWeaponProf(id)
			&& deityWeaponProfFacet.getSet(id).contains(wp);
	}

	/**
	 * Returns true if a Player Character is proficient with a given Weapon;
	 * false otherwise.
	 * 
	 * While this method will accept any Equipment, it is only guaranteed to
	 * have "good behavior" for a Weapon (must have a WeaponProf required for
	 * the Equipment). All other equipment will - at least - return false. No
	 * guarantee is made that this method will not throw an exception if the
	 * given Equipment is not a Weapon.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character for which the
	 *            proficiency will be tested.
	 * @param eq
	 *            The Weapon (as an Equipment object) for which the proficiency
	 *            is being tested.
	 * @return true if a Player Character is proficient with the given Weapon;
	 *         false otherwise.
	 */
	public boolean isProficientWithWeapon(CharID id, Equipment eq)
	{
		if (eq.isNatural())
		{
			return true;
		}

		CDOMSingleRef<WeaponProf> ref = eq.get(ObjectKey.WEAPON_PROF);
		if (ref == null)
		{
			return false;
		}

		return containsProf(id, ref.resolvesTo());
	}

	public void setAutoWeaponProfFacet(AutoWeaponProfFacet autoWeaponProfFacet)
	{
		this.autoWeaponProfFacet = autoWeaponProfFacet;
	}

	public void setHasDeityWeaponProfFacet(
		HasDeityWeaponProfFacet hasDeityWeaponProfFacet)
	{
		this.hasDeityWeaponProfFacet = hasDeityWeaponProfFacet;
	}

	public void setDeityWeaponProfFacet(
		DeityWeaponProfFacet deityWeaponProfFacet)
	{
		this.deityWeaponProfFacet = deityWeaponProfFacet;
	}
}
