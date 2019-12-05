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
package pcgen.cdom.content.factset;

import java.util.Objects;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.ContentDefinition;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.FactSetKey;
import pcgen.cdom.facet.CDOMWrapperInfoFacet;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.output.actor.FactSetKeyActor;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * A FactSetDefinition is a definition of a legal entry for a FACTSET: in a
 * file. This contains both the legal location (e.g. SKILL) as well as the name
 * of the factset (e.g. Possibility).
 * <p>
 * This is a structure used to contain information about a FactSet. This is then
 * used to derive the other necessary components, including input tokens,
 * enforcement of required, output tokens, etc.
 * <p>
 * A FactSetDefinition is created when a FACTSETDEF: line is encountered in the
 * Data Control LST file.
 *
 * @param <T> The Type of object upon which the FACTSET for this
 *            FactSetDefinition can be applied
 * @param <F> The format of the data stored in the FactSet
 */
public class FactSetDefinition<T extends CDOMObject, F> extends ContentDefinition<T, F> implements FactSetInfo<T, F>
{

    /**
     * The FactSet name for this FactSetDefinition
     */
    private String factSetName;

    @Override
    protected void activateKey()
    {
        //Necessary to get the key with getConstant early so later items can use valueOf
        getFactSetKey();
    }

    @Override
    protected void activateOutput(DataSetID dsID)
    {
        FactSetKeyActor<F> fca = new FactSetKeyActor<>(getFactSetKey());
        CDOMWrapperInfoFacet wiFacet = FacetLibrary.getFacet(CDOMWrapperInfoFacet.class);
        if (!wiFacet.set(dsID, getUsableLocation(), factSetName.toLowerCase(), fca))
        {
            Logging.errorPrint(getUsableLocation().getSimpleName() + " output " + factSetName.toLowerCase()
                    + " already exists, ignoring Visibility to EXPORT for FACTSET: " + factSetName);
        }
    }

    @Override
    protected void activateTokens(LoadContext context)
    {
        context.loadLocalToken(new FactSetParser<>(this));
        Boolean required = getRequired();
        if ((required != null) && required)
        {
            context.loadLocalToken(new FactSetDefinitionEnforcer<>(this));
        }
        Boolean selectable = getSelectable();
        if ((selectable != null) && selectable)
        {
            context.loadLocalToken(new FactSetGroupDefinition<>(this));
        }
    }

    /**
     * Sets the Fact Set Name for this FactDefinition
     *
     * @param name The Fact Set Name for this FactDefinition
     * @throws IllegalArgumentException if the given name is null or empty
     */
    public void setFactSetName(String name)
    {
        Objects.requireNonNull(name, "Fact Set Name cannot be null");
        if (name.isEmpty())
        {
            throw new IllegalArgumentException("Fact Set Name cannot be empty");
        }
        factSetName = name;
    }

    @Override
    public String getFactSetName()
    {
        return factSetName;
    }

    @Override
    public FactSetKey<F> getFactSetKey()
    {
        return FactSetKey.getConstant(factSetName, getFormatManager());
    }

}
