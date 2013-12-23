/*
 * ExportUtilitiesTest.java
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
 *
 * Created on 24/12/2013
 *
 * $Id$
 */
package pcgen.io;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

/**
 * ExportUtilitiesTest checks the function of the ExportUtilities class.
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class ExportUtilitiesTest
{

	/**
	 * Test method for {@link pcgen.io.ExportUtilities#getOutputExtension(java.lang.String, boolean)}.
	 */
	@Test
	public void testGetOutputExtensionNonPdf()
	{
		assertEquals("Incorrect non pdf extension", "html",
			ExportUtilities.getOutputExtension("foo.html", false));
		assertEquals("Incorrect non pdf extension", "htm",
			ExportUtilities.getOutputExtension("foo.htm", false));
		assertEquals("Incorrect non pdf extension", "txt",
			ExportUtilities.getOutputExtension("bar.foo.txt", false));
		assertEquals("Incorrect non pdf extension", "txt",
			ExportUtilities.getOutputExtension("bar.htm.txt", false));
		assertEquals("Incorrect non pdf extension", "xml",
			ExportUtilities.getOutputExtension("foo-991.xml", false));
		assertEquals("Incorrect non pdf extension", "csv",
			ExportUtilities.getOutputExtension("foo.csv", false));
		assertEquals("Incorrect non pdf extension", "xslt",
			ExportUtilities.getOutputExtension("foo.xslt", false));
		
		assertEquals("Incorrect non pdf freemarker extension", "html",
			ExportUtilities.getOutputExtension("foo.html.ftl", false));
		assertEquals("Incorrect non pdf freemarker extension", "html",
			ExportUtilities.getOutputExtension("foo-html.ftl", false));
		assertEquals("Incorrect non pdf freemarker extension", "xml",
			ExportUtilities.getOutputExtension("foo.xml.ftl", false));
		assertEquals("Incorrect non pdf freemarker extension", "xml",
			ExportUtilities.getOutputExtension("foo-xml.ftl", false));
	}

	/**
	 * Test method for {@link pcgen.io.ExportUtilities#getOutputExtension(java.lang.String, boolean)}.
	 */
	@Test
	public void testGetOutputExtensionPdf()
	{
		assertEquals("Incorrect pdf extension", "pdf",
			ExportUtilities.getOutputExtension("foo.xslt", true));
		assertEquals("Incorrect freemarker pdf extension", "pdf",
			ExportUtilities.getOutputExtension("foo.xslt.ftl", true));
	}

	/**
	 * Test method for {@link pcgen.io.ExportUtilities#isPdfTemplate(java.io.File)}.
	 */
	@Test
	public void testIsPdfTemplateFile()
	{
		assertEquals("Should not be PDF", false,
			ExportUtilities.isPdfTemplate(new File("foo.html")));
		assertEquals("Should not be PDF", false,
			ExportUtilities.isPdfTemplate(new File("foo.htm")));
		assertEquals("Should not be PDF", false,
			ExportUtilities.isPdfTemplate(new File("foo.xml")));
		
		assertEquals("Should not be PDF", false,
			ExportUtilities.isPdfTemplate(new File("foo-html.ftl")));
		assertEquals("Should not be PDF", false,
			ExportUtilities.isPdfTemplate(new File("foo.html.ftl")));
		assertEquals("Should not be PDF", false,
			ExportUtilities.isPdfTemplate(new File("foo.txt.ftl")));
		assertEquals("Should not be PDF", false,
			ExportUtilities.isPdfTemplate(new File("foo.xml.ftl")));
		
		assertEquals("Should be PDF", true,
			ExportUtilities.isPdfTemplate(new File("foo.xslt.ftl")));
		assertEquals("Should be PDF", true,
			ExportUtilities.isPdfTemplate(new File("foo-fo.ftl")));
		
		assertEquals("Should be PDF", true,
			ExportUtilities.isPdfTemplate(new File("foo.xsl")));
		assertEquals("Should be PDF", true,
			ExportUtilities.isPdfTemplate(new File("foo.pdf")));
	}

	/**
	 * Test method for {@link pcgen.io.ExportUtilities#isPdfTemplate(java.lang.String)}.
	 */
	@Test
	public void testIsPdfTemplateString()
	{
		assertEquals("Should not be PDF", false,
			ExportUtilities.isPdfTemplate("foo.html"));
		assertEquals("Should not be PDF", false,
			ExportUtilities.isPdfTemplate("foo.htm"));
		assertEquals("Should not be PDF", false,
			ExportUtilities.isPdfTemplate("foo.xml"));
		
		assertEquals("Should not be PDF", false,
			ExportUtilities.isPdfTemplate("foo-html.ftl"));
		assertEquals("Should not be PDF", false,
			ExportUtilities.isPdfTemplate("foo.html.ftl"));
		assertEquals("Should not be PDF", false,
			ExportUtilities.isPdfTemplate("foo.txt.ftl"));
		assertEquals("Should not be PDF", false,
			ExportUtilities.isPdfTemplate("foo.xml.ftl"));
		
		assertEquals("Should be PDF", true,
			ExportUtilities.isPdfTemplate("foo.xsl"));
		assertEquals("Should be PDF", true,
			ExportUtilities.isPdfTemplate("foo.pdf"));

		assertEquals("Should be PDF", true,
			ExportUtilities.isPdfTemplate("foo.xslt.ftl"));
		assertEquals("Should be PDF", true,
			ExportUtilities.isPdfTemplate("foo-fo.ftl"));
	}

}
