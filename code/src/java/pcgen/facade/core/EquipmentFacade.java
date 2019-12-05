/*
 * Copyright 2009 Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.facade.core;

import java.io.File;
import java.util.List;

public interface EquipmentFacade extends InfoFacade
{

    String[] getTypes();

    /**
     * Retrieve the icon for this ewuipment icon. This may be directly set for
     * the item, or it may be for one of the item's types. The types are
     * checked from right to left.
     *
     * @return The icon for this equipment item, or null if none
     */
    File getIcon();

    /**
     * @return A list of the equipment's types suitable for display.
     */
    List<String> getTypesForDisplay();

    /**
     * Get the raw special properties as a comma separated string.
     *
     * @return raw special properties
     */
    String getRawSpecialProperties();

}
