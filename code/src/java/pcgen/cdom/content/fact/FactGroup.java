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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.base.util.Indirect;
import pcgen.base.util.ObjectContainer;
import pcgen.cdom.base.CDOMObject;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;

/**
 * A FactGroup is an ObjectContainer that contains objects of a specific type
 * (e.g. Skill) that contain a specific value in a given FACT
 *
 * @param <T> The Type of object contained in this FactGroup
 * @param <F> The Type of the Fact being checked in this FactGroup
 */
public class FactGroup<T extends CDOMObject, F> implements ObjectContainer<T>
{

    /**
     * Contains the underlying FactInfo for this FactGroup (identifying the
     * "rules of the road")
     */
    private final FactInfo<T, F> def;

    /**
     * The Indirect containing the fact to be matched
     */
    private final Indirect<F> toMatch;

    /**
     * The ObjectContainer containing all of the objects of the type of object
     * where this FactGroup (and thus the FactInfo) are usable
     */
    private final ObjectContainer<T> allObjects;

    /**
     * The cached answers for this FactGroup.
     * <p>
     * Note that this cache can exist because these items are facts, and thus
     * semantically can't change. There is also the assumption that the global
     * list of objects cannot change.
     */
    private List<T> cache;

    /**
     * Constructs a new FactGroup from the given context, FactInfo and value.
     *
     * @param context The LoadContext used to resolve references
     * @param fi      The FactInfo indicating the underlying characteristics of the
     *                Fact that this FactGroup will check
     * @param value   The String representation of the value that this FactGroup
     *                will be looking for
     */
    public FactGroup(LoadContext context, FactInfo<T, F> fi, String value)
    {
        if (fi.getUsableLocation().equals(CDOMObject.class))
        {
            throw new IllegalArgumentException("FactGroup cannot be global");
        }
        def = fi;
        AbstractReferenceContext refContext = context.getReferenceContext();
        allObjects = refContext.getCDOMAllReference(def.getUsableLocation());
        toMatch = def.getFormatManager().convertIndirect(value);
        if (toMatch == null)
        {
            throw new IllegalArgumentException(
                    "Failed to convert " + value + " as a " + def.getFormatManager().getManagedClass().getSimpleName());
        }
    }

    @Override
    public Collection<T> getContainedObjects()
    {
        if (cache == null)
        {
            List<T> setupCache = new ArrayList<>();
            for (T obj : allObjects.getContainedObjects())
            {
                if (contains(obj))
                {
                    setupCache.add(obj);
                }
            }
            cache = setupCache;
        }
        return Collections.unmodifiableCollection(cache);
    }

    @Override
    public String getLSTformat(boolean useAny)
    {
        return def.getFactName() + "=" + def.getFormatManager().unconvert(toMatch.get());
    }

    @Override
    public boolean contains(T obj)
    {
        Indirect<?> fact = obj.get(def.getFactKey());
        return fact != null && fact.get().equals(toMatch.get());
    }

    @Override
    public Class<T> getReferenceClass()
    {
        return allObjects.getReferenceClass();
    }
}
