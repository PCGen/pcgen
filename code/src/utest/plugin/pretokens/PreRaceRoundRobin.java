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
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreRaceWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PreRaceRoundRobin extends AbstractBasicRoundRobin
{
    @BeforeEach
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        TokenRegistration.register(new PreRaceParser());
        TokenRegistration.register(new PreRaceWriter());
    }

    @Override
    public String getBaseString()
    {
        return "RACE";
    }

    @Override
    public boolean isTypeAllowed()
    {
        return true;
    }

    @Test
    public void testRaceType()
    {
        runRoundRobin("PRE" + getBaseString() + ":1,RACETYPE=Foo");
    }

    @Test
    public void testRaceTypeCount()
    {
        runRoundRobin("PRE" + getBaseString() + ":2,RACETYPE=Foo");
    }

    @Test
    public void testMultipleRaceType()
    {
        runRoundRobin("PRE" + getBaseString() + ":1,RACETYPE=Bar,RACETYPE=Foo");
    }

    @Test
    public void testRaceTypeComplex()
    {
        runRoundRobin("PRE" + getBaseString() + ":3,Foo,RACETYPE=Foo.Bar");
    }

    @Test
    public void testRaceSubType()
    {
        runRoundRobin("PRE" + getBaseString() + ":1,RACESUBTYPE=Foo");
    }

    @Test
    public void testRaceSubTypeCount()
    {
        runRoundRobin("PRE" + getBaseString() + ":2,RACESUBTYPE=Foo");
    }

    @Test
    public void testMultipleRaceSubType()
    {
        runRoundRobin("PRE" + getBaseString()
                + ":1,RACESUBTYPE=Bar,RACESUBTYPE=Foo");
    }

    @Test
    public void testRaceSubTypeComplex()
    {
        runRoundRobin("PRE" + getBaseString() + ":3,Foo,RACESUBTYPE=Bar");
    }

    @Test
    public void testNegateItem()
    {
        AbstractPreRoundRobin.runSimpleRoundRobin("PRE" + getBaseString() + ":1,Foo,[TYPE=Bar]",
                "PREMULT:2,[PRE" + getBaseString() + ":1,Foo],[!PRE"
                        + getBaseString() + ":1,TYPE=Bar]");
    }

    @Test
    public void testNegateItemRaceType()
    {
        AbstractPreRoundRobin.runSimpleRoundRobin("PRE" + getBaseString()
                + ":1,Foo,[RACETYPE=Bar]", "PREMULT:2,[PRE" + getBaseString()
                + ":1,Foo],[!PRE" + getBaseString() + ":1,RACETYPE=Bar]");
    }

    @Test
    public void testNegateItemRaceSubType()
    {
        AbstractPreRoundRobin.runSimpleRoundRobin("PRE" + getBaseString()
                + ":1,Foo,[RACESUBTYPE=Bar]", "PREMULT:2,[PRE"
                + getBaseString() + ":1,Foo],[!PRE" + getBaseString()
                + ":1,RACESUBTYPE=Bar]");
    }
}
