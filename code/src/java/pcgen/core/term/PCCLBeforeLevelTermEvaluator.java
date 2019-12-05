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
 * Created 04-Aug-2008 17:02:58
 */

package pcgen.core.term;

import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;

public class PCCLBeforeLevelTermEvaluator extends BasePCTermEvaluator implements TermEvaluator
{

    private final String source;
    private final int level;

    public PCCLBeforeLevelTermEvaluator(String originalText, String source, int level)
    {
        this.originalText = originalText;
        this.source = source;
        this.level = level;
    }

    @Override
    public Float resolve(PlayerCharacter pc)
    {

        final PCClass aClass = pc.getClassKeyed(source);

        if (aClass != null)
        {
            if (level > 0)
            {
                return (float) pc.getLevelBefore(aClass.getKeyName(), level);
            }

            return (float) pc.getDisplay().getLevel(aClass);
        }

        return 0.0f;
    }

    @Override
    public boolean isSourceDependant()
    {
        return true;
    }

    public boolean isStatic()
    {
        return false;
    }
}
