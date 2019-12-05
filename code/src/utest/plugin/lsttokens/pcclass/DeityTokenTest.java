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
package plugin.lsttokens.pcclass;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Deity;
import pcgen.core.PCClass;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractListInputTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class DeityTokenTest extends AbstractListInputTokenTestCase<PCClass, Deity>
{

    static DeityToken token = new DeityToken();
    static CDOMTokenLoader<PCClass> loader = new CDOMTokenLoader<>();

    @Override
    public Class<PCClass> getCDOMClass()
    {
        return PCClass.class;
    }

    @Override
    public CDOMLoader<PCClass> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<PCClass> getToken()
    {
        return token;
    }

    @Override
    public Class<Deity> getTargetClass()
    {
        return Deity.class;
    }

    @Override
    public boolean isTypeLegal()
    {
        return false;
    }

    @Override
    public boolean isAllLegal()
    {
        return false;
    }

    @Override
    public boolean isClearDotLegal()
    {
        return false;
    }

    @Override
    public boolean isClearLegal()
    {
        return false;
    }

    @Override
    public char getJoinCharacter()
    {
        return '|';
    }

    @Override
    public boolean allowDups()
    {
        return false;
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.removeListFor(ListKey.DEITY);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    @Test
    public void testUnparseSingle()
    {
        Deity wp1 = construct(primaryContext, "TestWP1");
        primaryProf.addToListFor(ListKey.DEITY, CDOMDirectSingleRef.getRef(wp1));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, getLegalValue());
    }

    @Test
    public void testUnparseNullInList()
    {
        primaryProf.addToListFor(ListKey.DEITY, null);
        try
        {
            getToken().unparse(primaryContext, primaryProf);
            fail();
        } catch (NullPointerException e)
        {
            // Yep!
        }
    }

    @Test
    public void testUnparseMultiple()
    {
        Deity wp1 = construct(primaryContext, getLegalValue());
        primaryProf.addToListFor(ListKey.DEITY, CDOMDirectSingleRef.getRef(wp1));
        Deity wp2 = construct(primaryContext, getAlternateLegalValue());
        primaryProf.addToListFor(ListKey.DEITY, CDOMDirectSingleRef.getRef(wp2));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, getLegalValue() + getJoinCharacter()
                + getAlternateLegalValue());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUnparseGenericsFail()
    {
        ListKey objectKey = ListKey.DEITY;
        primaryProf.addToListFor(objectKey, new Object());
        try
        {
            getToken().unparse(primaryContext, primaryProf);
            fail();
        } catch (ClassCastException e)
        {
            // Yep!
        }
    }
}
