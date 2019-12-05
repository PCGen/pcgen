/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.output.model;

import java.util.Objects;

import pcgen.base.formula.base.VariableID;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.ObjectWrapperFacet;
import pcgen.cdom.facet.VariableStoreFacet;
import pcgen.cdom.formula.VariableUtilities;

import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * GlobalVarModel provides the services to expose global variables for a Player
 * Character to the output system.
 */
public class GlobalVarModel implements TemplateHashModel
{

    /**
     * The global VariableStore Facet used to get VariableID values
     */
    private final VariableStoreFacet variableStoreFacet = FacetLibrary.getFacet(VariableStoreFacet.class);

    /**
     * The global ObjectWrapperFacet used to wrap the current value of a
     * variable
     */
    private final ObjectWrapperFacet wrapperFacet = FacetLibrary.getFacet(ObjectWrapperFacet.class);

    /**
     * The underlying CharID for this InfoModel
     */
    private final CharID id;

    /**
     * Constructs a new GlobalVarModel with the given CharID
     *
     * @param id The CharID that underlies this GlobalVarModel
     */
    public GlobalVarModel(CharID id)
    {
        Objects.requireNonNull(id, "CharID cannot be null");
        this.id = id;
    }

    /**
     * Gets the global variable (new formula system) represented by the given
     * key.
     */
    @Override
    public TemplateModel get(String varName) throws TemplateModelException
    {
        VariableID<?> varID =
                VariableUtilities.getGlobalVariableID(id, varName);
        Object value = variableStoreFacet.getValue(id, varID);
        return wrapperFacet.wrap(id, value);
    }

    @Override
    public boolean isEmpty()
    {
        //Assume there is at least one global variable
        return false;
    }

}
