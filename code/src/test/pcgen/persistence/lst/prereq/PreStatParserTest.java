/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
import pcgen.util.TestHelper;
import plugin.pretokens.parser.PreStatParser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class PreStatParserTest extends EnUsLocaleDependentTestCase
{

    @BeforeEach
    void setUp()
    {
        TestHelper.loadPlugins();
    }


    /**
     * Test dex 9.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testDex9() throws PersistenceLayerException
    {
        PreStatParser producer = new PreStatParser();

        Prerequisite prereq = producer.parse("STAT", "1,DEX=9", false, false);

        //assertEquals("<prereq operator=\"gteq\" operand=\"1\" >\n"
        //	+ "<prereq kind=\"stat\" key=\"DEX\" operator=\"gteq\" operand=\"9\" >\n" + "</prereq>\n" + "</prereq>\n",
        //	prereq.toString());
        assertEquals(
                "<prereq kind=\"stat\" key=\"DEX\" operator=\"GTEQ\" operand=\"9\" >\n"
                        + "</prereq>\n", prereq.toString());
    }


    /**
     * Test dex 9 a.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testDex9a() throws PersistenceLayerException
    {
        PreParserFactory parser = PreParserFactory.getInstance();
        Prerequisite prereq = parser.parse("PRESTAT:1,DEX=9");
        assertEquals(
                "<prereq kind=\"stat\" key=\"DEX\" operator=\"GTEQ\" operand=\"9\" >\n"
                        + "</prereq>\n", prereq.toString());
    }


    /**
     * Test dex 9 str 13.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testDex9Str13() throws PersistenceLayerException
    {
        PreStatParser producer = new PreStatParser();

        Prerequisite prereq =
                producer.parse("STAT", "2,DEX=9,STR=13", false, false);
        assertEquals(
                "<prereq operator=\"GTEQ\" operand=\"2\" >\n"
                        + "<prereq kind=\"stat\" key=\"DEX\" operator=\"GTEQ\" operand=\"9\" >\n"
                        + "</prereq>\n"
                        + "<prereq kind=\"stat\" key=\"STR\" operator=\"GTEQ\" operand=\"13\" >\n"
                        + "</prereq>\n" + "</prereq>\n", prereq.toString());
    }


    /**
     * Test dex equal 9.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testDexEqual9() throws PersistenceLayerException
    {
        PreStatParser producer = new PreStatParser();

        Prerequisite prereq = producer.parse("STATEQ", "1,DEX=9", false, false);

        //		assertEquals("<prereq operator=\"gteq\" operand=\"1\" >\n"
        //		+ "<prereq kind=\"stat\" key=\"DEX\" operator=\"eq\" operand=\"9\" >\n" + "</prereq>\n" + "</prereq>\n",
        //		prereq.toString());
        assertEquals(
                "<prereq kind=\"stat\" key=\"DEX\" operator=\"EQ\" operand=\"9\" >\n"
                        + "</prereq>\n", prereq.toString());
    }


    /**
     * Test dex negative.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testDexNegative() throws PersistenceLayerException
    {
        PreStatParser producer = new PreStatParser();

        Prerequisite prereq = producer.parse("STAT", "1,DEX=-1", false, false);

        assertEquals(
                "<prereq kind=\"stat\" key=\"DEX\" operator=\"GTEQ\" operand=\"-1\" >\n"
                        + "</prereq>\n", prereq.toString());
    }


    /**
     * Test empty.
     *
     * @throws PersistenceLayerException the persistence layer exception
     */
    @Test
    public void testEmpty() throws PersistenceLayerException
    {
        PreStatParser producer = new PreStatParser();

        Prerequisite prereq = producer.parse("STAT", "1", false, false);

        assertEquals("<prereq operator=\"GTEQ\" operand=\"1\" >\n"
                + "</prereq>\n", prereq.toString());
    }
}
