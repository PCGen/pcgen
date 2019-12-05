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
 *
 *
 *
 *
 *
 */
package pcgen.persistence.lst.output.prereq;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrereqObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;

public class PrerequisiteWriter
{
    public void write(Writer stringWriter, Prerequisite prereq) throws PersistenceLayerException
    {
        PrerequisiteWriterFactory factory = PrerequisiteWriterFactory.getInstance();
        PrerequisiteWriterInterface writer = factory.getWriter(prereq.getKind());
        if (writer == null)
        {
            throw new PersistenceLayerException("Can not find a Writer for prerequisites fo kind: " + prereq.getKind());
        }
        writer.write(stringWriter, prereq);
    }

    /**
     * Convert the prerequisites of a PObject to a String in .lst-compatible
     * form
     *
     * @param pObj A PObject object.
     * @return The .lst-compatible string representation of the prerequisite
     * list.
     */
    public static String prereqsToString(final PrereqObject pObj)
    {
        if (pObj.hasPrerequisites())
        {
            final PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
            return prereqWriter.getPrerequisiteString(pObj.getPrerequisiteList(), Constants.TAB);
        }
        return "";
    }

    public String getPrerequisiteString(Collection<Prerequisite> prereqs) throws PersistenceLayerException
    {
        return getPrereqString(prereqs, Constants.PIPE);
    }

    private String getPrereqString(Collection<Prerequisite> prereqs, String separator) throws PersistenceLayerException
    {
        String prereqString = null;
        if (prereqs != null && !prereqs.isEmpty())
        {
            TreeSet<String> list = new TreeSet<>();
            for (Prerequisite p : prereqs)
            {
                StringWriter swriter = new StringWriter();
                write(swriter, p);
                list.add(swriter.toString());
            }
            prereqString = StringUtil.join(list, separator);
        }
        return prereqString;
    }

    public String getPrerequisiteString(Collection<Prerequisite> prereqs, String separator)
    {
        try
        {
            return getPrereqString(prereqs, separator);
        } catch (PersistenceLayerException e)
        {
            Logging.errorPrint("Error writing Prerequisites: " + prereqs);
        }
        return "";
    }

}
