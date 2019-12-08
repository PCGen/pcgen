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

import java.io.IOException;
import java.io.Writer;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;

public class PrerequisiteMultWriter extends AbstractPrerequisiteWriter implements PrerequisiteWriterInterface
{
	private boolean allSkillTot = false;

	@Override
	public String kindHandled()
	{
		return null;
	}

	@Override
	public PrerequisiteOperator[] operatorsHandled()
	{
		return new PrerequisiteOperator[]{PrerequisiteOperator.GTEQ, PrerequisiteOperator.LT, PrerequisiteOperator.EQ,
			PrerequisiteOperator.NEQ};
	}

	@Override
	public void write(Writer writer, Prerequisite prereq) throws PersistenceLayerException
	{
		checkValidOperator(prereq, operatorsHandled());
		try
		{
			Prerequisite subreq;
			//
			// Check to see if this is a special case for PREMULT
			//
			if (isSpecialCase(prereq))
			{
				handleSpecialCase(writer, prereq);
				return;
			}
			if (isNegatedPreability(prereq))
			{
				handleNegatedPreAbility(writer, prereq);
				return;
			}
			if (prereq.getPrerequisiteCount() != 0)
			{
				subreq = prereq.getPrerequisites().get(0);
				final PrerequisiteWriterInterface test =
						PrerequisiteWriterFactory.getInstance().getWriter(subreq.getKind());
				if ((test != null) && (test instanceof AbstractPrerequisiteWriter)
					&& ((AbstractPrerequisiteWriter) test).specialCase(writer, prereq))
				{
					return;
				}
			}

			if (prereq.getOperator().equals(PrerequisiteOperator.LT))
			{
				writer.write('!');
			}

			writer.write("PREMULT:");
			writer.write(prereq.getOperand());
			writer.write(',');
			int i = 0;
			for (Prerequisite pre : prereq.getPrerequisites())
			{
				if (i > 0)
				{
					writer.write(',');
				}
				writer.write('[');

				PrerequisiteWriterFactory factory = PrerequisiteWriterFactory.getInstance();
				PrerequisiteWriterInterface w = factory.getWriter(pre.getKind());
				if (w != null)
				{
					w.write(writer, pre);
				}
				else
				{
					writer.write("unrecognized kind:" + pre.getKind());
				}
				writer.write(']');
				i++;
			}
		}
		catch (IOException e)
		{
			throw new PersistenceLayerException(e);
		}
	}

	/**
	 * @param writer
	 * @param prereq
	 * @throws IOException
	 */
	private void handleSpecialCase(Writer writer, Prerequisite prereq) throws IOException
	{
		if (allSkillTot)
		{
			if (prereq.getOperator().equals(PrerequisiteOperator.LT))
			{
				writer.write('!');
			}
			writer.write("PRESKILLTOT:");

			int i = 0;
			for (Prerequisite subreq : prereq.getPrerequisites())
			{
				if (i > 0)
				{
					writer.write(',');
				}
				writer.write(subreq.getKey());
				i++;
			}
			writer.write('=');
			writer.write(prereq.getOperand());
		}
	}

	/**
	 * @param prereq
	 * @return TRUE if special case, else FALSE
	 */
	private boolean isSpecialCase(Prerequisite prereq)
	{
		// Special case of all subreqs being SKILL with total-values=true
		allSkillTot = true;
		for (Prerequisite element : prereq.getPrerequisites())
		{
			if (!allSkillTot)
			{
				break;
			}
			if (!"skill".equalsIgnoreCase(element.getKind()) || !element.isTotalValues())
			{
				allSkillTot = false;
			}
		}
		if (allSkillTot)
		{
			return true;
		}

		return false;
	}

	/**
	 * Identify if this is a PREABILITY which has been converted into a PREMULT 
	 * to include a negated check, i.e. ensure a particular ability is not
	 * present in the character.
	 *   
	 * @param prereq The PREMULT to be checked.
	 * @return true if this is a negated PREABILITY, false if not.
	 */
	private boolean isNegatedPreability(Prerequisite prereq)
	{
		if (prereq.getPrerequisites().isEmpty())
		{
			return false;
		}
		boolean hasNegated = false;
		for (Prerequisite element : prereq.getPrerequisites())
		{
			if (!"ability".equalsIgnoreCase(element.getKind()))
			{
				return false;
			}
			if (element.getOperator() == PrerequisiteOperator.LT && "1".equals(element.getOperand()))
			{
				hasNegated = true;
			}
		}
		return hasNegated;
	}

	/**
	 * Restore the format of a prereq such as 
	 * PREABILITY:1,CATEGORY=FEAT,[Surprise Strike]
	 * 
	 * @param writer The output destination writer.
	 * @param prereq The prereq to be written, must be a negated PREABILITY
	 * @throws IOException If the output cannot be written.
	 */
	private void handleNegatedPreAbility(Writer writer, Prerequisite prereq) throws IOException
	{
		writer.write("PREABILITY:");
		writer.write(String.valueOf(Integer.parseInt(prereq.getOperand()) - 1));
		writer.write(",");
		String cat = prereq.getPrerequisites().get(0).getCategoryName();
		if (cat == null)
		{
			writer.write("CATEGORY=ANY");
		}
		else
		{
			writer.write("CATEGORY=" + cat);
		}
		for (Prerequisite child : prereq.getPrerequisites())
		{
			writer.write(",");
			if (child.getOperator() == PrerequisiteOperator.LT)
			{
				writer.write("[");
			}
			writer.write(child.getKey());
			if (child.getSubKey() != null)
			{
				writer.write(" (");
				writer.write(child.getSubKey());
				writer.write(")");
			}
			if (child.getOperator() == PrerequisiteOperator.LT)
			{
				writer.write("]");
			}
		}
	}
}
