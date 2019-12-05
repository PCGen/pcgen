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
package pcgen.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

/**
 * ExportUtilitiesTest checks the function of the ExportUtilities class.
 */
class ExportUtilitiesTest
{

    /**
     * Test method for {@link pcgen.io.ExportUtilities#getOutputExtension(java.lang.String, boolean)}.
     */
    @Test
    public void testGetOutputExtensionNonPdf()
    {
        assertEquals("html",
                ExportUtilities.getOutputExtension("foo.html", false), "Incorrect non pdf extension"
        );
        assertEquals("htm",
                ExportUtilities.getOutputExtension("foo.htm", false), "Incorrect non pdf extension"
        );
        assertEquals("txt",
                ExportUtilities.getOutputExtension("bar.foo.txt", false), "Incorrect non pdf extension"
        );
        assertEquals("txt",
                ExportUtilities.getOutputExtension("bar.htm.txt", false), "Incorrect non pdf extension"
        );
        assertEquals("xml",
                ExportUtilities.getOutputExtension("foo-991.xml", false), "Incorrect non pdf extension"
        );
        assertEquals("csv",
                ExportUtilities.getOutputExtension("foo.csv", false), "Incorrect non pdf extension"
        );
        assertEquals("xslt",
                ExportUtilities.getOutputExtension("foo.xslt", false), "Incorrect non pdf extension"
        );

        assertEquals("html",
                ExportUtilities.getOutputExtension("foo.html.ftl", false), "Incorrect non pdf freemarker extension"
        );
        assertEquals("html",
                ExportUtilities.getOutputExtension("foo-html.ftl", false), "Incorrect non pdf freemarker extension"
        );
        assertEquals("xml",
                ExportUtilities.getOutputExtension("foo.xml.ftl", false), "Incorrect non pdf freemarker extension"
        );
        assertEquals("xml",
                ExportUtilities.getOutputExtension("foo-xml.ftl", false), "Incorrect non pdf freemarker extension"
        );
    }

    /**
     * Test method for {@link pcgen.io.ExportUtilities#getOutputExtension(java.lang.String, boolean)}.
     */
    @Test
    public void testGetOutputExtensionPdf()
    {
        assertEquals("pdf",
                ExportUtilities.getOutputExtension("foo.xslt", true), "Incorrect pdf extension"
        );
        assertEquals("pdf",
                ExportUtilities.getOutputExtension("foo.xslt.ftl", true), "Incorrect freemarker pdf extension"
        );
    }

    /**
     * Test method for {@link pcgen.io.ExportUtilities#isPdfTemplate(java.io.File)}.
     */
    @Test
    public void testIsPdfTemplateFile()
    {
        assertFalse(
                ExportUtilities.isPdfTemplate(new File("foo.html")),
                "Should not be PDF"
        );
        assertFalse(
                ExportUtilities.isPdfTemplate(new File("foo.htm")),
                "Should not be PDF"
        );
        assertFalse(
                ExportUtilities.isPdfTemplate(new File("foo.xml")),
                "Should not be PDF"
        );

        assertFalse(
                ExportUtilities.isPdfTemplate(new File("foo-html.ftl")),
                "Should not be PDF"
        );
        assertFalse(
                ExportUtilities.isPdfTemplate(new File("foo.html.ftl")),
                "Should not be PDF"
        );
        assertFalse(
                ExportUtilities.isPdfTemplate(new File("foo.txt.ftl")),
                "Should not be PDF"
        );
        assertFalse(
                ExportUtilities.isPdfTemplate(new File("foo.xml.ftl")),
                "Should not be PDF"
        );

        assertTrue(
                ExportUtilities.isPdfTemplate(new File("foo.xslt.ftl")),
                "Should be PDF"
        );
        assertTrue(
                ExportUtilities.isPdfTemplate(new File("foo-fo.ftl")),
                "Should be PDF"
        );

        assertTrue(ExportUtilities.isPdfTemplate(new File("foo.xsl")), "Should be PDF");
        assertTrue(ExportUtilities.isPdfTemplate(new File("foo.pdf")), "Should be PDF");
    }

    /**
     * Test method for {@link pcgen.io.ExportUtilities#isPdfTemplate(java.lang.String)}.
     */
    @Test
    public void testIsPdfTemplateString()
    {
        assertFalse(ExportUtilities.isPdfTemplate("foo.html"), "Should not be PDF");
        assertFalse(ExportUtilities.isPdfTemplate("foo.htm"), "Should not be PDF");
        assertFalse(ExportUtilities.isPdfTemplate("foo.xml"), "Should not be PDF");

        assertFalse(ExportUtilities.isPdfTemplate("foo-html.ftl"), "Should not be PDF");
        assertFalse(ExportUtilities.isPdfTemplate("foo.html.ftl"), "Should not be PDF");
        assertFalse(ExportUtilities.isPdfTemplate("foo.txt.ftl"), "Should not be PDF");
        assertFalse(ExportUtilities.isPdfTemplate("foo.xml.ftl"), "Should not be PDF");

        assertTrue(ExportUtilities.isPdfTemplate("foo.xsl"), "Should be PDF");
        assertTrue(ExportUtilities.isPdfTemplate("foo.pdf"), "Should be PDF");

        assertTrue(ExportUtilities.isPdfTemplate("foo.xslt.ftl"), "Should be PDF");
        assertTrue(ExportUtilities.isPdfTemplate("foo-fo.ftl"), "Should be PDF");
    }
}
