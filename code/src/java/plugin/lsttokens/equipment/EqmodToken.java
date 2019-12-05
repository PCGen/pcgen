/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.equipment;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.helper.EqModRef;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.rules.context.AbstractObjectContext;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

/**
 * Deals with EQMOD token
 */
public class EqmodToken extends AbstractTokenWithSeparator<Equipment> implements CDOMPrimaryToken<Equipment>
{

    private static final Class<EquipmentModifier> EQMOD_CLASS = EquipmentModifier.class;

    private static final String EQMOD_WEIGHT = "_WEIGHTADD";

    private static final String EQMOD_DAMAGE = "_DAMAGE";

    @Override
    public String getTokenName()
    {
        return "EQMOD";
    }

    @Override
    protected char separator()
    {
        return '.';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, Equipment eq, String value)
    {
        StringTokenizer dotTok = new StringTokenizer(value, Constants.DOT);
        EquipmentHead head = eq.getEquipmentHead(1);
        while (dotTok.hasMoreTokens())
        {
            String modInfo = dotTok.nextToken();

            if (modInfo.equalsIgnoreCase(Constants.NONE))
            {
                Logging.deprecationPrint("'NONE' EqMod in " + getTokenName() + " will be ignored", context);
                continue;
            }
            ParseResult pr = checkForIllegalSeparator('|', modInfo);
            if (!pr.passed())
            {
                return pr;
            }
            StringTokenizer aTok = new StringTokenizer(modInfo, Constants.PIPE);

            // The type of EqMod, eg: ABILITYPLUS
            String eqModKey = aTok.nextToken();
            if (eqModKey.equals(EQMOD_WEIGHT))
            {
                if (aTok.hasMoreTokens())
                {
                    context.getObjectContext().put(eq, ObjectKey.WEIGHT_MOD,
                            new BigDecimal(aTok.nextToken().replace(',', '.')));
                }
                continue;
            }

            if (eqModKey.equals(EQMOD_DAMAGE))
            {
                if (aTok.hasMoreTokens())
                {
                    context.getObjectContext().put(eq, StringKey.DAMAGE_OVERRIDE, aTok.nextToken());
                }
                continue;
            }
            CDOMSingleRef<EquipmentModifier> ref =
                    context.getReferenceContext().getCDOMReference(EQMOD_CLASS, eqModKey);
            EqModRef modref = new EqModRef(ref);

            while (aTok.hasMoreTokens())
            {
                modref.addChoice(aTok.nextToken().replace('=', '|'));
            }
            context.getObjectContext().addToList(head, ListKey.EQMOD_INFO, modref);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Equipment eq)
    {
        AbstractObjectContext obj = context.getObjectContext();
        String damage = obj.getString(eq, StringKey.DAMAGE_OVERRIDE);
        Set<String> set = new TreeSet<>();
        if (damage != null)
        {
            set.add(EQMOD_DAMAGE + Constants.PIPE + damage);
        }
        BigDecimal weight = obj.getObject(eq, ObjectKey.WEIGHT_MOD);
        if (weight != null)
        {
            set.add(EQMOD_WEIGHT + Constants.PIPE + weight.toString().replace('.', ','));
        }
        EquipmentHead head = eq.getEquipmentHeadReference(1);
        if (head != null)
        {
            Changes<EqModRef> changes = obj.getListChanges(head, ListKey.EQMOD_INFO);
            Collection<EqModRef> added = changes.getAdded();
            if (added != null)
            {
                for (EqModRef modRef : added)
                {
                    String key = modRef.getRef().getLSTformat(false);
                    StringBuilder sb = new StringBuilder();
                    sb.append(key);
                    for (String s : modRef.getChoices())
                    {
                        sb.append(Constants.PIPE);
                        sb.append(s.replace('|', '='));
                    }
                    set.add(sb.toString());
                }
            }
        }
        if (set.isEmpty())
        {
            return null;
        }
        return new String[]{StringUtil.join(set, Constants.DOT)};
    }

    @Override
    public Class<Equipment> getTokenClass()
    {
        return Equipment.class;
    }

}
