/*
 * Copyright 2010-18 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.Objects;

import pcgen.cdom.base.ClassIdentity;
import pcgen.cdom.base.Loadable;
import pcgen.util.Logging;

/**
 * A CDOMFactory is designed to build references for a specific ClassIdentity.
 *
 * @param <T> The Class of object represented by this CDOMFactory.
 */
public class CDOMFactory<T extends Loadable> implements ManufacturableFactory<T>
{

    /**
     * The underlying ClassIdentity for which this CDOMFactory will produce CDOMReference
     * objects.
     */
    private final ClassIdentity<T> classIdentity;

    /**
     * Constructs a new CDOMFactory for the given ClassIdentity.
     *
     * @param classIdentity The ClassIdentity that this CDOMFactory will represent as it builds
     *                      CDOMReference objects
     */
    public CDOMFactory(ClassIdentity<T> classIdentity)
    {
        this.classIdentity = Objects.requireNonNull(classIdentity);
    }

    @Override
    public CDOMGroupRef<T> getAllReference()
    {
        return new CDOMAllRef<>(classIdentity);
    }

    @Override
    public CDOMGroupRef<T> getTypeReference(String... types)
    {
        return new CDOMTypeRef<>(classIdentity, types);
    }

    @Override
    public CDOMSingleRef<T> getReference(String key)
    {
        return new CDOMSimpleSingleRef<>(classIdentity, key);
    }

    @Override
    public T newInstance()
    {
        return classIdentity.newInstance();
    }

    @Override
    public boolean isMember(T item)
    {
        return classIdentity.isMember(item);
    }

    @Override
    public Class<T> getReferenceClass()
    {
        return classIdentity.getReferenceClass();
    }

    @Override
    public String getReferenceDescription()
    {
        return classIdentity.getReferenceDescription();
    }

    @Override
    public boolean resolve(ReferenceManufacturer<T> rm, String name, CDOMSingleRef<T> value,
            UnconstructedValidator validator)
    {
        boolean returnGood = true;
        T activeObj = rm.getObject(name);
        if (activeObj == null)
        {
            // Wasn't constructed!
            if (name.charAt(0) != '*' && !report(validator, name))
            {
                Logging.errorPrint("Unconstructed Reference: " + getReferenceDescription() + " " + name);
                rm.fireUnconstuctedEvent(value);
                returnGood = false;
            }
            activeObj = rm.buildObject(name);
        }
        value.addResolution(activeObj);
        return returnGood;
    }

    private boolean report(UnconstructedValidator validator, String key)
    {
        return validator != null && validator.allowUnconstructed(getReferenceIdentity(), key);
    }

    @Override
    public boolean populate(ReferenceManufacturer<T> parentCrm, ReferenceManufacturer<T> rm,
            UnconstructedValidator validator)
    {
        // Nothing to do
        return true;
    }

    @Override
    public ManufacturableFactory<T> getParent()
    {
        /*
         * CONSIDER This is a limitation that prevents this from being used for
         * AbilityCategory. Need to figure out if this should really know it's parent and
         * if so, how? (when the problem is that the parentage is really now in
         * ClassIdentity)
         */
        return null;
    }

    @Override
    public ClassIdentity<T> getReferenceIdentity()
    {
        return classIdentity;
    }

    @Override
    public String toString()
    {
        return "CDOMFactory for " + getReferenceIdentity();
    }

    @Override
    public String getPersistentFormat()
    {
        return classIdentity.getPersistentFormat();
    }
}
