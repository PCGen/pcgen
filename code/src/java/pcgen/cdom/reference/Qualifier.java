/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.cdom.base.Loadable;

/**
 * A Qualifier is used to identify a specific instance of a certain type of
 * CDOMObject in order to establish a relationship for that CDOMObject. (This
 * relationship may be automatic qualification, to bypass prerequisites, or may
 * be other relationships in other tokens)
 */
public class Qualifier
{

    /**
     * A reference to the specific instance of the class of object this
     * Qualifier contains.
     */
    private final CDOMSingleRef<? extends Loadable> qualRef;

    /**
     * Constructs a new Qualifier for the given reference to the specific instance of
     * CDOMObject this Qualifier contains.
     *
     * @param ref The reference to the specific instance of CDOMObject this Qualifier
     *            contains
     */
    public Qualifier(CDOMSingleRef<? extends Loadable> ref)
    {
        qualRef = Objects.requireNonNull(ref);
    }

    /**
     * Returns the reference to the specific instance of CDOMObject this
     * Qualifier contains.
     *
     * @return The reference to the specific instance of CDOMObject this
     * Qualifier contains.
     */
    public CDOMSingleRef<? extends Loadable> getQualifiedReference()
    {
        return qualRef;
    }

    @Override
    public int hashCode()
    {
        return qualRef.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Qualifier)
        {
            Qualifier other = (Qualifier) obj;
            return qualRef.equals(other.qualRef);
        }
        return false;
    }

}
