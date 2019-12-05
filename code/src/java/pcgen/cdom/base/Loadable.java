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
package pcgen.cdom.base;

import java.net.URI;

/**
 * A Loadable is an object that PCGen can load from its persistent file storage (generally "LST" files).
 */
public interface Loadable extends Identified
{

    /**
     * Sets the name of the Loadable to the given name.
     *
     * @param name The name to be given to the Loadable
     */
    void setName(String name);

    /**
     * Returns the source URI for this Loadable.
     *
     * @return The source URI for this Loadable
     */
    URI getSourceURI();

    /**
     * Sets the source URI for this Loadable.
     *
     * @param source The source URI for this Loadable
     */
    void setSourceURI(URI source);

    /**
     * Returns true if this object is "internal" (meaning PCGen creates it and it doesn't
     * need to be persisted to a set of saved LST data).
     *
     * @return true if this object is "internal"; false otherwise
     */
    boolean isInternal();

    /**
     * Returns true if the object is of the given type.
     * <p>
     * Note that the given String may contain a TYPE= or TYPE. prefix (which will be
     * discarded) and can contain more than one type (separated by periods '.'). If more
     * than one type is provided, this Loadable must be of ALL of the given types for this
     * method to return true.
     *
     * @param type The type to be checked to see if this Loadable is of the given Type
     * @return true if the object is of the given type; false otherwise.
     */
    boolean isType(String type);

    /**
     * Returns the ClassIdentity of this Loadable.
     *
     * @return The ClassIdentity representing this Loadable
     */
    default ClassIdentity<? extends Loadable> getClassIdentity()
    {
        return BasicClassIdentity.getIdentity(getClass());
    }

}
