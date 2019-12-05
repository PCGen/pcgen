/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.helper.ClassSource;
import pcgen.core.Ability;
import pcgen.core.Domain;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.analysis.SpellCountCalc;
import pcgen.core.spell.Spell;

/**
 * {@code PCClass}.
 */
public final class CharacterSpell implements Comparable<CharacterSpell>
{
    private final List<SpellInfo> infoList = new ArrayList<>();
    private final CDOMObject owner; // PCClass/Race/etc. in whose list this object resides
    private final Spell spell;
    private String fixedCasterLevel = null;

    /**
     * Constructor
     *
     * @param o
     * @param aSpell
     */
    public CharacterSpell(final CDOMObject o, final Spell aSpell)
    {
        owner = o;
        spell = aSpell;
    }

    /**
     * Returns index of SpellInfo in infoList, or -1 if it doesn't exist.
     *
     * @param pc        The character to query.
     * @param bookName  name of spellbook/list
     * @param level     actual level of spell (adjusted by feats)
     * @param specialty -1 = inSpecialty insensitive;<br>0 = inSpecialty==false; and <br> 1 = inSpecialty==true
     * @return info index
     */
    public int getInfoIndexFor(PlayerCharacter pc, final String bookName, final int level, final int specialty)
    {
        if (infoList.isEmpty())
        {
            return -1;
        }

        boolean sp = specialty == 1;

        if (sp)
        {
            sp = isSpecialtySpell(pc);
        }

        int i = 0;

        for (SpellInfo s : infoList)
        {
            if (("".equals(bookName) || bookName.equals(s.getBook())) && (level == -1 || s.getActualLevel() == level)
                    && (specialty == -1 || sp))
            {
                return i;
            }

            i++;
        }

        return -1;
    }

    /**
     * Get info list
     *
     * @return info list
     */
    public List<SpellInfo> getInfoList()
    {
        return infoList;
    }

    /**
     * Get Owner
     *
     * @return owner
     */
    public CDOMObject getOwner()
    {
        return owner;
    }

    /**
     * is speciality spell
     *
     * @return TRUE if speciality spell
     */
    public boolean isSpecialtySpell(PlayerCharacter pc)
    {
        final boolean result;

        if (spell == null)
        {
            result = false;
        } else if (owner instanceof Domain)
        {
            result = true;
        } else if (owner instanceof PCClass)
        {
            final PCClass a = (PCClass) owner;
            result = SpellCountCalc.isSpecialtySpell(pc, a, spell);
        } else
        {
            result = false;
        }

        return result;
    }

    /**
     * Get spell
     *
     * @return spell
     */
    public Spell getSpell()
    {
        return spell;
    }

    /**
     * Get the spell info
     *
     * @param bookName
     * @param level
     * @return SpellInfo
     */
    public SpellInfo getSpellInfoFor(final String bookName, final int level)
    {
        return getSpellInfoFor(bookName, level, null);
    }

    /**
     * Get the Spell info
     *
     * @param bookName
     * @param level
     * @param featList
     * @return Spell Info
     */
    public SpellInfo getSpellInfoFor(final String bookName, final int level, final List<Ability> featList)
    {
        if (infoList.isEmpty())
        {
            return null;
        }

        for (SpellInfo s : infoList)
        {
            if (("".equals(bookName) || bookName.equals(s.getBook())) && (level == -1 || s.getActualLevel() == level)
                    && (featList == null || featList.isEmpty() && (s.getFeatList() == null || s.getFeatList().isEmpty())
                    || s.getFeatList() != null && featList.toString().equals(s.getFeatList().toString())))
            {
                return s;
            }
        }

        return null;
    }

    public boolean hasSpellInfoFor(int level)
    {
        if (infoList.isEmpty())
        {
            return false;
        }

        return infoList.stream()
                .anyMatch(spellInfo -> spellInfo.getActualLevel() == level);
    }

