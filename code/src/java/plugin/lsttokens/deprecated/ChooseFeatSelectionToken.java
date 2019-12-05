/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.deprecated;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.BasicChooseInformation;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.Chooser;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.base.PrimitiveCollection;
import pcgen.cdom.choiceset.CollectionToAbilitySelection;
import pcgen.cdom.content.AbilitySelection;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * New chooser plugin, handles feat selection.
 */
public class ChooseFeatSelectionToken extends AbstractTokenWithSeparator<CDOMObject>
        implements CDOMSecondaryToken<CDOMObject>, Chooser<AbilitySelection>
{

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
            CDOMObject obj, String value)
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
                if (title == null || title.isEmpty())
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
        PrimitiveChoiceSet<AbilitySelection> pcs = new CollectionToAbilitySelection(AbilityCategory.FEAT, prim);
        //be tricky for compatibility
        BasicChooseInformation<AbilitySelection> tc =
                new BasicChooseInformation<>("ABILITYSELECTION", pcs, AbilityCategory.FEAT.getPersistentFormat());
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
        //Nothing here anymore - defer to ABILITYSELECTION
        return null;
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

    @Override
    public String getTokenName()
    {
        return "FEATSELECTION";
    }

    @Override
    public ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String value)
    {
        return parseTokenWithSeparator(context, context.getReferenceContext().getManufacturerId(AbilityCategory.FEAT),
                obj, value);
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
        return AssociationListKey.getKeyFor(AbilitySelection.class, "CHOOSE*FEATSELECTION");
    }

    @Override
    public AbilitySelection decodeChoice(LoadContext context, String s)
    {
        Ability ability = context.getReferenceContext().getManufacturerId(AbilityCategory.FEAT).getActiveObject(s);

        if (ability == null)
        {
            List<String> choices = new ArrayList<>();
            String baseKey = AbilityUtilities.getUndecoratedName(s, choices);
            ability = context.getReferenceContext().getManufacturerId(AbilityCategory.FEAT).getActiveObject(baseKey);
            if (ability == null)
            {
                throw new IllegalArgumentException("String in decodeChoice " + "must be a Feat Key "
                        + "(or Feat Key with Selection if appropriate), was: " + s);
            }
            return new AbilitySelection(ability, choices.get(0));
        } else if (ability.getSafe(ObjectKey.MULTIPLE_ALLOWED))
        {
            /*
             * MULT:YES, CHOOSE:NOCHOICE can land here
             *
             * TODO There needs to be better validation at some point that this
             * is proper (meaning it is actually CHOOSE:NOCHOICE!)
             */
            return new AbilitySelection(ability, "");
        } else
        {
            return new AbilitySelection(ability, null);
        }
    }

    @Override
    public String encodeChoice(AbilitySelection choice)
    {
        Ability ability = choice.getObject();
        StringBuilder sb = new StringBuilder(50);
        sb.append(ability.getKeyName());
        String selection = choice.getSelection();
        if (selection != null && !selection.isEmpty())
        {
            sb.append(" (");
            sb.append(selection);
            sb.append(')');
        }
        return sb.toString();
    }
}
