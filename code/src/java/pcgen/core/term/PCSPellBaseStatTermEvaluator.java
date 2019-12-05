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
 * Created 10-Aug-2008 00:22:33
 */

package pcgen.core.term;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;

public class PCSPellBaseStatTermEvaluator extends BasePCTermEvaluator implements TermEvaluator
{
    private final String classKey;

    public PCSPellBaseStatTermEvaluator(String originalText, String classKey)
    {
        this.originalText = originalText;
        this.classKey = classKey;
    }

    @Override
    public Float resolve(PlayerCharacter pc)
    {
        final PCClass aClass = pc.getClassKeyed(classKey);

        if (aClass == null)
        {
            return 0.0f;
        }

        CDOMSingleRef<PCStat> ss = aClass.get(ObjectKey.SPELL_STAT);

        if (ss == null)
        {
            return 10.0f;
        }

        return (float) pc.getDisplay().getTotalStatFor(ss.get());
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
