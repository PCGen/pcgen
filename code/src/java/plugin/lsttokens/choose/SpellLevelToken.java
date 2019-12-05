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

import java.util.ArrayList;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.base.text.ParsingSeparator;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.Chooser;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PrimitiveCollection;
import pcgen.cdom.base.SpellLevelChooseInformation;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.SpellLevel;
import pcgen.cdom.helper.SpellLevelInfo;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * New chooser plugin, handles Spell Level.
 */
public class SpellLevelToken extends AbstractTokenWithSeparator<CDOMObject>
        implements CDOMSecondaryToken<CDOMObject>, Chooser<SpellLevel>
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

    @Override
    public ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String value)
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
                activeValue = value.substring(0, pipeLoc);
            } else
            {
                activeValue = value;
                title = getDefaultTitle();
            }
        }

        pipeLoc = value.indexOf('|');

        ParsingSeparator sep = new ParsingSeparator(activeValue, '|');
        sep.addGroupingPair('[', ']');
        sep.addGroupingPair('(', ')');

        if (!sep.hasNext())
        {
            return new ParseResult.Fail("Found no arguments in " + getFullName() + ": " + value);
        }
        List<SpellLevelInfo> sliList = new ArrayList<>();
        while (sep.hasNext())
        {
            String token = sep.next();
            PrimitiveCollection<PCClass> pcf = context
                    .getPrimitiveChoiceFilter(context.getReferenceContext().getManufacturer(PCClass.class), token);
            if (!sep.hasNext())
            {
                return new ParseResult.Fail(
                        "Expected minimum level argument after " + token + " in " + getFullName() + ": " + value);
            }
            String minLevelString = sep.next();
            Formula minLevel = FormulaFactory.getFormulaFor(minLevelString);
            if (!sep.hasNext())
            {
                return new ParseResult.Fail(
                        "Expected maximum level argument after " + minLevelString + " in " + getFullName() + ": " + value);
            }
            String maxLevelString = sep.next();
            Formula maxLevel = FormulaFactory.getFormulaFor(maxLevelString);
            if (!maxLevel.isValid())
            {
                return new ParseResult.Fail(
                        "Max Level Formula in " + getTokenName() + " was not valid: " + maxLevel.toString());
            }
            SpellLevelInfo sli = new SpellLevelInfo(pcf, minLevel, maxLevel);
            sliList.add(sli);
        }
        SpellLevelChooseInformation tc = new SpellLevelChooseInformation(getTokenName(), sliList);
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
    public void applyChoice(ChooseDriver owner, SpellLevel st, PlayerCharacter pc)
    {
        restoreChoice(pc, owner, st);
    }

    @Override
    public void removeChoice(PlayerCharacter pc, ChooseDriver owner, SpellLevel choice)
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
    public void restoreChoice(PlayerCharacter pc, ChooseDriver owner, SpellLevel choice)
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
    public List<SpellLevel> getCurrentlySelected(ChooseDriver owner, PlayerCharacter pc)
    {
        return pc.getAssocList(owner, getListKey());
    }

    @Override
    public boolean allow(SpellLevel choice, PlayerCharacter pc, boolean allowStack)
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
        return "SPELLLEVEL";
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }

    protected String getDefaultTitle()
    {
        return "Choose Spell Level";
    }

    protected AssociationListKey<SpellLevel> getListKey()
    {
        return AssociationListKey.getKeyFor(SpellLevel.class, "CHOOSE*SPELLLEVEL");
    }

    @Override
    public SpellLevel decodeChoice(LoadContext context, String s)
    {
        return SpellLevel.decodeChoice(context, s);
    }

    @Override
    public String encodeChoice(SpellLevel choice)
    {
        return choice.encodeChoice();
    }

}
