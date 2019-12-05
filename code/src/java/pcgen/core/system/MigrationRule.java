/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
package pcgen.core.system;

import pcgen.core.utils.CoreUtility;

/**
 * The Class {@code MigrationRule} defines a key change that needs to
 * be made to a character when it is loaded.
 */
public class MigrationRule
{

    /**
     * A type of rules object that may be migrated.
     */
    public enum ObjectType
    {
        ABILITY(true), EQUIPMENT(false), RACE(false), SOURCE(false), SPELL(false);

        private final boolean categorized;

        private ObjectType(boolean categorized)
        {
            this.categorized = categorized;
        }

        /**
         * @return true if the object type needs a category as well as a key for uniqueness.
         */
        public boolean isCategorized()
        {
            return categorized;
        }
    }

    private ObjectType objectType;
    private String oldKey;
    private String newKey;
    private String oldCategory;
    private String newCategory;
    private String maxVer;
    private String maxDevVer;
    private String minVer;
    private String minDevVer;

    /**
     * Create a new MigrationRule for a categorized object.
     *
     * @param objectType  The type of object to be migrated.
     * @param oldCategory The original category of the object. (e.g. AbilityCategory)
     * @param oldKey      The original key of the object.
     */
    public MigrationRule(ObjectType objectType, String oldCategory, String oldKey)
    {
        if (!objectType.isCategorized())
        {
            throw new IllegalArgumentException(objectType + " must not have a category.");
        }
        this.objectType = objectType;
        this.oldCategory = oldCategory;
        this.oldKey = oldKey;
    }

    /**
     * Create a new MigrationRule for a non-categorized object.
     *
     * @param objectType The type of object to be migrated.
     * @param oldKey     The original key of the object.
     */
    public MigrationRule(ObjectType objectType, String oldKey)
    {
        if (objectType.isCategorized())
        {
            throw new IllegalArgumentException(objectType + " must have a category.");
        }
        this.objectType = objectType;
        this.oldKey = oldKey;
    }

    public ObjectType getObjectType()
    {
        return objectType;
    }

    public void setObjectType(ObjectType objectType)
    {
        this.objectType = objectType;
    }

    public String getOldKey()
    {
        return oldKey;
    }

    public void setOldKey(String oldKey)
    {
        this.oldKey = oldKey;
    }

    public String getNewKey()
    {
        return newKey;
    }

    public void setNewKey(String newKey)
    {
        this.newKey = newKey;
    }

    public String getOldCategory()
    {
        return oldCategory;
    }

    public void setOldCategory(String oldCategory)
    {
        this.oldCategory = oldCategory;
    }

    public String getNewCategory()
    {
        return newCategory;
    }

    public void setNewCategory(String newCategory)
    {
        this.newCategory = newCategory;
    }

    public String getMaxVer()
    {
        return maxVer;
    }

    public void setMaxVer(String maxVer)
    {
        this.maxVer = maxVer;
    }

    public String getMaxDevVer()
    {
        return maxDevVer;
    }

    public void setMaxDevVer(String maxDevVer)
    {
        this.maxDevVer = maxDevVer;
    }

    public String getMinVer()
    {
        return minVer;
    }

    public void setMinVer(String minVer)
    {
        this.minVer = minVer;
    }

    public String getMinDevVer()
    {
        return minDevVer;
    }

    public void setMinDevVer(String minDevVer)
    {
        this.minDevVer = minDevVer;
    }

    /**
     * Check if this migration rule applies to a character from a particular version of pcgen.
     *
     * @param pcgVer The version to be checked.
     * @return true if the migration is applicable.
     */
    public boolean changeAppliesToVer(int[] pcgVer)
    {
        // PC ver must be before or equal MAXVER, or
        //   (before or equal MAXDEVVER (and same major.minor ver))
        // PC ver must be after or equal MINVER, and
        // PC ver must be after or equal MINDEVVER (and same major.minor ver)

        // e.g. 5.12.1 - 5.16.4/5.17.10
        int[] maxVerInt = CoreUtility.convertVersionToNumber(maxVer);
        if (CoreUtility.compareVersions(pcgVer, maxVerInt) > 0)
        {
            // PCG is from after the max prod ver, check it is not a dev ver before the max dev ver
            if (maxDevVer == null)
            {
                return false;
            }
            int[] maxDevVerInt = CoreUtility.convertVersionToNumber(maxDevVer);
            if (CoreUtility.compareVersions(pcgVer, maxDevVerInt) > 0
                    || !CoreUtility.sameMajorMinorVer(pcgVer, maxDevVerInt))
            {
                return false;
            }
        }

        if (minVer != null)
        {
            int[] minVerInt = CoreUtility.convertVersionToNumber(minVer);
            if (CoreUtility.compareVersions(pcgVer, minVerInt) < 0)
            {
                return false;
            }
            if (minDevVer != null)
            {
                int[] minDevVerInt = CoreUtility.convertVersionToNumber(minDevVer);
                return CoreUtility.compareVersions(pcgVer, minDevVerInt) >= 0
                        || !CoreUtility.sameMajorMinorVer(pcgVer, minDevVerInt);
            }
        }

        return true;
    }

    @Override
    public String toString()
    {
        return "MigrationRule [objectType=" + objectType + ", oldKey=" + oldKey + ", maxVer=" + maxVer + "]";
    }
}
