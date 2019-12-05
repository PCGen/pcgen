/*
 * Copyright 2014-15 (C) Tom Parker <thpr@users.sourceforge.net>
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
 */
package pcgen.output.factory;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.fact.FactFacet;
import pcgen.output.base.ModelFactory;
import pcgen.output.model.FactFacetModel;

/**
 * An FactModelFactory is a ModelFactory that can wrap an FactFacet and produce
 * an FactFacetModel for a given CharID
 */
public class FactModelFactory implements ModelFactory
{
    /**
     * The FactFacet for which this FactModelFactory can produce a ModelFactory.
     */
    private final FactFacet facet;

    /**
     * Constructs a new FactModelFactory for the given FactFacet.
     *
     * @param ffacet The FactFacet for which this FactModelFactory will produce
     *               ModelFactory objects
     */
    public FactModelFactory(FactFacet ffacet)
    {
        facet = ffacet;
    }

    @Override
    public FactFacetModel generate(CharID id)
    {
        return new FactFacetModel(id, facet);
    }
}
