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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with HIDETYPE Token
 */
public class HidetypeToken extends AbstractTokenWithSeparator<Campaign> implements CDOMPrimaryToken<Campaign>
{

    @Override
    public String getTokenName()
    {
        return "HIDETYPE";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, Campaign campaign, String value)
    {
        ListKey<String> lk = null;
        String types = null;
        if (value.startsWith("EQUIP|"))
        {
            lk = ListKey.HIDDEN_Equipment;
            types = value.substring(6);
        } else if (value.startsWith("FEAT|"))
        {
            lk = ListKey.HIDDEN_Ability;
            types = value.substring(5);
        } else if (value.startsWith("SKILL|"))
        {
            lk = ListKey.HIDDEN_Skill;
            types = value.substring(6);
        }
        if (lk == null)
        {
            return new ParseResult.Fail(
                    getTokenName() + " did not understand: " + value + " in " + campaign.getKeyName());
        }
        StringTokenizer st = new StringTokenizer(types, Constants.PIPE);
        while (st.hasMoreTokens())
        {
            context.getObjectContext().addToList(campaign, lk, st.nextToken());
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Campaign campaign)
    {
        List<String> returnList = new ArrayList<>();
        Changes<String> ech = context.getObjectContext().getListChanges(campaign, ListKey.HIDDEN_Equipment);
        Changes<String> ach = context.getObjectContext().getListChanges(campaign, ListKey.HIDDEN_Ability);
        Changes<String> sch = context.getObjectContext().getListChanges(campaign, ListKey.HIDDEN_Skill);

        Collection<String> added = ech.getAdded();
        if (added != null)
        {
            returnList.add("EQUIP|" + StringUtil.join(added, Constants.PIPE));
        }
        added = ach.getAdded();
        if (added != null)
        {
            returnList.add("FEAT|" + StringUtil.join(added, Constants.PIPE));
        }
        added = sch.getAdded();
        if (added != null)
        {
            returnList.add("SKILL|" + StringUtil.join(added, Constants.PIPE));
        }
        if (returnList.isEmpty())
        {
            // Empty is okay - no token
            return null;
        }
        return returnList.toArray(new String[0]);
    }

    @Override
    public Class<Campaign> getTokenClass()
    {
        return Campaign.class;
    }
}