    public boolean hasSpellInfoFor(String bookName)
    {
        if (infoList.isEmpty())
        {
            return false;
        }

        for (SpellInfo s : infoList)
        {
            if (bookName.equals(s.getBook()))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Add Spell info
     *
     * @param level
     * @param times
     * @param book
     * @return SpellInfo
     */
    public SpellInfo addInfo(final int level, final int times, final String book)
    {
        return addInfo(level, level, times, book, null);
    }

    /**
     * Add Spell info
     *
     * @param level
     * @param times
     * @param book
     * @param featList
     * @return SpellInfo
     */
    public SpellInfo addInfo(final int origLevel, final int level, final int times, final String book,
            final List<Ability> featList)
    {
        final SpellInfo si = new SpellInfo(this, origLevel, level, times, book);

        if (featList != null)
        {
            si.addFeatsToList(featList);
        }

        infoList.add(si);

        return si;
    }

    /**
     * Compares with another object. The implementation compares the CharacterSpell's contained spell object with the
     * passed-in CharacterSpell's spell object.
     *
     * @param obj the CharacterSpell to compare with
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(final CharacterSpell obj)
    {
        int compare = spell.compareTo(obj.spell);
        if (compare == 0)
        {
            if (fixedCasterLevel == null)
            {
                if (obj.fixedCasterLevel != null)
                {
                    compare = -1;
                }
            } else if (obj.fixedCasterLevel == null)
            {
                compare = 1;
            } else
            {
                compare = fixedCasterLevel.compareTo(obj.fixedCasterLevel);
            }
        }
        if (compare == 0)
        {
            compare = owner.getClass().toString().compareTo(obj.owner.getClass().toString());
        }
        if (compare == 0)
        {
            compare = owner.getKeyName().compareTo(obj.owner.getKeyName());
        }
        if (compare == 0)
        {
            int thisILsize = infoList.size();
            int otherILsize = obj.infoList.size();
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
                    compare = infoList.get(i).compareTo(obj.infoList.get(i));
                    if (compare != 0)
                    {
                        break;
                    }
                }
            }
        }
        return compare;
    }

    /**
     * returns true if
     * obj.getName() equals this.getName()
     * or obj == this
     *
     * @param obj
     * @return true if equal
     */
    @Override
    public boolean equals(final Object obj)
    {
        return obj != null && obj instanceof CharacterSpell && ((CharacterSpell) obj).getName().equals(getName());
    }

    /**
     * this method is used the same as equals() but for hash tables
     *
     * @return hash code
     */
    @Override
    public int hashCode()
    {
        return toString().hashCode();
    }

    /**
     * Remove spell info
     *
     * @param x
     */
    public void removeSpellInfo(final SpellInfo x)
    {
        if (x != null)
        {
            infoList.remove(x);
        }
    }

    ///////////////////////////////////////////////////////////////////////
    // Accessor(s) and Mutator(s)
    ///////////////////////////////////////////////////////////////////////

    /**
     * Returns the Spell's Name for Tree's display
     *
     * @return the Spell's Name for Tree's display
     */
    @Override
    public String toString()
    {
        final String result;

        if (spell == null)
        {
            result = "";
        } else
        {
            result = spell.getDisplayName();
        }

        return result;
    }

    /**
     * Returns the name of the spell for this Character Spell
     *
     * @return name
     */
    private String getName()
    {
        final StringBuilder buf = new StringBuilder(owner.toString());

        if (spell != null)
        {
            buf.append(':').append(spell.getDisplayName());
        }
        return buf.toString();
    }

    /**
     * @return Returns the fixedCasterLevel.
     */
    public String getFixedCasterLevel()
    {
        return fixedCasterLevel;
    }

    /**
     * @param fixedCasterLevel The fixedCasterLevel to set.
     */
    public void setFixedCasterLevel(final String fixedCasterLevel)
    {
        this.fixedCasterLevel = fixedCasterLevel;
    }

    public String getVariableSource(PlayerCharacter pc)
    {
        if (owner instanceof Domain)
        {
            ClassSource source = pc.getDomainSource((Domain) owner);
            if (source != null)
            {
                return "CLASS:" + pc.getClassKeyed(source.getPcclass().getKeyName());
            }
        } else if (owner instanceof PCClass)
        {
            return "CLASS:" + owner.getKeyName();
        } else if (owner instanceof Race) // could be innate spell for race
        {
            return "RACE:" + owner.getKeyName();
        }
        return Constants.EMPTY_STRING;
    }

}
