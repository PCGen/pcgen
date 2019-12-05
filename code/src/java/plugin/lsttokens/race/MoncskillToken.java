/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.race;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.list.ClassSkillList;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.PatternMatchingReference;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with MONCSKILL Token
 */
public class MoncskillToken extends AbstractTokenWithSeparator<Race>
        implements CDOMPrimaryToken<Race>, ChooseSelectionActor<Skill>
{

    private static final Class<Skill> SKILL_CLASS = Skill.class;

    @Override
    public String getTokenName()
    {
        return "MONCSKILL";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, Race race, String value)
    {
        boolean firstToken = true;
        boolean foundAny = false;
        boolean foundOther = false;

        StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
        CDOMGroupRef<ClassSkillList> monsterList =
                context.getReferenceContext().getCDOMTypeReference(ClassSkillList.class, "Monster");

        while (tok.hasMoreTokens())
        {
            String tokText = tok.nextToken();
            if (Constants.LST_DOT_CLEAR.equals(tokText))
            {
                if (!firstToken)
                {
                    return new ParseResult.Fail("Non-sensical situation was " + "encountered while parsing "
                            + getTokenName() + ": When used, .CLEAR must be the first argument");
                }
                context.getListContext().removeAllFromList(getTokenName(), race, monsterList);
            } else if (tokText.startsWith(Constants.LST_DOT_CLEAR_DOT))
            {
                CDOMReference<Skill> skill = null;
                String clearText = tokText.substring(7);
                if (Constants.LST_ALL.equals(clearText))
                {
                    skill = context.getReferenceContext().getCDOMAllReference(SKILL_CLASS);
                } else
                {
                    if (Constants.LST_LIST.equals(clearText))
                    {
                        context.getObjectContext().removeFromList(race, ListKey.NEW_CHOOSE_ACTOR, this);
                    } else
                    {
                        skill = getSkillReference(context, clearText);
                        if (skill == null)
                        {
                            return new ParseResult.Fail("  Error was encountered while parsing " + getTokenName());
                        }
                    }
                }
                if (skill != null)
                {
                    context.getListContext().removeFromList(getTokenName(), race, monsterList, skill);
                }
            } else
            {
                /*
                 * Note this is done one-by-one, because .CLEAR. token type
                 * needs to be able to perform the unlink. That could be
                 * changed, but the increase in complexity isn't worth it.
                 * (Changing it to a grouping object that didn't place links in
                 * the graph would also make it harder to trace the source of
                 * class skills, etc.)
                 */
                CDOMReference<Skill> skill = null;
                if (Constants.LST_ALL.equals(tokText))
                {
                    foundAny = true;
                    skill = context.getReferenceContext().getCDOMAllReference(SKILL_CLASS);
                } else
                {
                    foundOther = true;
                    if (Constants.LST_LIST.equals(tokText))
                    {
                        context.getObjectContext().addToList(race, ListKey.NEW_CHOOSE_ACTOR, this);
                    } else
                    {
                        skill = getSkillReference(context, tokText);
                        if (skill == null)
                        {
                            return new ParseResult.Fail("  Error was encountered while parsing " + getTokenName());
                        }
                    }
                }
                if (skill != null)
                {
                    AssociatedPrereqObject apo =
                            context.getListContext().addToList(getTokenName(), race, monsterList, skill);
                    apo.setAssociation(AssociationKey.SKILL_COST, SkillCost.CLASS);
                }
            }
            firstToken = false;
        }
        if (foundAny && foundOther)
        {
            return new ParseResult.Fail(
                    "Non-sensical " + getTokenName() + ": Contains ANY and a specific reference: " + value);
        }
        return ParseResult.SUCCESS;
    }

    private CDOMReference<Skill> getSkillReference(LoadContext context, String tokText)
    {
        if (tokText.endsWith(Constants.PERCENT))
        {
            return new PatternMatchingReference<>(context.getReferenceContext().getCDOMAllReference(SKILL_CLASS),
                    tokText);
        } else
        {
            return TokenUtilities.getTypeOrPrimitive(context, SKILL_CLASS, tokText);
        }
    }

    @Override
    public String[] unparse(LoadContext context, Race race)
    {
        CDOMGroupRef<ClassSkillList> monsterList =
                context.getReferenceContext().getCDOMTypeReference(ClassSkillList.class, "Monster");
        AssociatedChanges<CDOMReference<Skill>> changes =
                context.getListContext().getChangesInList(getTokenName(), race, monsterList);
        Changes<ChooseSelectionActor<?>> listChanges =
                context.getObjectContext().getListChanges(race, ListKey.NEW_CHOOSE_ACTOR);
        List<String> list = new ArrayList<>();
        Collection<CDOMReference<Skill>> removedItems = changes.getRemoved();
        if (removedItems != null && !removedItems.isEmpty())
        {
            if (changes.includesGlobalClear())
            {
                context.addWriteMessage(
                        "Non-sensical relationship in " + getTokenName() + ": global .CLEAR and local .CLEAR. performed");
                return null;
            }
            list.add(Constants.LST_DOT_CLEAR_DOT + ReferenceUtilities.joinLstFormat(removedItems, "|.CLEAR."));
        }
        Collection<ChooseSelectionActor<?>> listRemoved = listChanges.getRemoved();
        if (listRemoved != null && !listRemoved.isEmpty())
        {
            if (listRemoved.contains(this))
            {
                list.add(".CLEAR.LIST");
            }
        }
        if (changes.includesGlobalClear())
        {
            list.add(Constants.LST_DOT_CLEAR);
        }
        MapToList<CDOMReference<Skill>, AssociatedPrereqObject> map = changes.getAddedAssociations();
        if (map != null && !map.isEmpty())
        {
            Set<CDOMReference<Skill>> added = map.getKeySet();
            for (CDOMReference<Skill> ab : added)
            {
                for (AssociatedPrereqObject assoc : map.getListFor(ab))
                {
                    if (!SkillCost.CLASS.equals(assoc.getAssociation(AssociationKey.SKILL_COST)))
                    {
                        context.addWriteMessage("Skill Cost must be " + "CLASS for Token " + getTokenName());
                        return null;
                    }
                }
            }
            list.add(ReferenceUtilities.joinLstFormat(added, Constants.PIPE));
        }
        Collection<ChooseSelectionActor<?>> listAdded = listChanges.getAdded();
        if (listAdded != null && !listAdded.isEmpty())
        {
            for (ChooseSelectionActor<?> csa : listAdded)
            {
                if (csa.getSource().equals(getTokenName()))
                {
                    try
                    {
                        list.add(csa.getLstFormat());
                    } catch (PersistenceLayerException e)
                    {
                        context.addWriteMessage("Error writing Prerequisite: " + e);
                        return null;
                    }
                }
            }
        }
        if (list.isEmpty())
        {
            // Zero indicates no add or clear
            return null;
        }
        return list.toArray(new String[0]);
    }

    @Override
    public Class<Race> getTokenClass()
    {
        return Race.class;
    }

    @Override
    public void applyChoice(ChooseDriver obj, Skill skill, PlayerCharacter pc)
    {
        pc.addMonCSkill(skill, obj);
    }

    @Override
    public void removeChoice(ChooseDriver obj, Skill skill, PlayerCharacter pc)
    {
        pc.removeMonCSkill(skill, obj);
    }

    @Override
    public String getSource()
    {
        return getTokenName();
    }

    @Override
    public String getLstFormat()
    {
        return "LIST";
    }

    @Override
    public Class<Skill> getChoiceClass()
    {
        return SKILL_CLASS;
    }
}
