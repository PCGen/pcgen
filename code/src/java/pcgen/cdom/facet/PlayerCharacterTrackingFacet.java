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

import java.lang.ref.WeakReference;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractStorageFacet;
import pcgen.core.PlayerCharacter;

/**
 * This is a transition class, designed to allow things to be taken out of
 * PlayerCharacter while a transition is made to the new facet system.
 */
public class PlayerCharacterTrackingFacet extends AbstractStorageFacet<CharID>
{
    public void associatePlayerCharacter(CharID id, PlayerCharacter pc)
    {
        setCache(id, new WeakReference<>(pc));
    }

    public PlayerCharacter getPC(CharID id)
    {
        WeakReference<PlayerCharacter> wr = (WeakReference<PlayerCharacter>) getCache(id);
        /*
         * In theory, wr should never be null. However, we have unit tests that
         * test the facets individually, and thus they implicitly call
         * PrerequisiteFacet (which calls PlayerCharacterTrackingFacet), but
         * they may not have prerequisites present. Therefore we have to protect
         * against that situation - Tom P Feb 11 '14
         */
        return (wr == null) ? null : wr.get();
    }

    @Override
    public void copyContents(CharID source, CharID copy)
    {
        //We explicitly ignore this, since the CharID identifies the PlayerCharacter
    }

}
