/*
 * Copyright 2005 (C) Aaron Divinsky <boomer70@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.kit;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.PCAlignment;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.RaceAlignment;
import pcgen.output.channel.compat.AlignmentCompat;

/**
 * Deals with the automatic assignment of an Alignment via a Kit
 */
public class KitAlignment extends BaseKit
{
    private List<CDOMSingleRef<PCAlignment>> alignments;

    // These members store the state of an instance of this class.  They are
    // not cloned.
    private PCAlignment align = null;

    /**
     * Actually applies the alignment to this PC.
     *
     * @param aPC The PlayerCharacter the alignment is applied to
     */
    @Override
    public void apply(PlayerCharacter aPC)
    {
        AlignmentCompat.setCurrentAlignment(aPC.getCharID(), align);
    }

    /**
     * testApply
     *
     * @param k
     * @param aPC      PlayerCharacter
     * @param warnings List
     */
    @Override
    public boolean testApply(Kit k, PlayerCharacter aPC, List<String> warnings)
    {
        align = null;
        if (alignments.size() == 1)
        {
            align = alignments.get(0).get();
        } else
        {
            List<PCAlignment> available = new ArrayList<>(alignments.size());
            for (CDOMSingleRef<PCAlignment> ref : alignments)
            {
                available.add(ref.get());
            }
            while (true)
            {
                List<PCAlignment> sel = new ArrayList<>(1);
                sel = Globals.getChoiceFromList("Choose alignment", available, sel, 1, aPC);
                if (sel.size() == 1)
                {
                    align = sel.get(0);
                    break;
                }
            }
        }
        apply(aPC);
        return RaceAlignment.canBeAlignment(aPC.getRace(), align);
    }

    @Override
    public String getObjectName()
    {
        return "Alignment";
    }

    @Override
    public String toString()
    {
        if (alignments == null || alignments.isEmpty())
        {
            //CONSIDER can this ever happen and not be an error that should be caught at LST load?
            return "";
        }
        if (alignments.size() == 1)
        {
            return alignments.get(0).get().getDisplayName();
        } else
        {
            // Build the string list.
            StringBuilder buf = new StringBuilder();
            buf.append("One of (");
            boolean needComma = false;
            for (CDOMSingleRef<PCAlignment> alref : alignments)
            {
                PCAlignment al = alref.get();
                if (needComma)
                {
                    buf.append(", ");
                }
                needComma = true;
                buf.append(al.getDisplayName());
            }
            buf.append(")");
            return buf.toString();
        }
    }

    public void addAlignment(CDOMSingleRef<PCAlignment> ref)
    {
        if (alignments == null)
        {
            alignments = new ArrayList<>();
        }
        alignments.add(ref);
    }

    public List<CDOMSingleRef<PCAlignment>> getAlignments()
    {
        return alignments;
    }
}
