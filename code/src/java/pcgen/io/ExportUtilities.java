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
 *
 *
 */
package pcgen.io;

import java.io.File;

import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.ObjectWrapper;
import freemarker.template.Version;
import org.apache.commons.lang3.StringUtils;

/**
 * ExportUtilities is a collection of useful tools for exporting characters.
 */
public final class ExportUtilities
{
    /**
     * This class should not be constructed.
     */
    private ExportUtilities()
    {
    }

    /**
     * Retrieve the extension that should be used for the output file. This is base don the template name.
     *
     * @param templateFilename The filename of the export template.
     * @param isPdf            Is this an export to a PDF file?
     * @return The output filename extension.
     */
    public static String getOutputExtension(String templateFilename, boolean isPdf)
    {
        if (isPdf)
        {
            return "pdf";
        }

        if (templateFilename.endsWith(".ftl"))
        {
            templateFilename = templateFilename.substring(0, templateFilename.length() - 4);
        }
        String extension = StringUtils.substringAfterLast(templateFilename, ".");
        if (StringUtils.isEmpty(extension))
        {
            extension = StringUtils.substringAfterLast(templateFilename, "-");
        }

        return extension;
    }

    /**
     * Identify if this template will result in a pdf file.
     *
     * @param templateFile The output template.
     * @return true if this is a pdf template.
     */
    public static boolean isPdfTemplate(File templateFile)
    {
        return isPdfTemplate(templateFile.getName());
    }

    /**
     * Identify if this template will result in a pdf file.
     *
     * @param templateFilename The name of the output template.
     * @return true if this is a pdf template.
     */
    public static boolean isPdfTemplate(String templateFilename)
    {
        String extension = getOutputExtension(templateFilename, false);
        return (extension.equalsIgnoreCase("pdf") || extension.equalsIgnoreCase("fo")
                || extension.equalsIgnoreCase("xslt") || extension.equalsIgnoreCase("xsl"));
    }

    /**
     * Returns an ObjectWrapper of sufficiently high version for pcgen
     */
    public static ObjectWrapper getObjectWrapper()
    {
        DefaultObjectWrapperBuilder defaultObjectWrapperBuilder = new DefaultObjectWrapperBuilder(
                new Version("2.3.28"));
        return defaultObjectWrapperBuilder.build();
    }
}
