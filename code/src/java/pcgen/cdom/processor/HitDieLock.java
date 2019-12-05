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
package pcgen.cdom.processor;

import java.util.Objects;

import pcgen.cdom.content.HitDie;
import pcgen.cdom.content.Processor;

/**
 * A HitDieLock represents a constrained HitDie that does not change. Since this
 * acts as a Processor, the effect of the application of this Processor is
 * unconditional return of the HitDie object provided at construction of the
 * HitDieLock object.
 */
public class HitDieLock implements Processor<HitDie>
{

    /**
     * The HitDie to which this HitDieLock is "locked"
     */
    private final HitDie hitDie;

    /**
     * Constructs a new HitDieLock which will unconditionally return the given
     * HitDie object.
     *
     * @param die The HitDie for this HitDieLock
     * @throws IllegalArgumentException if the given HitDie is null
     */
    public HitDieLock(HitDie die)
    {
        Objects.requireNonNull(die, "Die for HitDieLock cannot be null");
        hitDie = die;
    }

    /**
     * Applies this Processor by returning the HitDie to which this HitDieLock is
     * set.
     * <p>
     * Since HitDieLock is universal, the given context is ignored.
     *
     * @param origHD  The input HitDie this Processor will act upon
     * @param context The context of this Processor, ignored by HitDieLock.
     * @return The modified object, of the same class as the input object.
     */
    @Override
    public HitDie applyProcessor(HitDie origHD, Object context)
    {
        return hitDie;
    }

    /**
     * Returns a representation of this HitDieLock, suitable for storing in an
     * LST file.
     *
     * @return A representation of this HitDieLock, suitable for storing in an
     * LST file.
     */
    @Override
    public String getLSTformat()
    {
        return Integer.toString(hitDie.getDie());
    }

    /**
     * The class of object this Processor acts upon (HitDie).
     *
     * @return The class of object this Processor acts upon (HitDie.class)
     */
    @Override
    public Class<HitDie> getModifiedClass()
    {
        return HitDie.class;
    }

    @Override
    public int hashCode()
    {
        return hitDie.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof HitDieLock && ((HitDieLock) obj).hitDie.equals(hitDie);
    }

}
