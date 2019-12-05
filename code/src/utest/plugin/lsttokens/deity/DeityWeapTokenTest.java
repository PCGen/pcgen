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
package plugin.lsttokens.deity;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Deity;
import pcgen.core.WeaponProf;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractListInputTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public class DeityWeapTokenTest extends AbstractListInputTokenTestCase<Deity, WeaponProf>
{
    static DeityweapToken token = new DeityweapToken();
    static CDOMTokenLoader<Deity> loader = new CDOMTokenLoader<>();

    @Override
    public char getJoinCharacter()
    {
        return '|';
    }

    @Override
    public Class<WeaponProf> getTargetClass()
    {
        return WeaponProf.class;
    }

    @Override
    public boolean isTypeLegal()
    {
        return false;
    }

    @Override
    public boolean isAllLegal()
    {
        return true;
    }

    @Override
    public boolean isClearDotLegal()
    {
        return false;
    }

    @Override
    public boolean isClearLegal()
    {
        return true;
    }

    @Override
    public Class<Deity> getCDOMClass()
    {
        return Deity.class;
    }

    @Override
    public CDOMLoader<Deity> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<Deity> getToken()
    {
        return token;
    }

    @Override
    protected WeaponProf construct(LoadContext loadContext, String one)
    {
        return loadContext.getReferenceContext().constructCDOMObject(WeaponProf.class, one);
    }

    @Override
    public boolean allowDups()
    {
        return false;
    }

    @Override
    protected String getAllString()
    {
        return "ANY";
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.removeListFor(ListKey.DEITYWEAPON);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    @Test
    public void testUnparseSingle()
    {
        WeaponProf wp1 = construct(primaryContext, "TestWP1");
        primaryProf.addToListFor(ListKey.DEITYWEAPON, CDOMDirectSingleRef
                .getRef(wp1));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, getLegalValue());
    }

    @Test
    public void testUnparseNullInList()
    {
        primaryProf.addToListFor(ListKey.DEITYWEAPON, null);
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
        WeaponProf wp1 = construct(primaryContext, getLegalValue());
        primaryProf.addToListFor(ListKey.DEITYWEAPON, CDOMDirectSingleRef
                .getRef(wp1));
        WeaponProf wp2 = construct(primaryContext, getAlternateLegalValue());
        primaryProf.addToListFor(ListKey.DEITYWEAPON, CDOMDirectSingleRef
                .getRef(wp2));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, getLegalValue() + getJoinCharacter()
                + getAlternateLegalValue());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUnparseGenericsFail()
    {
        ListKey objectKey = ListKey.DEITYWEAPON;
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
