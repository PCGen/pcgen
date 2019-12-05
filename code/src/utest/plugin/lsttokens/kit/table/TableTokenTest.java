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
package plugin.lsttokens.kit.table;

import pcgen.core.kit.KitTable;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractKitTokenTestCase;

import org.junit.jupiter.api.Test;

public class TableTokenTest extends AbstractKitTokenTestCase<KitTable>
{

    static TableToken token = new TableToken();
    static CDOMSubLineLoader<KitTable> loader = new CDOMSubLineLoader<>(
            "TABLE", KitTable.class);

    @Override
    public Class<KitTable> getCDOMClass()
    {
        return KitTable.class;
    }

    @Override
    public CDOMSubLineLoader<KitTable> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<KitTable> getToken()
    {
        return token;
    }

    @Test
    public void testRoundRobinName() throws PersistenceLayerException
    {
        runRoundRobin("Table Name");
    }
}
