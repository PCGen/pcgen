/*
 * PrerequisitePointBuyMethodWriter.java
 * Copyright 2005 (C) Greg Bingleman <byngl@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on September 16, 2005
 *
 * $Id$
 */
package plugin.pretokens.writer;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.AbstractPrerequisiteWriter;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface;

import java.io.IOException;
import java.io.Writer;

/**
 * <code>PrerequisitePointBuyMethodWriter</code>.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
public class PrePointBuyMethodWriter extends AbstractPrerequisiteWriter
		implements PrerequisiteWriterInterface
{

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface#kindHandled()
	 */
	public String kindHandled()
	{
		return "pointbuymethod";
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface#operatorsHandled()
	 */
	public PrerequisiteOperator[] operatorsHandled()
	{
		return new PrerequisiteOperator[]{PrerequisiteOperator.GTEQ,
			PrerequisiteOperator.LT};
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface#write(java.io.Writer, pcgen.core.prereq.Prerequisite)
	 */
	public void write(Writer writer, Prerequisite prereq)
		throws PersistenceLayerException
	{
		checkValidOperator(prereq, operatorsHandled());

		try
		{
			if (prereq.getOperator().equals(PrerequisiteOperator.LT))
			{
				writer.write('!');
			}

			writer.write("PREPOINTBUYMETHOD:" + (prereq.isOverrideQualify() ? "Q:":""));
			writer.write(prereq.getOperand());
			writer.write(',');
			writer.write(prereq.getKey());
		}
		catch (IOException e)
		{
			throw new PersistenceLayerException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.output.prereq.AbstractPrerequisiteWriter#specialCase(java.io.Writer writer, pcgen.core.prereq.Prerequisite prereq)
	 */
	@Override
	public boolean specialCase(Writer writer, Prerequisite prereq)
		throws IOException
	{
		//
		// If this is a PREMULT with all PREPOINTBUYMETHODs ...
		//
		if (checkForPremultOfKind(prereq, kindHandled(), false))
		{
			if (prereq.getOperator().equals(PrerequisiteOperator.LT))
			{
				writer.write('!');
			}

			writer.write("PREPOINTBUYMETHOD:" + (prereq.isOverrideQualify() ? "Q:":""));
			boolean needComma = false;
			for (Prerequisite element : prereq.getPrerequisites())
			{
				if (needComma)
				{
					writer.write(',');
				}
				needComma = true;
				writer.write(element.getKey());
			}
			return true;
		}
		return false;
	}
}
