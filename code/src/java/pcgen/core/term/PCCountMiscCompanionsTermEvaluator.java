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
 * Created 09-Aug-2008 20:05:58
 */

package pcgen.core.term;

import java.util.Arrays;
import java.util.List;

import pcgen.cdom.enumeration.PCStringKey;
import pcgen.core.display.CharacterDisplay;

public class PCCountMiscCompanionsTermEvaluator extends BasePCDTermEvaluator implements TermEvaluator
{
    public PCCountMiscCompanionsTermEvaluator(String originalText)
    {
        this.originalText = originalText;
    }

    @Override
    public Float resolve(CharacterDisplay display)
    {
        String CompString = display.getSafeStringFor(PCStringKey.COMPANIONS);
        List<String> companions = Arrays.asList(CompString.split("\r?\n"));
        return (float) companions.size();
    }

    @Override
    public boolean isSourceDependant()
    {
        return false;
    }
}
