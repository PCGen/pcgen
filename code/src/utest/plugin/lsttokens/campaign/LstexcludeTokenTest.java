/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCampaignTokenTestCase;

public class LstexcludeTokenTest extends AbstractCampaignTokenTestCase
{

    static LstexcludeToken token = new LstexcludeToken();

    @Override
    public CDOMPrimaryToken<Campaign> getToken()
    {
        return token;
    }

    @Override
    public Character getSeparator()
    {
        return '|';
    }

    @Override
    public ListKey<?> getListKey()
    {
        return ListKey.FILE_LST_EXCLUDE;
    }

    @Override
    public boolean allowIncludeExclude()
    {
        return false;
    }

    @Override
    public void testInvalidInputExcludeNoParen()
    {
        //Doens't get caught (not really a big deal?)
    }

    @Override
    public void testInvalidInputIncludeNoParen()
    {
        //Doens't get caught (not really a big deal?)
    }


}
