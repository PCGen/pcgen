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
 * Created 11-Sep-2008 02:30:27
 */

package pcgen.core.term;

import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.core.display.CharacterDisplay;
import pcgen.util.Logging;

public class PCStatModTermEvaluator extends BasePCDTermEvaluator implements TermEvaluator
{
    private final String statAbbrev;

    public PCStatModTermEvaluator(String originalText, String statAbbrev)
    {
        this.originalText = originalText;
        this.statAbbrev = statAbbrev;
    }

    @Override
    public Float resolve(CharacterDisplay display)
    {
        final PCStat stat =
                Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(PCStat.class, statAbbrev);
        if (stat == null)
        {
            Logging.errorPrint("Unable to find stat for " + statAbbrev + " in " + originalText);
            return 0.0f;
        }
        return (float) display.getStatModFor(stat);
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
