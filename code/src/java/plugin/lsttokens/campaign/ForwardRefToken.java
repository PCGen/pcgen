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
package plugin.lsttokens.campaign;

import java.util.Collection;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.Qualifier;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Campaign;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;

public class ForwardRefToken extends AbstractTokenWithSeparator<Campaign> implements CDOMPrimaryToken<Campaign>
{

    @Override
    public String getTokenName()
    {
        return "FORWARDREF";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, Campaign obj, String value)
    {
        int pipeLoc = value.indexOf('|');
        if (pipeLoc == -1)
        {
            return new ParseResult.Fail(
                    getTokenName() + " requires at least two arguments, " + "ReferenceType and Key: " + value);
        }
        if (value.lastIndexOf('|') != pipeLoc)
        {
            ComplexParseResult cpr = new ComplexParseResult();
            cpr.addErrorMessage(getTokenName() + " requires at only two pipe separated arguments, "
                    + "ReferenceType and Keys: " + value);
            cpr.addErrorMessage("  keys are comma separated");
            return cpr;
        }
        String firstToken = value.substring(0, pipeLoc);
        if ("FEAT".equals(firstToken))
        {
            firstToken = "ABILITY=FEAT";
        }
        ReferenceManufacturer<? extends Loadable> rm = context.getManufacturer(firstToken);
        if (rm == null)
        {
            return new ParseResult.Fail(getTokenName() + " unable to generate manufacturer for type: " + firstToken);
        }

        String rest = value.substring(pipeLoc + 1);
        ParseResult pr = checkForIllegalSeparator(',', rest);
        if (!pr.passed())
        {
            return pr;
        }
        StringTokenizer st = new StringTokenizer(rest, Constants.COMMA);
        while (st.hasMoreTokens())
        {
            CDOMSingleRef<? extends Loadable> ref = rm.getReference(st.nextToken());
            context.getObjectContext().addToList(obj, ListKey.FORWARDREF, new Qualifier(ref));
        }

        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Campaign obj)
    {
        Changes<Qualifier> changes = context.getObjectContext().getListChanges(obj, ListKey.FORWARDREF);
        if (changes == null || changes.isEmpty())
        {
            return null;
        }
        Collection<Qualifier> quals = changes.getAdded();
        HashMapToList<String, CDOMSingleRef<?>> map = new HashMapToList<>();
        for (Qualifier qual : quals)
        {
            CDOMSingleRef<?> ref = qual.getQualifiedReference();
            String key = ref.getPersistentFormat();
            map.addToListFor(key, ref);
        }
        Set<CDOMSingleRef<?>> set = new TreeSet<>(ReferenceUtilities.REFERENCE_SORTER);
        Set<String> returnSet = new TreeSet<>();
        for (String key : map.getKeySet())
        {
            set.clear();
            set.addAll(map.getListFor(key));
            returnSet.add(key + Constants.PIPE + ReferenceUtilities.joinLstFormat(set, Constants.COMMA));
        }
        return returnSet.toArray(new String[0]);
    }

    @Override
    public Class<Campaign> getTokenClass()
    {
        return Campaign.class;
    }
}
