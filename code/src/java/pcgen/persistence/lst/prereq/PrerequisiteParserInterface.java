/*
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 *
 */
package pcgen.persistence.lst.prereq;

import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;

/**
 * Interface to the Prerequisite parser. Each Prerequisite
 * is parsed by a separate class, each class implements this
 * interface.
 * <p>
 * Each class implementing this interface will parse 1 or more
 * prerequisite types. Normally if an implementation handles more
 * than 1 type the types will be very similar (i.e. a single
 * implementation might handle PRESTAT, PRESTATGTEQ and PRESTATNEQ
 * each of which differs only by the operator).
 */
public interface PrerequisiteParserInterface
{
    /**
     * @return An array of Strings each of which defines a type
     * of prerequisite that the parser will parse.
     */
    String[] kindsHandled();

    /**
     * Parses the.
     *
     * @param kind            the kind of the prerequisite (less the "PRE" prefix)
     * @param formula         The body of the prerequisite;
     * @param invertResult    If the prerequisite should invert the result
     *                        before it is returned
     * @param overrideQualify the override qualify
     * @return Returns a Prerequisite instance containing the parsed contents
     * of the input string "value". If the input could not be parsed
     * for any reason a PersistenceLayerException will be thrown.
     * @throws PersistenceLayerException the persistence layer exception
     */
    Prerequisite parse(String kind, String formula, boolean invertResult, boolean overrideQualify)
            throws PersistenceLayerException;
}
