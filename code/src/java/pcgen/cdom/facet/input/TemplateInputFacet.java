/*
 * Copyright (c) Thomas Parker, 2012.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet.input;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.PlayerCharacterTrackingFacet;
import pcgen.cdom.facet.TemplateSelectionFacet;
import pcgen.cdom.facet.UnconditionalTemplateFacet;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.ChooseActivation;
import pcgen.core.chooser.ChoiceManagerList;
import pcgen.core.chooser.ChooserUtilities;

/**
 * TemplateInputFacet is a Facet that handles addition of PCTempaltes to a
 * Player Character.
 */
public class TemplateInputFacet
{

    private TemplateSelectionFacet templateSelectionFacet;

    private UnconditionalTemplateFacet unconditionalTemplateFacet;

    private final PlayerCharacterTrackingFacet trackingFacet =
            FacetLibrary.getFacet(PlayerCharacterTrackingFacet.class);

    public boolean add(CharID id, PCTemplate obj)
    {
        PlayerCharacter pc = trackingFacet.getPC(id);
        if (pc.isAllowInteraction() && ChooseActivation.hasNewChooseToken(obj))
        {
            ChoiceManagerList<?> aMan = ChooserUtilities.getChoiceManager(obj, pc);
            return processChoice(id, pc, obj, aMan);
        } else
        {
            directAdd(id, obj, null);
        }
        return true;
    }

    private <T> boolean processChoice(CharID id, PlayerCharacter pc, PCTemplate obj, ChoiceManagerList<T> aMan)
    {
        List<T> selectedList = new ArrayList<>();
        List<T> availableList = new ArrayList<>();
        aMan.getChoices(pc, availableList, selectedList);

        if (availableList.isEmpty())
        {
            return false;
        }
        if (!selectedList.isEmpty())
        {
            //Error?
        }
        final List<T> newSelections = aMan.doChooser(pc, availableList, selectedList, new ArrayList<>());
        if (newSelections.size() != 1)
        {
            //Error?
            return false;
        }
        for (T sel : newSelections)
        {
            directAdd(id, obj, sel);
        }
        return true;
    }

    public void importSelection(CharID id, PCTemplate obj, String choice)
    {
        PlayerCharacter pc = trackingFacet.getPC(id);
        if (ChooseActivation.hasNewChooseToken(obj))
        {
            ChoiceManagerList<?> aMan = ChooserUtilities.getChoiceManager(obj, pc);
            processImport(id, obj, aMan, choice);
        } else
        {
            directAdd(id, obj, null);
        }
    }

    private <T> void processImport(CharID id, PCTemplate obj, ChoiceManagerList<T> aMan, String choice)
    {
        directAdd(id, obj, aMan.decodeChoice(choice));
    }

    public <T> void directAdd(CharID id, PCTemplate obj, T sel)
    {
        unconditionalTemplateFacet.add(id, obj);
        if (sel != null)
        {
            templateSelectionFacet.set(id, obj, sel);
        }
    }

    public void remove(CharID id, PCTemplate obj)
    {
        unconditionalTemplateFacet.remove(id, obj);
        PlayerCharacter pc = trackingFacet.getPC(id);
        if (pc.isAllowInteraction())
        {
            templateSelectionFacet.remove(id, obj);
        }
    }

    public void setTemplateSelectionFacet(TemplateSelectionFacet templateSelectionFacet)
    {
        this.templateSelectionFacet = templateSelectionFacet;
    }

    public void setUnconditionalTemplateFacet(UnconditionalTemplateFacet unconditionalTemplateFacet)
    {
        this.unconditionalTemplateFacet = unconditionalTemplateFacet;
    }

}
