/*
 * Copyright 2006 (C) Tom Parker <thpr@users.sourceforge.net>
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
 *
 *
 */
package pcgen.cdom.choiceset;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.core.PlayerCharacter;

/**
 * A SimpleChoiceSet contains Objects
 * <p>
 * The contents of a SimpleChoiceSet is defined at construction of the
 * SimpleChoiceSet. The contents of a SimpleChoiceSet is fixed, and will not
 * vary by the PlayerCharacter used to resolve the SimpleChoiceSet.
 *
 * @param <T> The class of object this SimpleChoiceSet contains.
 */
public class SimpleChoiceSet<T> implements PrimitiveChoiceSet<T>
{

    /**
     * The Comparator to use for this SimpleChoiceSet.
     */
    private final Comparator<? super T> comparator;

    /**
     * The underlying Set of objects in this SimpleChoiceSet
     */
    private final Set<T> set;

    /**
     * The separator
     */
    private final String separator;

    /**
     * Constructs a new SimpleChoiceSet which contains the Set of objects.
     * <p>
     * This constructor is both reference-semantic and value-semantic. Ownership
     * of the Collection provided to this constructor is not transferred and
     * this constructor will not modify the given Collection. Modification of
     * the Collection (after this constructor completes) does not result in
     * modifying the SimpleChoiceSet, and the SimpleChoiceSet will not modify
     * the given Collection. However, this SimpleChoiceSet will maintain hard
     * references to the objects contained within the given Collection.
     *
     * @param col A Collection of objects contained within the SimpleChoiceSet
     * @throws IllegalArgumentException if the given Collection is null or empty.
     */
    public SimpleChoiceSet(Collection<? extends T> col)
    {
        this(col, null, null);
    }

    /**
     * Constructs a new SimpleChoiceSet which contains the Set of objects.
     * <p>
     * This constructor is both reference-semantic and value-semantic. Ownership
     * of the Collection provided to this constructor is not transferred and
     * this constructor will not modify the given Collection. Modification of
     * the Collection (after this constructor completes) does not result in
     * modifying the SimpleChoiceSet, and the SimpleChoiceSet will not modify
     * the given Collection. However, this SimpleChoiceSet will maintain hard
     * references to the objects contained within the given Collection.
     *
     * @param col A Collection of objects contained within the SimpleChoiceSet
     * @throws IllegalArgumentException if the given Collection is null or empty.
     */
    public SimpleChoiceSet(Collection<? extends T> col, String separator)
    {
        this(col, null, separator);
    }

    /**
     * Constructs a new SimpleChoiceSet which contains the Set of objects and
     * uses the given Comparator to sort the objects.
     * <p>
     * This constructor is both reference-semantic and value-semantic. Ownership
     * of the Collection provided to this constructor is not transferred and
     * this constructor will not modify the given Collection. Modification of
     * the Collection (after this constructor completes) does not result in
     * modifying the SimpleChoiceSet, and the SimpleChoiceSet will not modify
     * the given Collection. However, this SimpleChoiceSet will maintain hard
     * references to the objects contained within the given Collection and the
     * given Comparator.
     *
     * @param col  A Collection of objects contained within the SimpleChoiceSet
     * @param comp A Comparator used to compare the objects in this
     *             SimpleChoiceSet
     * @throws IllegalArgumentException if the given Collection is null or empty.
     */
    public SimpleChoiceSet(Collection<? extends T> col, Comparator<? super T> comp)
    {
        this(col, comp, null);
    }

    /**
     * Constructs a new SimpleChoiceSet which contains the Set of objects and
     * uses the given Comparator to sort the objects.
     * <p>
     * This constructor is both reference-semantic and value-semantic. Ownership
     * of the Collection provided to this constructor is not transferred and
     * this constructor will not modify the given Collection. Modification of
     * the Collection (after this constructor completes) does not result in
     * modifying the SimpleChoiceSet, and the SimpleChoiceSet will not modify
     * the given Collection. However, this SimpleChoiceSet will maintain hard
     * references to the objects contained within the given Collection and the
     * given Comparator.
     *
     * @param col  A Collection of objects contained within the SimpleChoiceSet
     * @param comp A Comparator used to compare the objects in this
     *             SimpleChoiceSet
     * @throws IllegalArgumentException if the given Collection is null or empty.
     */
    public SimpleChoiceSet(Collection<? extends T> col, Comparator<? super T> comp, String sep)
    {
        Objects.requireNonNull(col, "Choice Collection cannot be null");
        if (col.isEmpty())
        {
            throw new IllegalArgumentException("Choice Collection cannot be empty");
        }
        set = new LinkedHashSet<>(col);
        if (set.size() != col.size())
        {
            throw new IllegalArgumentException("Choice Collection cannot possess a duplicate item");
        }
        comparator = comp;
        separator = (sep == null) ? Constants.COMMA : sep;
    }

    /**
     * Returns a representation of this SimpleChoiceSet, suitable for storing in
     * an LST file.
     *
     * @param useAny use "ANY" for the global "ALL" reference when creating the LST
     *               format
     * @return A representation of this SimpleChoiceSet, suitable for storing in
     * an LST file.
     */
    @Override
    public String getLSTformat(boolean useAny)
    {
        Set<T> sortingSet;
        try
        {
            sortingSet = new TreeSet<>(comparator);
            sortingSet.addAll(set);
        } catch (ClassCastException cce)
        {
            sortingSet = set;
        }
        return StringUtil.join(sortingSet, separator);
    }

    /**
     * The class of object this SimpleChoiceSet contains.
     *
     * @return The class of object this SimpleChoiceSet contains.
     */
    @Override
    public Class<T> getChoiceClass()
    {
        return (Class<T>) (set == null ? null : set.iterator().next().getClass());
    }

    /**
     * Returns a Set containing the Objects which this SimpleChoiceSet contains.
     * The contents of a SimpleChoiceSet is fixed, and will not vary by the
     * PlayerCharacter used to resolve the SimpleChoiceSet.
     * <p>
     * Ownership of the Set returned by this method will be transferred to the
     * calling object. Modification of the returned Set should not result in
     * modifying the SimpleChoiceSet, and modifying the SimpleChoiceSet after
     * the Set is returned should not modify the Set. However, the objects
     * contained within the set are transferred by reference, so modification of
     * the objects contained in the Set will result in modification of the
     * objects within this SimpleChoiceSet.
     *
     * @param pc The PlayerCharacter for which the choices in this
     *           SimpleChoiceSet should be returned.
     * @return A Set containing the Objects which this SimpleChoiceSet contains.
     */
    @Override
    public Set<T> getSet(PlayerCharacter pc)
    {
        return new HashSet<>(set);
    }

    @Override
    public int hashCode()
    {
        return set.size();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof SimpleChoiceSet)
        {
            SimpleChoiceSet<?> other = (SimpleChoiceSet<?>) obj;
            return set.equals(other.set);
        }
        return false;
    }

    /**
     * Returns the GroupingState for this SimpleChoiceSet. The GroupingState
     * indicates how this SimpleChoiceSet can be combined with other
     * PrimitiveChoiceSets.
     *
     * @return The GroupingState for this SimpleChoiceSet.
     */
    @Override
    public GroupingState getGroupingState()
    {
        return GroupingState.ANY;
    }
}
