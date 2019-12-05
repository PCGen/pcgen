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
package plugin.lsttokens.template;

import java.util.Collection;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SubClassCategory;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.SubClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with FAVOREDCLASS Token
 */
public class FavoredclassToken extends AbstractTokenWithSeparator<PCTemplate>
        implements CDOMPrimaryToken<PCTemplate>, ChooseSelectionActor<PCClass>
{

    public static final Class<PCClass> PCCLASS_CLASS = PCClass.class;
    public static final Class<SubClass> SUBCLASS_CLASS = SubClass.class;

    @Override
    public String getTokenName()
    {
        return "FAVOREDCLASS";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, PCTemplate pct, String value)
    {
        context.getObjectContext().remove(pct, ObjectKey.ANY_FAVORED_CLASS);
        context.getObjectContext().removeList(pct, ListKey.FAVORED_CLASS);
        context.getObjectContext().removeFromList(pct, ListKey.NEW_CHOOSE_ACTOR, this);
        return parseFavoredClass(context, pct, value);
    }

    public ParseResult parseFavoredClass(LoadContext context, CDOMObject cdo, String value)
    {
        boolean foundAny = false;
        boolean foundOther = false;

        StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

        while (tok.hasMoreTokens())
        {
            String token = tok.nextToken();
            if (Constants.HIGHEST_LEVEL_CLASS.equalsIgnoreCase(token))
            {
                foundAny = true;
                context.getObjectContext().put(cdo, ObjectKey.ANY_FAVORED_CLASS, true);
            } else if (Constants.LST_PERCENT_LIST.equalsIgnoreCase(token))
            {
                context.getObjectContext().addToList(cdo, ListKey.NEW_CHOOSE_ACTOR, this);
            } else
            {
                CDOMReference<? extends PCClass> ref;
                foundOther = true;
                int dotLoc = token.indexOf('.');
                if (dotLoc == -1)
                {
                    // Primitive
                    ref = context.getReferenceContext().getCDOMReference(PCCLASS_CLASS, token);
                } else
                {
                    ParseResult pr = checkForIllegalSeparator('.', token);
                    if (!pr.passed())
                    {
                        return pr;
                    }
                    // SubClass
                    String parent = token.substring(0, dotLoc);
                    String subclass = token.substring(dotLoc + 1);
                    SubClassCategory scc = SubClassCategory.getConstant(parent);
                    ref = context.getReferenceContext().getManufacturerId(scc).getReference(subclass);
                }
                context.getObjectContext().addToList(cdo, ListKey.FAVORED_CLASS, ref);
            }
        }
        if (foundAny && foundOther)
        {
            return new ParseResult.Fail("Non-sensical " + getTokenName() + ": Contains " + Constants.HIGHEST_LEVEL_CLASS
                    + " and a specific reference: " + value);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, PCTemplate pct)
    {
        Changes<CDOMReference<? extends PCClass>> changes =
                context.getObjectContext().getListChanges(pct, ListKey.FAVORED_CLASS);
        Changes<ChooseSelectionActor<?>> listChanges =
                context.getObjectContext().getListChanges(pct, ListKey.NEW_CHOOSE_ACTOR);
        Boolean anyfavored = context.getObjectContext().getObject(pct, ObjectKey.ANY_FAVORED_CLASS);
        SortedSet<String> set = new TreeSet<>();
        if (anyfavored != null && anyfavored)
        {
            set.add(Constants.HIGHEST_LEVEL_CLASS);
        }
        if (changes != null && !changes.isEmpty() && changes.hasAddedItems())
        {
            for (CDOMReference<? extends PCClass> ref : changes.getAdded())
            {
                String prefix = ref.getPersistentFormat();
                if (prefix.startsWith("SUBCLASS="))
                {
                    set.add(prefix.substring(9) + Constants.DOT + ref.getLSTformat(false));
                } else
                {
                    set.add(ref.getLSTformat(false));
                }
            }
        }
        Collection<ChooseSelectionActor<?>> listAdded = listChanges.getAdded();
        if (listAdded != null && !listAdded.isEmpty())
        {
            for (ChooseSelectionActor<?> cra : listAdded)
            {
                if (cra.getSource().equals(getTokenName()))
                {
                    try
                    {
                        set.add(cra.getLstFormat());
                    } catch (PersistenceLayerException e)
                    {
                        context.addWriteMessage("Error writing Prerequisite: " + e);
                        return null;
                    }
                }
            }
        }
        if (set.isEmpty())
        {
            // Zero indicates no add or clear
            return null;
        }
        return new String[]{StringUtil.join(set, Constants.PIPE)};
    }

    @Override
    public Class<PCTemplate> getTokenClass()
    {
        return PCTemplate.class;
    }

    @Override
    public String getSource()
    {
        return getTokenName();
    }

    @Override
    public String getLstFormat()
    {
        return Constants.LST_PERCENT_LIST;
    }

    @Override
    public void applyChoice(ChooseDriver obj, PCClass cls, PlayerCharacter pc)
    {
        pc.addFavoredClass(cls, obj);
    }

    @Override
    public void removeChoice(ChooseDriver obj, PCClass cls, PlayerCharacter pc)
    {
        pc.removeFavoredClass(cls, obj);
    }

    @Override
    public Class<PCClass> getChoiceClass()
    {
        return PCCLASS_CLASS;
    }
}
