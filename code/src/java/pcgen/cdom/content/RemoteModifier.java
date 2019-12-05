/*
 * Copyright 2015 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.content;

import java.util.Objects;

import pcgen.cdom.grouping.GroupingCollection;

/**
 * A RemoteModifier is a container for all the information necessary to modify a
 * remote variable.
 * <p>
 * This includes a VarModifier (containing the scope, the variable name, and the
 * Modifier to be applied) as well as an GroupingCollection (indicating the items to
 * which this RemoteModifier should be applied).
 * <p>
 * This allows that grouping of information to be passed as a single unit of
 * information.
 *
 * @param <MT> The format of the variable modified by the Modifier in this
 *             VarModifier
 */
public class RemoteModifier<MT>
{

    /**
     * This is an empty array of RemoteModifier objects, available for use by a
     * VarContainer.
     */
    public static final RemoteModifier<?>[] EMPTY_REMOTEMODIFIER = new RemoteModifier[0];

    /**
     * The VarModifier indicating the variable to which the Modifier should be
     * applied.
     */
    private final VarModifier<MT> varModifier;

    /**
     * The GroupingCollection indicating the objects upon which this RemoteModifier
     * should be applied.
     */
    private final GroupingCollection<?> grouping;

    /**
     * Constructs a new RemoteModifier from the given ObjectContainer and
     * VarModifier.
     *
     * @param grouping the objects upon which this RemoteModifier should be applied
     * @param modifier the variable to which the Modifier should be applied
     */
    public RemoteModifier(GroupingCollection<?> grouping, VarModifier<MT> modifier)
    {
        this.grouping = Objects.requireNonNull(grouping);
        this.varModifier = Objects.requireNonNull(modifier);
    }

    /**
     * Returns the GroupingCollection of objects to which this RemoteModifier should
     * be applied.
     *
     * @return the GroupingCollection of objects to which this RemoteModifier should
     * be applied
     */
    public GroupingCollection<?> getGrouping()
    {
        return grouping;
    }

    /**
     * Returns the VarModifier indicating the change that should be made by this
     * RemoteModifier.
     *
     * @return The VarModifier indicating the change that should be made by this
     * RemoteModifier
     */
    public VarModifier<MT> getVarModifier()
    {
        return varModifier;
    }
}
