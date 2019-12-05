/*
 * Copyright 2009 (C) Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.cdom.base.ClassIdentity;

/**
 * An UnconstructedValidator indicates what behaviors are allowed for a given
 * Class/ClassIdentity for things like unconstructed references and duplicates.
 */
public interface UnconstructedValidator
{
    /**
     * Returns true if the given Class allows duplicate objects to exist.
     *
     * @param objClass The Class to be checked
     * @return true if the given Class allows duplicate objects to exist; false otherwise
     */
    boolean allowDuplicates(Class<?> objClass);

    /**
     * Returns true if the given key for the given ClassIdentity is allowed to be
     * unconstructed.
     *
     * @param identity The ClassIdentity of the object key to be validated
     * @param key      The key of the object to be checked to see if it is allowed to remain
     *                 unconstructed
     * @return true if the given key for the given ClassIdentity is allowed to be
     * unconstructed; false otherwise
     */
    <T> boolean allowUnconstructed(ClassIdentity<T> identity, String key);
}
