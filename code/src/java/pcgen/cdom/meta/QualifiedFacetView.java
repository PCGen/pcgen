/*
 * Copyright (c) Thomas Parker, 2013.
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
package pcgen.cdom.meta;

import java.util.Collection;

import pcgen.cdom.base.QualifyingObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractQualifiedListFacet;

public class QualifiedFacetView<T extends QualifyingObject> implements FacetView<T>
{

    private AbstractQualifiedListFacet<T> facet;

    QualifiedFacetView(AbstractQualifiedListFacet<T> facet)
    {
        this.facet = facet;
    }

    @Override
    public Collection<T> getSet(CharID id)
    {
        return facet.getSet(id);
    }

    @Override
    public Collection<Object> getSources(CharID id, T obj)
    {
        return facet.getSources(id, obj);
    }

    @Override
    public Object[] getChildren()
    {
        return facet.getDataFacetChangeListeners();
    }

    @Override
    public String getDescription()
    {
        return facet.getClass().getSimpleName();
    }

    @Override
    public boolean represents(Object src)
    {
        return facet.equals(src);
    }

    @Override
    public String toString()
    {
        return "Facet: " + facet.getClass().getSimpleName();
    }
}
