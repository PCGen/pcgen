/*
 * AbilitySelectionToken.java
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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

import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CategorizedAbilitySelectionChooseInformation;
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.Chooser;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrimitiveCollection;
import pcgen.cdom.choiceset.CollectionToAbilitySelection;
import pcgen.cdom.content.AbilitySelection;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * New chooser plugin, handles ability selection.
 */
public class AbilitySelectionToken extends AbstractTokenWithSeparator<CDOMObject>
        implements CDOMSecondaryToken<CDOMObject>, Chooser<AbilitySelection>
{
    private static final Class<AbilityCategory> ABILITY_CATEGORY_CLASS = AbilityCategory.class;

    @Override
    public String getParentToken()
    {
        return "CHOOSE";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    protected ParseResult parseTokenWithSeparator(LoadContext context, ReferenceManufacturer<Ability> rm,
            AbilityCategory cat, CDOMObject obj, String value)
    {
        int pipeLoc = value.lastIndexOf('|');
        String activeValue;
        String title;
        if (pipeLoc == -1)
        {
            activeValue = value;
            title = getDefaultTitle();
        } else
        {
            String titleString = value.substring(pipeLoc + 1);
            if (titleString.startsWith("TITLE="))
            {
                title = titleString.substring(6);
                if (title.startsWith("\""))
                {
                    title = title.substring(1, title.length() - 1);
                }
                if ((title == null) || title.isEmpty())
                {
                    return new ParseResult.Fail(
                            getParentToken() + Constants.COLON + getTokenName() + " had TITLE= but no title: " + value);
                }
                activeValue = value.substring(0, pipeLoc);
            } else
            {
                activeValue = value;
                title = getDefaultTitle();
            }
        }

        PrimitiveCollection<Ability> prim = context.getChoiceSet(rm, activeValue);
        if (prim == null)
        {
            return ParseResult.INTERNAL_ERROR;
        }
        if (!prim.getGroupingState().isValid())
        {
            return new ParseResult.Fail(
                    "Non-sensical " + getFullName() + ": Contains ANY and a specific reference: " + value);
        }
        CollectionToAbilitySelection pcs = new CollectionToAbilitySelection(cat, prim);
        CategorizedAbilitySelectionChooseInformation tc =
                new CategorizedAbilitySelectionChooseInformation(getTokenName(), pcs);
        tc.setTitle(title);
        tc.setChoiceActor(this);
        context.getObjectContext().put(obj, ObjectKey.CHOOSE_INFO, tc);
        return ParseResult.SUCCESS;
    }

    private String getFullName()
    {
        return getParentToken() + Constants.COLON + getTokenName();
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject cdo)
    {
        ChooseInformation<?> tc = context.getObjectContext().getObject(cdo, ObjectKey.CHOOSE_INFO);
        if (tc == null)
        {
            return null;
        }
        if (!tc.getName().equals(getTokenName()))
        {
            // Don't unparse anything that isn't owned by this SecondaryToken
            /*
             * TODO Either this really needs to be a check against the subtoken
             * (which thus needs to be stored in the ChooseInfo) or there needs
             * to be a loadtime check that no more than once CHOOSE subtoken
             * uses the same AssociationListKey... :P
             */
            return null;
        }
        if (!tc.getGroupingState().isValid())
        {
            context.addWriteMessage("Invalid combination of objects" + " was used in: " + getParentToken()
                    + Constants.COLON + getTokenName());
            return null;
        }

        StringBuilder sb = new StringBuilder();
        if (tc instanceof CategorizedAbilitySelectionChooseInformation)
        {
            sb.append(((CategorizedAbilitySelectionChooseInformation) tc).getCategory().getKeyName());
        } else
        {
            // We have a migrating FEATSELECTION token
            sb.append("FEAT");
        }
        sb.append('|');
        sb.append(tc.getLSTformat());
        String title = tc.getTitle();
        if (!title.equals(getDefaultTitle()))
        {
            sb.append("|TITLE=");
            sb.append(title);
        }
        return new String[]{sb.toString()};
    }

    @Override
    public void applyChoice(ChooseDriver owner, AbilitySelection st, PlayerCharacter pc)
    {
        restoreChoice(pc, owner, st);
    }

    @Override
    public void removeChoice(PlayerCharacter pc, ChooseDriver owner, AbilitySelection choice)
    {
        pc.removeAssoc(owner, getListKey(), choice);
        List<ChooseSelectionActor<?>> actors = owner.getActors();
        if (actors != null)
        {
            for (ChooseSelectionActor ca : actors)
            {
                ca.removeChoice(owner, choice, pc);
            }
        }
    }

    @Override
    public void restoreChoice(PlayerCharacter pc, ChooseDriver owner, AbilitySelection choice)
    {
        pc.addAssoc(owner, getListKey(), choice);
        List<ChooseSelectionActor<?>> actors = owner.getActors();
        if (actors != null)
        {
            for (ChooseSelectionActor ca : actors)
            {
                ca.applyChoice(owner, choice, pc);
            }
        }
    }

    @Override
    public List<AbilitySelection> getCurrentlySelected(ChooseDriver owner, PlayerCharacter pc)
    {
        return pc.getAssocList(owner, getListKey());
    }

    @Override
    public boolean allow(AbilitySelection choice, PlayerCharacter pc, boolean allowStack)
    {
        /*
         * This is universally true, as any filter for qualify, etc. was dealt
         * with by the ChoiceSet built during parse
         */
        return true;
    }

    private static final Class<Ability> ABILITY_CLASS = Ability.class;

    @Override
    public String getTokenName()
    {
        return "ABILITYSELECTION";
    }

    @Override
    public ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String value)
    {
        int barLoc = value.indexOf('|');
        if (barLoc == -1)
        {
            return new ParseResult.Fail("CHOOSE:" + getTokenName() + " requires a CATEGORY and arguments : " + value);
        }
        String cat = value.substring(0, barLoc);
        AbilityCategory acRef = context.getReferenceContext().get(ABILITY_CATEGORY_CLASS, cat);
        String abilities = value.substring(barLoc + 1);
        ReferenceManufacturer<Ability> rm =
                context.getReferenceContext().getManufacturerByFormatName("ABILITY=" + cat, ABILITY_CLASS);
        if (rm == null)
        {
            return new ParseResult.Fail("Could not get Reference Manufacturer for Category: " + cat);
        }
        return parseTokenWithSeparator(context, rm, acRef, obj, abilities);
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }

    protected String getDefaultTitle()
    {
        return "Ability choice";
    }

    protected AssociationListKey<AbilitySelection> getListKey()
    {
        return AssociationListKey.getKeyFor(AbilitySelection.class, "CHOOSE*ABILITYSELECTION");
    }

    // TODO - code below here needs to be made category aware
    @Override
    public AbilitySelection decodeChoice(LoadContext context, String s)
    {
        return AbilitySelection.getAbilitySelectionFromPersistentFormat(context, s);
    }

    @Override
    public String encodeChoice(AbilitySelection choice)
    {
        return choice.getPersistentFormat();
    }

}
