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

public class PreSpellCastMemorizeWriter extends AbstractPrerequisiteWriter implements PrerequisiteWriterInterface
{

    @Override
    public String kindHandled()
    {
        return "spellcast.memorize";
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
            if (prereq.getOperator().equals(PrerequisiteOperator.LT))
            {
                writer.write('!');
            }

            writer.write("PRESPELLCAST:" + (prereq.isOverrideQualify() ? "Q:" : "") + "MEMORIZE=");
            writer.write(prereq.getKey());
        } catch (IOException e)
        {
            throw new PersistenceLayerException(e);
        }
    }

}
