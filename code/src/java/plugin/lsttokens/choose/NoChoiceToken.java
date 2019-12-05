/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.choose;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.Chooser;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PlayerCharacter;
import pcgen.core.chooser.ChoiceManagerList;
import pcgen.core.chooser.NoChoiceManager;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

import org.jetbrains.annotations.NotNull;

/**
 * New chooser plugin, handles no Choice.
 */
public class NoChoiceToken
        implements CDOMSecondaryToken<CDOMObject>, ChooseInformation<String>, Chooser<String>, DeferredToken<CDOMObject>
{

    @Override
    public String getTokenName()
    {
        return "NOCHOICE";
    }

    @Override
    public String getParentToken()
    {
        return "CHOOSE";
    }

    @Override
    public ParseResult parseToken(LoadContext context, CDOMObject obj, String value)
    {
        if (value == null)
        {
            // No args - legal
            context.getObjectContext().put(obj, ObjectKey.CHOOSE_INFO, this);
            return ParseResult.SUCCESS;
        }
        return new ParseResult.Fail("CHOOSE:" + getTokenName() + " will ignore arguments: " + value);
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject cdo)
    {
        ChooseInformation<?> chooseString = context.getObjectContext().getObject(cdo, ObjectKey.CHOOSE_INFO);
        if ((chooseString == null) || !chooseString.equals(this))
        {
            return null;
        }
        return new String[]{""};
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }

    @Override
    public Class<String> getReferenceClass()
    {
        return String.class;
    }

    @Override
    public ChoiceManagerList getChoiceManager(ChooseDriver owner, int cost)
    {
        return new NoChoiceManager(owner, this, cost);
    }

    @Override
    public GroupingState getGroupingState()
    {
        return GroupingState.ALLOWS_NONE;
    }

    @Override
    public String getLSTformat()
    {
        return "*NOCHOICE";
    }

    @Override
    public String getName()
    {
        return "No Choice";
    }

    @Override
    public Collection<String> getSet(PlayerCharacter pc)
    {
        return Collections.singletonList("");
    }

    @Override
    public String getTitle()
    {
        return "No Choice Available";
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

    @Override
    public Chooser<String> getChoiceActor()
    {
        return this;
    }

    @Override
    public void setChoiceActor(Chooser<String> ca)
    {
        // ignore
    }

    @Override
    public boolean allow(String choice, PlayerCharacter pc, boolean allowStack)
    {
        return true;
    }

    @Override
    public void applyChoice(ChooseDriver owner, String st, PlayerCharacter pc)
    {
        restoreChoice(pc, owner, "");
    }

    private void applyChoice(ChooseDriver owner, PlayerCharacter pc, ChooseSelectionActor<String> ca)
    {
        ca.applyChoice(owner, "", pc);
    }

    @Override
    public void removeChoice(PlayerCharacter pc, ChooseDriver owner, String choice)
    {
        pc.removeAssoc(owner, getListKey(), "");
        List<ChooseSelectionActor<?>> actors = owner.getActors();
        if (actors != null)
        {
            for (ChooseSelectionActor ca : actors)
            {
                ca.removeChoice(owner, "", pc);
            }
        }
    }

    @Override
    public void restoreChoice(PlayerCharacter pc, ChooseDriver owner, String choice)
    {
        pc.addAssoc(owner, getListKey(), "");
        List<ChooseSelectionActor<?>> actors = owner.getActors();
        if (actors != null)
        {
            for (ChooseSelectionActor ca : actors)
            {
                applyChoice(owner, pc, ca);
            }
        }
    }

    @Override
    public List<String> getCurrentlySelected(ChooseDriver owner, PlayerCharacter pc)
    {
        return pc.getAssocList(owner, getListKey());
    }

    private AssociationListKey<String> getListKey()
    {
        return AssociationListKey.getKeyFor(String.class, "CHOOSE*NOCHOICE");
    }

    @Override
    public CharSequence composeDisplay(@NotNull Collection<? extends String> collection)
    {
        StringBuilder sb = new StringBuilder(5);
        int count = collection.size();
        if (count > 1)
        {
            sb.append(count);
            sb.append('x');
        }
        return sb;
    }

    @Override
    public boolean process(LoadContext context, CDOMObject obj)
    {
        ChooseInformation<?> ci = obj.get(ObjectKey.CHOOSE_INFO);
        if ((ci == this) && !obj.getSafe(ObjectKey.STACKS))
        {
            Logging.errorPrint("CHOOSE:NOCHOICE requires both MULT:YES and STACK:YES, was STACK:NO on "
                    + obj.getClass().getSimpleName() + ' ' + obj.getKeyName(), obj.getSourceURI());
            return false;
        }
        return true;
    }

    @Override
    public Class<CDOMObject> getDeferredTokenClass()
    {
        return CDOMObject.class;
    }

    @Override
    public String getPersistentFormat()
    {
        return "STRING";
    }

}
