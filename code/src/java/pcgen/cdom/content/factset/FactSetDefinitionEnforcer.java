/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.List;
import java.util.Objects;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.FactSetKey;
import pcgen.persistence.lst.LstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.util.Logging;

/**
 * A FactSetDefinitionEnforcer is a dynamically built DeferredToken used when a
 * FACTSET: is defined as REQUIRED:YES
 * <p>
 * This will thus enforce that every object of that type has the required
 * FACTSET present on the object.
 *
 * @param <T> The type of object upon which the FactSetDefinitionEnforcer
 *            will be used
 * @param <F> The format of the data stored in the FactSet
 */
public class FactSetDefinitionEnforcer<T extends CDOMObject, F> implements DeferredToken<T>, LstToken
{

    /**
     * The FactSetInfo that this FactDefinitionEnforcer is ensuring is present
     * in the data
     */
    private final FactSetInfo<T, F> def;

    /**
     * Constructs a new FactSetDefintionEnforcer to enforce that the given
     * FactSetInfo is required in the data.
     *
     * @param fsi The FactSetInfo that will be enforced to ensure it exists in
     *            the data
     */
    public FactSetDefinitionEnforcer(FactSetInfo<T, F> fsi)
    {
        Objects.requireNonNull(fsi, "FactSet Info cannot be null");
        def = fsi;
    }

    @Override
    public boolean process(LoadContext context, T obj)
    {
        FactSetKey<?> fk = def.getFactSetKey();
        List<?> list = obj.getSetFor(fk);
        /*
         * Note, even if the Indirects in list are empty this should pass,
         * because they TRIED, right?
         */
        if ((list != null) && !list.isEmpty())
        {
            return true;
        }
        Logging.errorPrint("FACTSET " + def.getFactSetName() + " was required but not set in "
                + obj.getClass().getSimpleName() + " " + obj.getKeyName());
        return false;
    }

    @Override
    public Class<T> getDeferredTokenClass()
    {
        return def.getUsableLocation();
    }

    @Override
    public String getTokenName()
    {
        return "FACTSETENFORCE";
    }

}
