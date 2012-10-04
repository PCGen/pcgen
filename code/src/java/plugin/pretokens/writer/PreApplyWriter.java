/*
 * PrerequisiteApplyWriter.java
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
package plugin.pretokens.writer;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.AbstractPrerequisiteWriter;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface;

import java.io.IOException;
import java.io.Writer;

public class PreApplyWriter extends AbstractPrerequisiteWriter implements
		PrerequisiteWriterInterface
{

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface#kindHandled()
	 */
	public String kindHandled()
	{
		return "apply";
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface#operatorsHandled()
	 */
	public PrerequisiteOperator[] operatorsHandled()
	{
		return new PrerequisiteOperator[]{PrerequisiteOperator.EQ,
			PrerequisiteOperator.NEQ};
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface#write(java.io.Writer, pcgen.core.prereq.Prerequisite)
	 */
	public void write(Writer writer, Prerequisite prereq)
		throws PersistenceLayerException
	{
		try
		{
			checkValidOperator(prereq, operatorsHandled());
			if (prereq.getOperator().equals(PrerequisiteOperator.NEQ))
			{
				writer.write('!');
			}
			writer.write("PREAPPLY:" + (prereq.isOverrideQualify() ? "Q:":""));

			if (Integer.parseInt(prereq.getOperand()) > 1)
			{
				// must be a "A and b" operation
				boolean needComma = false;
				for (Prerequisite subreq : prereq.getPrerequisites())
				{
					if (needComma)
					{
						writer.write(',');
					}
					needComma = true;
					writeOredPrereqs(writer, subreq);
				}
			}
			else
			{
				for (Prerequisite subreq : prereq.getPrerequisites())
				{
					writeOredPrereqs(writer, subreq);
				}
			}
		}
		catch (NumberFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			throw new PersistenceLayerException(e.getMessage());
		}
	}

	private void writeOredPrereqs(Writer writer, Prerequisite subreq)
		throws IOException
	{
		if (subreq.getKind() == null)
		{
			// must be an "A or B" operation
			boolean needSemi = false;
			for (Prerequisite subsubreq : subreq.getPrerequisites())
			{
				if (needSemi)
				{
					writer.write(';');
				}
				needSemi = true;
				writer.write(subsubreq.getOperand());
			}
		}
		else
		{
			writer.write(subreq.getOperand());
		}
	}

}
