/*
 * Copyright (c) 2016-18 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.inst;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import pcgen.base.formula.base.VarScoped;
import pcgen.cdom.base.Categorized;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.base.VarContainer;
import pcgen.cdom.base.VarHolder;
import pcgen.cdom.content.RemoteModifier;
import pcgen.cdom.content.VarModifier;
import pcgen.cdom.formula.PCGenScoped;
import pcgen.cdom.helper.VarHolderSupport;

/**
 * A Dynamic is an object designed to behave as defined by the Data, rather than having
 * hard-coded behaviors in the PCGen core.
 * <p>
 * By having almost no information hard-coded, this class produces a very flexible
 * framework for use in a data driven system.
 */
public class Dynamic
        implements Loadable, VarHolder, VarContainer, PCGenScoped, Categorized<Dynamic>
{

    /**
     * The source URI for this Dynamic (where it was first created).
     */
    private URI sourceURI;

    /**
     * The Category of the Dynamic.
     * <p>
     * This is the actual type of the dynamic object as the data wishes to refer to that
     * object. This would be a peer to "SKILL" or "SPELL" for the hard-coded objects.
     */
    private Category<Dynamic> category;

    /**
     * The name of this Dynamic.
     * <p>
     * This is effectively the key for the Dynamic.
     */
    private String name;

    /**
     * Support object to store the variable information on an object (i.e. delegate that
     * implements VarHolder).
     */
    private VarHolderSupport varHolder = new VarHolderSupport();

    @Override
    public URI getSourceURI()
    {
        return sourceURI;
    }

    @Override
    public void setSourceURI(URI source)
    {
        sourceURI = source;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String getDisplayName()
    {
        return name;
    }

    @Override
    public String getKeyName()
    {
        return getDisplayName();
    }

    @Override
    public boolean isInternal()
    {
        return false;
    }

    @Override
    public boolean isType(String type)
    {
        return false;
    }

    @Override
    public Category<Dynamic> getCDOMCategory()
    {
        return category;
    }

    @Override
    public void setCDOMCategory(Category<Dynamic> cat)
    {
        category = cat;
    }

    @Override
    public Optional<String> getLocalScopeName()
    {
        return Optional.of("PC." + category.getKeyName());
    }

    @Override
    public Optional<VarScoped> getVariableParent()
    {
        return Optional.empty();
    }

    @Override
    public PCGenScoped getLocalChild(String childType, String childName)
    {
        return null;
    }

    @Override
    public List<String> getChildTypes()
    {
        return Collections.emptyList();
    }

    @Override
    public List<PCGenScoped> getChildren(String childType)
    {
        return null;
    }

    @Override
    public String toString()
    {
        return category.getDisplayName() + ":" + name;
    }

    /*
     * Begin implementation / delegation of the VarHolder interface.
     */
    @Override
    public void addModifier(VarModifier<?> vm)
    {
        varHolder.addModifier(vm);
    }

    @Override
    public VarModifier<?>[] getModifierArray()
    {
        return varHolder.getModifierArray();
    }

    @Override
    public void addRemoteModifier(RemoteModifier<?> vm)
    {
        varHolder.addRemoteModifier(vm);
    }

    @Override
    public RemoteModifier<?>[] getRemoteModifierArray()
    {
        return varHolder.getRemoteModifierArray();
    }

    @Override
    public void addGrantedVariable(String variableName)
    {
        varHolder.addGrantedVariable(variableName);
    }

    @Override
    public String[] getGrantedVariableArray()
    {
        return varHolder.getGrantedVariableArray();
    }
    /*
     * End implementation / delegation of the VarHolder interface.
     */
}
