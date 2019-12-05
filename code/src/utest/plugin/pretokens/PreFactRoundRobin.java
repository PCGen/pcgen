/*
 * Copyright James Dempsey, 2015
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.pretokens;

import pcgen.base.format.StringManager;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.facet.FacetInitialization;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreFactParser;
import plugin.pretokens.writer.PreFactWriter;

/**
 * The Class {@code PreFactRoundRobin} tests the parsing and unparsing of
 * PREFACTs.
 */
public class PreFactRoundRobin extends AbstractPreRoundRobin
{
    private static boolean initialised = false;
    private static final StringManager STR_MGR = new StringManager();

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        TokenRegistration.register(new PreFactParser());
        TokenRegistration.register(new PreFactWriter());
        FactKey.getConstant("Foo", STR_MGR);
        FactKey.getConstant("Bard_Archetype_BardicKnowledge", STR_MGR);
        FactKey.getConstant("Bard_Archetype_Countersong", STR_MGR);
        FactKey.getConstant("Bard_Archetype_BardicPerformance", STR_MGR);

        if (!initialised)
        {
            FacetInitialization.initialize();
            initialised = true;
        }
    }

    public void testBoolean()
    {
        runPositiveRoundRobin("PREFACT:1,RACE,Foo=true");
    }

    public void testString()
    {
        runPositiveRoundRobin("PREFACT:1,RACE,Foo=Bar");
    }

    public void testMultipleBoolean()
    {
        runPositiveRoundRobin("PREFACT:1,RACE,"
                + "Bard_Archetype_BardicKnowledge=True,"
                + "Bard_Archetype_Countersong=True,"
                + "Bard_Archetype_BardicPerformance=True");
    }

}
