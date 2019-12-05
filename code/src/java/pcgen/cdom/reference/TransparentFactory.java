/*
 * Copyright 2010 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.reference;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import pcgen.base.lang.UnreachableError;
import pcgen.cdom.base.ClassIdentity;
import pcgen.cdom.base.Loadable;

/**
 * A TransparentFactory is a ManufacturableFactory that produces Transparent CDOMReference
 * objects. These are CDOMReferences that it will not be reasonable to directly resolve -
 * because they may be needed across many different data loads (as - for example - the
 * game modes are not reloaded). These references are therefore resolved a different way
 * (by loading them with another reference).
 * <p>
 * Please note one significant item about a TransparentFactory (which will impact the
 * GameMode's ReferenceContext). No Categorized object should ever be constructed directly
 * in the game mode - it will not be properly initialized with it's category, and thus
 * will encounter many problems during its life cycle.
 *
 * @param <T> The type of object managed by this TransparentFactory
 */
public class TransparentFactory<T extends Loadable> implements ManufacturableFactory<T>
{

    /**
     * The reference Class of object processed by this TrasnsparentFactory.
     */
    private final Class<T> refClass;

    /**
     * The String representation of the Format of objects in this TransparentFactory (e.g.
     * "ABILITY=FEAT").
     */
    private final String formatRepresentation;

    /**
     * Constructs a new TransparentFactory that will process objects of the given Class
     */
    public TransparentFactory(String formatRepresentation, Class<T> objClass)
    {
        this.formatRepresentation = Objects.requireNonNull(formatRepresentation);
        if (objClass == null)
        {
            throw new IllegalArgumentException("Reference Class for " + getClass().getName() + " cannot be null");
        }
        try
        {
            objClass.newInstance();
        } catch (InstantiationException e)
        {
            throw new IllegalArgumentException(
                    "Class for " + getClass().getName() + " must possess a zero-argument constructor", e);
        } catch (IllegalAccessException e)
        {
            throw new IllegalArgumentException(
                    "Class for " + getClass().getName() + " must possess a public zero-argument constructor", e);
        }
        refClass = objClass;
    }

    @Override
    public CDOMGroupRef<T> getAllReference()
    {
        return new CDOMTransparentAllRef<>(formatRepresentation, refClass);
    }

    @Override
    public CDOMGroupRef<T> getTypeReference(String... types)
    {
        return new CDOMTransparentTypeRef<>(formatRepresentation, refClass, types);
    }

    @Override
    public CDOMSingleRef<T> getReference(String key)
    {
        return new CDOMTransparentSingleRef<>(formatRepresentation, refClass, key);
    }

    @Override
    public T newInstance()
    {
        try
        {
            return refClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
        {
            throw new UnreachableError(
                    "Class was tested at " + "construction to ensure it had a public, " + "zero-argument constructor", e);
        }
    }

    @Override
    public boolean isMember(T item)
    {
        return refClass.equals(item.getClass());
    }

    @Override
    public Class<T> getReferenceClass()
    {
        return refClass;
    }

    @Override
    public String getReferenceDescription()
    {
        return refClass.getSimpleName();
    }

    @Override
    public boolean resolve(ReferenceManufacturer<T> rm, String name, CDOMSingleRef<T> value,
            UnconstructedValidator validator)
    {
        throw new UnsupportedOperationException("Resolution should not occur on Transparent object");
    }

    @Override
    public boolean populate(ReferenceManufacturer<T> parentCrm, ReferenceManufacturer<T> rm,
            UnconstructedValidator validator)
    {
        // No work to do?
        return true;
    }

    @Override
    public ManufacturableFactory<T> getParent()
    {
        throw new UnsupportedOperationException("Resolution of Parent should not occur on Transparent object");
    }

    @Override
    public ClassIdentity<T> getReferenceIdentity()
    {
        throw new UnsupportedOperationException("Resolution to Identity should not occur on Transparent object");
    }

    @Override
    public String getPersistentFormat()
    {
        return formatRepresentation;
    }
}
