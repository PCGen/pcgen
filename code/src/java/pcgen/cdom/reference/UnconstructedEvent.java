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

import java.util.EventObject;
import java.util.Objects;

import pcgen.cdom.base.CDOMReference;

/**
 * An UnconstructedEvent is issued when an unconstruted reference has been found
 * during data load, and was not permitted by an UnconstructedValidator.
 */
public class UnconstructedEvent extends EventObject
{
    /**
     * The CDOMReference that was found to be unconstructed.
     */
    private final CDOMReference<?> reference;

    /**
     * Creates a new UnconstructedEvent, triggered by the given source, and
     * referring to the given CDOMReference.
     *
     * @param source The source for this UnconstructedEvent
     * @param ref    The CDOMReference to which this UnconstructedEvent refers (the
     *               CDOMReference that does not have an underlying constructed
     *               object)
     */
    public UnconstructedEvent(Object source, CDOMReference<?> ref)
    {
        super(source);
        Objects.requireNonNull(ref, "UnconstructedEvent cannot be null");
        reference = ref;
    }

    /**
     * Returns the CDOMReference that this UnconstructedEvent is reporting as
     * unconstructed.
     *
     * @return The CDOMReference that this UnconstructedEvent is reporting as
     * unconstructed.
     */
    public CDOMReference<?> getReference()
    {
        return reference;
    }

}
