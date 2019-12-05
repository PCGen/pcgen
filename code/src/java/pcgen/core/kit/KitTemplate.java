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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.base.util.HashMapToList;
import pcgen.base.util.MapToList;
import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Kit;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.util.chooser.ChooserFactory;

/**
 * Deals with applying a Template via a Kit
 */
public class KitTemplate extends BaseKit
{
    private final HashMapToList<CDOMSingleRef<PCTemplate>, CDOMSingleRef<PCTemplate>> templateList =
            new HashMapToList<>();

    /**
     * Actually applies the templates to this PC.
     *
     * @param aPC The PlayerCharacter the alignment is applied to
     */
    @Override
    public void apply(PlayerCharacter aPC)
    {
        HashMapToList<PCTemplate, PCTemplate> selectedMap = buildSelectedTemplateMap(aPC, true);

        boolean tempShowHP = SettingsHandler.getShowHPDialogAtLevelUp();
        SettingsHandler.setShowHPDialogAtLevelUp(false);

        for (PCTemplate template : selectedMap.getKeySet())
        {
            List<PCTemplate> added = selectedMap.getListFor(template);
            if (added != null)
            {
                for (PCTemplate subtemplate : added)
                {
                    aPC.setTemplatesAdded(template, subtemplate);
                }
            }
            aPC.addTemplate(template);
        }

        SettingsHandler.setShowHPDialogAtLevelUp(tempShowHP);
    }

    /**
     * testApply
     *
     * @param aPC      PlayerCharacter
     * @param aKit     Kit
     * @param warnings List
     */
    @Override
    public boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings)
    {
        MapToList<PCTemplate, PCTemplate> selectedMap = buildSelectedTemplateMap(aPC, false);

        return !selectedMap.isEmpty();
    }

    /**
     * Extract the templates to be applied and their choices
     *
     * @param aPC   The PC the kit is being applied to.
     * @param apply Is this a real application, false if a test run.
     * @return The map of templates and child templates to be added
     */
    private HashMapToList<PCTemplate, PCTemplate> buildSelectedTemplateMap(PlayerCharacter aPC, boolean apply)
    {
        boolean tempShowHP = SettingsHandler.getShowHPDialogAtLevelUp();
        SettingsHandler.setShowHPDialogAtLevelUp(false);
        if (!apply)
        {
            ChooserFactory.useRandomChooser(); //$NON-NLS-1$
        }
        HashMapToList<PCTemplate, PCTemplate> selectedMap = new HashMapToList<>();

        for (CDOMSingleRef<PCTemplate> ref : templateList.getKeySet())
        {
            PCTemplate templateToAdd = ref.get();
            List<CDOMSingleRef<PCTemplate>> subList = templateList.getListFor(ref);
            List<PCTemplate> subAdded = new ArrayList<>();
            if (subList != null)
            {
                for (CDOMSingleRef<PCTemplate> subRef : subList)
                {
                    PCTemplate ownedTemplate = subRef.get();
                    subAdded.add(ownedTemplate);
                    aPC.setTemplatesAdded(templateToAdd, ownedTemplate);
                }
            }

            aPC.addTemplate(templateToAdd);
            selectedMap.initializeListFor(templateToAdd);
            selectedMap.addAllToListFor(templateToAdd, subAdded);
        }

        SettingsHandler.setShowHPDialogAtLevelUp(tempShowHP);
        return selectedMap;
    }

    @Override
    public String getObjectName()
    {
        return "Templates";
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        boolean needsPipe = false;
        for (CDOMSingleRef<PCTemplate> ref : templateList.getKeySet())
        {
            if (needsPipe)
            {
                sb.append(Constants.PIPE);
            }
            needsPipe = true;
            sb.append(ref.getLSTformat(false));
            List<CDOMSingleRef<PCTemplate>> subList = templateList.getListFor(ref);
            if (subList != null)
            {
                for (CDOMSingleRef<PCTemplate> subref : subList)
                {
                    sb.append("[TEMPLATE:");
                    sb.append(subref.getLSTformat(false));
                    sb.append(']');
                }
            }
        }
        return sb.toString();
    }

    public void addTemplate(CDOMSingleRef<PCTemplate> ref, Collection<CDOMSingleRef<PCTemplate>> subList)
    {
        templateList.initializeListFor(ref);
        templateList.addAllToListFor(ref, subList);
    }

    public boolean isEmpty()
    {
        return templateList.isEmpty();
    }
}
