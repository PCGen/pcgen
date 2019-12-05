/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.editcontext;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Domain;
import pcgen.core.Kit;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.KitLst;
import plugin.lsttokens.editcontext.testsupport.AbstractListIntegrationTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public class KitIntegrationTest extends
        AbstractListIntegrationTestCase<CDOMObject, Kit>
{

    private static KitLst token = new KitLst();
    private static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<>();

    @Override
    public String getPrefix()
    {
        return "1|";
    }

    @Override
    public Class<Domain> getCDOMClass()
    {
        return Domain.class;
    }

    @Override
    public CDOMLoader<CDOMObject> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<CDOMObject> getToken()
    {
        return token;
    }

    @Override
    public Class<Kit> getTargetClass()
    {
        return Kit.class;
    }

    @Override
    public boolean isTypeLegal()
    {
        return false;
    }

    @Override
    public char getJoinCharacter()
    {
        return '|';
    }

    @Test
    public void dummyTest()
    {
        // Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
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
    public boolean isPrereqLegal()
    {
        return false;
    }

    @Override
    public boolean isAllLegal()
    {
        return true;
    }
}
