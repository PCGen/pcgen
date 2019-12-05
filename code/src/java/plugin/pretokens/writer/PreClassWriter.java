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

public class PreClassWriter extends AbstractPrerequisiteWriter implements PrerequisiteWriterInterface
{

    @Override
    public String kindHandled()
    {
        return "class";
    }

    @Override
    public PrerequisiteOperator[] operatorsHandled()
    {
        return new PrerequisiteOperator[]{PrerequisiteOperator.GTEQ, PrerequisiteOperator.LT};
    }

    @Override
    public void write(Writer writer, Prerequisite prereq) throws PersistenceLayerException
    {
        checkValidOperator(prereq, operatorsHandled());

        try
        {
            if (prereq.getOperator() == PrerequisiteOperator.LT)
            {
                writer.write('!');
            }

            writer.write("PRECLASS:" + (prereq.isOverrideQualify() ? "Q:" : "") + "1,");
            writer.write(prereq.getKey());
            writer.write('=');
            writer.write(prereq.getOperand());
        } catch (IOException e)
        {
            throw new PersistenceLayerException(e);
        }
    }

    @Override
    public boolean specialCase(Writer writer, Prerequisite prereq) throws IOException
    {
        PrerequisiteOperator po = getConsolidateMethod(kindHandled(), prereq, true);
        if (po == null)
        {
            return false;
        }
        if (po != prereq.getOperator())
        {
            writer.write('!');
        }

        writer.write("PRE" + kindHandled().toUpperCase(Locale.ENGLISH) + ':' + (prereq.isOverrideQualify() ? "Q:" : ""));
        writer.write(po == PrerequisiteOperator.GTEQ ? prereq.getOperand() : "1");
        for (Prerequisite p : prereq.getPrerequisites())
        {
            writer.write(',');
            writer.write(p.getKey());
            writer.write('=');
            writer.write(p.getOperand());
        }
        return true;
    }
}
