/*
 * Copyright 2004 (C) Chris Ward <frugal@purplewombat.co.uk>
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
package pcgen.io;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import pcgen.system.PCGenPropBundle;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;


class PCGVer2ParserTest
{

    @Test
    public void test_1045596_1() throws PCGParseException
    {
        PCGVer2Parser parser = new PCGVer2Parser(null);

        parser.parseVersionLine("VERSION:5.7.1");

        int[] version = parser.getPcgenVersion();
        String suffix = parser.getPcgenVersionSuffix();

        assertEquals(3, version.length);
        assertEquals(5, version[0]);
        assertEquals(7, version[1]);
        assertEquals(1, version[2]);
        assertNull(suffix);
    }

    @Test
    public void test_1045596_2() throws PCGParseException
    {
        PCGVer2Parser parser = new PCGVer2Parser(null);

        parser.parseVersionLine("VERSION:5-7-1");

        int[] version = parser.getPcgenVersion();
        String suffix = parser.getPcgenVersionSuffix();

        assertEquals(3, version.length);
        assertEquals(5, version[0]);
        assertEquals(7, version[1]);
        assertEquals(1, version[2]);
        assertNull(suffix);
    }

    @Test
    public void test_1045596_3() throws PCGParseException
    {
        PCGVer2Parser parser = new PCGVer2Parser(null);

        parser.parseVersionLine("VERSION:5 7 1");

        int[] version = parser.getPcgenVersion();
        String suffix = parser.getPcgenVersionSuffix();

        assertEquals(3, version.length);
        assertEquals(5, version[0]);
        assertEquals(7, version[1]);
        assertEquals(1, version[2]);
        assertNull(suffix);
    }

    @Test
    public void test_1045596_4() throws PCGParseException
    {
        PCGVer2Parser parser = new PCGVer2Parser(null);

        parser.parseVersionLine("VERSION:5.7.1 RC1");

        int[] version = parser.getPcgenVersion();
        String suffix = parser.getPcgenVersionSuffix();

        assertEquals(3, version.length);
        assertEquals(5, version[0]);
        assertEquals(7, version[1]);
        assertEquals(1, version[2]);
        assertEquals("RC1", suffix);
    }

    @Test
    public void test_1045596_5() throws PCGParseException
    {
        PCGVer2Parser parser = new PCGVer2Parser(null);

        parser.parseVersionLine("VERSION:5.7.1.autobuild.20041119-18:00");

        int[] version = parser.getPcgenVersion();
        String suffix = parser.getPcgenVersionSuffix();

        assertEquals(3, version.length);
        assertEquals(5, version[0]);
        assertEquals(7, version[1]);
        assertEquals(1, version[2]);
        assertEquals("autobuild.20041119-18:00", suffix);
    }

    @Test
    public void test_1045596_6()
    {
        PCGVer2Parser parser = new PCGVer2Parser(null);
        PCGParseException pcgParseException =
                assertThrows(PCGParseException.class, () -> parser.parseVersionLine("5.7.1"));
        assertEquals("Not a Version Line.", pcgParseException.getMessage());
    }

    @Test
    public void test_1045596_7()
    {
        PCGVer2Parser parser = new PCGVer2Parser(null);
        PCGParseException pcgParseException =
                assertThrows(PCGParseException.class, () -> parser.parseVersionLine("VERSION:5.7.1RC1"));
        assertEquals("Invalid PCGen version.", pcgParseException.getMessage());
    }

    /**
     * Test parsing of version line for broken 5.12RC1 version number.
     *
     * @throws PCGParseException the PCG parse exception
     */
    @Test
    public void test_1045596_8() throws PCGParseException
    {
        PCGVer2Parser parser = new PCGVer2Parser(null);

        parser.parseVersionLine("VERSION:5.12 RC1");

        int[] version = parser.getPcgenVersion();
        String suffix = parser.getPcgenVersionSuffix();

        assertEquals(3, version.length);
        assertEquals(5, version[0]);
        assertEquals(12, version[1]);
        assertEquals(0, version[2]);
        assertEquals("RC1", suffix);
    }


    /**
     * Test that the currently specified version can be parsed.
     *
     * @throws PCGParseException the PCG parse exception
     */
    @Test
    public void testCurrVersion() throws PCGParseException
    {
        PCGVer2Parser parser = new PCGVer2Parser(null);

        parser.parseVersionLine("VERSION:" + PCGenPropBundle.getVersionNumber());

        int[] version = parser.getPcgenVersion();

        MatcherAssert.assertThat("version length is correct", version.length, is(3));
    }

    @Test
    public void testCompareVersionTo() throws PCGParseException
    {
        PCGVer2Parser parser = new PCGVer2Parser(null);

        parser.parseVersionLine("VERSION:5.13.6");
        assertEquals(0, parser
                .compareVersionTo(new int[]{5, 13, 6}), "Check of a matching version");
        assertEquals(-1, parser
                .compareVersionTo(new int[]{5, 13, 7}), "Check of an earlier version");
        assertEquals(1, parser
                .compareVersionTo(new int[]{5, 13, 5}), "Check of a later version");
    }
}
