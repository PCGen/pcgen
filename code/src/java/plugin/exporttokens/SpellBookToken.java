/*
 * SpellBookToken.java
 * Copyright 2006 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.exporttokens;

import java.util.StringTokenizer;

import pcgen.core.character.SpellBook;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbstractExportToken;

/**
 * {@code SpellBookToken} gives the requested details of a spellbook.
 */
public class SpellBookToken extends AbstractExportToken
{

    @Override
    public String getTokenName()
    {
        return "SPELLBOOK";
    }

    @Override
    public String getToken(String tokenSource, CharacterDisplay display, ExportHandler eh)
    {
        String retString = "";

        final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");

        // Ignore the token itself
        aTok.nextToken();

        final int bookNum = Integer.parseInt(aTok.nextToken());
        String aLabel = "NAME";

        if (aTok.hasMoreTokens())
        {
            aLabel = aTok.nextToken();
        }

        SpellBook book = null;
        if (bookNum >= 0 && bookNum < display.getSpellBookCount())
        {
            String bookName = display.getSpellBookNames().get(bookNum);
            book = display.getSpellBookByName(bookName);
        }
        if (book != null)
        {
            if ("NAME".equals(aLabel))
            {
                retString = book.getName();
            } else if ("NUMPAGES".equals(aLabel))
            {
                retString = String.valueOf(book.getNumPages());
            } else if ("NUMPAGESUSED".equals(aLabel))
            {
                retString = String.valueOf(book.getNumPagesUsed());
            } else if ("NUMSPELLS".equals(aLabel))
            {
                retString = String.valueOf(book.getNumSpells());
            } else if ("PAGEFORMULA".equals(aLabel))
            {
                retString = book.getPageFormula().toString();
            } else if ("TYPE".equals(aLabel))
            {
                retString = book.getTypeName();
            }
        }

        return retString;
    }

}
