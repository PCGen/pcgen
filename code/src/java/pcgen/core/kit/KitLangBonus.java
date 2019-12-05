/*
 * Copyright 2008 (C) James Dempsey
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
package pcgen.core.kit;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.UserSelection;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.AbilityCategory;
import pcgen.core.Kit;
import pcgen.core.Language;
import pcgen.core.PlayerCharacter;
import pcgen.core.chooser.ChoiceManagerList;
import pcgen.core.chooser.ChooserUtilities;

/**
 * Deals with applying a bonus language via a Kit
 */
public class KitLangBonus extends BaseKit
{
    /**
     * The list of language names.
     */
    private List<CDOMSingleRef<Language>> langList = new ArrayList<>();

    // These members store the state of an instance of this class.  They are
    // not cloned.
    private List<Language> theLanguages = new ArrayList<>();

    /**
     * Actually applies the bonus languages to this PC.
     *
     * @param aPC The PlayerCharacter the languages are to be applied to
     */
    @Override
    public void apply(PlayerCharacter aPC)
    {
        CNAbility cna = aPC.getBonusLanguageAbility();
        for (Language l : theLanguages)
        {
            aPC.addAbility(new CNAbilitySelection(cna, l.getKeyName()), UserSelection.getInstance(),
                    UserSelection.getInstance());
        }
    }

    /**
     * Prepare the languages to be added and test their application.
     *
     * @param aPC      The character to be processed.
     * @param aKit     The kit being processed
     * @param warnings List of warnigns.
     * @return true, if the languages could be added
     */
    @Override
    public boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings)
    {
        theLanguages = new ArrayList<>();

        CNAbility cna = aPC.getBonusLanguageAbility();

        List<String> reservedList = new ArrayList<>();

        /*
         * While this direct use of the controller seems strange, the use of
         * conditionallyApply in the controller is reasonable (there is no need
         * to actually try to apply the langbonus ability in this case)
         */
        ChoiceManagerList<Language> controller =
                ChooserUtilities.getConfiguredController(cna, aPC, AbilityCategory.LANGBONUS, reservedList);
        if (controller == null)
        {
            return false;
        }

        int allowedCount = aPC.getAvailableAbilityPool(AbilityCategory.LANGBONUS).intValue();
        int remaining = allowedCount;
        for (CDOMSingleRef<Language> ref : langList)
        {
            Language lang = ref.get();
            if (remaining > 0 && controller.conditionallyApply(aPC, lang))
            {
                theLanguages.add(lang);
                remaining--;
            } else
            {
                warnings.add("LANGUAGE: Could not add bonus language \"" + lang.getKeyName() + "\"");
            }
        }

        if (langList.size() > allowedCount)
        {
            warnings.add("LANGUAGE: Too many bonus languages specified. " + (langList.size() - allowedCount)
                    + " had to be ignored.");
        }

        return !theLanguages.isEmpty();
    }

    @Override
    public String getObjectName()
    {
        return "Languages";
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        for (CDOMSingleRef<Language> lang : langList)
        {
            if (result.length() > 0)
            {
                result.append(", ");
            }
            result.append(lang.getLSTformat(false));
        }
        return result.toString();
    }

    public void addLanguage(CDOMSingleRef<Language> reference)
    {
        langList.add(reference);
    }

    public List<CDOMSingleRef<Language>> getLanguages()
    {
        return langList;
    }
}
