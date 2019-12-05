/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.rules.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import pcgen.cdom.base.BasicClassIdentity;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Categorized;
import pcgen.cdom.base.ClassIdentity;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.reference.ManufacturableFactory;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.SimpleReferenceManufacturer;
import pcgen.cdom.reference.TransparentFactory;
import pcgen.cdom.reference.UnconstructedValidator;

/**
 * The Class {@code GameReferenceContext} is a ReferenceContext which is
 * capable of delegating its transparent references to references built later
 * in the process.  Transparent references are a new concept, they are basically
 * references that allow later resolution to other references (meaning in the
 * long run, they delegate to another reference of the same general type, though
 * they can be created before the delegate target is created)
 */
public final class GameReferenceContext extends AbstractReferenceContext
{
    private final Map<String, ReferenceManufacturer<?>> mapByPers = new HashMap<>();

    private GameReferenceContext()
    {
    }

    @Override
    public <T extends Loadable> ReferenceManufacturer<T> getManufacturer(Class<T> cl)
    {
        if (Categorized.class.isAssignableFrom(cl))
        {
            throw new InternalError(cl + " is categorized but was fetched without a category");
        }
        ClassIdentity<T> identity = BasicClassIdentity.getIdentity(cl);
        return getManufacturerId(identity);
    }

    @Override
    protected <T extends Loadable> ReferenceManufacturer<T> constructReferenceManufacturer(ClassIdentity<T> identity)
    {
        return new SimpleReferenceManufacturer<>(
                new TransparentFactory<>(identity.getPersistentFormat(), identity.getReferenceClass()));
    }

    @Override
    public Collection<ReferenceManufacturer<?>> getAllManufacturers()
    {
        return new ArrayList<>(mapByPers.values());
    }

    @Override
    public boolean validate(UnconstructedValidator validator)
    {
        return true;
    }

    @Override
    <T extends CDOMObject> T performCopy(T obj, String copyName)
    {
        throw new UnsupportedOperationException("GameReferenceContext cannot copy objects");
    }

    @Override
    public <T extends CDOMObject> T performMod(T obj)
    {
        throw new UnsupportedOperationException("GameReferenceContext cannot mod objects");
    }

    @Override
    public <T extends Loadable> boolean hasManufacturer(ClassIdentity<T> cl)
    {
        return false;
    }

    @Override
    public <T extends Loadable> ReferenceManufacturer<T> getManufacturerFac(ManufacturableFactory<T> factory)
    {
        throw new UnsupportedOperationException("GameReferenceContext cannot provide a factory based manufacturer");
    }

    /**
     * Return a new GameReferenceContext. This ReferenceContext is initialized as per
     * the rules of AbstractReferenceContext.
     *
     * @return A new GameReferenceContext
     */
    public static GameReferenceContext createGameReferenceContext()
    {
        GameReferenceContext context = new GameReferenceContext();
        context.initialize();
        return context;
    }

    @Override
    public <T extends Loadable> ReferenceManufacturer<T> getManufacturerId(ClassIdentity<T> identity)
    {
        String persistent = identity.getPersistentFormat();
        return getManufacturerByFormatName(persistent, identity.getReferenceClass());
    }

    @Override
    public <T extends Loadable> ReferenceManufacturer<T> getManufacturerByFormatName(String formatName,
            Class<T> refClass)
    {
        @SuppressWarnings("unchecked")
        ReferenceManufacturer<T> mfg = (ReferenceManufacturer<T>) mapByPers.get(formatName);
        if (mfg == null)
        {
            mfg = new SimpleReferenceManufacturer<>(new TransparentFactory<>(formatName, refClass));
            mapByPers.put(formatName, mfg);
        }
        return mfg;
    }
}
