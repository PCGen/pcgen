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
package plugin.lsttokens.editcontext.pcclass.level;

import pcgen.cdom.inst.PCClassLevel;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegerIntegrationTestCase;
import plugin.lsttokens.pcclass.level.HitdieLst;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class HitDieIntegrationTest extends
        AbstractIntegerIntegrationTestCase<PCClassLevel>
{

    private static HitdieLst token = new HitdieLst();
    private static CDOMTokenLoader<PCClassLevel> loader = new CDOMTokenLoader<>();

    @Override
    public Class<PCClassLevel> getCDOMClass()
    {
        return PCClassLevel.class;
    }

    @Override
    public CDOMLoader<PCClassLevel> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<PCClassLevel> getToken()
    {
        return token;
    }

    @Override
    public boolean isNegativeAllowed()
    {
        return false;
    }

    @Override
    public boolean doesOverwrite()
    {
        return true;
    }

    @Override
    public boolean isPositiveAllowed()
    {
        return true;
    }

    @Override
    public boolean isZeroAllowed()
    {
        return false;
    }

    @Override
    protected boolean isClearAllowed()
    {
        return false;
    }
}
