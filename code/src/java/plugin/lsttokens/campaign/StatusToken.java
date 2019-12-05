/*
 * Copyright (c) 2010 Stefan Radermacher <zaister@users.sourceforge.net>
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
package plugin.lsttokens.campaign;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Status;
import pcgen.core.Campaign;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with STATUS Token
 */
public class StatusToken implements CDOMPrimaryToken<Campaign>
{

    @Override
    public String getTokenName()
    {
        return "STATUS";
    }

    @Override
    public ParseResult parseToken(LoadContext context, Campaign campaign, String value)
    {
        Status set;
        if (value.equalsIgnoreCase("RELEASE"))
        {
            set = Status.Release;
        } else if (value.equalsIgnoreCase("ALPHA"))
        {
            set = Status.Alpha;
        } else if (value.equalsIgnoreCase("BETA"))
        {
            set = Status.Beta;
        } else if (value.equalsIgnoreCase("TESTONLY"))
        {
            set = Status.TestOnly;
        } else
        {
            return new ParseResult.Fail(
                    "You should use 'RELEASE', 'ALPHA', 'BETA', or 'TESTONLY' as the " + getTokenName() + ": " + value);
        }
        context.getObjectContext().put(campaign, ObjectKey.STATUS, set);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Campaign campaign)
    {
        Status status = context.getObjectContext().getObject(campaign, ObjectKey.STATUS);
        if (status == null)
        {
            return null;
        }
        return new String[]{status.toString()};
    }

    @Override
    public Class<Campaign> getTokenClass()
    {
        return Campaign.class;
    }
}
