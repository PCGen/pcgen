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
package pcgen.core.chooser;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.Constants;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;

public class NoChoiceManager implements ChoiceManagerList<String>
{

    private final ChooseDriver owner;
    private final int choicesPerUnitCost;
    private ChooseController<String> controller = new ChooseController<>();
    private final ChooseInformation<String> info;

    private int preChooserChoices;

    public NoChoiceManager(ChooseDriver cdo, ChooseInformation<String> chooseType, int cost)
    {
        owner = cdo;
        info = chooseType;
        choicesPerUnitCost = cost;
    }

    @Override
    public void getChoices(PlayerCharacter pc, List<String> availableList, List<String> selectedList)
    {
        availableList.addAll(info.getSet(pc));
        selectedList.addAll(pc.getAssociationList(owner));
        preChooserChoices = selectedList.size();
    }

    @Override
    public boolean conditionallyApply(PlayerCharacter pc, String item)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean applyChoices(PlayerCharacter pc, List<String> selected)
    {
        List<? extends String> oldSelections = info.getChoiceActor().getCurrentlySelected(owner, pc);
        int oldSelectionSize = (oldSelections == null) ? 0 : oldSelections.size();
        int newSelectionSize = selected.size();
        for (int i = oldSelectionSize;i > newSelectionSize;i--)
        {
            info.getChoiceActor().removeChoice(pc, owner, Constants.EMPTY_STRING);
        }
        for (int i = oldSelectionSize;i < newSelectionSize;i++)
        {
            info.getChoiceActor().applyChoice(owner, Constants.EMPTY_STRING, pc);
        }
        adjustPool(selected);
        return oldSelectionSize != newSelectionSize;
    }

    @Override
    public List<String> doChooser(PlayerCharacter aPc, final List<String> availableList,
            final List<String> selectedList, final List<String> reservedList)
    {
        selectedList.add("");
        return new ArrayList<>(selectedList);
    }

    @Override
    public List<String> doChooserRemove(PlayerCharacter aPC, List<String> availableList, List<String> selectedList,
            List<String> reservedList)
    {
        selectedList.remove(0);
        return selectedList;
    }

    protected void adjustPool(List<String> selected)
    {
        controller.adjustPool(selected);
    }

    @Override
    public void setController(ChooseController<String> cc)
    {
        controller = cc;
    }

    @Override
    public int getChoicesPerUnitCost()
    {
        return choicesPerUnitCost;
    }

    @Override
    public int getPreChooserChoices()
    {
        return preChooserChoices;
    }

    @Override
    public void restoreChoice(PlayerCharacter pc, ChooseDriver target, String choice)
    {
        info.restoreChoice(pc, target, info.decodeChoice(Globals.getContext(), choice));
    }

    @Override
    public int getNumEffectiveChoices(List<? extends String> selectedList, List<String> reservedList,
            PlayerCharacter aPc)
    {
        return 0;
    }

    @Override
    public void removeChoice(PlayerCharacter pc, ChooseDriver obj, String selection)
    {
        info.removeChoice(pc, obj, selection);
    }

    @Override
    public String decodeChoice(String choice)
    {
        return info.decodeChoice(Globals.getContext(), choice);
    }

    @Override
    public void applyChoice(PlayerCharacter pc, ChooseDriver cdo, String selection)
    {
        info.getChoiceActor().applyChoice(cdo, Constants.EMPTY_STRING, pc);
    }

    @Override
    public String encodeChoice(String obj)
    {
        return info.encodeChoice(obj);
    }
}
