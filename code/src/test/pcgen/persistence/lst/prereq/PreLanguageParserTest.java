/*
 *
 * Copyright 2004 (C) Frugal <frugal@purplewombat.co.uk>
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

import static org.junit.Assert.assertEquals;

import pcgen.EnUsLocaleDependentTestCase;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import plugin.pretokens.parser.PreLanguageParser;

import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
public class PreLanguageParserTest extends EnUsLocaleDependentTestCase
{

    /**
     * Test 1 language of 2.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void test1LanguageOf2() throws PersistenceLayerException
    {
        PreLanguageParser parser = new PreLanguageParser();
        Prerequisite prereq =
                parser.parse("LANG", "1,Dwarven,Elven", false, false);

        System.out.println(prereq);
        assertEquals(
                "<prereq operator=\"GTEQ\" operand=\"1\" >\n"
                        + "<prereq kind=\"lang\" count-multiples=\"true\" key=\"Dwarven\" operator=\"GTEQ\" operand=\"1\" >\n"
                        + "</prereq>\n"
                        + "<prereq kind=\"lang\" count-multiples=\"true\" key=\"Elven\" operator=\"GTEQ\" operand=\"1\" >\n"
                        + "</prereq>\n" + "</prereq>\n", prereq.toString());
    }


    /**
     * Test not 1 language of 2.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testNot1LanguageOf2() throws PersistenceLayerException
    {
        PreLanguageParser parser = new PreLanguageParser();
        Prerequisite prereq =
                parser.parse("LANG", "1,Dwarven,Elven", true, false);

        System.out.println(prereq);
        assertEquals(
                "<prereq operator=\"LT\" operand=\"1\" >\n"
                        + "<prereq kind=\"lang\" count-multiples=\"true\" key=\"Dwarven\" operator=\"GTEQ\" operand=\"1\" >\n"
                        + "</prereq>\n"
                        + "<prereq kind=\"lang\" count-multiples=\"true\" key=\"Elven\" operator=\"GTEQ\" operand=\"1\" >\n"
                        + "</prereq>\n" + "</prereq>\n", prereq.toString());
    }


    /**
     * Test 2 language of any.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void test2LanguageOfAny() throws PersistenceLayerException
    {
        PreLanguageParser parser = new PreLanguageParser();
        Prerequisite prereq = parser.parse("LANG", "2,ANY", false, false);

        System.out.println(prereq);
        assertEquals(
                "<prereq kind=\"lang\" count-multiples=\"true\" key=\"ANY\" operator=\"GTEQ\" operand=\"2\" >\n"
                        + "</prereq>\n", prereq.toString());
    }

    /**
     * Test not 2 language of any.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testNot2LanguageOfAny() throws PersistenceLayerException
    {
        PreLanguageParser parser = new PreLanguageParser();
        Prerequisite prereq = parser.parse("LANG", "2,ANY", true, false);

        System.out.println(prereq);
        assertEquals(
                "<prereq kind=\"lang\" count-multiples=\"true\" key=\"ANY\" operator=\"LT\" operand=\"2\" >\n"
                        + "</prereq>\n", prereq.toString());
    }

}
