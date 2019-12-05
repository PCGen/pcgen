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
 * Created 07-Aug-2008 21:45:43
 */

package pcgen.core.term;

import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;

public class PCCountSpellsInbookTermEvaluator extends BasePCTermEvaluator implements TermEvaluator
{
    private final String book;

    public PCCountSpellsInbookTermEvaluator(String originalText, String book)
    {
        this.originalText = originalText;
        this.book = book;
    }

    @Override
    public Float resolve(PlayerCharacter pc)
    {
        return SettingsHandler.getPrintSpellsWithPC() ? (float) pc.countSpellsInBook(book) : 0.0f;
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
