/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.facet;

import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.core.Ability;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.util.Logging;

public class AbilitySelectionApplication implements DataFacetChangeListener<CharID, CNAbilitySelection>
{
    private final PlayerCharacterTrackingFacet pcFacet = FacetLibrary.getFacet(PlayerCharacterTrackingFacet.class);

    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, CNAbilitySelection> dfce)
    {
        CharID id = dfce.getCharID();
        PlayerCharacter pc = pcFacet.getPC(id);
        CNAbilitySelection cnas = dfce.getCDOMObject();
        CNAbility cna = cnas.getCNAbility();
        Ability ability = cna.getAbility();
        String selection = cnas.getSelection();
        if (selection != null)
        {
            ChooseInformation<?> chooseInfo = ability.get(ObjectKey.CHOOSE_INFO);
            if (chooseInfo != null)
            {
                applySelection(pc, chooseInfo, cna, selection);
            }
        }
    }

    private <T> void applySelection(PlayerCharacter pc, ChooseInformation<T> chooseInfo, CNAbility cna,
            String selection)
    {
        Ability ability = cna.getAbility();
        T obj = chooseInfo.decodeChoice(Globals.getContext(), selection);
        if (obj == null)
        {
            Logging.errorPrint("Unable to apply Selection: '" + selection + "' to Ability " + ability + " ("
                    + ability.getCDOMCategory() + ") because the given selection does not exist in the loaded data");
        } else
        {
            chooseInfo.getChoiceActor().applyChoice(cna, obj, pc);
        }
    }

    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, CNAbilitySelection> dfce)
    {
        CharID id = dfce.getCharID();
        CNAbilitySelection cnas = dfce.getCDOMObject();
        PlayerCharacter pc = pcFacet.getPC(id);
        CNAbility cna = cnas.getCNAbility();
        Ability ability = cna.getAbility();
        String selection = cnas.getSelection();
        if (selection != null)
        {
            ChooseInformation<?> chooseInfo = ability.get(ObjectKey.CHOOSE_INFO);
            if (chooseInfo != null)
            {
                removeSelection(pc, chooseInfo, cna, selection);
            }
        }
    }

    private <T> void removeSelection(PlayerCharacter pc, ChooseInformation<T> chooseInfo, CNAbility cna,
            String selection)
    {
        T obj = chooseInfo.decodeChoice(Globals.getContext(), selection);
        chooseInfo.getChoiceActor().removeChoice(pc, cna, obj);
    }

}
