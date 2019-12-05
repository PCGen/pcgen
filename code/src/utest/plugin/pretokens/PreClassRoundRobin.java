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

import static org.junit.jupiter.api.Assertions.fail;

import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.writer.PreClassWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PreClassRoundRobin extends AbstractRankedRoundRobin
{
    @BeforeEach
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        TokenRegistration.register(new PreClassParser());
        TokenRegistration.register(new PreClassWriter());
    }

    @Override
    public String getBaseString()
    {
        return "CLASS";
    }

    @Override
    public boolean isAnyAllowed()
    {
        return true;
    }

    @Override
    public boolean isTypeAllowed()
    {
        return true;
    }

    @Test
    public void testSpellcaster()
    {
        runRoundRobin("PRECLASS:1,SPELLCASTER=2");
    }

    @Test
    public void testSpellcasterTyped()
    {
        runRoundRobin("PRECLASS:1,SPELLCASTER.Arcane=2");
    }

    @Test
    public void testNestedInvalid()
    {
        try
        {
            String prereqStr = "PRECLASS:1,Oracle=2[!PRESPELL:1,Veil of Heaven]";
            Prerequisite p = PreParserFactory.getInstance().parse(prereqStr);
            fail(() -> "Expected " + prereqStr + " to be rejected but got " + p);
        } catch (PersistenceLayerException e)
        {
            // Expected
        }
    }
}
