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
import plugin.pretokens.parser.PreClassLevelMaxParser;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.writer.PreClassWriter;

public class PreClassLevelMaxRobin extends AbstractPreRoundRobin
{


    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        TokenRegistration.register(new PreClassLevelMaxParser());
        TokenRegistration.register(new PreClassParser());
        TokenRegistration.register(new PreClassWriter());
    }

    public void testBasic()
    {
        runSimpleRoundRobin("PRECLASSLEVELMAX:1,Barbarian=1",
                "!PRECLASS:1,Barbarian=2");
    }

    public void testNegated()
    {
        runSimpleRoundRobin("!PRECLASSLEVELMAX:1,Barbarian=1",
                "PRECLASS:1,Barbarian=2");
    }

    public void testSimple()
    {
        runSimpleRoundRobin("PRECLASSLEVELMAX:1,Fighter=1,SPELLCASTER=2",
                "PREMULT:1,[!PRECLASS:1,Fighter=2],[!PRECLASS:1,SPELLCASTER=3]");
    }

    public void testTwo()
    {
        runSimpleRoundRobin("PRECLASSLEVELMAX:2,Fighter=1,SPELLCASTER=2",
                "!PRECLASS:1,Fighter=2,SPELLCASTER=3");
    }

}
