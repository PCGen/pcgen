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
import plugin.pretokens.parser.PreDeityDomainParser;
import plugin.pretokens.writer.PreDeityDomainWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PreDeityDomainRoundRobin extends AbstractBasicRoundRobin
{
    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        TokenRegistration.register(new PreDeityDomainParser());
        TokenRegistration.register(new PreDeityDomainWriter());
    }

    @Override
    public String getBaseString()
    {
        return "DEITYDOMAIN";
    }

    @Override
    public boolean isTypeAllowed()
    {
        return false;
    }

    @Test
    public void testY()
    {
        runRoundRobin("PRE" + getBaseString() + ":1,Y");
    }

    @Test
    public void testN()
    {
        runRoundRobin("PRE" + getBaseString() + ":1,N");
    }

    @Test
    public void testPantheon()
    {
        runRoundRobin("PRE" + getBaseString() + ":1,PANTHEON.Foo");
    }

    @Test
    public void testMultiplePantheon()
    {
        runRoundRobin("PRE" + getBaseString() + ":1,PANTHEON.Bar,PANTHEON.Foo");
    }

    @Test
    public void testPantheonComplex()
    {
        runRoundRobin("PRE" + getBaseString() + ":3,Foo,PANTHEON.Bar");
    }

}
