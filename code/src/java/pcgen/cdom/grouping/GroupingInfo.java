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

import java.util.Objects;

import pcgen.cdom.formula.scope.PCGenScope;
import pcgen.rules.context.LoadContext;

/**
 * GroupingInfo contains the (hierarchical) information about how items are grouped in the
 * new formula system.
 *
 * @param <T> The Format (class) of the type of object which this GroupingInfo is
 *            identifying
 */
public class GroupingInfo<T>
{
    /*
     * Note that the constructor and all set methods are package since field are set
     * (only) in the static factory.
     */

    /**
     * The Object type for this GroupingInfo. This will be "null" for a root GroupingInfo,
     * and non-null for any child GroupingInfo. This value represents the "name" of the
     * type of child object that this GroupingInfo will identify.
     * <p>
     * For example, it may be "PART" for Equipment to identify the different parts of
     * Equipment.
     */
    private String objectType;

    /**
     * The characteristic of the objects this GroupingInfo identifies will be tested
     * against the value in this GroupingInfo.
     * <p>
     * Examples include "GROUP" or "KEY".
     */
    private String characteristic;

    /**
     * The value for this GroupingInfo. This indicates what subset of items matching this
     * value for the "characteristic" is included.
     */
    private String value;

    /**
     * The "child" of this GroupingInfo, if any. This is optional and it is legal for it
     * to be null.
     */
    private GroupingInfo<?> child;

    /**
     * The PGenScope of objects that this GroupingInfo should identify.
     */
    private PCGenScope scope;

    /**
     * Constructs a new GroupingInfo.
     */
    GroupingInfo()
    {
        //Package, as GroupingInfoFactory should be the sole source of GroupingInfo objects.
    }

    /**
     * Sets the (non-null) object type for this GroupingInfo.
     *
     * @param objectType The object type for this GroupingInfo
     */
    void setObjectType(String objectType)
    {
        this.objectType = Objects.requireNonNull(objectType);
    }

    /**
     * Returns the object type for this GroupingInfo.
     *
     * @return The object type for this GroupingInfo.
     */
    public String getObjectType()
    {
        return objectType;
    }

    /**
     * Sets the characteristic of the objects this GroupingInfo identifies will be tested
     * against the value in this GroupingInfo.
     *
     * @param characteristic The characteristic of the objects this GroupingInfo identifies will be
     *                       tested against the value in this GroupingInfo
     */
    void setCharacteristic(String characteristic)
    {
        this.characteristic = characteristic;
    }

    /**
     * Returns the characteristic of the objects this GroupingInfo identifies will be
     * tested against the value in this GroupingInfo.
     *
     * @return The characteristic of the objects this GroupingInfo identifies will be
     * tested against the value in this GroupingInfo
     */
    public String getCharacteristic()
    {
        return characteristic;
    }

    /**
     * Sets the value used to identify which objects this GroupingInfo identifies when
     * tested against the characteristic in this GroupingInfo.
     *
     * @param value The the value used to identify which objects this GroupingInfo
     *              identifies when tested against the characteristic in this GroupingInfo
     */
    void setValue(String value)
    {
        this.value = Objects.requireNonNull(value);
    }

    /**
     * Returns the value used to identify which objects this GroupingInfo identifies when
     * tested against the characteristic in this GroupingInfo.
     *
     * @return The value used to identify which objects this GroupingInfo identifies when
     * tested against the characteristic in this GroupingInfo
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Sets the child GroupingInfo of this GroupingInfo.
     *
     * @param child The child GroupingInfo of this GroupingInfo
     */
    void setChild(GroupingInfo<?> child)
    {
        this.child = child;
    }

    /**
     * Returns true if this GroupingInfo has a child; false otherwise.
     *
     * @return true if this GroupingInfo has a child; false otherwise
     */
    public boolean hasChild()
    {
        return child != null;
    }

    /**
     * Returns the child GroupingInfo of this GroupingInfo. May return null if this
     * GroupingInfo has no child.
     *
     * @return The child GroupingInfo of this GroupingInfo
     */
    public GroupingInfo<?> getChild()
    {
        return child;
    }

    /**
     * Returns the raw instructions for this GroupingInfo.
     *
     * @return The raw instructions for this GroupingInfo
     */
    public String getInstructions()
    {
        if (child == null)
        {
            return (characteristic == null) ? value : (characteristic + '=' + value);
        }
        StringBuilder sb = new StringBuilder();
        if (characteristic != null)
        {
            sb.append(characteristic);
            sb.append('=');
        }
        sb.append(value);
        sb.append('[');
        sb.append(child.getInstructions());
        sb.append(']');
        return sb.toString();
    }

    /**
     * Sets the Scope of the objects this GroupingInfo is identifying.
     *
     * @param scope The Scope of the objects this GroupingInfo is identifying
     */
    public void setScope(PCGenScope scope)
    {
        this.scope = scope;
    }

    /**
     * Returns the Scope of the objects this GroupingInfo is identifying.
     *
     * @return The Scope of the objects this GroupingInfo is identifying
     */
    public PCGenScope getScope()
    {
        return scope;
    }

    /**
     * Returns the Class of objects managed by this GroupingInfo.
     *
     * @return The Class of objects managed by this GroupingInfo
     */
    @SuppressWarnings("unchecked")
    public Class<T> getManagedClass(LoadContext context)
    {
        return (Class<T>) scope.getFormatManager(context).get().getManagedClass();
    }

}
