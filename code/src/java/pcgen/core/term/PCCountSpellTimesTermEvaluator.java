/**
 * Copyright (c) 2008 Andrew Wilson <nuance@users.sourceforge.net>.
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * <p>
 * Created 07-Aug-2008 23:22:14
 */

package pcgen.core.term;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellInfo;

public class PCCountSpellTimesTermEvaluator extends BasePCTermEvaluator implements TermEvaluator
{
    final int classNum;
    final int bookNum;
    final int spellLevel;
    final int spellNumber;

    public PCCountSpellTimesTermEvaluator(String originalText, int[] fields)
    {
        this.originalText = originalText;

        classNum = fields[0];
        bookNum = fields[1];
        spellLevel = (classNum == -1) ? -1 : fields[2];
        spellNumber = fields[3];
    }

    @Override
    public Float resolve(PlayerCharacter pc)
    {
        String bookName = (classNum == -1) ? Globals.getDefaultSpellBook()
                : (bookNum > 0) ? pc.getDisplay().getSpellBookNames().get(bookNum) : Globals.getDefaultSpellBook();

        if (!"".equals(bookName))
        {
            List<CharacterSpell> csList = new ArrayList<>();

            if (classNum == -1)
            {
                csList = new ArrayList<>();

                for (PObject cl : pc.getDisplay().getClassSet())
                {
                    for (CharacterSpell cs : pc.getCharacterSpells(cl, bookName))
                    {
                        if (!csList.contains(cs))
                        {
                            csList.add(cs);
                        }
                    }
                }

                Collections.sort(csList);
            } else
            {
                final PObject pcClass = pc.getSpellClassAtIndex(classNum);
                if (pcClass != null)
                {
                    csList = pc.getCharacterSpells(pcClass, null, bookName, spellLevel);
                }
            }

            boolean found = false;
            SpellInfo si = null;

            if (spellNumber < csList.size())
            {
                final CharacterSpell cs = csList.get(spellNumber);
                si = cs.getSpellInfoFor(bookName, spellLevel);
                found = true;
            }

            if (found && (si != null))
            {
                return (float) si.getTimes();
            }
        }

        return 0.0f;
    }

    @Override
    public boolean isSourceDependant()
    {
        return false;
    }

    public boolean isStatic()
    {
        return false;
    }
}
