/*
 * PrerequisiteWriter.java
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
 * Created on 18-Dec-2003
 *
 * Current Ver: $Revision$
 *
 * Last Editor: $Author$
 *
 * Last Edited: $Date$
 *
 */
package pcgen.persistence.lst.output.prereq;

import pcgen.core.PObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;

import java.io.StringWriter;
import java.io.Writer;

public class PrerequisiteWriter
{
	public void write(Writer stringWriter, Prerequisite prereq)
		throws PersistenceLayerException
	{
		PrerequisiteWriterFactory factory =
				PrerequisiteWriterFactory.getInstance();
		PrerequisiteWriterInterface writer =
				factory.getWriter(prereq.getKind());
		if (writer == null)
		{
			throw new PersistenceLayerException(
				"Can not find a Writer for prerequisites fo kind: "
					+ prereq.getKind());
		}
		writer.write(stringWriter, prereq);
	}

	/**
	 * Convert the prerequisites of a PObject to a String in .lst-compatible form
	 *
	 * @param pObj	A PObject object.
	 * @return		The .lst-compatible string representation of the prerequisite list.
	 */
	static public String prereqsToString(final PObject pObj)
	{
		if (pObj.hasPreReqs()) {
			final PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
			final StringWriter swriter = new StringWriter();
			for (Prerequisite p : pObj.getPreReqList()) {
				try
				{
					swriter.write('\t');
					prereqWriter.write(swriter, p);
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
				}
			}
			return swriter.toString();
		}
		return "";
	}
}
