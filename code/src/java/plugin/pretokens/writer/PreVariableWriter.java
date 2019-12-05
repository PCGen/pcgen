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
import java.util.Locale;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.AbstractPrerequisiteWriter;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface;

/**
 * Writes out PREVAR token
 */
public class PreVariableWriter extends AbstractPrerequisiteWriter implements PrerequisiteWriterInterface
{

    @Override
    public String kindHandled()
    {
        return "var";
    }

    @Override
    public PrerequisiteOperator[] operatorsHandled()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void write(Writer writer, Prerequisite prereq) throws PersistenceLayerException
    {
        try
        {
            writer.write("PREVAR");
            writer.write(prereq.getOperator().toString().toUpperCase(Locale.ENGLISH));
            writer.write(':' + (prereq.isOverrideQualify() ? "Q:" : ""));
            writer.write(prereq.getKey());
            writer.write(',');
            writer.write(prereq.getOperand());
            // for (Iterator iter = prereq.getPrerequisites().iterator();
            // iter.hasNext(); )
            // {
            // final Prerequisite p = (Prerequisite) iter.next();
            // writer.write(',');
            // writer.write(p.getKey());
            // writer.write(',');
            // writer.write(p.getOperand());
            // }
        } catch (IOException e)
        {
            throw new PersistenceLayerException(e);
        }
    }

    @Override
    public boolean specialCase(Writer writer, Prerequisite prereq) throws IOException
    {
        if (prereq.getKind() != null)
        {
            return false;
        }
        String handled = kindHandled();
        String count = prereq.getOperand();
        try
        {
            int i = Integer.parseInt(count);
            if (prereq.getPrerequisiteCount() != i)
            {
                return false;
            }
        } catch (NumberFormatException e)
        {
            return false;
        }
        PrerequisiteOperator oper = null;
        for (Prerequisite p : prereq.getPrerequisites())
        {
            //
            // ...with all PREARMORTYPE entries...
            //
            if (!handled.equalsIgnoreCase(p.getKind()))
            {
                return false;
            }
            //
            // ...and the same operator...
            //
            if (oper == null)
            {
                oper = p.getOperator();
            } else
            {
                if (!oper.equals(p.getOperator()))
                {
                    return false;
                }
            }
        }
        writer.write("PREVAR");
        if (prereq.getOperator() == PrerequisiteOperator.LT)
        {
            writer.write(oper.invert().toString().toUpperCase(Locale.ENGLISH));
        } else
        {
            writer.write(oper.toString().toUpperCase(Locale.ENGLISH));
        }
        writer.write(':' + (prereq.isOverrideQualify() ? "Q:" : ""));
        boolean first = true;
        for (Prerequisite p : prereq.getPrerequisites())
        {
            if (!first)
            {
                writer.write(',');
            }
            writer.write(p.getKey());
            writer.write(',');
            writer.write(p.getOperand());
            first = false;
        }
        return true;
    }
}
