/*
 * Copyright 2015 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package pcgen.persistence.lst;

import java.util.List;
import java.util.Objects;

import pcgen.cdom.base.CDOMObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;

/**
 * A GenericLocalVariableLoader is an LST File Loader that processes files that
 * contain local variables.
 * <p>
 * Specifically, it is an extension of GenericLoader that is aware that it needs
 * to "drop into context" when loading certain types.
 *
 * @param <T> The type of object that this GenericLocalVariableLoader loads from
 *            LST files
 */
public class GenericLocalVariableLoader<T extends CDOMObject> extends GenericLoader<T>
{

    /**
     * The String representation of the Variable Scope for variables loaded by
     * this GenericLocalVariableLoader.
     */
    private final String varScope;

    /**
     * Constructs a new GenericLocalVariableLoader that loads files of the given
     * type and operates within the given variable scope.
     *
     * @param cl       The class of object loaded by this GenericLocalVariableLoader
     * @param varScope The variable scope in which the objects are loaded
     */
    public GenericLocalVariableLoader(Class<T> cl, String varScope)
    {
        super(cl);
        Objects.requireNonNull(varScope, "VariableScope cannot be null");
        this.varScope = varScope;
    }

    @Override
    public void loadLstFiles(LoadContext context, List<CampaignSourceEntry> fileList) throws PersistenceLayerException
    {
        LoadContext subContext = context.dropIntoContext(varScope);
        super.loadLstFiles(subContext, fileList);
    }

}
