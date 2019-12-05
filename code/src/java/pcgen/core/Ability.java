/*
 * Ability.java Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite
 * 330, Boston, MA 02111-1307 USA
 *
 *
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Categorized;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.facade.core.AbilityFacade;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;

/**
 * Definition and games rules for an Ability.
 */
@SuppressWarnings("serial")
public final class Ability extends PObject implements Categorized<Ability>, AbilityFacade, Cloneable
{
    /**
     * Get the category of this ability
     *
     * @return The category of this Ability
     */
    public String getCategory()
    {
        return get(ObjectKey.ABILITY_CAT).getKeyName();
    }

    /**
     * Bog standard clone method
     *
     * @return a copy of this Ability
     */
    @Override
    public Ability clone()
    {
        try
        {
            return (Ability) super.clone();
        } catch (CloneNotSupportedException e)
        {
            ShowMessageDelegate.showMessageDialog(e.getMessage(), Constants.APPLICATION_NAME, MessageType.ERROR);
            return null;
        }
    }

    /**
     * Make a string that can be saved that will represent this Ability object
     *
     * @return a string representation that can be parsed to rebuild the
     * Ability
     */
    @Override
    public String getPCCText()
    {
        StringJoiner txt = new StringJoiner("\t");
        txt.add(getDisplayName());
        txt.add("CATEGORY:" + getCategory());
        Globals.getContext().unparse(this).forEach(txt::add);
        txt.add(PrerequisiteWriter.prereqsToString(this));
        return txt.toString();
    }

    /**
     * Compare an ability (category) to another one
     * Returns the compare value from String.compareToIgnoreCase
     *
     * @param obj the object that we're comparing against
     * @return compare value
     */
    @Override
    public int compareTo(final Object obj)
    {
        if (obj != null)
        {
            try
            {
                final Ability ab = (Ability) obj;
                Category<Ability> cat = getCDOMCategory();
                Category<Ability> othercat = ab.getCDOMCategory();
                if ((cat == null) && (othercat != null))
                {
                    return -1;
                } else if ((cat != null) && (othercat == null))
                {
                    return 1;
                } else if (cat != null)
                {
                    int diff = cat.getKeyName().compareTo(othercat.getKeyName());
                    if (diff != 0)
                    {
                        return diff;
                    }
                }
            } catch (ClassCastException e)
            {
                // Do nothing.  If the cast to Ability doesn't work, we assume that
                // the category of the Object passed in matches the category of this
                // Ability and compare KeyNames
            }

            // this should throw a ClassCastException for non-PObjects, like the
            // Comparable interface calls for
            return this.getKeyName().compareToIgnoreCase(((CDOMObject) obj).getKeyName());
        }
        return 1;
    }

    /**
     * Equals function, uses compareTo to do the work
     *
     * @param other Ability to compare to
     * @return true if they are equal
     */
    @Override
    public boolean equals(final Object other)
    {
        return (other instanceof Ability) && (this.compareTo(other) == 0);
    }

    /**
     * Must be consistent with equals
     */
    @Override
    public int hashCode()
    {
        //Can't be more complicated because the weird nature of compareTo
        return getKeyName().hashCode();
    }

    @Override
    public Category<Ability> getCDOMCategory()
    {
        return get(ObjectKey.ABILITY_CAT);
    }

    @Override
    public void setCDOMCategory(Category<Ability> cat)
    {
        put(ObjectKey.ABILITY_CAT, cat);
    }

    @Override
    public ListKey<Description> getDescriptionKey()
    {
        return ListKey.DESCRIPTION;
    }

    @Override
    public List<String> getTypes()
    {
        List<Type> trueTypeList = getTrueTypeList(true);
        List<String> typeNames = new ArrayList<>();
        for (Type type : trueTypeList)
        {
            typeNames.add(type.toString());
        }
        return typeNames;
    }

    @Override
    public boolean isMult()
    {
        return getSafe(ObjectKey.MULTIPLE_ALLOWED);
    }

    @Override
    public boolean isStackable()
    {
        return getSafe(ObjectKey.STACKS);
    }

    @Override
    public double getCost()
    {
        return getSafe(ObjectKey.SELECTION_COST).doubleValue();
    }
}
