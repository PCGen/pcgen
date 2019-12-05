/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.helper;

import java.util.ArrayList;
import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.VarContainer;
import pcgen.cdom.base.VarHolder;
import pcgen.cdom.content.RemoteModifier;
import pcgen.cdom.content.VarModifier;

/**
 * VarHolderSupport implements a standard implementation of the VarHolder interface. This
 * allows this behavior to be shared through delegation to this support object.
 */
public class VarHolderSupport implements VarHolder, VarContainer
{
    /**
     * The list of VarModifier objects in this VarHolderSupport.
     * <p>
     * Lazily instantiated.
     */
    private List<VarModifier<?>> modifierList;

    /**
     * The list of RemoteModifier objects in this VarHolderSupport.
     * <p>
     * Lazily instantiated.
     */
    private List<RemoteModifier<?>> remoteModifierList;

    /**
     * The list of granted variables in this VarHolderSupport.
     * <p>
     * Lazily instantiated.
     */
    private List<String> grantedVars;

    @Override
    public void addModifier(VarModifier<?> vm)
    {
        if (modifierList == null)
        {
            modifierList = new ArrayList<>();
        }
        modifierList.add(vm);
    }

    @Override
    public VarModifier<?>[] getModifierArray()
    {
        return (modifierList == null) ? VarModifier.EMPTY_VARMODIFIER
                : modifierList.toArray(new VarModifier[0]);
    }

    @Override
    public void addRemoteModifier(RemoteModifier<?> vm)
    {
        if (remoteModifierList == null)
        {
            remoteModifierList = new ArrayList<>();
        }
        remoteModifierList.add(vm);
    }

    @Override
    public RemoteModifier<?>[] getRemoteModifierArray()
    {
        return (remoteModifierList == null) ? RemoteModifier.EMPTY_REMOTEMODIFIER
                : remoteModifierList.toArray(new RemoteModifier[0]);
    }

    @Override
    public void addGrantedVariable(String variableName)
    {
        if (grantedVars == null)
        {
            grantedVars = new ArrayList<>();
        }
        grantedVars.add(variableName);
    }

    @Override
    public String[] getGrantedVariableArray()
    {
        return (grantedVars == null) ? StringUtil.EMPTY_STRING_ARRAY
                : grantedVars.toArray(new String[0]);
    }

}
