/*
 * Copyright 2010 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.base;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.core.PlayerCharacter;
import pcgen.core.chooser.ChoiceManagerList;
import pcgen.core.chooser.UserInputManager;
import pcgen.rules.context.LoadContext;

import org.jetbrains.annotations.NotNull;

public class UserChooseInformation implements ChooseInformation<String>, Chooser<String>
{

    public static final String UCI_NAME = "User Input";

    /**
     * The title (presented to the user) of this ChoiceSet
     */
    private String title = null;

    @Override
    public Class<String> getReferenceClass()
    {
        return String.class;
    }

    @Override
    public ChoiceManagerList<String> getChoiceManager(ChooseDriver owner, int cost)
    {
        return new UserInputManager(owner, this, cost);
    }

    @Override
    public GroupingState getGroupingState()
    {
        return GroupingState.ALLOWS_NONE;
    }

    @Override
    public String getLSTformat()
    {
        return "*USERINPUT";
    }

    @Override
    public String getName()
    {
        return UCI_NAME;
    }

    @Override
    public Collection<String> getSet(PlayerCharacter pc)
    {
        return Collections.singletonList("USERINPUT");
    }

    @Override
    public String getTitle()
    {
        return title == null ? "Provide User Input" : title;
    }

    @Override
    public CharSequence composeDisplay(@NotNull Collection<? extends String> collection)
    {
        return ChooseInformationUtilities.buildEncodedString(collection);
    }

    @Override
    public void restoreChoice(PlayerCharacter pc, ChooseDriver owner, String choice)
    {
        pc.addAssoc(owner, getListKey(), choice);
    }

    @Override
    public List<String> getCurrentlySelected(ChooseDriver owner, PlayerCharacter pc)
    {
        return pc.getAssocList(owner, getListKey());
    }

    @Override
    public void applyChoice(ChooseDriver owner, String choice, PlayerCharacter pc)
    {
        restoreChoice(pc, owner, choice);
        List<ChooseSelectionActor<?>> actors = owner.getActors();
        if (actors != null)
        {
            for (ChooseSelectionActor csa : actors)
            {
                applyChoice(owner, pc, choice, csa);
            }
        }
    }

    private static void applyChoice(ChooseDriver owner, PlayerCharacter pc, String choice,
            ChooseSelectionActor<String> csa)
    {
        csa.applyChoice(owner, choice, pc);
    }

    @Override
    public void removeChoice(PlayerCharacter pc, ChooseDriver owner, String choice)
    {
        pc.removeAssoc(owner, getListKey(), choice);
        List<ChooseSelectionActor<?>> actors = owner.getActors();
        if (actors != null)
        {
            for (ChooseSelectionActor csa : actors)
            {
                csa.removeChoice(owner, choice, pc);
            }
        }
    }

    @Override
    public Chooser<String> getChoiceActor()
    {
        return this;
    }

    @Override
    public void setChoiceActor(Chooser<String> actor)
    {
        // ignore
    }

    @Override
    public boolean allow(String choice, PlayerCharacter pc, boolean allowStack)
    {
        return true;
    }

    @Override
    public String decodeChoice(LoadContext context, String choice)
    {
        return choice;
    }

    @Override
    public String encodeChoice(String choice)
    {
        return choice;
    }

    public void setTitle(String chooseTitle)
    {
        title = chooseTitle;
    }

    private static AssociationListKey<String> getListKey()
    {
        return AssociationListKey.getKeyFor(String.class, "CHOOSE*USERCHOICE");
    }

    @Override
    public String getPersistentFormat()
    {
        return "STRING";
    }

}
