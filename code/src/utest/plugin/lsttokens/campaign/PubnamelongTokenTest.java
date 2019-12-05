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

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractStringTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class PubnamelongTokenTest extends AbstractStringTokenTestCase<Campaign>
{

    static PubnamelongToken token = new PubnamelongToken();
    static CDOMTokenLoader<Campaign> loader = new CDOMTokenLoader<>();

    @Override
    public Class<Campaign> getCDOMClass()
    {
        return Campaign.class;
    }

    @Override
    public CDOMLoader<Campaign> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<Campaign> getToken()
    {
        return token;
    }

    @Override
    protected boolean isClearLegal()
    {
        return false;
    }

    @Override
    public StringKey getStringKey()
    {
        return StringKey.PUB_NAME_LONG;
    }
}
