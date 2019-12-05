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

import pcgen.base.formula.Formula;
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseInformation;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.facade.core.ChooserFacade.ChooserTreeViewType;
import pcgen.gui2.facade.Gui2InfoFactory;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;

import org.apache.commons.lang3.StringUtils;

public class CDOMChoiceManager<T> implements ChoiceManagerList<T>
{

    private final ChooseDriver owner;
    private final Integer numberOfChoices;
    protected final int choicesPerUnitCost;
    protected ChooseController<T> controller = new ChooseController<>();
    protected final ChooseInformation<T> info;

    private int preChooserChoices;

    public CDOMChoiceManager(ChooseDriver cdo, ChooseInformation<T> chooseType, Integer numChoices, int cost)
    {
        numberOfChoices = numChoices;
        owner = cdo;
        info = chooseType;
        choicesPerUnitCost = cost;
    }

    @Override
    public void getChoices(PlayerCharacter pc, List<T> availableList, List<T> selectedList)
    {
        availableList.addAll(info.getSet(pc));
        List<? extends T> selected = info.getChoiceActor().getCurrentlySelected(owner, pc);
        if (selected != null)
        {
            selectedList.addAll(selected);
        }
        preChooserChoices = selectedList.size();
    }

    /**
     * Conditionally add the selected item
     *
     * @param pc
     * @param item
     */
    @Override
    public boolean conditionallyApply(PlayerCharacter pc, T item)
    {
        List<? extends T> oldSelections = info.getChoiceActor().getCurrentlySelected(owner, pc);
        boolean applied = false;
        if (oldSelections == null || !oldSelections.contains(item))
        {
            if (info.getSet(pc).contains(item))
            {
                info.getChoiceActor().applyChoice(owner, item, pc);
                applied = true;
            }
        }
        adjustPool(info.getChoiceActor().getCurrentlySelected(owner, pc));
        return applied;
    }

    /**
     * Add the selected Feat proficiencies
     *
     * @param pc
     * @param selected
     */
    @Override
    public boolean applyChoices(PlayerCharacter pc, List<T> selected)
    {
        List<? extends T> oldSelections = info.getChoiceActor().getCurrentlySelected(owner, pc);
        List<T> toAdd = new ArrayList<>();
        for (T obj : selected)
        {
            if (oldSelections == null || !oldSelections.remove(obj))
            {
                toAdd.add(obj);
            }
        }
        int oldSize = 0;
        if (oldSelections != null)
        {
            oldSize = oldSelections.size();
            for (T obj : oldSelections)
            {
                info.getChoiceActor().removeChoice(pc, owner, obj);
            }
        }
        for (T obj : toAdd)
        {
            info.getChoiceActor().applyChoice(owner, obj, pc);
        }
        adjustPool(selected);
        return oldSize != selected.size();
    }

    /**
     * Display a chooser to the user.
     *
     * @param aPc           The character the choice is for.
     * @param availableList The list of possible choices.
     * @param selectedList  The list of existing selections.
     * @return list The list of the new selections made by the user (unchanged if the dialog was cancelled)
     */
    @Override
    public List<T> doChooser(PlayerCharacter aPc, final List<T> availableList, final List<T> selectedList,
            final List<String> reservedList)
    {
        int effectiveChoices = getNumEffectiveChoices(selectedList, reservedList, aPc);

        boolean dupsAllowed = controller.isMultYes() && controller.isStackYes();

        /*
         * TODO This is temporarily commented out until the correct behavior of
         * the "available" list is established. This is done to make
         * CDOMChoiceManager not remove items when selected, which is consistent
         * with the (buggy?) old Choose system
         */
        if (!dupsAllowed)
        {
            // availableList.removeAll(reservedList);
            availableList.removeAll(selectedList);
        }

        Globals.sortChooserLists(availableList, selectedList);

        String title = StringUtils.isBlank(info.getTitle()) ? "in_chooser" //$NON-NLS-1$
                : info.getTitle();
        if (title.startsWith("in_")) //$NON-NLS-1$
        {
            title = LanguageBundle.getString(title);
        }

        CDOMChooserFacadeImpl<T> chooserFacade =
                new CDOMChooserFacadeImpl<>(title, availableList, selectedList, effectiveChoices);
        chooserFacade.setDefaultView(ChooserTreeViewType.NAME);
        chooserFacade.setAllowsDups(dupsAllowed);
        chooserFacade.setInfoFactory(new Gui2InfoFactory(aPc));
        ChooserFactory.getDelegate().showGeneralChooser(chooserFacade);

        return chooserFacade.getFinalSelected();
    }

    /**
     * Calculate the number of effective choices the user can make.
     *
     * @param selectedList The list of already selected items.
     * @param reservedList The list of options which cannot be offered.
     * @param aPc          The character the choice applies to.
     * @return The number of choices that may be made
     */
    @Override
    public int getNumEffectiveChoices(final List<? extends T> selectedList, final List<String> reservedList,
            PlayerCharacter aPc)
    {
        int selectedPoolValue = (selectedList.size() + (choicesPerUnitCost - 1)) / choicesPerUnitCost;
        int reservedPoolValue = (reservedList.size() + (choicesPerUnitCost - 1)) / choicesPerUnitCost;
        int effectiveTotalChoices;
        if (numberOfChoices == null)
        {
            effectiveTotalChoices = controller.getTotalChoices();
        } else
        {
            effectiveTotalChoices = (numberOfChoices - reservedPoolValue + selectedPoolValue);
        }
        int effectiveChoices =
                Math.min(controller.getPool() + selectedPoolValue, effectiveTotalChoices / choicesPerUnitCost);
        effectiveChoices *= choicesPerUnitCost;
        Formula formula = owner.getNumChoices();
        if (formula != null)
        {
            int numChoices = formula.resolve(aPc, owner.getFormulaSource()).intValue();
            if (numChoices > 0)
            {
                effectiveChoices = Math.min(effectiveChoices, numChoices);
            }
        }
        effectiveChoices -= selectedList.size();
        return effectiveChoices;
    }

    /**
     * @param pc
     * @param availableList
     * @param selectedList
     */
    @Override
    public List<T> doChooserRemove(PlayerCharacter pc, List<T> availableList, List<T> selectedList,
            List<String> reservedList)
    {
        return doChooser(pc, availableList, selectedList, reservedList);
    }

    protected void adjustPool(List<? extends T> selected)
    {
        controller.adjustPool(selected);
    }

    @Override
    public void setController(ChooseController<T> cc)
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
        if (!choice.isEmpty())
        {
            T ch = info.decodeChoice(Globals.getContext(), choice);
            if (ch == null)
            {
                Logging.errorPrint(
                        "Error finding " + info.getReferenceClass().getSimpleName() + " " + choice + ": Not Found");
            } else
            {
                info.restoreChoice(pc, target, ch);
            }
        }
    }

    protected String getTitle()
    {
        return info.getTitle() + " (" + owner.getDisplayName() + ')';
    }

    @Override
    public void removeChoice(PlayerCharacter pc, ChooseDriver obj, T selection)
    {
        info.removeChoice(pc, obj, selection);
    }

    @Override
    public T decodeChoice(String choice)
    {
        return info.decodeChoice(Globals.getContext(), choice);
    }

    @Override
    public void applyChoice(PlayerCharacter pc, ChooseDriver cdo, T selection)
    {
        info.getChoiceActor().applyChoice(cdo, selection, pc);
    }

    @Override
    public String encodeChoice(T obj)
    {
        return info.encodeChoice(obj);
    }

}
