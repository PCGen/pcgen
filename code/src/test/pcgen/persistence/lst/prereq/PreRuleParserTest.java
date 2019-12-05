/*
 * Copyright 2007 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
import static org.junit.jupiter.api.Assertions.assertFalse;

import pcgen.EnUsLocaleDependentTestCase;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import plugin.pretokens.parser.PreRuleParser;

import org.junit.jupiter.api.Test;

class PreRuleParserTest extends EnUsLocaleDependentTestCase
{
    @Test
    public void testPositive() throws PersistenceLayerException
    {
        PreRuleParser parser = new PreRuleParser();
        Prerequisite prereq = parser.parse("RULE", "1,DISPLAYTYPETRAITS", false, false);

        assertEquals(
                "<prereq kind=\"rule\" key=\"DISPLAYTYPETRAITS\" operator=\"GTEQ\" operand=\"1\" >\n</prereq>\n",
                prereq.toString());
        assertFalse(prereq.isCharacterRequired(), "Prerule should nto need a character");
    }


    @Test
    public void testNegative() throws PersistenceLayerException
    {
        PreRuleParser parser = new PreRuleParser();
        Prerequisite prereq = parser.parse("RULE", "1,DISPLAYTYPETRAITS", true, false);

        assertEquals(
                "<prereq kind=\"rule\" key=\"DISPLAYTYPETRAITS\" operator=\"LT\" operand=\"1\" >\n</prereq>\n",
                prereq.toString());
        assertFalse(prereq.isCharacterRequired(), "Prerule should nto need a character");
    }

}
