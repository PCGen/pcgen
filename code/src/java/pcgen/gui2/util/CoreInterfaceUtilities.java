/*
 * Copyright (c) 2018-9 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.gui2.util;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.LoadContextFacet;
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.VetoableReferenceFacade;
import pcgen.rules.context.LoadContext;

/**
 * CoreInterfaceUtilities provides utility methods for Channels/Wrappers for the UI.
 */
public final class CoreInterfaceUtilities
{

    /**
     * The LoadContextFacet.
     */
    private static LoadContextFacet LOAD_CONTEXT_FACET =
            FacetLibrary.getFacet(LoadContextFacet.class);

    private CoreInterfaceUtilities()
    {
        //Do not instantiate utility class
    }

    /**
     * Returns the ReferenceFacade for the channel defined by a given CodeControl.
     *
     * @param id          The CharID representing the PlayerCharacter on which the Channel is
     *                    located
     * @param codeControl The CodeControl indicating the channel for which the ReferenceFacade
     *                    should be returned
     * @return The ReferenceFacade for the channel defined by a given CodeControl
     */
    public static <T> VetoableReferenceFacade<T> getReferenceFacade(CharID id,
            CControl codeControl)
    {
        LoadContext context = LOAD_CONTEXT_FACET.get(id.getDatasetID()).get();
        String channelName =
                ControlUtilities.getControlToken(context, codeControl);
        return (VetoableReferenceFacade<T>) context.getVariableContext()
                .getGlobalChannel(id, channelName);
    }

    /**
     * Returns the ReferenceFacade for the variable defined by a given CodeControl
     *
     * @param id          The CharID representing the PlayerCharacter on which the variable is
     *                    located
     * @param codeControl The CodeControl indicating the variable for which the ReferenceFacade
     *                    should be returned
     * @return The ReferenceFacade for a the variable defined by a given CodeControl
     */
    public static ReferenceFacade<?> getReadOnlyFacade(CharID id,
            CControl codeControl)
    {
        LoadContext context = LOAD_CONTEXT_FACET.get(id.getDatasetID()).get();
        String variableName =
                ControlUtilities.getControlToken(context, codeControl);
        return getReadOnlyFacade(id, variableName);
    }


    /**
     * Returns the ReferenceFacade for a the variable with the given name.
     *
     * @param id           The CharID representing the PlayerCharacter on which the variable is
     *                     located
     * @param variableName The Variable name for which the ReferenceFacade should be returned
     * @return The ReferenceFacade for the variable with the given name
     */
    public static ReferenceFacade<?> getReadOnlyFacade(CharID id, String variableName)
    {
        LoadContext context = LOAD_CONTEXT_FACET.get(id.getDatasetID()).get();
        return context.getVariableContext().getGlobalWrapper(id, variableName);
    }

}
