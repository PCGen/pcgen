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
package pcgen.rules.context;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ClassIdentity;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ManufacturableFactory;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.UnconstructedListener;
import pcgen.cdom.reference.UnconstructedValidator;

class TrackingManufacturer<T extends Loadable> implements ReferenceManufacturer<T>
{

    private final ReferenceManufacturer<T> rm;
    private final TrackingReferenceContext context;

    protected TrackingManufacturer(TrackingReferenceContext trc, ReferenceManufacturer<T> mfg)
    {
        context = Objects.requireNonNull(trc);
        rm = Objects.requireNonNull(mfg);
    }

    @Override
    public void addObject(T o, String key)
    {
        rm.addObject(o, key);
    }

    @Override
    public void addUnconstructedListener(UnconstructedListener listener)
    {
        rm.addUnconstructedListener(listener);
    }

    @Override
    public void buildDeferredObjects()
    {
        rm.buildDeferredObjects();
    }

    @Override
    public void constructIfNecessary(String value)
    {
        rm.constructIfNecessary(value);
    }

    @Override
    public T constructNowIfNecessary(String name)
    {
        return rm.constructNowIfNecessary(name);
    }

    @Override
    public T constructObject(String key)
    {
        return rm.constructObject(key);
    }

    @Override
    public boolean containsObjectKeyed(String key)
    {
        return rm.containsObjectKeyed(key);
    }

    @Override
    public boolean forgetObject(T o)
    {
        return rm.forgetObject(o);
    }

    @Override
    public T getActiveObject(String key)
    {
        return rm.getActiveObject(key);
    }

    @Override
    public Collection<T> getAllObjects()
    {
        return rm.getAllObjects();
    }

    @Override
    public CDOMGroupRef<T> getAllReference()
    {
        CDOMGroupRef<T> ref = rm.getAllReference();
        context.trackReference(ref);
        return ref;
    }

    @Override
    public int getConstructedObjectCount()
    {
        return rm.getConstructedObjectCount();
    }

    @Override
    public T getObject(String key)
    {
        return rm.getObject(key);
    }

    @Override
    public CDOMSingleRef<T> getReference(String key)
    {
        CDOMSingleRef<T> ref = rm.getReference(key);
        context.trackReference(ref);
        return ref;
    }

    @Override
    public Class<T> getReferenceClass()
    {
        return rm.getReferenceClass();
    }

    @Override
    public CDOMGroupRef<T> getTypeReference(String... types)
    {
        CDOMGroupRef<T> ref = rm.getTypeReference(types);
        context.trackReference(ref);
        return ref;
    }

    @Override
    public UnconstructedListener[] getUnconstructedListeners()
    {
        return rm.getUnconstructedListeners();
    }

    @Override
    public void removeUnconstructedListener(UnconstructedListener listener)
    {
        rm.removeUnconstructedListener(listener);
    }

    @Override
    public void renameObject(String key, T o)
    {
        rm.renameObject(key, o);
    }

    @Override
    public boolean resolveReferences(UnconstructedValidator validator)
    {
        return rm.resolveReferences(validator);
    }

    @Override
    public boolean validate(UnconstructedValidator validator)
    {
        return rm.validate(validator);
    }

    @Override
    public String getReferenceDescription()
    {
        return rm.getReferenceDescription();
    }

    @Override
    public T buildObject(String name)
    {
        return rm.buildObject(name);
    }

    @Override
    public void fireUnconstuctedEvent(CDOMReference<?> reference)
    {
        rm.fireUnconstuctedEvent(reference);
    }

    @Override
    public Collection<CDOMSingleRef<T>> getReferenced()
    {
        return rm.getReferenced();
    }

    @Override
    public ManufacturableFactory<T> getFactory()
    {
        return rm.getFactory();
    }

    @Override
    public Collection<CDOMReference<T>> getAllReferences()
    {
        return rm.getAllReferences();
    }

    @Override
    public void injectConstructed(ReferenceManufacturer<T> mfg)
    {
        mfg.injectConstructed(mfg);
    }

    @Override
    public void addDerivativeObject(T obj)
    {
        rm.addDerivativeObject(obj);
    }

    @Override
    public Collection<T> getDerivativeObjects()
    {
        return rm.getDerivativeObjects();
    }

    @Override
    public T convert(String arg0)
    {
        return rm.convert(arg0);
    }

    @Override
    public Indirect<T> convertIndirect(String arg0)
    {
        return rm.convertIndirect(arg0);
    }

    @Override
    public String getIdentifierType()
    {
        return rm.getIdentifierType();
    }

    @Override
    public Class<T> getManagedClass()
    {
        return rm.getManagedClass();
    }

    @Override
    public String unconvert(T arg0)
    {
        return rm.unconvert(arg0);
    }

    @Override
    public Optional<FormatManager<?>> getComponentManager()
    {
        return Optional.empty();
    }

    @Override
    public boolean isDirect()
    {
        return rm.isDirect();
    }

    @Override
    public ClassIdentity<T> getReferenceIdentity()
    {
        return rm.getReferenceIdentity();
    }

    @Override
    public String getPersistentFormat()
    {
        return rm.getPersistentFormat();
    }

    @Override
    public int hashCode()
    {
        return 37 + rm.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof TrackingManufacturer)
                && rm.equals(((TrackingManufacturer<?>) obj).rm);
    }


}
