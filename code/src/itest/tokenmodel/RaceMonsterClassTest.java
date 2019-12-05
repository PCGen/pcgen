/*
 * Copyright (c) 2012 Tom Parker <thpr@users.sourceforge.net>
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
package tokenmodel;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.core.Race;
import pcgen.rules.persistence.token.CDOMToken;
import pcgen.rules.persistence.token.ParseResult;
import plugin.lsttokens.race.MonsterclassToken;
import plugin.lsttokens.testsupport.TokenRegistration;

import org.junit.jupiter.api.Test;
import tokenmodel.testsupport.AbstractTokenModelTest;
import util.TestURI;

public class RaceMonsterClassTest extends AbstractTokenModelTest
{

    private static MonsterclassToken token = new MonsterclassToken();

    @Test
    public void testSimple()
    {
        TokenRegistration.register(plugin.bonustokens.Feat.class);
        Race source = create(Race.class, "Source");
        create(PCClass.class, "Granted").put(ObjectKey.IS_MONSTER, Boolean.TRUE);
        ParseResult result = token.parseToken(context, source, "Granted:3");
        if (result != ParseResult.SUCCESS)
        {
            result.printMessages(TestURI.getURI());
            fail("Test Setup Failed");
        }
        finishLoad();
        assertEquals(0, classFacet.getCount(id));
        raceFacet.directSet(id, source, getAssoc());
        assertEquals(1, classFacet.getCount(id));
        assertNotNull(pc.getClassKeyed("Granted"));
        raceFacet.remove(id);
        assertEquals(0, classFacet.getCount(id));
    }

    @Override
    public CDOMToken<?> getToken()
    {
        return token;
    }

}
