/**
 * Copyright (c) 2008 Andrew Wilson <nuance@users.sourceforge.net>.
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * <p>
 * Created 09-Aug-2008 16:08:40
 */

package pcgen.core.term;

import java.util.Collection;

import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.util.enumeration.View;
import pcgen.util.enumeration.Visibility;

public abstract class BasePCCountAbilitiesTermEvaluator extends BasePCTermEvaluator
{
    protected AbilityCategory abCat;
    protected boolean visible;
    protected boolean hidden;

    abstract Collection<CNAbility> getAbilities(PlayerCharacter pc);

    /**
     * This function takes a list of feats and returns the number of visible,
     * or hidden feats that are in the list The visible flag determines if
     * the result should be the number of hidden feats, or the number of
     * visible feats
     *
     * @param pc      the Character with the abilities
     * @param aList   a list of the feats to look through.
     * @param visible Count visible abilities
     * @param hidden  Count hidden abilities @return  An int containing the number of feats in the list
     * @return the number of matching abilities
     */
    protected Float countVisibleAbilities(PlayerCharacter pc, final Iterable<CNAbility> aList, final boolean visible,
            final boolean hidden)
    {
        Float count = 0.0f;

        for (CNAbility ability : aList)
        {
            count += countVisibleAbility(pc, ability, visible, hidden, true);
        }

        return count;
    }

    /**
     * Count the number of times the character has the ability. This can be
     * limited to either hidden or visible Abilities, and can be limited to only
     * counting once per ability rather than once per time taken (e.g.
     * Weapon Specialisation in two weapons would count as 2 unless the onceOnly
     * flag was true).
     *
     * @param pc       the Character with the abilities
     * @param cna      The feat to be counted.
     * @param visible  Should it be counted if it is visible?
     * @param hidden   Should it be counted if it is hidden?
     * @param onceOnly Should it be counted as one if was taken multiple times?
     * @return The number of occurrences of the ability.
     */
    protected Float countVisibleAbility(PlayerCharacter pc, final CNAbility cna, final boolean visible,
            final boolean hidden, final boolean onceOnly)
    {
        Visibility v = cna.getAbility().getSafe(ObjectKey.VISIBILITY);

        //TODO This is a bug, it assumes export
        boolean abilityInvisibile = v.isVisibleTo(View.HIDDEN_EXPORT);

        int count = 0;

        if (abilityInvisibile)
        {
            if (hidden)
            {
                count += onceOnly ? 1 : Math.max(1, pc.getSelectCorrectedAssociationCount(cna));
            }
        } else
        {
            if (visible)
            {
                count += onceOnly ? 1 : Math.max(1, pc.getSelectCorrectedAssociationCount(cna));
            }
        }

        return (float) count;
    }
}
