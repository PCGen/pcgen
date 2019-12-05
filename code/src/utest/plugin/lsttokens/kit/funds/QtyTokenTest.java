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
package plugin.lsttokens.kit.funds;

import pcgen.core.kit.KitFunds;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractKitTokenTestCase;

import org.junit.jupiter.api.Test;

public class QtyTokenTest extends AbstractKitTokenTestCase<KitFunds>
{

    static QtyToken token = new QtyToken();
    static CDOMSubLineLoader<KitFunds> loader = new CDOMSubLineLoader<>(
            "SPELLS", KitFunds.class);

    @Override
    public Class<KitFunds> getCDOMClass()
    {
        return KitFunds.class;
    }

    @Override
    public CDOMSubLineLoader<KitFunds> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<KitFunds> getToken()
    {
        return token;
    }

    @Test
    public void testRoundRobinNumber() throws PersistenceLayerException
    {
        runRoundRobin("3");
    }

    @Test
    public void testRoundRobinFormula() throws PersistenceLayerException
    {
        runRoundRobin("Formula");
    }
}
