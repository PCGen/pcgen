/*
 * CategorisedDataFacetChangeEvent.java Copyright James Dempsey, 2012
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
 *
 *
 */
package pcgen.cdom.facet;

import pcgen.cdom.base.Category;
import pcgen.cdom.base.PCGenIdentifier;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.facet.event.DataFacetChangeEvent;

/**
 * The Class {@code CategorizedDataFacetChangeEvent} indicates that a facet
 * with a category has changed. This is usually used for Ability add/remove
 * events to allow the listener to determine the category and nature to which
 * the ability is being applied.
 * <p>
 * (Mon, 14 Jan 2013) $
 */

public class CategorizedDataFacetChangeEvent<IDT extends PCGenIdentifier, T> extends DataFacetChangeEvent<IDT, T>
{

    private final Category category;
    private final Nature nature;

    /**
     * Constructs a new DataFacetChangeEvent for the given PCGenIdentifier. The
     * CDOMObject which was added or removed and an indication of the action
     * (Addition or Removal) is also provided.
     *
     * @param id     The PCGenIdentifier identifying the resource in which the
     *               event took place
     * @param cdo    The CDOMObject which was added to or removed from the Graph
     * @param source The object that the event originates from.
     * @param type   An integer identifying whether the given CDOMObject was added
     *               or removed from the resource
     * @param cat    The category in which cdo was added.
     * @param nature The nature of the ability being manipulated.
     */
    public CategorizedDataFacetChangeEvent(IDT id, T cdo, Object source, int type, Category cat, Nature nature)
    {
        super(id, cdo, source, type);
        this.category = cat;
        this.nature = nature;
    }

    /**
     * @return the category
     */
    public Category getCategory()
    {
        return category;
    }

    /**
     * @return the nature
     */
    public Nature getNature()
    {
        return nature;
    }

}
