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
 */
package plugin.pretokens.writer;

import java.io.IOException;
import java.io.Writer;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.AbstractPrerequisiteWriter;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface;

public class PreAlignWriter extends AbstractPrerequisiteWriter implements
		PrerequisiteWriterInterface
{

	/**
	 * @see pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface#kindHandled()
	 */
    @Override
	public String kindHandled()
	{
		return "align";
	}

	/**
	 * @see pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface#operatorsHandled()
	 */
    @Override
	public PrerequisiteOperator[] operatorsHandled()
	{
		return new PrerequisiteOperator[]{PrerequisiteOperator.EQ,
			PrerequisiteOperator.NEQ};
	}

	/**
	 * @see pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface#write(java.io.Writer, pcgen.core.prereq.Prerequisite)
	 */
    @Override
	public void write(Writer writer, Prerequisite prereq)
		throws PersistenceLayerException
	{
		checkValidOperator(prereq, operatorsHandled());
		try
		{
			if (prereq.getOperator().equals(PrerequisiteOperator.NEQ))
			{
				writer.write('!');
			}
			writer.write("PREALIGN:" + (prereq.isOverrideQualify() ? "Q:":""));
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
		// If this is NOT a PREMULT... fail
		if (prereq.getKind() != null)
		{
			return false;
		}
		PrerequisiteOperator oper = null;
		for (Prerequisite p : prereq.getPrerequisites())
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
		String count = prereq.getOperand();
		if (PrerequisiteOperator.NEQ.equals(oper))
		{
			try
			{
				int i = Integer.parseInt(count);
				if (prereq.getPrerequisiteCount() != i)
				{
					return false;
				}
			}
			catch (NumberFormatException e)
			{
				return false;
			}
		}
		else if (!PrerequisiteOperator.EQ.equals(oper))
		{
			return false;
		}
		if (oper.equals(PrerequisiteOperator.EQ)
				&& !"1".equals(prereq.getOperand()))
		{
			return false;
		}
		if (!PrerequisiteOperator.GTEQ.equals(prereq.getOperator())
				^ !PrerequisiteOperator.EQ.equals(oper))
		{
			writer.write('!');
		}

		writer.write("PRE" + kindHandled().toUpperCase() + ':'
				+ (prereq.isOverrideQualify() ? "Q:" : ""));
		boolean first = true;
		for (Prerequisite p : prereq.getPrerequisites())
		{
			if (!first)
			{
				writer.write(',');
			}
			writer.write(p.getKey());
			first = false;
		}
		return true;
	}
}
