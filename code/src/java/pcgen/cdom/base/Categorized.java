/*
 * Copyright (c) 2007-18 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.base;

/**
 * Categorized represents an object which can possess a Category object. This
 * Category is used for establishing unique identity of an object.
 *
 * @param <T> The Class of object being identified
 * @see pcgen.cdom.base.Category
 */
public interface Categorized<T extends Categorized<T>> extends Loadable
{
    /**
     * Returns the Category of the Categorized object.
     *
     * @return the Category of the Categorized
     */
    Category<T> getCDOMCategory();

    /**
     * Sets the Category of the Categorized object.
     *
     * @param category The Category the Categorized should be set to
     */
    void setCDOMCategory(Category<T> category);

    @Override
    default ClassIdentity<? extends Loadable> getClassIdentity()
    {
        return getCDOMCategory();
    }

}
