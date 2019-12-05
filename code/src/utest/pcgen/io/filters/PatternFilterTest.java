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
package pcgen.io.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import pcgen.io.EntityEncoder;

import org.junit.jupiter.api.Test;


/**
 * PatternFilterTest checks the functioning of the PatternFilter class.
 */
class PatternFilterTest
{

    @Test
    public void testHtmlFilterStringSingleLine() throws Exception
    {
        OutputFilter filter = new PatternFilter("foo.htm");
        String testString = "foo\r\nbar";
        assertEquals("<p>foo</p>\r\n<p>bar</p>",
                filter.filterString(testString));
        testString = "foo\nbar";
        assertEquals("<p>foo</p>\n<p>bar</p>", filter.filterString(testString));
    }

    @Test
    public void testHtmlFilterStringMultiLine() throws Exception
    {
        OutputFilter filter = new PatternFilter("foo.htm");
        String testString = "foo\r\nbar\nbaz";
        assertEquals("<p>foo</p>\r\n<p>bar</p>\n<p>baz</p>",
                filter.filterString(testString));
    }

    @Test
    public void testHtmlFilterStringComplex() throws Exception
    {
        String testString =
                "Mirror Image&nl;       blindness/deafness, \u2028alter self, cacophoneus call, ";
        testString = EntityEncoder.decode(testString);
        OutputFilter filter = new PatternFilter("foo.htm");
        assertEquals(
                "<p>Mirror Image</p>\n<p>       blindness/deafness, </p>\n<p>alter self, cacophoneus call, </p>",
                filter.filterString(testString));
    }

    @Test
    public void testPdfFilterStringComplex() throws Exception
    {
        String testString =
                "Mirror Image&nl;       blindness/deafness, \u2028alter self, cacophoneus call, ";
        testString = EntityEncoder.decode(testString);
        OutputFilter filter = new PatternFilter("foo.fo");
        assertEquals(
                "<fo:block>Mirror Image</fo:block><fo:block>       blindness/deafness, </fo:block>"
                        + "<fo:block>alter self, cacophoneus call, </fo:block>",
                filter.filterString(testString));
    }

    @Test
    public void testXmlFilterStringComplex() throws Exception
    {
        String testString =
                "Mirror Image&nl;       blindness/deafness, \u2028alter self, cacophoneus call, ";
        testString = EntityEncoder.decode(testString);
        OutputFilter filter = new PatternFilter("foo.xml");
        assertEquals(
                "<para>Mirror Image</para><para>       blindness/deafness, </para>"
                        + "<para>alter self, cacophoneus call, </para>",
                filter.filterString(testString));
    }

    @Test
    public void testTxtFilterStringComplex() throws Exception
    {
        String testString =
                "Mirror Image&nl;       blindness/deafness, \u2028alter self, cacophoneus call, ";
        testString = EntityEncoder.decode(testString);
        OutputFilter filter = new PatternFilter("foo.txt");
        assertEquals(
                "Mirror Image\n       blindness/deafness, \u2028alter self, cacophoneus call, ",
                filter.filterString(testString));
    }

}
