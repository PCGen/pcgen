/*
 *
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.persistence.lst.prereq;

import static org.junit.jupiter.api.Assertions.assertEquals;

import pcgen.EnUsLocaleDependentTestCase;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import plugin.pretokens.parser.PreVisionParser;

import org.junit.jupiter.api.Test;

class PreVisionParserTest extends EnUsLocaleDependentTestCase
{
    @Test
    public void testMultiplePasses() throws PersistenceLayerException
    {
        PreVisionParser parser = new PreVisionParser();

        Prerequisite prereq =
                parser.parse("VISION", "1,Blindsight=30,Darkvision=30", false,
                        false);

        assertEquals(
                "<prereq operator=\"GTEQ\" operand=\"1\" >\n"
                        + "<prereq kind=\"vision\" count-multiples=\"true\" key=\"Blindsight\" operator=\"GTEQ\" operand=\"30\" >\n"
                        + "</prereq>\n"
                        + "<prereq kind=\"vision\" count-multiples=\"true\" key=\"Darkvision\" operator=\"GTEQ\" operand=\"30\" >\n"
                        + "</prereq>\n" + "</prereq>\n", prereq.toString());
    }
}
