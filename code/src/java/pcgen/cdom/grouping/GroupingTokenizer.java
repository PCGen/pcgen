/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.grouping;

import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * A GroupingTokenizer is a specialized Tokenizer designed to split Groupings into the
 * underlying elements.
 */
public class GroupingTokenizer implements Iterator<String>
{

    /**
     * An underlying StringTokenizer that is splitting the original String based on the
     * grouping rules.
     */
    private final StringTokenizer underlying;

    /**
     * A StringBuilder which contains the contents of the original String that have
     * already been consumed by this GroupingTokenizer.
     */
    private final StringBuilder consumed;

    /**
     * true if this GroupingTokenizer has peeked ahead at the contents of the underlying
     * StringTokenizer; false otherwise.
     */
    private boolean peeked;

    /**
     * The contents of the peek-ahead value from the underlying StringTokenizer. Is only
     * correct if peeked is true.
     */
    private String peekedString;

    /**
     * Constructs a new GroupingTokenizer that will separate the given String.
     *
     * @param string The String this GroupingTokenizer will separate
     */
    public GroupingTokenizer(String string)
    {
        underlying = new StringTokenizer(string, "=[]", true);
        consumed = new StringBuilder(string.length());
    }

    @Override
    public boolean hasNext()
    {
        return peeked || underlying.hasMoreTokens();
    }

    @Override
    public String next()
    {
        if (peeked)
        {
            peeked = false;
            consumed.append(peekedString);
            return peekedString;
        }
        String next = underlying.nextToken();
        consumed.append(next);
        return next;
    }

    /**
     * Returns the portions of this GroupingTokenizer that have already been consumed.
     *
     * @return The portions of this GroupingTokenizer that have already been consumed
     */
    public String getConsumed()
    {
        return consumed.toString();
    }

    /**
     * Peeks at the next element that will be returned by nextElement() without
     * "consuming" that element.
     *
     * @return the next element that will be returned by nextElement()
     */
    public String peek()
    {
        if (!peeked)
        {
            peekedString = underlying.nextToken();
            peeked = true;
        }
        return peekedString;
    }
}
