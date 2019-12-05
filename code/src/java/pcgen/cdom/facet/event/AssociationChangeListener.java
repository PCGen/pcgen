/*
 * Copyright (c) Thomas Parker, 2014.
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
package pcgen.cdom.facet.event;

/**
 * AssociationChangeListener is the interface that must be implemented by a
 * class for it to receive AssociationChangeEvents from the
 * AssociationChangeFacet when a Association Bonus value has changed for a
 * Player Character.
 */
@FunctionalInterface
public interface AssociationChangeListener
{

    /**
     * Method called when a Association Bonus value has changed on a Player
     * Character. The AssociationChangeEvent contains the relevant details of
     * the Association Bonus value change.
     *
     * @param srce The AssociationChangeEvent containing the details of the
     *             Association Bonus value change for a Player Character
     */
    void bonusChange(AssociationChangeEvent srce);

}
