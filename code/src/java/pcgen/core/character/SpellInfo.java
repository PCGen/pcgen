/*
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.character;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.core.Ability;
import pcgen.core.Globals;

/**
 * {@code SpellInfo}
 * this is a helper-class for CharacterSpell
 * meant to contain the book, whether or not this spell
 * is in the specialtySlot for characters which have them,
 * and the list of meta-magic feats which have been applied.
 */
public final class SpellInfo implements Comparable<SpellInfo>
{
    /**
     * The special value for number of times per unit for 'At Will' spells.
     */
    public static final int TIMES_AT_WILL = -1;

    private CharacterSpell owner;
    private List<Ability> featList; // a List of Feat objects
    private String book = Globals.getDefaultSpellBook(); // name of book
    private final int origLevel;
    private final int actualLevel;
    private int times; // times the spell is in this list
    private String timeUnit; // the timeunit the times is for (day, week etc)
    private int actualPPCost = -1;
    private int numPages = 0;
    private String fixedDC = null;

    SpellInfo(final CharacterSpell owner, final int originalLevel, final int actualLevel, final int times,
            final String book)
    {
        this.owner = owner;
        this.actualLevel = actualLevel;
        this.origLevel = originalLevel;
        this.times = times;

        //
        // use the default book
        //
        if (book != null)
        {
            this.book = book;
        }
    }

    public int getActualLevel()
    {
        return actualLevel;
    }

    public int getOriginalLevel()
    {
        return origLevel;
    }

    public void setActualPPCost(final int argActualPPCost)
    {
        actualPPCost = argActualPPCost;
    }

    public int getActualPPCost()
    {
        return actualPPCost;
    }

    public String getBook()
    {
        return book;
    }

    public List<Ability> getFeatList()
    {
        return featList;
    }

    public CharacterSpell getOwner()
    {
        return owner;
    }

    public void setTimes(final int times)
    {
        this.times = times;
    }

    public int getTimes()
    {
        return times;
    }

    /**
     * @return the timeUnit
     */
    public String getTimeUnit()
    {
        return timeUnit;
    }

    /**
     * @param timeUnit the timeUnit to set
     */
    public void setTimeUnit(String timeUnit)
    {
        this.timeUnit = timeUnit;
    }

    public int getNumPages()
    {
        return numPages;
    }

    public void setNumPages(int numPages)
    {
        this.numPages = numPages;
    }

    public void addFeatsToList(final Collection<Ability> aList)
    {
        if (featList == null)
        {
            featList = new ArrayList<>(aList.size());
        }

        featList.addAll(aList);
    }

    @Override
    public String toString()
    {
        if (featList == null || featList.isEmpty())
        {
            return "";
        }

        final StringBuilder aBuf = new StringBuilder(" [" + featList.get(0));

        for (int i = 1;i < featList.size();i++)
        {
            aBuf.append(", ").append(featList.get(i));
        }

        aBuf.append("] ");

        return aBuf.toString();
    }

    /**
     * @return Returns the fixedDC.
     */
    public String getFixedDC()
    {
        return fixedDC;
    }

    /**
     * @param fixedDC The fixedDC to set.
     */
    public void setFixedDC(final String fixedDC)
    {
        this.fixedDC = fixedDC;
    }

    @Override
    public int compareTo(SpellInfo other)
    {
        //We can't compare based on owner since that would be infinite loop
        int compare = book.compareTo(other.book);
        if (compare == 0)
        {
            if (origLevel < other.origLevel)
            {
                compare = -1;
            } else if (origLevel > other.origLevel)
            {
                compare = 1;
            }
        }
        if (compare == 0)
        {
            if (actualLevel < other.actualLevel)
            {
                compare = -1;
            } else if (actualLevel > other.actualLevel)
            {
                compare = 1;
            }
        }
        if (compare == 0)
        {
            if (times < other.times)
            {
                compare = -1;
            } else if (times > other.times)
            {
                compare = 1;
            }
        }
        if (compare == 0)
        {
            if (timeUnit == null)
            {
                if (other.timeUnit != null)
                {
                    compare = -1;
                }
            } else if (other.timeUnit == null)
            {
                compare = 1;
            } else
            {
                compare = timeUnit.compareTo(other.timeUnit);
            }
        }
        if (compare == 0)
        {
            if (actualPPCost < other.actualPPCost)
            {
                compare = -1;
            } else if (actualPPCost > other.actualPPCost)
            {
                compare = 1;
            }
        }
        if (compare == 0)
        {
            if (numPages < other.numPages)
            {
                compare = -1;
            } else if (numPages > other.numPages)
            {
                compare = 1;
            }
        }
        if (compare == 0)
        {
            if (fixedDC == null)
            {
                if (other.fixedDC != null)
                {
                    compare = -1;
                }
            } else if (other.fixedDC == null)
            {
                compare = 1;
            } else
            {
                compare = fixedDC.compareTo(other.fixedDC);
            }
        }
        if (compare == 0)
        {
            if (featList == null)
            {
                if (other.featList != null)
                {
                    compare = -1;
                }
            } else if (other.featList == null)
            {
                compare = 1;
            } else
            {
                int thisILsize = featList.size();
                int otherILsize = other.featList.size();
                if (thisILsize < otherILsize)
                {
                    compare = -1;
                } else if (thisILsize > otherILsize)
                {
                    compare = 1;
                } else
                {
                    //compare contents...
                    for (int i = 0;i < thisILsize;i++)
                    {
                        compare = featList.get(i).compareTo(other.featList.get(i));
                        if (compare != 0)
                        {
                            break;
                        }
                    }
                }
            }
        }
        return compare;
    }

}
