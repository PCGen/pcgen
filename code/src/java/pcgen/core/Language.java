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
package pcgen.core;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.list.LanguageList;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;

/**
 * {@code Language}.
 */
public final class Language extends PObject implements Comparable<Object>, Ungranted, Cloneable
{
    public static final CDOMReference<LanguageList> STARTING_LIST;

    static
    {
        LanguageList wpl = new LanguageList();
        wpl.setName("*Starting");
        STARTING_LIST = CDOMDirectSingleRef.getRef(wpl);
    }

    /**
     * Compares keyName only
     *
     * @param o1
     * @return int
     */
    @Override
    public int compareTo(final Object o1)
    {
        /*
         * TODO This behavior of compareTo could be improved... need to figure
         * out why this is present in the code (where a language should be
         * compared to a String) and get RID of it... explicitly grab the key
         * name and compare the strings. -thpr 06/18/05
         */
        if (o1 instanceof String)
        {
            return getKeyName().compareToIgnoreCase((String) o1);
        }

        return getKeyName().compareToIgnoreCase(((Language) o1).getKeyName());
    }

    /**
     * Compares keyName only
     *
     * @param o1
     * @return true if equal
     */
    @Override
    public boolean equals(final Object o1)
    {
        /*
         * TODO This is behavior of equals could be improved... need to figure
         * out why this is present in the code (where a language should be
         * compared to a String) and get RID of it... explicitly grab the key
         * name and call .equals() on the strings. -thpr 06/18/05
         */
        if (o1 == null)
        {
            return false;
        }
        if (o1 instanceof String)
        {
            return getKeyName().equals(o1);
        }
        if (!o1.getClass().equals(Language.class))
        {
            return false;
        }

        return getKeyName().equals(((Language) o1).getKeyName());
    }

    /**
     * Hashcode of the keyName
     *
     * @return hash code
     */
    @Override
    public int hashCode()
    {
        return getKeyName().hashCode();
    }

    @Override
    public Language clone()
    {
        Language l = null;

        try
        {
            l = (Language) super.clone();
        } catch (CloneNotSupportedException e)
        {
            ShowMessageDelegate.showMessageDialog(e.getMessage(), Constants.APPLICATION_NAME, MessageType.ERROR);
        }

        return l;
    }
}
