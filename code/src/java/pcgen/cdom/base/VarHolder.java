/*
 * Copyright (c) Thomas Parker, 2018.
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
package pcgen.cdom.base;

import pcgen.cdom.content.RemoteModifier;
import pcgen.cdom.content.VarModifier;

/**
 * A VarHolder is a (writeable) object that holds Modifiers.
 */
public interface VarHolder
{
    /**
     * Adds a new local Modifier to this VarContainer.
     *
     * @param varModifier The VarModifier to be added to this VarContainer.
     */
    void addModifier(VarModifier<?> varModifier);

    /**
     * Adds a new RemoteModifier to this VarContainer.
     *
     * @param remoteModifier The RemoteModifier to be added to this VarContainer.
     */
    void addRemoteModifier(RemoteModifier<?> remoteModifier);

    /**
     * Adds a new variable that will grant objects.
     *
     * @param variableName The (Global) variable name that will indicate granted objects
     */
    void addGrantedVariable(String variableName);

}
