/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.grouping;

import java.util.function.Consumer;

import pcgen.cdom.formula.PCGenScoped;

/**
 * A GroupingCollection is a representation of a set of objects.
 * <p>
 * This set can be deep in that the primary FormatManager for the GroupingCollection may
 * be Equipment, but the processAs method may return an EquipmentHead.
 *
 * @param <T> The format of the object that this GroupingCollection can analyze.
 */
public interface GroupingCollection<T>
{

    /**
     * Returns a String identifying the value used for determining the members of the
     * identified group.
     *
     * @return A String identifying the value used for determining the members of the
     * identified group
     */
    String getInstructions();

    /**
     * Runs the given Consumer on any relevant objects in this GroupingCollection.
     *
     * @param target   the PCGenScoped object to check to see if it, or any child, is valid
     * @param consumer The Consumer to be run on any relevant objects in this
     *                 GroupingCollection
     */
    void process(PCGenScoped target, Consumer<PCGenScoped> consumer);
}
