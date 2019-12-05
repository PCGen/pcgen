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
import plugin.pretokens.parser.PreVariableParser;
import plugin.pretokens.writer.PreVariableWriter;

public class PreVarRoundRobin extends AbstractComparatorRoundRobin
{


    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        TokenRegistration.register(new PreVariableParser());
        TokenRegistration.register(new PreVariableWriter());
    }

    public void testSimpleFormula()
    {
        runRoundRobin("INT,5");
    }

    public void testSimpleCompare()
    {
        runRoundRobin("abs(STR),4");
    }

    @Override
    public String getBaseString()
    {
        return "VAR";
    }

    public void testMultipleCompare()
    {
        runRoundRobin("abs(STR),4,abs(INT),3");
    }

    public void testDiffCompare()
    {
        runSimpleRoundRobin(
                "PREMULT:2,[PREVARGT:abs(STR),4],[PREVARLT:abs(INT),3]",
                "PREMULT:2,[PREVARGT:abs(STR),4],[PREVARLT:abs(INT),3]");
    }

    public void testCloseCompare()
    {
        runSimpleRoundRobin(
                "PREMULT:2,[PREVARGT:abs(STR),4],[PREVARGTEQ:abs(INT),3]",
                "PREMULT:2,[PREVARGT:abs(STR),4],[PREVARGTEQ:abs(INT),3]");
    }

    public void testCountOne()
    {
        runSimpleRoundRobin(
                "PREMULT:1,[PREVARGT:abs(STR),4],[PREVARGT:abs(INT),3]",
                "PREMULT:1,[PREVARGT:abs(STR),4],[PREVARGT:abs(INT),3]");
    }

    public void testFunConsolidation()
    {
        runSimpleRoundRobin(
                "PREMULT:2,[PREVARGT:abs(STR),4],[!PREVARLTEQ:abs(INT),3]",
                "PREVARGT:abs(STR),4,abs(INT),3");
    }

    @Override
    public boolean isBaseAllowed()
    {
        return true;
    }

}
