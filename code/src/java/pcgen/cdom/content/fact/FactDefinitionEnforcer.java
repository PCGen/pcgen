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
package pcgen.cdom.content.fact;

import java.util.Objects;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.FactKey;
import pcgen.persistence.lst.LstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.util.Logging;

/**
 * A FactDefinitionEnforcer is a dynamically built DeferredToken used when a
 * FACT: is defined as REQUIRED:YES
 * <p>
 * This will thus enforce that every object of that type has the required FACT
 * present on the object.
 *
 * @param <T> The type of object upon which the FactDefinitionEnforcer will
 *            be used
 * @param <F> The format of the data stored in the Fact
 */
public class FactDefinitionEnforcer<T extends CDOMObject, F> implements DeferredToken<T>, LstToken
{

    /**
     * The FactInfo that this FactDefinitionEnforcer is ensuring is present in
     * the data
     */
    private final FactInfo<T, F> def;

    /**
     * Constructs a new FactDefintionEnforcer to enforce that the given FactInfo
     * is required in the data.
     *
     * @param fi The FactInfo that will be enforced to ensure it exists in the
     *           data
     */
    public FactDefinitionEnforcer(FactInfo<T, F> fi)
    {
        Objects.requireNonNull(fi, "Fact Info cannot be null");
        def = fi;
    }

    @Override
    public boolean process(LoadContext context, T obj)
    {
        FactKey<F> fk = def.getFactKey();
        if (context.getObjectContext().getFact(obj, fk) != null)
        {
            return true;
        }
        Logging.errorPrint("FACT " + def.getFactName() + " was required but not set in "
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
        return "FACTENFORCE";
    }

}
