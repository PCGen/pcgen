/*
 * Copyright (c) Thomas Parker, 2015.
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
package pcgen.cdom.facet.analysis;

import java.util.Optional;

import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.base.VariableID;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.ScopeFacet;
import pcgen.cdom.facet.VariableStoreFacet;
import pcgen.cdom.formula.VariableUtilities;
import pcgen.util.Logging;

/**
 * ResultFacet is a consolidated location to determine the value of a variable.
 * This gives a location where a system can request in terms of owning object
 * and variable name (string) and the conversion to VariableID is done
 * internally to this Facet in order to then get the result from the appropriate
 * VariableStore.
 */
public class ResultFacet
{

    private ScopeFacet scopeFacet;

    private VariableStoreFacet variableStoreFacet;

    public Object getGlobalVariable(CharID id, String varName)
    {
        VariableID<?> varID = VariableUtilities.getGlobalVariableID(id, varName);
        return variableStoreFacet.getValue(id, varID);
    }

    public Object getLocalVariable(CharID id, CDOMObject cdo, String varName)
    {
        Optional<String> localScopeName = cdo.getLocalScopeName();
        if (localScopeName.isEmpty())
        {
            return getGlobalVariable(id, varName);
        }

        ScopeInstance scope = scopeFacet.get(id, localScopeName.get(), cdo);
        if (scope == null)
        {
            Logging.errorPrint("Improperly built " + cdo.getClass().getSimpleName() + ": " + cdo.getKeyName()
                    + " had no VariableScope");
            return null;
        }
        VariableID<?> varID = VariableUtilities.getLocalVariableID(id, scope, varName);
        return variableStoreFacet.getValue(id, varID);
    }

    public void setScopeFacet(ScopeFacet scopeFacet)
    {
        this.scopeFacet = scopeFacet;
    }

    public void setVariableStoreFacet(VariableStoreFacet variableStoreFacet)
    {
        this.variableStoreFacet = variableStoreFacet;
    }

}
