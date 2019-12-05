/*
 * Copyright 2008 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.TreeMapToList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.core.Ability;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.StringPClassUtil;

/**
 * Deals with the SERVESAS token for Abilities
 */
public class ServesAsToken extends AbstractTokenWithSeparator<CDOMObject> implements CDOMPrimaryToken<CDOMObject>
{

    @Override
    public String getTokenName()
    {
        return "SERVESAS";
    }

    public List<Class<? extends PObject>> getLegalTypes()
    {
        return Arrays.asList(PCClass.class, Ability.class, Skill.class, Race.class
                // Ability.class, Deity.class, Domain.class,Equipment.class,
                // Race.class, Skill.class,Spell.class, PCTemplate.class,
                // WeaponProf.class
        );
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, CDOMObject obj, String value)
    {
        if (!getLegalTypes().contains(obj.getClass()))
        {
            ComplexParseResult cpr = new ComplexParseResult();
            cpr.addErrorMessage("Cannot use SERVESAS on a " + obj.getClass());
            cpr.addErrorMessage("   bad use found in " + obj.getClass().getSimpleName() + ' ' + obj.getKeyName());
            return cpr;
        }
        return super.parseNonEmptyToken(context, obj, value);
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String value)
    {
        StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
        String firstToken = st.nextToken();
        ReferenceManufacturer<? extends Loadable> rm = context.getManufacturer(firstToken);
        if (rm == null)
        {
            return new ParseResult.Fail(getTokenName() + " unable to generate manufacturer for type: " + value);
        }
        if (!st.hasMoreTokens())
        {
            return new ParseResult.Fail(getTokenName() + " must include at least one target object");
        }
        if (!rm.getReferenceClass().equals(obj.getClass()))
        {
            return new ParseResult.Fail(getTokenName() + " expecting a POBJECT Type valid for "
                    + obj.getClass().getSimpleName() + ", found: " + firstToken);
        }

        String servekey = StringPClassUtil.getStringFor(obj.getClass());
        ListKey<CDOMReference> listkey = ListKey.getKeyFor(CDOMReference.class, "SERVES_AS_" + servekey);
        while (st.hasMoreTokens())
        {
            CDOMSingleRef<?> ref = rm.getReference(st.nextToken());
            context.getObjectContext().addToList(obj, listkey, ref);
        }

        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject obj)
    {
        String key = StringPClassUtil.getStringFor(obj.getClass());
        ListKey<CDOMReference> listkey = ListKey.getKeyFor(CDOMReference.class, "SERVES_AS_" + key);
        Changes<CDOMReference> changes = context.getObjectContext().getListChanges(obj, listkey);
        Collection<CDOMReference> removedItems = changes.getRemoved();
        if (removedItems != null && !removedItems.isEmpty() || changes.includesGlobalClear())
        {
            context.addWriteMessage(getTokenName() + " does not support .CLEAR");
            return null;
        }
        if (!changes.hasAddedItems())
        {
            // Zero indicates no Token (and no global clear, so nothing to do)
            return null;
        }
        TreeMapToList<String, String> map = new TreeMapToList<>();
        for (CDOMReference<?> ref : changes.getAdded())
        {
            map.addToListFor(ref.getPersistentFormat(), ref.getLSTformat(false));
        }
        List<String> returnList = new ArrayList<>();
        for (String mapKey : map.getKeySet())
        {
            Set<String> set = new TreeSet<>(map.getListFor(mapKey));
            returnList.add(mapKey + '|' + StringUtil.join(set, Constants.PIPE));
        }
        return returnList.toArray(new String[0]);
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }
}
