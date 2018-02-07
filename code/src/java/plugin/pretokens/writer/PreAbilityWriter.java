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

/**
 * {@code PreAbilityWriter} outputs ability prereqs.
 */
public class PreAbilityWriter extends AbstractPrerequisiteWriter implements
		PrerequisiteWriterInterface
{

    @Override
	public String kindHandled()
	{
		return "ability";
	}

    @Override
	public PrerequisiteOperator[] operatorsHandled()
	{
		return new PrerequisiteOperator[]{PrerequisiteOperator.GTEQ,
			PrerequisiteOperator.LT};
	}

    @Override
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
			writer.write("PREABILITY:" + (prereq.isOverrideQualify() ? "Q:":""));
			writer.write(prereq.getOperand());
			writer.write(',');

			if (prereq.isOriginalCheckMult())
			{
				writer.write("CHECKMULT,");
			}
			String cat = prereq.getCategoryName();
			if (cat == null)
			{
				writer.write("CATEGORY=ANY,");
			}
			else
			{
				writer.write("CATEGORY=" + cat + ',');
			}

			writer.write(prereq.getKey());
			if (prereq.getSubKey() != null)
			{
				writer.write(" (");
				writer.write(prereq.getSubKey());
				writer.write(")");
			}
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
		PrerequisiteOperator po = getConsolidateMethod(kindHandled(), prereq, false);
		if (po == null)
		{
			return false;
		}
		if (hasSubordinateCheckMult(prereq))
		{
			return false;
		}
		String cat = null;
		boolean foundCat = false;
		for (Prerequisite p : prereq.getPrerequisites())
		{
			if (foundCat)
			{
				String thiscat = p.getCategoryName();
				if (thiscat == null)
				{
					if (cat != null)
					{
						return false;
					}
				}
				else
				{
					if (!thiscat.equals(cat))
					{
						return false;
					}
				}
			}
			else
			{
				cat = p.getCategoryName();
				foundCat = true;
			}
		}
		if (!po.equals(prereq.getOperator()))
		{
			writer.write('!');
		}

		writer.write("PRE" + kindHandled().toUpperCase() + ':'
				+ (prereq.isOverrideQualify() ? "Q:" : ""));
		writer.write(po.equals(PrerequisiteOperator.GTEQ) ? prereq.getOperand()
				: "1");
		if (prereq.isOriginalCheckMult())
		{
			writer.write(",CHECKMULT");
		}
		if (cat == null)
		{
			writer.write(",CATEGORY=ANY");
		}
		else
		{
			writer.write(",CATEGORY=" + cat);
		}
		for (Prerequisite p : prereq.getPrerequisites())
		{
			writer.write(',');
			writer.write(p.getKey());
			if (p.getSubKey() != null)
			{
				writer.write(" (");
				writer.write(p.getSubKey());
				writer.write(")");
			}
		}
		return true;
	}

	private boolean hasSubordinateCheckMult(Prerequisite prereq)
	{
		for (Prerequisite p : prereq.getPrerequisites())
		{
			if (p.isOriginalCheckMult())
			{
				return true;
			}
			for (Prerequisite sub : p.getPrerequisites())
			{
				if (hasSubordinateCheckMult(sub))
				{
					return true;
				}
			}
		}
		return false;
	}
}
