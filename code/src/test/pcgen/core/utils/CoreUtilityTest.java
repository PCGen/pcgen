/*
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
package pcgen.core.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.system.PCGenPropBundle;

import org.junit.jupiter.api.Test;

/**
 * Tests the CoreUtility class.
 */
public class CoreUtilityTest
{
    @Test
    public void testisNetURL() throws MalformedURLException
    {
        URL https = new URL("https://127.0.0.1");
        URL http = new URL("http://127.0.0.1");
        URL ftp = new URL("ftp://127.0.0.1");
        assertTrue(CoreUtility.isNetURL(https));
        assertTrue(CoreUtility.isNetURL(http));
        assertTrue(CoreUtility.isNetURL(ftp));
    }

    /**
     * Test unsplit string (join method).
     */
    @Test
    public void testJoin()
    {
        final String sep = "|";
        final List<String> list = List.of("one", "two", "three", "four");
        final String result = StringUtil.join(list, sep);
        final String trueResult = "one|two|three|four";
        assertEquals(trueResult, result, "join returned bad String");
    }

    @Test
    public void testCompareVersions()
    {
        int[] firstVer = {5, 13, 6};
        int[] secondVer = {5, 13, 6};

        assertEquals(0, CoreUtility.compareVersions(firstVer, secondVer), "Check for equal values");
        secondVer[2] = 4;
        assertEquals(1, CoreUtility.compareVersions(firstVer, secondVer), "Check for first later");
        secondVer[2] = 7;
        assertEquals(-1, CoreUtility.compareVersions(firstVer, secondVer), "Check for first earlier");
        secondVer[2] = 6;
        secondVer[1] = 12;
        assertEquals(1, CoreUtility.compareVersions(firstVer, secondVer), "Check for first later");
        secondVer[1] = 14;
        assertEquals(-1, CoreUtility.compareVersions(firstVer, secondVer), "Check for first earlier");
        secondVer[1] = 13;
        secondVer[0] = 4;
        assertEquals(1, CoreUtility.compareVersions(firstVer, secondVer), "Check for first later");
        secondVer[0] = 6;
        assertEquals(-1, CoreUtility.compareVersions(firstVer, secondVer), "Check for first earlier");
    }

    @Test
    public void testCompareVersionsString()
    {
        String firstVer = "5.13.6";

        assertEquals(0, CoreUtility.compareVersions(firstVer, firstVer), "Check for equal values");
        assertEquals(1, CoreUtility.compareVersions(firstVer, "5.13.4"), "Check for first later");
    }

    @Test
    public void testConvertVersionToNumber()
    {
        int[] norc = CoreUtility.convertVersionToNumber("5.13.6");
        assertArrayEquals(new int[]{5, 13, 6}, norc, "pcgen version");
        int[] rc = CoreUtility.convertVersionToNumber("5.13.6 RC1");
        assertArrayEquals(new int[]{5, 13, 6}, rc, "pcgen version");
    }

    @Test
    public void testIsCurrMinorVer()
    {
        String currVerStr = PCGenPropBundle.getVersionNumber();
        assertTrue(CoreUtility
                .isCurrMinorVer(currVerStr), "Check for same verison");
        int[] currVer = CoreUtility.convertVersionToNumber(currVerStr);
        currVer[2] = 99;
        String verStr = currVer[0] + "." + currVer[1] + "." + currVer[2];
        assertTrue(CoreUtility
                .isCurrMinorVer(verStr), "Check for differing release");
        int oldMinor = currVer[1];
        currVer[1] = 99;
        verStr = currVer[0] + "." + currVer[1] + "." + currVer[2];
        assertFalse(CoreUtility
                .isCurrMinorVer(verStr), "Check for differing minor");
        currVer[1] = oldMinor;
        verStr = currVer[0] + "." + currVer[1] + "." + currVer[2];
        assertTrue(CoreUtility
                .isCurrMinorVer(verStr), "Check for returned minor");
        currVer[0] = 2;
        verStr = currVer[0] + "." + currVer[1] + "." + currVer[2];
        assertFalse(CoreUtility
                .isCurrMinorVer(verStr), "Check for differing major");
    }

    @Test
    public void testIsPriorToCurrent()
    {
        String currVerStr = PCGenPropBundle.getVersionNumber();
        assertTrue(CoreUtility
                .isPriorToCurrent(currVerStr), "Check for same verison");
        int[] currVer = CoreUtility.convertVersionToNumber(currVerStr);
        currVer[2] = 99;
        String verStr = currVer[0] + "." + currVer[1] + "." + currVer[2];
        assertFalse(CoreUtility
                .isPriorToCurrent(verStr), "Check for differing release");
        currVer[2] = 0;
        verStr = currVer[0] + "." + currVer[1] + "." + currVer[2];
        assertTrue(CoreUtility
                .isPriorToCurrent(verStr), "Check for earlier release");
        currVer[1] = 99;
        verStr = currVer[0] + "." + currVer[1] + "." + currVer[2];
        assertFalse(CoreUtility
                .isPriorToCurrent(verStr), "Check for differing minor");
        currVer[1] = 0;
        verStr = currVer[0] + "." + currVer[1] + "." + currVer[2];
        assertTrue(CoreUtility
                .isPriorToCurrent(verStr), "Check for earlier minor");
        currVer[0] = 99;
        verStr = currVer[0] + "." + currVer[1] + "." + currVer[2];
        assertFalse(CoreUtility
                .isPriorToCurrent(verStr), "Check for differing major");
        currVer[0] = 0;
        verStr = currVer[0] + "." + currVer[1] + "." + currVer[2];
        assertTrue(CoreUtility
                .isPriorToCurrent(verStr), "Check for earlier major");
    }
}
