/*
 * PrerequisiteEquippedBothWriter.java
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
import java.util.List;

public class PreEquipBothWriter extends AbstractPrerequisiteWriter implements
		PrerequisiteWriterInterface
{

	/* (non-Javadoc)
	 * @see pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface#kindHandled()
	 */
	public String kindHandled()
	{
		return "EQUIPBOTH";
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

			writer.write("PREEQUIPBOTH:" + (prereq.isOverrideQualify() ? "Q:":"") + "1,");
			writer.write(prereq.getKey());
		}
		catch (IOException e)
		{
			throw new PersistenceLayerException(e.getMessage());
		}
	}


	@Override
	public boolean specialCase(Writer writer, Prerequisite prereq)
			throws IOException
	{
		//
		// If this is a PREMULT...
		//
		if (prereq.getKind() == null)
		{
			List<Prerequisite> prereqList = prereq.getPrerequisites();
			PrerequisiteOperator oper = null;
			for (Prerequisite p : prereqList)
			{
				//
				// ...testing one item...
				//
				if (!"1".equals(p.getOperand()))
				{
					return false;
				}
				//
				// ...with all PREARMORTYPE entries...
				//
				if (!kindHandled().equalsIgnoreCase(p.getKind()))
				{
					return false;
				}
				//
				// ...and the same operator...
				//
				if (oper == null)
				{
					oper = p.getOperator();
				}
				else
				{
					if (!oper.equals(p.getOperator()))
					{
						return false;
					}
				}
			}
			if (oper.equals(PrerequisiteOperator.LT))
			{
				writer.write('!');
			}

			writer.write("PRE" + kindHandled().toUpperCase() + ":"
					+ (prereq.isOverrideQualify() ? "Q:" : ""));
			writer.write(prereq.getOperand());
			for (Prerequisite p : prereqList)
			{
				writer.write(',');
				writer.write(p.getKey());
			}
			return true;
		}
		return false;
	}
}
