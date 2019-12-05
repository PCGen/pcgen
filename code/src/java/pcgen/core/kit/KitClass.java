/*
 * Copyright 2005 (C) Aaron Divinsky <boomer70@yahoo.com>
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

import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Kit;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.SubClass;
import pcgen.core.analysis.SubClassApplication;
import pcgen.core.prereq.PrereqHandler;
import pcgen.gui2.UIPropertyContext;

/**
 * {@code KitClass} <strong>needs documentation</strong>.
 */
public class KitClass extends BaseKit
{
    private CDOMSingleRef<PCClass> pcClass;
    private Formula levelFormula;
    private CDOMReference<SubClass> subClass;

    // These members store the state of an instance of this class.  They are
    // not cloned.
    private PCClass theClass = null;
    private String theOrigSubClass = null;
    private int theLevel = -1;
    private boolean doLevelAbilities = true;

    @Override
    public String toString()
    {
        StringBuilder ret = new StringBuilder(100);
        ret.append(pcClass.getLSTformat(false));
        if (subClass != null)
        {
            ret.append("(").append(subClass.getLSTformat(false)).append(")");
        }
        ret.append(levelFormula);
        return ret.toString();
    }

    @Override
    public boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings)
    {
        theLevel = -1;
        doLevelAbilities = true;

        theClass = pcClass.get();

        theOrigSubClass = aPC.getSubClassName(theClass);
        applySubClass(aPC);

        if (!PrereqHandler.passesAll(theClass, aPC, aKit))
        {
            PrereqHandler.toHtmlString(theClass.getPrerequisiteList());
            warnings.add("CLASS: Not qualified for class \"" + theClass.getKeyName() + "\".");
            return false;
        }

        doLevelAbilities = aKit.doLevelAbilities();

        // Temporarily increase the PCs level.
        theLevel = levelFormula.resolve(aPC, "").intValue();
        addLevel(aPC, theLevel, theClass, doLevelAbilities);

        return true;
    }

    private void applySubClass(PlayerCharacter aPC)
    {
        if (subClass != null)
        {
            // Ensure the character has the class
            PCClass heldClass = aPC.getClassKeyed(theClass.getKeyName());
            if (heldClass == null)
            {
                aPC.incrementClassLevel(0, theClass);
                heldClass = aPC.getClassKeyed(theClass.getKeyName());
            }

            // try and set a subclass too.
            SubClassApplication.setSubClassKey(aPC, heldClass, getSubClass().getLSTformat(false));
        }
    }

    @Override
    public void apply(PlayerCharacter aPC)
    {
        applySubClass(aPC);
        addLevel(aPC, theLevel, theClass, doLevelAbilities);
        if (theOrigSubClass != null)
        {
            SubClassApplication.setSubClassKey(aPC, theClass, theOrigSubClass);
        }
        theClass = null;
    }

    private void addLevel(final PlayerCharacter pc, final int numLevels, final PCClass aClass,
            final boolean doLevelAbilitiesIn)
    {
        // We want to level up as quietly as possible for kits.
        boolean tempShowHP = SettingsHandler.getShowHPDialogAtLevelUp();
        SettingsHandler.setShowHPDialogAtLevelUp(false);
        //		boolean tempFeatDlg = SettingsHandler.getShowFeatDialogAtLevelUp();
        int tempChoicePref = UIPropertyContext.getSingleChoiceAction();
        UIPropertyContext.setSingleChoiceAction(Constants.CHOOSER_SINGLE_CHOICE_METHOD_SELECT_EXIT);

        boolean tempDoLevelAbilities = pc.doLevelAbilities();
        pc.setDoLevelAbilities(doLevelAbilitiesIn);
        pc.incrementClassLevel(numLevels, aClass, true);
        pc.setDoLevelAbilities(tempDoLevelAbilities);

        UIPropertyContext.setSingleChoiceAction(tempChoicePref);
        SettingsHandler.setShowHPDialogAtLevelUp(tempShowHP);
    }

    @Override
    public String getObjectName()
    {
        return "Classes";
    }

    public void setPcclass(CDOMSingleRef<PCClass> ref)
    {
        pcClass = ref;
    }

    public CDOMReference<PCClass> getPcclass()
    {
        return pcClass;
    }

    public void setLevel(Formula formula)
    {
        levelFormula = formula;
    }

    public Formula getLevel()
    {
        return levelFormula;
    }

    public void setSubClass(CDOMReference<SubClass> sc)
    {
        subClass = sc;
    }

    public CDOMReference<SubClass> getSubClass()
    {
        return subClass;
    }
}
