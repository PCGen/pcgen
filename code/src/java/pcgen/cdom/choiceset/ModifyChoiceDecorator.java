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
package pcgen.cdom.choiceset;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;

/**
 * A ModifyChoiceDecorator is a PrimitiveChoiceSet that returns the MULT:YES
 * Feats that are possessed by the PlayerCharacter. This is a special case
 * PrimitiveChoiceSet for the MODIFYFEATCHOICE token.
 */
public class ModifyChoiceDecorator implements PrimitiveChoiceSet<CNAbility>
{

    /**
     * The starting set from which to select objects. This is the
     * PrimitiveChoiceSet that identifies the Ability objects (Feats, really)
     * that were listed in the LST file.
     */
    private final PrimitiveChoiceSet<Ability> pcs;

    /**
     * Constructs a new ModifyChoiceDecorator with the given underlying
     * PrimitiveChoiceSet.
     *
     * @param underlyingSet The PrimitiveChoiceSet that identifies the Feats that were
     *                      listed in the LST file.
     */
    public ModifyChoiceDecorator(PrimitiveChoiceSet<Ability> underlyingSet)
    {
        pcs = underlyingSet;
    }

    /**
     * The class of object this ModifyChoiceDecorator contains (Ability.class)
     *
     * @return The class of object this ModifyChoiceDecorator contains.
     */
    @Override
    public Class<? super CNAbility> getChoiceClass()
    {
        return CNAbility.class;
    }

    /**
     * Returns a representation of this ModifyChoiceDecorator, suitable for
     * storing in an LST file.
     *
     * @param useAny use "ANY" for the global "ALL" reference when creating the LST
     *               format
     * @return A representation of this ModifyChoiceDecorator, suitable for
     * storing in an LST file.
     */
    @Override
    public String getLSTformat(boolean useAny)
    {
        return pcs.getLSTformat(useAny);
    }

    /**
     * Returns a Set containing the Objects which this ModifyChoiceDecorator
     * contains and which are also possessed by the PlayerCharacter.
     * <p>
     * It is intended that classes which implement ModifyChoiceDecorator will
     * make this method value-semantic, meaning that ownership of the Set
     * returned by this method will be transferred to the calling object.
     * Modification of the returned Set will not result in modifying the
     * ModifyChoiceDecorator (and vice versa since the ModifyChoiceDecorator is
     * near immutable)
     *
     * @param pc The PlayerCharacter for which the choices in this
     *           ModifyChoiceDecorator should be returned.
     * @return A Set containing the Objects which this ModifyChoiceDecorator
     * contains and which are also possessed by the PlayerCharacter.
     */
    @Override
    public Set<CNAbility> getSet(PlayerCharacter pc)
    {
        Collection<? extends Ability> collection = pcs.getSet(pc);
        List<CNAbility> pcfeats = pc.getPoolAbilities(AbilityCategory.FEAT);
        Set<CNAbility> returnSet = new HashSet<>();
        for (CNAbility cna : pcfeats)
        {
            Ability a = cna.getAbility();
            if (a.getSafe(ObjectKey.MULTIPLE_ALLOWED) && collection.contains(a))
            {
                returnSet.add(cna);
            }
        }
        return returnSet;
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof ModifyChoiceDecorator) && ((ModifyChoiceDecorator) obj).pcs.equals(pcs);
    }

    @Override
    public int hashCode()
    {
        return pcs.hashCode();
    }

    /**
     * Returns the GroupingState for this ModifyChoiceDecorator. The
     * GroupingState indicates how this ModifyChoiceDecorator can be combined
     * with other PrimitiveChoiceSets.
     *
     * @return The GroupingState for this ModifyChoiceDecorator.
     */
    @Override
    public GroupingState getGroupingState()
    {
        return pcs.getGroupingState();
    }
}
