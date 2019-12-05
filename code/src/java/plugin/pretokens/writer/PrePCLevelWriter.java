/*
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
import java.util.List;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.AbstractPrerequisiteWriter;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface;

public class PrePCLevelWriter extends AbstractPrerequisiteWriter implements PrerequisiteWriterInterface
{

    @Override
    public String kindHandled()
    {
        return "pclevel";
    }

    @Override
    public PrerequisiteOperator[] operatorsHandled()
    {
        return new PrerequisiteOperator[]{PrerequisiteOperator.GTEQ, PrerequisiteOperator.LT, PrerequisiteOperator.LTEQ,
                PrerequisiteOperator.GT};
    }

    @Override
    public void write(Writer writer, Prerequisite prereq) throws PersistenceLayerException
    {
        checkValidOperator(prereq, operatorsHandled());

        try
        {
            if (prereq.getOperator().equals(PrerequisiteOperator.LT))
            {
                writer.write('!');
                writer.write("PREPCLEVEL:" + (prereq.isOverrideQualify() ? "Q:" : "") + "MIN=");
                writer.write(prereq.getOperand());
            } else if (prereq.getOperator().equals(PrerequisiteOperator.GT))
            {
                writer.write('!');
                writer.write("PREPCLEVEL:" + (prereq.isOverrideQualify() ? "Q:" : "") + "MAX=");
                writer.write(prereq.getOperand());
            } else if (prereq.getOperator().equals(PrerequisiteOperator.GTEQ))
            {
                writer.write("PREPCLEVEL:" + (prereq.isOverrideQualify() ? "Q:" : "") + "MIN=");
                writer.write(prereq.getOperand());
            } else if (prereq.getOperator().equals(PrerequisiteOperator.LTEQ))
            {
                writer.write("PREPCLEVEL:" + (prereq.isOverrideQualify() ? "Q:" : "") + "MAX=");
                writer.write(prereq.getOperand());
            }

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
            //
            // ...with exactly 2 entries...
            //
            List<Prerequisite> prereqList = prereq.getPrerequisites();
            if (prereqList.size() == 2)
            {
                //
                // ...both of which are PREHD. The first must specify >= and the second <=
                //
                final Prerequisite elementGTEQ = prereqList.get(0);
                final Prerequisite elementLTEQ = prereqList.get(1);
                if ("pclevel".equalsIgnoreCase(elementGTEQ.getKind())
                        && elementGTEQ.getOperator().equals(PrerequisiteOperator.GTEQ)
                        && "pclevel".equalsIgnoreCase(elementLTEQ.getKind())
                        && elementLTEQ.getOperator().equals(PrerequisiteOperator.LTEQ))
                {
                    if (prereq.getOperator().equals(PrerequisiteOperator.LT))
                    {
                        writer.write('!');
                    }
                    writer.write("PREPCLEVEL:" + (prereq.isOverrideQualify() ? "Q:" : ""));
                    writer.write("MIN=");
                    writer.write(elementGTEQ.getOperand());
                    writer.write(",MAX=");
                    writer.write(elementLTEQ.getOperand());
                    return true;
                }
            }
        }
        return false;
    }

}
