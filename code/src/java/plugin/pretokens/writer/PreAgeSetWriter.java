/*
 * PreAgeSetWriter.java
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
 * Created on 30-Dec-2006
 *
 * Current Ver: $Revision: 1821 $
 *
 * Last Editor: $Author: jdempsey $
 *
 * Last Edited: $Date: 2006-12-28 07:12:38 +0100 (Thu, 28 Dec 2006) $
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


//TODO ADJUST THIS

public class PreAgeSetWriter extends AbstractPrerequisiteWriter implements
                PrerequisiteWriterInterface
{

        /* (non-Javadoc)
         * @see pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface#kindHandled()
         */
        public String kindHandled()
        {
                return "ageset";
        }

        /* (non-Javadoc)
         * @see pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface#operatorsHandled()
         */
        //TODO consider supporting LT, GT too
        public PrerequisiteOperator[] operatorsHandled()
        {
                return new PrerequisiteOperator[]{PrerequisiteOperator.EQ,
                        PrerequisiteOperator.NEQ};
        }

        /* (non-Javadoc)
         * @see pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface#write(java.io.Writer, pcgen.core.prereq.Prerequisite)
         */
        //TODO make it work
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
                        writer.write("PREAGESET:" + (prereq.isOverrideQualify() ? "Q:":""));
                        writer.write(prereq.getKey());
                }
                catch (IOException e)
                {
                        throw new PersistenceLayerException(e.getMessage());
                }
        }

}
