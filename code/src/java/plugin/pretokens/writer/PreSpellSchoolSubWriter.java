/*
 * PrerequisiteSpellSchoolSubWriter.java
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


public class PreSpellSchoolSubWriter extends AbstractPrerequisiteWriter implements PrerequisiteWriterInterface
{

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface#kindHandled()
	 */
	public String kindHandled()
	{
		return "spell.subschool";
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface#operatorsHandled()
	 */
	public PrerequisiteOperator[] operatorsHandled()
	{
		return new PrerequisiteOperator[] {
				PrerequisiteOperator.GTEQ,
				PrerequisiteOperator.LT
		} ;
	}

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface#write(java.io.Writer, pcgen.core.prereq.Prerequisite)
	 */
	public void write(Writer writer, Prerequisite prereq) throws PersistenceLayerException
	{
		checkValidOperator(prereq, operatorsHandled());

		try
		{
			if (prereq.getOperator().equals(PrerequisiteOperator.LT))
			{
				writer.write('!');
			}

			writer.write("PRESPELLSCHOOLSUB:");
			writer.write(prereq.getOperand());
			writer.write(',');
			writer.write(prereq.getKey());
			writer.write('=');
			writer.write(prereq.getSubKey());
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
	public boolean specialCase(Writer writer, Prerequisite prereq) throws IOException
	{
		//
		// If this is a PREMULT with all PRESPELLSCHOOLSUBs ...
		//
		if (checkForPremultOfKind(prereq, kindHandled(), true))
		{
			if (prereq.getOperator().equals(PrerequisiteOperator.LT))
			{
				writer.write('!');
			}

			writer.write("PRESPELLSCHOOLSUB:");
			writer.write(prereq.getOperand());
			for (Prerequisite element : prereq.getPrerequisites())
			{
				writer.write(',');
				writer.write(element.getKey());
				writer.write('=');
				writer.write(element.getSubKey());
			}
			return true;
		}
		return false;
	}
}
