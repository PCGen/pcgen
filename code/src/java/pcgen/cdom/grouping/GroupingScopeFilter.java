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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import pcgen.base.formula.base.LegalScope;
import pcgen.cdom.formula.PCGenScoped;
import pcgen.cdom.formula.scope.PCGenScope;

/**
 * A GroupingScopeFilter decorates a GroupingCollection in order to filter out objects
 * based on their scope.
 * <p>
 * As context: In general, the grouping objects returned when analyzing something like
 * GROUP=x are naive to their surroundings. They simply check for a GROUP= or a key, they
 * do not check that that actual object they are checking is the correct type of object
 * (Race, Skill, whatever). This could, of course, produce some nasty false positives.
 * This filter enforces that larger contract (ensuring the appropriate scope) so that
 * combined with the underlying GroupingCollection, one has both objects limited to a
 * specific scope (the first argument of MODIFYOTHER, as enforced by this filter) as well
 * as those that meet the appropriate grouping (the second argument of MODIFYOTHER, as
 * enforced by the underlying GroupingCollection).
 *
 * @param <T> The class of the object being stored in this GroupingScopeFilter
 */
public class GroupingScopeFilter<T> implements GroupingCollection<T>
{
    /**
     * The underlying GroupingCollection that is responsible for filtering based on the
     * group instructions.
     */
    private final GroupingCollection<T> underlying;

    /**
     * The scope name of objects that this GroupingScopeFilter should be allowed to
     * impact; Any items in this scope will be passed to the underlying GroupingCollection
     * for processing.
     */
    private final String scopeName;

    /**
     * Constructs a new GroupingScopeFilter for the given scope and underlying
     * GroupingCollection.
     *
     * @param scope    The PCGenScope of objects that this GroupingCollection should process
     * @param grouping The underlying GroupingCollection to process objects that are in the
     *                 given PCGenScope
     */
    public GroupingScopeFilter(PCGenScope scope, GroupingCollection<T> grouping)
    {
        this.scopeName = LegalScope.getFullName(scope);
        this.underlying = Objects.requireNonNull(grouping);
    }

    @Override
    public String getInstructions()
    {
        return underlying.getInstructions();
    }

    @Override
    public void process(PCGenScoped target, Consumer<PCGenScoped> consumer)
    {
        Optional<String> localScopeName = target.getLocalScopeName();
        if (localScopeName.isPresent() && localScopeName.get().equalsIgnoreCase(scopeName))
        {
            underlying.process(target, consumer);
        }
    }

}
