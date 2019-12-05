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
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.content.ChangeProf;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.WeaponProf;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;

public class ChangeprofLst extends AbstractTokenWithSeparator<CDOMObject> implements CDOMPrimaryToken<CDOMObject>
{

    private static final Class<WeaponProf> WEAPONPROF_CLASS = WeaponProf.class;

    @Override
    public String getTokenName()
    {
        return "CHANGEPROF";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String value)
    {
        if (obj instanceof Ungranted)
        {
            return new ParseResult.Fail(
                    "Cannot use " + getTokenName() + " on an Ungranted object type: " + obj.getClass().getSimpleName());
        }
        // value should be of the format:
        // Name1,TYPE.type1,Name3=Prof1|Name4,Name5=Prof2
        //
        // e.g.: TYPE.Hammer,Hand Axe=Simple|Urgosh,Waraxe=Martial

        StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
        List<ChangeProf> list = new ArrayList<>();

        while (tok.hasMoreTokens())
        {
            String tokText = tok.nextToken();
            int equalLoc = tokText.indexOf('=');
            if (equalLoc < 0)
            {
                ComplexParseResult cpr = new ComplexParseResult();
                cpr.addErrorMessage(
                        "Improper " + getTokenName() + ": No = found. " + "Expect format to be <Prof>,<Prof>=<Prof Type>");
                cpr.addErrorMessage("  Token was: " + tokText);
                cpr.addErrorMessage("  Tag was: " + value);
                return cpr;
            } else if (equalLoc != tokText.lastIndexOf('='))
            {
                ComplexParseResult cpr = new ComplexParseResult();
                cpr.addErrorMessage("Improper " + getTokenName() + ": Two = found.  "
                        + "Expect format to be <Prof>,<Prof>=<Prof Type>");
                cpr.addErrorMessage("  Token was: " + tokText);
                cpr.addErrorMessage("  Tag was: " + value);
                return cpr;
            }

            String newType = tokText.substring(equalLoc + 1);
            if (newType.isEmpty())
            {
                ComplexParseResult cpr = new ComplexParseResult();
                cpr.addErrorMessage("Improper " + getTokenName() + ": Empty Result Type.  "
                        + "Expect format to be <Prof>,<Prof>=<Prof Type>");
                cpr.addErrorMessage("  Token was: " + tokText);
                cpr.addErrorMessage("  Tag was: " + value);
                return cpr;
            }
            if (newType.contains(Constants.DOT))
            {
                ComplexParseResult cpr = new ComplexParseResult();
                cpr.addErrorMessage(
                        "Improper " + getTokenName() + ": Invalid (Compound) Result Type: cannot contain a period (.)  "
                                + "Expect format to be <Prof>,<Prof>=<Prof Type>");
                cpr.addErrorMessage("  Token was: " + tokText);
                cpr.addErrorMessage("  Tag was: " + value);
                return cpr;
            }
            String[] val = {newType};

            CDOMGroupRef<WeaponProf> newTypeProf =
                    context.getReferenceContext().getCDOMTypeReference(WEAPONPROF_CLASS, val);

            String profs = tokText.substring(0, equalLoc);
            if (profs.isEmpty())
            {
                ComplexParseResult cpr = new ComplexParseResult();
                cpr.addErrorMessage("Improper " + getTokenName() + ": Empty Source Prof.  "
                        + "Expect format to be <Prof>,<Prof>=<Prof Type>");
                cpr.addErrorMessage("  Token was: " + tokText);
                cpr.addErrorMessage("  Tag was: " + value);
                return cpr;
            }

            StringTokenizer pTok = new StringTokenizer(profs, Constants.COMMA);
            while (pTok.hasMoreTokens())
            {
                CDOMReference<WeaponProf> wp =
                        TokenUtilities.getTypeOrPrimitive(context, WEAPONPROF_CLASS, pTok.nextToken());
                list.add(new ChangeProf(wp, newTypeProf));
            }
        }
        for (ChangeProf cp : list)
        {
            context.getObjectContext().addToList(obj, ListKey.CHANGEPROF, cp);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject obj)
    {
        Changes<ChangeProf> changes = context.getObjectContext().getListChanges(obj, ListKey.CHANGEPROF);
        Collection<ChangeProf> added = changes.getAdded();
        if (added == null || added.isEmpty())
        {
            // Zero indicates no Token
            return null;
        }
        HashMapToList<CDOMGroupRef<WeaponProf>, CDOMReference<WeaponProf>> m = new HashMapToList<>();
        for (ChangeProf cp : added)
        {
            CDOMReference<WeaponProf> source = cp.getSource();
            CDOMGroupRef<WeaponProf> result = cp.getResult();
            m.addToListFor(result, source);
        }

        SortedSet<CDOMReference<WeaponProf>> set = new TreeSet<>(ReferenceUtilities.REFERENCE_SORTER);
        Set<String> returnSet = new TreeSet<>();
        for (CDOMGroupRef<WeaponProf> result : m.getKeySet())
        {
            StringBuilder sb = new StringBuilder();
            boolean needComma = false;
            set.clear();
            set.addAll(m.getListFor(result));
            for (CDOMReference<WeaponProf> source : set)
            {
                if (needComma)
                {
                    sb.append(Constants.COMMA);
                }
                needComma = true;
                String sourceLst = source.getLSTformat(false);
                if (sourceLst.startsWith(Constants.LST_TYPE_EQUAL))
                {
                    sb.append(Constants.LST_TYPE_DOT);
                    sb.append(sourceLst.substring(5));
                } else
                {
                    sb.append(sourceLst);
                }
            }
            sb.append(Constants.EQUALS).append(result.getLSTformat(false).substring(5));
            returnSet.add(sb.toString());
        }
        return new String[]{StringUtil.join(returnSet, Constants.PIPE)};
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }
}
