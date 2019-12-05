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
package pcgen.cdom.reference;

import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.primitive.PrimitiveUtilities;

/**
 * ReferenceUtilities is a utility class designed to provide utility methods
 * when working with pcgen.cdom.base.CDOMReference Objects
 */
public final class ReferenceUtilities
{

    /**
     * A COLLATOR used to sort Strings in a locale-aware method.
     */
    private static final Collator COLLATOR = Collator.getInstance();

    /**
     * A Comparator to consistently sort CDOMReference objects. This is done
     * using the ReferenceUtilities.compareRefs method.
     */
    public static final Comparator<CDOMReference<?>> REFERENCE_SORTER = ReferenceUtilities::compareRefs;

    private ReferenceUtilities()
    {
        // Cannot construct utility class
    }

    /**
     * Concatenates the LST format of the given Collection of CDOMReference
     * objects into a String using the separator as the delimiter.
     * <p>
     * The LST format for each CDOMReference is determined by calling the
     * getLSTformat() method on the CDOMReference.
     * <p>
     * The items will be joined in the order determined by the ordering of the
     * given Collection.
     *
     * @param refCollection A Collection of CDOMReference objects
     * @param separator     The separating string
     * @return A 'separator' separated String containing the LST format of the
     * given Collection of CDOMReference objects
     */
    public static String joinLstFormat(Collection<? extends CDOMReference<?>> refCollection, String separator)
    {
        return PrimitiveUtilities.joinLstFormat(refCollection, separator, false);
    }

    /**
     * Concatenates the Display Name of the contents of the given Collection of
     * CDOMReference objects into a String using the separator as the delimiter.
     * <p>
     * Each CDOMReference in the given Collection is expanded to the contained
     * objects, and each of those contained CDOMObjects has getDisplayName()
     * called to establish the Display Name of the CDOMObject.
     * <p>
     * The LST format for each CDOMReference is determined by calling the
     * getLSTformat() method on the CDOMReference.
     * <p>
     * The items will be joined in the order determined by the ordering of the
     * given Collection and the getContainedObjects() method of the
     * CDOMReferences contained in the given Collection.
     *
     * @param refCollection A Collection of CDOMReference objects
     * @param separator     The separating string
     * @return A 'separator' separated String containing the Display Name of the
     * given CDOMObjects contained within the given Collection of
     * CDOMReference objects
     */
    public static String joinDisplayFormat(Collection<? extends CDOMReference<? extends CDOMObject>> refCollection,
            String separator)
    {
        if (refCollection == null)
        {
            return "";
        }

        Set<String> resultSet = new TreeSet<>();
        for (CDOMReference<? extends CDOMObject> ref : refCollection)
        {
            for (CDOMObject obj : ref.getContainedObjects())
            {
                resultSet.add(obj.getDisplayName());
            }
        }

        return StringUtil.join(resultSet, separator);
    }

    /**
     * Compares two CDOMReference objects to establish order. The primary
     * purpose of this is to establish a consistent order across different
     * hashing/ordering algorithms of various lists/sets that may contain
     * CDOMReference objects.
     * <p>
     * This method is compatible with (although not strictly adherent to the
     * consistent-with-equals conditions of) the Comparable interface, in that
     * it returns 0 if the CDOMReference objects are equal (at least in name,
     * may not be consistent-with-equals), less than zero if the first given
     * CDOMReference should be sorted before the second, and greater than zero
     * if the first given CDOMReference should be sorted after the second.
     *
     * @param ref1 The first CDOMReference to be compared
     * @param ref2 The second CDOMReference to be compared
     * @return 0 if the CDOMReference objects are equal (at least in name, may
     * not be consistent-with-equals), less than zero if the first given
     * CDOMReference should be sorted before the second, and greater
     * than zero if the first given CDOMReference should be sorted after
     * the second.
     */
    public static int compareRefs(CDOMReference<?> ref1, CDOMReference<?> ref2)
    {
        if (ref1 instanceof CDOMSingleRef)
        {
            if (!(ref2 instanceof CDOMSingleRef))
            {
                return -1;
            }
            return COLLATOR.compare(ref1.getName(), ref2.getName());
        }
        if (ref2 instanceof CDOMSingleRef)
        {
            return 1;
        }
        return COLLATOR.compare(ref1.getName(), ref2.getName());
    }

    /**
     * Concatenates the LST format of the given Collection of CDOMReference
     * objects into a String using the separator as the delimiter. This is a
     * secondary method that joins using ANY for the global reference rather
     * than ALL
     * <p>
     * The LST format for each CDOMReference is determined by calling the
     * getLSTformat() method on the CDOMReference.
     * <p>
     * The items will be joined in the order determined by the ordering of the
     * given Collection.
     *
     * @param refCollection An Collection of CDOMReference objects
     * @param separator     The separating string
     * @param useAny        use "ANY" for the global "ALL" reference when creating the LST
     *                      format
     * @return A 'separator' separated String containing the LST format of the
     * given Collection of CDOMReference objects
     */
    public static String joinLstFormat(Collection<? extends CDOMReference<?>> refCollection, String separator,
            boolean useAny)
    {
        if (refCollection == null)
        {
            return "";
        }

        final StringBuilder result = new StringBuilder(refCollection.size() * 10);

        boolean needjoin = false;

        for (CDOMReference<?> obj : refCollection)
        {
            if (needjoin)
            {
                result.append(separator);
            }
            needjoin = true;
            result.append(obj.getLSTformat(useAny));
        }

        return result.toString();
    }
}
