/*
 * Copyright (c) Thomas Parker, 2018-9.
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
package pcgen.cdom.formula;

import pcgen.base.formula.base.VarScoped;
import pcgen.cdom.enumeration.CharID;

/**
 * A VariableWrapperFactory is a Factory that can produce and disconnect VariableWrapper
 * objects.
 */
public interface VariableWrapperFactory
{
    /**
     * Retrieves a (Global) Wrapper for the given CharID and name of the Variable.
     *
     * @param id   The CharID identifying the PlayerCharacter on which the Wrapper resides
     * @param name The name of the Variable
     * @return A Wrapper for the given CharID and name of the Wrapper
     */
    VariableWrapper<?> getGlobalWrapper(CharID id, String name);

    /**
     * Retrieves a Wrapper for the given CharID, owning object, and name of the Variable.
     *
     * @param id    The CharID identifying the PlayerCharacter on which the Wrapper resides
     * @param owner The owning object of the Wrapper
     * @param name  The name of the Variable
     * @return A Wrapper for the given CharID, owning object, and name of the Wrapper
     */
    VariableWrapper<?> getWrapper(CharID id, VarScoped owner,
            String name);

    /**
     * Disconnects the given VariableWrapper from the WriteableVariableStore. This is
     * necessary before a VariableWrapper is disposed of, so that it does not cause a
     * memory leak.
     * <p>
     * Note, if disconnect() is called and the VariableWrapper continues to be used, then
     * it is effectively a WriteableVariableStore, not a MonitorableVariableStore. It will
     * no longer send any ReferenceEvents if the underlying VariableID changes.
     *
     * @param variableWrapper The VariableWrapper to be disconnected from its VariableStore
     */
    void disconnect(VariableWrapper<?> variableWrapper);
}
