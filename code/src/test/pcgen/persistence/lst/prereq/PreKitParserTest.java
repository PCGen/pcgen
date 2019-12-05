/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
package pcgen.persistence.lst.prereq;

import static org.junit.Assert.assertEquals;

import pcgen.EnUsLocaleDependentTestCase;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import plugin.pretokens.parser.PreKitParser;

import org.junit.jupiter.api.Test;

/**
 * Tests PREKIT parsing
 */
@SuppressWarnings("nls")
public class PreKitParserTest extends EnUsLocaleDependentTestCase
{


    /**
     * Test parse.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testParse() throws PersistenceLayerException
    {
        PreKitParser parser = new PreKitParser();

        Prerequisite prereq =
                parser.parse("KIT", "1,Dungeoneering Kit (Common)", false, false);

        assertEquals(
                "<prereq kind=\"kit\" key=\"Dungeoneering Kit (Common)\" operator=\"GTEQ\" operand=\"1\" >\n</prereq>\n",
                prereq.toString());
    }


    /**
     * Test parse negated.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testParseNegated() throws PersistenceLayerException
    {
        PreKitParser parser = new PreKitParser();

        Prerequisite prereq =
                parser.parse("KIT", "1,Dungeoneering Kit (Common)", true, false);

        assertEquals(
                "<prereq kind=\"kit\" key=\"Dungeoneering Kit (Common)\" operator=\"LT\" operand=\"1\" >\n</prereq>\n",
                prereq.toString());
    }

}
