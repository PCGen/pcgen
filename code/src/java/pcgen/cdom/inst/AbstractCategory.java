/*
 * Copyright (c) 2016-18 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.inst;

import java.net.URI;

import pcgen.cdom.base.Categorized;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.ClassIdentity;
import pcgen.cdom.reference.CDOMAllRef;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.CDOMSimpleSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.CDOMTypeRef;
import pcgen.cdom.reference.ManufacturableFactory;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.UnconstructedValidator;
import pcgen.util.Logging;

/**
 * An AbstractCategory is designed to facilitate the building of Category objects by
 * sharing common infrastructure and behavior.
 *
 * @param <T> The Class that this AbstractCategory will categorize
 */
public abstract class AbstractCategory<T extends Categorized<T>> implements Category<T>, ManufacturableFactory<T>
{

    /**
     * The name for this AbstractCategory.
     */
    private String categoryName;

    /**
     * The source URI for this AbstractCategory.
     */
    private URI sourceURI;

    @Override
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    public Category<T> getParentCategory()
    {
        //By default, no hierarchy
        return null;
    }

    @Override
    public String getKeyName()
    {
        return categoryName;
    }

    @Override
    public String getDisplayName()
    {
        return categoryName;
    }

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
    public void setName(String name)
    {
        categoryName = name;
    }

    @Override
    public CDOMGroupRef<T> getAllReference()
    {
        return new CDOMAllRef<>(this);
    }

    @Override
    public CDOMGroupRef<T> getTypeReference(String... types)
    {
        return new CDOMTypeRef<>(this, types);
    }

    @Override
    public CDOMSingleRef<T> getReference(String identifier)
    {
        return new CDOMSimpleSingleRef<>(this, identifier);
    }

    @Override
    public boolean isMember(T item)
    {
        return getReferenceClass().equals(item.getClass()) && this.equals(item.getCDOMCategory());
    }

    @Override
    public boolean resolve(ReferenceManufacturer<T> rm, String key, CDOMSingleRef<T> reference,
            UnconstructedValidator validator)
    {
        boolean returnGood = true;
        T activeObj = rm.getObject(key);
        if (activeObj == null)
        {
            // Wasn't constructed!
            if (key.charAt(0) != '*' && reportUnconstructed(validator, key))
            {
                Logging.errorPrint("Unconstructed Reference: " + getReferenceDescription() + " " + key);
                rm.fireUnconstuctedEvent(reference);
                returnGood = false;
            }
            activeObj = rm.buildObject(key);
        }
        reference.addResolution(activeObj);
        return returnGood;
    }

    //Identify if an item needs to be reported as unconstructed
    private boolean reportUnconstructed(UnconstructedValidator validator, String key)
    {
        return (validator == null) || !validator.allowUnconstructed(getReferenceIdentity(), key);
    }

    @Override
    public boolean populate(ReferenceManufacturer<T> parentCrm, ReferenceManufacturer<T> rm,
            UnconstructedValidator validator)
    {
        // Nothing to do (for now!)
        return true;
    }

    @Override
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    public ManufacturableFactory<T> getParent()
    {
        return null;
    }

    @Override
    public ClassIdentity<T> getReferenceIdentity()
    {
        return this;
    }

    @Override
    public String getName()
    {
        return getKeyName();
    }

    @Override
    public String toString()
    {
        return categoryName;
    }

    @Override
    public int hashCode()
    {
        return categoryName.hashCode() ^ getReferenceClass().hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (getClass().equals(o.getClass()))
        {
            AbstractCategory<?> other = (AbstractCategory<?>) o;
            return categoryName.equals(other.categoryName);
        }
        return false;
    }
}
