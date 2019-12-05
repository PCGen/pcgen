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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import pcgen.base.util.CaseInsensitiveMap;
import pcgen.cdom.base.BasicClassIdentity;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Categorized;
import pcgen.cdom.base.ClassIdentity;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.reference.CDOMFactory;
import pcgen.cdom.reference.ManufacturableFactory;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.SimpleReferenceManufacturer;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;
import pcgen.util.StringPClassUtil;

public class RuntimeReferenceContext extends AbstractReferenceContext
{
    @SuppressWarnings("rawtypes")
    private static final Class<Categorized> CATEGORIZED_CLASS = Categorized.class;

    private final Map<ClassIdentity<?>, ReferenceManufacturer<?>> map = new HashMap<>();

    private final CaseInsensitiveMap<ClassIdentity<?>> nameMap = new CaseInsensitiveMap<>();

    protected RuntimeReferenceContext()
    {
    }

    @Override
    public void initialize()
    {
        super.initialize();
        StringPClassUtil.getBaseClasses()
                .forEach(this::importCDOMToFormat);
    }

    @Override
    public <T extends Loadable> ReferenceManufacturer<T> getManufacturer(Class<T> cl)
    {
        Objects.requireNonNull(cl);
        if (Categorized.class.isAssignableFrom(cl))
        {
            throw new InternalError(cl + " is categorized but was fetched without a category");
        }
        ClassIdentity<T> identity = BasicClassIdentity.getIdentity(cl);
        return getManufacturerId(identity);
    }

    @Override
    public <T extends Loadable> ReferenceManufacturer<T> getManufacturerId(ClassIdentity<T> identity)
    {
        Objects.requireNonNull(identity);
        @SuppressWarnings("unchecked")
        ReferenceManufacturer<T> mfg = (ReferenceManufacturer<T>) map.get(identity);
        if (mfg == null)
        {
            mfg = constructReferenceManufacturer(identity);
            map.put(identity, mfg);
            nameMap.put(identity.getPersistentFormat(), identity);
        }
        return mfg;
    }

    @Override
    protected <T extends Loadable> ReferenceManufacturer<T> constructReferenceManufacturer(ClassIdentity<T> identity)
    {
        Objects.requireNonNull(identity);
        //TODO Need a special case here for Ability?!? YUCK
        if (identity instanceof ManufacturableFactory)
        {
            return new SimpleReferenceManufacturer<>((ManufacturableFactory<T>) identity);
        }
        return new SimpleReferenceManufacturer<>(new CDOMFactory<>(identity));
    }

    @Override
    public Collection<ReferenceManufacturer<?>> getAllManufacturers()
    {
        List<ReferenceManufacturer<?>> list = new ArrayList<>(map.values());
        list.sort(IdentitySorter);
        return list;
    }

    /**
     * This implements a Comparator used for a Sorter of ClassIdentity. Note that this
     * Comparator is NOT CONSISTENT WITH EQUALS. It is designed solely to sort items with
     * categories to the end of the list, so that things they depend upon (such as
     * AbilityCategory) are resolved first.
     */
    private static final Comparator<ReferenceManufacturer<?>> IdentitySorter = Comparator.comparing(
            referenceManufacturer -> CATEGORIZED_CLASS.isAssignableFrom(
                    referenceManufacturer.getReferenceIdentity().getReferenceClass())
    );

    @Override
    public <T extends Loadable> ReferenceManufacturer<T> getManufacturerFac(ManufacturableFactory<T> factory)
    {
        ClassIdentity<T> identity = factory.getReferenceIdentity();
        @SuppressWarnings("unchecked")
        ReferenceManufacturer<T> rm = (ReferenceManufacturer<T>) map.get(identity);
        if (rm == null)
        {
            rm = new SimpleReferenceManufacturer<>(factory);
            map.put(identity, rm);
            nameMap.put(identity.getPersistentFormat(), identity);
        }
        return rm;
    }

    /**
     * This method will perform a single .COPY operation.
     *
     * @param object   the object to copy
     * @param copyName String name of the target object
     */
    @Override
    <T extends CDOMObject> T performCopy(T object, String copyName)
    {
        try
        {
            T clone = (T) object.clone();
            clone.setName(copyName);
            clone.put(StringKey.KEY_NAME, copyName);
            importObject(clone);
            return clone;
        } catch (CloneNotSupportedException e)
        {
            String message = LanguageBundle.getFormattedString("Errors.LstFileLoader.CopyNotSupported", //$NON-NLS-1$
                    object.getClass().getName(), object.getKeyName(), copyName);
            Logging.errorPrint(message);
        }
        return null;
    }

    @Override
    public <T extends CDOMObject> T performMod(T obj)
    {
        return obj;
    }

    @Override
    public <T extends Loadable> boolean hasManufacturer(ClassIdentity<T> cl)
    {
        return map.containsKey(cl);
    }

    /**
     * Return a new RuntimeReferenceContext. This ReferenceContext is initialized as per
     * the rules of AbstractReferenceContext.
     *
     * @return A new RuntimeReferenceContext
     */
    public static RuntimeReferenceContext createRuntimeReferenceContext()
    {
        RuntimeReferenceContext context = new RuntimeReferenceContext();
        context.initialize();
        return context;
    }

    @Override
    public <T extends Loadable> ReferenceManufacturer<T> getManufacturerByFormatName(String formatName,
            Class<T> refClass)
    {
        Objects.requireNonNull(refClass);
        Objects.requireNonNull(formatName);
        ClassIdentity<?> identity = nameMap.get(formatName);
        if (identity == null)
        {
            /*
             * CONSIDER This is risky, but supportable for now. In practice, nothing can
             * break this, but that is only due to luck. Longer term, we really need to be
             * able to understand how to convert a String into a ClassIdentity, but right
             * now we have that post-hoc based on the ReferenceManufacturer objects that
             * have already been created. (To some degree, there are items that will
             * ALWAYS be post-hoc due to data driven items like DYNAMIC, so maybe this
             * isn't fatal, there will always be order of operations risk here, it's just
             * an understanding of how things have to work)
             */
            return null;
        }
        return (ReferenceManufacturer<T>) map.get(identity);
    }
}
