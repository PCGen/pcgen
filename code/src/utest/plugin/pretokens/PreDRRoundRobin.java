/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.pretokens;

import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreDamageReductionParser;
import plugin.pretokens.writer.PreDamageReductionWriter;

import org.junit.jupiter.api.BeforeEach;

public class PreDRRoundRobin extends AbstractRankedRoundRobin
{
    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        TokenRegistration.register(new PreDamageReductionParser());
        TokenRegistration.register(new PreDamageReductionWriter());
    }

    @Override
    public String getBaseString()
    {
        return "DR";
    }

    @Override
    public boolean isAnyAllowed()
    {
        return false;
    }

    @Override
    public boolean isTypeAllowed()
    {
        return false;
    }
}
