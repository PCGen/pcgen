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
package pcgen.cdom.facet.model;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import pcgen.cdom.base.SetFacet;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.AutoWeaponProfFacet;
import pcgen.cdom.facet.DeityWeaponProfFacet;
import pcgen.cdom.facet.HasDeityWeaponProfFacet;
import pcgen.cdom.facet.WeaponProfFacet;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Equipment;
import pcgen.core.WeaponProf;
import pcgen.output.publish.OutputDB;

/**
 * WeaponProfModelFacet is a Facet that tracks the WeaponProfs that have been
 * granted to a Player Character.
 */
public class WeaponProfModelFacet implements SetFacet<CharID, WeaponProf>
{

    private WeaponProfFacet weaponProfFacet;
    private AutoWeaponProfFacet autoWeaponProfFacet;
    private HasDeityWeaponProfFacet hasDeityWeaponProfFacet;
    private DeityWeaponProfFacet deityWeaponProfFacet;

    /**
     * Returns a non-null copy of the Set of WeaponProfs in this
     * WeaponProfModelFacet for the Player Character represented by the given
     * CharID. This method returns an empty set if no WeaponProfs are in this
     * WeaponProfModelFacet for the Player Character identified by the given
     * CharID.
     * <p>
     * This method is value-semantic in that ownership of the returned Set is
     * transferred to the class calling this method. Modification of the
     * returned Set will not modify this WeaponProfModelFacet and modification
     * of this WeaponProfModelFacet will not modify the returned Set.
     * Modifications to the returned Set will also not modify any future or
     * previous objects returned by this (or other) methods on
     * WeaponProfModelFacet. If you wish to modify the information stored in
     * this WeaponProfModelFacet, you must use the add*() and remove*() methods
     * of WeaponProfModelFacet.
     *
     * @param id The CharID representing the Player Character for which the
     *           WeaponProfs in this WeaponProfModelFacet should be returned.
     * @return A non-null copy of the Set of WeaponProfs in this
     * WeaponProfModelFacet for the Player Character represented by the
     * given CharID
     */
    @Override
    public Set<WeaponProf> getSet(CharID id)
    {
        Set<WeaponProf> ret = Collections.newSetFromMap(new IdentityHashMap<>());
        ret.addAll(weaponProfFacet.getSet(id));
        ret.addAll(autoWeaponProfFacet.getWeaponProfs(id));
        if (hasDeityWeaponProfFacet.hasDeityWeaponProf(id))
        {
            ret.addAll(deityWeaponProfFacet.getSet(id));
        }
        return ret;
    }

    /**
     * Returns true if this WeaponProfModelFacet contains the given WeaponProf
     * in the list of WeaponProfs for the Player Character represented by the
     * given CharID.
     *
     * @param id The CharID representing the Player Character used for testing
     * @param wp The WeaponProf to test if this WeaponProfModelFacet contains
     *           that WeaponProf for the Player Character represented by the
     *           given CharID
     * @return true if this WeaponProfModelFacet contains the given WeaponProf
     * for the Player Character represented by the given CharID; false
     * otherwise
     */
    public boolean containsProf(CharID id, WeaponProf wp)
    {
        return weaponProfFacet.contains(id, wp) || autoWeaponProfFacet.containsProf(id, wp)
                || hasDeityWeaponProfFacet.hasDeityWeaponProf(id) && deityWeaponProfFacet.getSet(id).contains(wp);
    }

    /**
     * Returns true if a Player Character is proficient with a given Weapon;
     * false otherwise.
     * <p>
     * While this method will accept any Equipment, it is only guaranteed to
     * have "good behavior" for a Weapon (must have a WeaponProf required for
     * the Equipment). All other equipment will - at least - return false. No
     * guarantee is made that this method will not throw an exception if the
     * given Equipment is not a Weapon.
     *
     * @param id The CharID identifying the Player Character for which the
     *           proficiency will be tested.
     * @param eq The Weapon (as an Equipment object) for which the proficiency
     *           is being tested.
     * @return true if a Player Character is proficient with the given Weapon;
     * false otherwise.
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

        return containsProf(id, ref.get());
    }

    public void setWeaponProfFacet(WeaponProfFacet weaponProfFacet)
    {
        this.weaponProfFacet = weaponProfFacet;
    }

    public void setAutoWeaponProfFacet(AutoWeaponProfFacet autoWeaponProfFacet)
    {
        this.autoWeaponProfFacet = autoWeaponProfFacet;
    }

    public void setHasDeityWeaponProfFacet(HasDeityWeaponProfFacet hasDeityWeaponProfFacet)
    {
        this.hasDeityWeaponProfFacet = hasDeityWeaponProfFacet;
    }

    public void setDeityWeaponProfFacet(DeityWeaponProfFacet deityWeaponProfFacet)
    {
        this.deityWeaponProfFacet = deityWeaponProfFacet;
    }

    public void init()
    {
        OutputDB.register("weaponprofs", this);
    }

    @Override
    public int getCount(CharID id)
    {
        return getSet(id).size();
    }
}
