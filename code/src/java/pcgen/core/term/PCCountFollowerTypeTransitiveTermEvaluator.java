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
 * Created 07-Aug-2008 00:47:38
 */

package pcgen.core.term;

import java.util.ArrayList;
import java.util.List;

import pcgen.core.Globals;
import pcgen.core.character.Follower;
import pcgen.core.display.CharacterDisplay;

public class PCCountFollowerTypeTransitiveTermEvaluator extends BasePCDTermEvaluator implements TermEvaluator
{

    private final String type;
    private final int index;
    private final String newCount;

    public PCCountFollowerTypeTransitiveTermEvaluator(String originalText, String type, int index, String newCount)
    {
        this.originalText = originalText;
        this.type = type;
        this.index = index;
        this.newCount = newCount;
    }

    @Override
    public Float resolve(CharacterDisplay display)
    {
        if (display.hasFollowers())
        {
            final List<Follower> aList = new ArrayList<>();

            for (Follower follower : display.getFollowerList())
            {
                if (follower.getType().getKeyName().equalsIgnoreCase(type))
                {
                    aList.add(follower);
                }
            }

            if (index < aList.size())
            {
                final Follower follower = aList.get(index);

                return Globals.getPCList().stream()
                        .filter(pc -> follower.getFileName().equals(pc.getFileName())
                                && follower.getName().equals(pc.getName()))
                        .findFirst().map(pc -> pc.getVariableValue(newCount, "")).orElse(0.0f);
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
