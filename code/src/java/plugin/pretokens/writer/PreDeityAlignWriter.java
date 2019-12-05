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

public class PreDeityAlignWriter extends AbstractPrerequisiteWriter implements PrerequisiteWriterInterface
{

    @Override
    public String kindHandled()
    {
        return "deityalign";
    }

    @Override
    public PrerequisiteOperator[] operatorsHandled()
    {
        return new PrerequisiteOperator[]{PrerequisiteOperator.EQ, PrerequisiteOperator.NEQ};
    }

    @Override
    public void write(Writer writer, Prerequisite prereq) throws PersistenceLayerException
    {
        checkValidOperator(prereq, operatorsHandled());

        try
        {
            if (prereq.getOperator().equals(PrerequisiteOperator.NEQ))
            {
                writer.write('!');
            }

            writer.write("PREDEITYALIGN:" + (prereq.isOverrideQualify() ? "Q:" : ""));
            writer.write(prereq.getOperand());
        } catch (IOException e)
        {
            throw new PersistenceLayerException(e);
        }
    }

    @Override
    public boolean specialCase(Writer writer, Prerequisite prereq) throws IOException
    {
        //
        // If this is a PREMULT...
        //
        if (prereq.getKind() == null)
        {
            PrerequisiteOperator oper = null;
            for (Prerequisite p : prereq.getPrerequisites())
            {
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
                } else
                {
                    if (!oper.equals(p.getOperator()))
                    {
                        return false;
                    }
                }
            }
            if (oper.equals(PrerequisiteOperator.EQ) && !"1".equals(prereq.getOperand()))
            {
                return false;
            }
            if (PrerequisiteOperator.LT.equals(prereq.getOperator()))
            {
                writer.write('!');
            }

            writer.write("PRE" + kindHandled().toUpperCase(Locale.ENGLISH) + ':' + (prereq.isOverrideQualify() ? "Q:" : ""));
            boolean first = true;
            for (Prerequisite p : prereq.getPrerequisites())
            {
                if (!first)
                {
                    writer.write(',');
                }
                writer.write(p.getOperand());
                first = false;
            }
            return true;
        }
        return false;
    }
}
