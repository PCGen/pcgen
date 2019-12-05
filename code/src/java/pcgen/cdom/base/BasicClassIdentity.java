/*
 * Copyright 2012-18 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.base;

import java.util.Objects;

/**
 * A BasicClassIdentity is a ClassIdentity designed to wrap a Class. This has no further
 * information designed to be considered or loaded into the object at construction.
 *
 * @param <T> The Format (Class) of the object represented by this ClassIdentity.
 */
public class BasicClassIdentity<T> implements ClassIdentity<T>
{

    /**
     * The underlying Class for this BasicClassIdentity. This is what the
     * BasicClassIdentity represents.
     */
    private final Class<T> underlyingClass;

    /**
     * Constructs a new BasicClassIdentity for the given Class.
     *
     * @param cl The Class for which a BasicClassIdentity should be constructed
     */
    public BasicClassIdentity(Class<T> cl)
    {
        /*
         * CONSIDER In theory, a Categorized object isn't great if it lands here, but we
         * have the challenge of an initial load where the CATEGORY is a separate token.
         * So that will now happen and can't be an error condition.
         *
         * It might be best if a specific file can't have more than one CATEGORY (and thus
         * CATEGORY can't be reassigned), although that could also be annoying to the data
         * team. Needs further evaluation after Dynamic is fully used in new data and we
         * see just how many abilities there really are...
         *
         * An alternative is to have a prefix on the line in an ABILITY file that has the
         * Category (much like Dynamic objects) rather than having it as a separate token.
         * Either way, both this situation as well as the whole "can reassign a CATEGORY"
         * are unique exceptions that it would be nice to get rid of someday.
         */
        underlyingClass = Objects.requireNonNull(cl);
        try
        {
            underlyingClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e)
        {
            throw new IllegalArgumentException(
                    "Class " + cl.getCanonicalName() + " for BasicClassIdentity must have public zero argument constructor",
                    e);
        }
    }

    @Override
    public String getName()
    {
        return underlyingClass.getSimpleName();
    }

    @Override
    public Class<T> getReferenceClass()
    {
        return underlyingClass;
    }

    @Override
    public T newInstance()
    {
        try
        {
            return underlyingClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e)
        {
            //InternalError due to check in constructor
            throw new InternalError(e);
        }
    }

    @Override
    public String getReferenceDescription()
    {
        return underlyingClass.getSimpleName();
    }

    @Override
    public boolean isMember(T item)
    {
        //TODO Does this fail for SubClass? Do we care?
        return underlyingClass.equals(item.getClass());
    }

    @Override
    public String toString()
    {
        return "Identity: " + getReferenceDescription();
    }

    @Override
    public int hashCode()
    {
        return underlyingClass.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof BasicClassIdentity)
        {
            BasicClassIdentity<?> other = (BasicClassIdentity<?>) o;
            return underlyingClass.equals(other.underlyingClass);
        }
        return false;
    }

    /**
     * Returns a BasicClassIdentity for the given Class.
     *
     * @param cl The Class for which a BasicClassIdentity should be returned
     */
    public static <T> ClassIdentity<T> getIdentity(Class<T> cl)
    {
        return new BasicClassIdentity<>(cl);
    }

    @Override
    public String getPersistentFormat()
    {
        return getName().toUpperCase();
    }
}
