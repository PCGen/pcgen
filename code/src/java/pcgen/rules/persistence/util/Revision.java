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
package pcgen.rules.persistence.util;

/**
 * A Revision represents a version number of PCGen (e.g. 6.5.1)
 */
public class Revision implements Comparable<Revision>
{
    /**
     * The primary/major version number
     */
    private final int primarySequence;

    /**
     * The secondary/minor version number
     */
    private final int secondarySequence;

    /**
     * The tertiary/patch version number
     */
    private final int tertiarySequence;

    /**
     * Constructs a new Revision with the given major, minor, and patch version
     * numbers
     *
     * @param major The major version number of this Revision
     * @param minor The minor version number of this Revision
     * @param patch The patch version number of this Revision
     */
    public Revision(int major, int minor, int patch)
    {
        primarySequence = major;
        secondarySequence = minor;
        tertiarySequence = patch;
    }

    @Override
    public int compareTo(Revision r)
    {
        if (primarySequence > r.primarySequence)
        {
            return -1;
        } else if (primarySequence < r.primarySequence)
        {
            return 1;
        } else if (secondarySequence > r.secondarySequence)
        {
            return -1;
        } else if (secondarySequence < r.secondarySequence)
        {
            return 1;
        } else if (tertiarySequence > r.tertiarySequence)
        {
            return -1;
        } else if (tertiarySequence < r.tertiarySequence)
        {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString()
    {
        return primarySequence + "." + secondarySequence + '-' + tertiarySequence;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj == this || obj instanceof Revision && compareTo((Revision) obj) == 0;
    }

    @Override
    public int hashCode()
    {
        return primarySequence * secondarySequence + tertiarySequence;
    }

}
