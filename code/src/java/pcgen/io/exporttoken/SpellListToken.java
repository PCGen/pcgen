/*
 * Copyright 2004 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 *
 *
 *
 */
package pcgen.io.exporttoken;

import java.util.StringTokenizer;

/**
 * {@code SpellListToken} is the base class for the SPELLLIST
 * family of tokens.
 */
public abstract class SpellListToken extends Token
{
    protected static final int SPELLTAG_CAST = 0;
    protected static final int SPELLTAG_KNOWN = 1;
    protected static final int SPELLTAG_BOOK = 2;
    protected static final int SPELLTAG_TYPE = 3;
    protected static final int SPELLTAG_CLASS = 4;
    protected static final int SPELLTAG_DC = 5;
    protected static final int SPELLTAG_DCSTAT = 6;
    protected static final int SPELLTAG_MEMORIZE = 7;
    protected static final int SPELLTAG_CONCENTRATION = 8;

    // ================== Inner class =======================

    /**
     * {@code SpellListTokenParams} is ...
     */
    protected static final class SpellListTokenParams
    {
        private int classNum = 0;
        private int level = 0;
        private int bookNum = 0;

        /**
         * @param tokenSource
         * @param tagType
         */
        public SpellListTokenParams(String tokenSource, int tagType)
        {
            final StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
            aTok.nextToken();

            classNum = Integer.parseInt(aTok.nextToken());

            if (aTok.hasMoreTokens() && (tagType != SPELLTAG_TYPE) && (tagType != SPELLTAG_CLASS)
                    && (tagType != SPELLTAG_MEMORIZE))
            {
                level = Integer.parseInt(aTok.nextToken());
            }

            if (aTok.hasMoreTokens() && (tagType == SPELLTAG_BOOK))
            {
                bookNum = Integer.parseInt(aTok.nextToken());
            }
        }

        /**
         * @return Returns the bookNum.
         */
        public int getBookNum()
        {
            return bookNum;
        }

        /**
         * @return Returns the classNum.
         */
        public int getClassNum()
        {
            return classNum;
        }

        /**
         * @return Returns the level.
         */
        public int getLevel()
        {
            return level;
        }
    }
}
