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
 * Created 07-Aug-2008 20:27:13
 */

package pcgen.core.term;

import java.util.Collection;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.util.enumeration.View;
import pcgen.util.enumeration.Visibility;

public class PCSkillTypeTermEvaluator extends BasePCTermEvaluator implements TermEvaluator
{

    private final String type;

    public PCSkillTypeTermEvaluator(String originalText, String type)
    {
        this.originalText = originalText;
        this.type = type;
    }

    @Override
    public Float resolve(PlayerCharacter pc)
    {
        int count = 0;
        Collection<Skill> skills = pc.getDisplay().getSkillSet();
        for (Skill sk : skills)
        {
            Visibility skVis = sk.getSafe(ObjectKey.VISIBILITY);
            //TODO This is a bug, it assumes export
            if (!skVis.isVisibleTo(View.HIDDEN_EXPORT) && sk.isType(type) && sk.qualifies(pc, null))
            {
                count++;
            }
        }
        return (float) count;
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
