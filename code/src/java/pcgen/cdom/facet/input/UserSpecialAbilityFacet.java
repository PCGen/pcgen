/*
 * Copyright (c) Thomas Parker, 2012.
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
package pcgen.cdom.facet.input;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.QualifiedActor;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.PlayerCharacterTrackingFacet;
import pcgen.cdom.facet.base.AbstractQualifiedListFacet;
import pcgen.cdom.helper.SAProcessor;
import pcgen.core.SpecialAbility;

/**
 * UserSpecialAbilityFacet tracks the SpecialAbility objects added to a Player
 * Character by explicit user action (old UI capability).
 * <p>
 * TODO Need to consider whether this sticks around, or how we handle this
 * capability in the new UI (and transition any Player Characters that have a
 * user SpecialAbility.
 */
public class UserSpecialAbilityFacet extends AbstractQualifiedListFacet<SpecialAbility>
{

    private final PlayerCharacterTrackingFacet trackingFacet =
            FacetLibrary.getFacet(PlayerCharacterTrackingFacet.class);

    /**
     * Returns a non-null copy of the List of resolved SpecialAbility objects
     * for the given source CDOMObject and the Player Character represented by
     * the given CharID. The Player Character must qualify for the Special
     * Ability (if it has prerequisites) in order for the resolved
     * SpecialAbility to be returned by this method. This method returns an
     * empty List if no SpecialAbility objects are in this
     * UserSpecialAbilityFacet for the given source CDOMObject and the Player
     * Character identified by the given CharID.
     * <p>
     * This method is value-semantic in that ownership of the returned List is
     * transferred to the class calling this method. Modification of the
     * returned List will not modify this UserSpecialAbilityFacet and
     * modification of this UserSpecialAbilityFacet will not modify the returned
     * List. Modifications to the returned List will also not modify any future
     * or previous objects returned by this (or other) methods on
     * UserSpecialAbilityFacet. If you wish to modify the information stored in
     * this UserSpecialAbilityFacet, you must use the add*() and remove*()
     * methods of UserSpecialAbilityFacet.
     *
     * @param id     The CharID representing the Player Character for which a copy
     *               of the resolved items in this UserSpecialAbilityFacet should
     *               be returned
     * @param source The source of the SpecialAbility objects for this
     *               UserSpecialAbilityFacet to be used for the resolution of the
     *               SpecialAbility objects in the Player Character
     * @return A non-null List of resolved SpecialAbility objects from this
     * UserSpecialAbilityFacet for the Player Character represented by
     * the given CharID
     */
    public List<SpecialAbility> getResolved(CharID id, Object source)
    {
        List<SpecialAbility> returnList = new ArrayList<>();
        SAProcessor proc = new SAProcessor(trackingFacet.getPC(id));
        for (SpecialAbility sa : getQualifiedSet(id, source))
        {
            returnList.add(proc.act(sa, source));
        }
        return returnList;
    }

    /**
     * Returns a non-null List of processed SpecialAbility objects for the
     * Player Character represented by the given CharID. The given
     * QualifiedActor will determine the type and contents of the returned List.
     * This method returns an empty List if no objects are in this
     * UserSpecialAbilityFacet for the Player Character identified by the given
     * CharID.
     * <p>
     * This method is value-semantic in that ownership of the returned List is
     * transferred to the class calling this method. Modification of the
     * returned List will not modify this UserSpecialAbilityFacet and
     * modification of this UserSpecialAbilityFacet will not modify the returned
     * List. Modifications to the returned List will also not modify any future
     * or previous objects returned by this (or other) methods on
     * UserSpecialAbilityFacet. If you wish to modify the information stored in
     * this UserSpecialAbilityFacet, you must use the add*() and remove*()
     * methods of UserSpecialAbilityFacet.
     * <p>
     * Note: If a particular item has been granted by more than one source, then
     * the QualifiedActor will only be called for the first source that
     * (successfully grants) the underlying object.
     *
     * @param <T> The type of objects returned by the given QualifiedActor (and
     *            thus also the type of the List returned by this method)
     * @param id  The CharID representing the Player Character for which the
     *            processed items in this UserSpecialAbilityFacet should be
     *            returned
     * @param qa  The QualifiedActor which will act on each of the items in this
     *            UserSpecialAbilityFacet for which the Player Character
     *            qualifies.
     * @return A non-null List of objects created by the QualifiedActor from
     * each of the objects in this UserSpecialAbilityFacet for which the
     * Player Character qualifies.
     */
    public <T> List<T> getAllResolved(CharID id, QualifiedActor<SpecialAbility, T> qa)
    {
        return actOnQualifiedSet(id, qa);
    }

}
