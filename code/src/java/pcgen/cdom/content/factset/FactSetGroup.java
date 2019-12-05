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
 * A FactSetGroup is an ObjectContainer that contains objects of a specific type
 * (e.g. Skill) that contain a specific value in a given FACTSET
 *
 * @param <T> The Type of object contained in this FactSetGroup
 * @param <F> The Type of the FactSet being checked in this FactSetGroup
 */
public class FactSetGroup<T extends CDOMObject, F> implements ObjectContainer<T>
{

    /**
     * Contains the underlying FactSetInfo for this FactSetGroup (identifying
     * the "rules of the road")
     */
    private final FactSetInfo<T, F> def;

    /**
     * The Indirect containing the fact to be matched
     */
    private final Indirect<F> toMatch;

    /**
     * The ObjectContainer containing all of the objects of the type of object
     * where this FactSetGroup (and thus the FactInfo) are usable
     */
    private final ObjectContainer<T> allObjects;

    /**
     * The cached answers for this FactSetGroup.
     * <p>
     * Note that this cache can exist because these items are facts, and thus
     * semantically can't change. There is also the assumption that the global
     * list of objects cannot change.
     */
    private List<T> cache;

    /**
     * Constructs a new FactSetGroup from the given context, FactSetInfo and
     * value.
     *
     * @param context The LoadContext used to resolve references
     * @param fsi     The FactSetInfo indicating the underlying characteristics of
     *                the Fact that this FactSetGroup will check
     * @param value   The String representation of the value that this FactSetGroup
     *                will be looking for
     */
    public FactSetGroup(LoadContext context, FactSetInfo<T, F> fsi, String value)
    {
        if (fsi.getUsableLocation().equals(CDOMObject.class))
        {
            throw new IllegalArgumentException("FactSetGroup cannot be global");
        }
        def = fsi;
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
        return def.getFactSetName() + "=" + def.getFormatManager().unconvert(toMatch.get());
    }

    @Override
    public boolean contains(T obj)
    {
        List<Indirect<F>> factset = obj.getSetFor(def.getFactSetKey());
        if (factset != null)
        {
            F tgt = toMatch.get();
            for (Indirect<F> indirect : factset)
            {
                if (indirect.get().equals(tgt))
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Class<T> getReferenceClass()
    {
        return allObjects.getReferenceClass();
    }
}
