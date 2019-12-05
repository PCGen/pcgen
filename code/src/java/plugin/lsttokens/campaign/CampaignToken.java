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

import java.net.URI;

import pcgen.core.Campaign;
import pcgen.persistence.lst.InstallLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with CAMPAIGN Token
 */
public class CampaignToken extends AbstractNonEmptyToken<Campaign>
        implements CDOMPrimaryToken<Campaign>, InstallLstToken
{

    @Override
    public String getTokenName()
    {
        return "CAMPAIGN";
    }

    @Override
    public boolean parse(Campaign campaign, String value, URI sourceUri)
    {
        campaign.setName(value.intern());
        return true;
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, Campaign campaign, String value)
    {
        /*
         * TODO This violates the CDOM direct-access token rules, but given how
         * complicated NAME is, this is much easier to cheat here, and fix
         * later. This really has no major negative effect since campaigns can't
         * be .MODed or .COPYed, so direct access isn't a critical problem. -
         * thpr 12/23/08
         */
        campaign.setName(value.intern());
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Campaign campaign)
    {
        return new String[]{campaign.getDisplayName()};
    }

    @Override
    public Class<Campaign> getTokenClass()
    {
        return Campaign.class;
    }

}
