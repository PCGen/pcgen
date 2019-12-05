/*
 * Copyright 2012 Connor Petty <cpmeister@users.sourceforge.net>
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
 *
 */
package pcgen.facade.util.event;

public class MapEvent<K, V> extends FacadeEvent
{

    public static final int KEY_ADDED = 0;
    public static final int KEY_REMOVED = 1;
    public static final int KEY_MODIFIED = 2;
    public static final int VALUE_CHANGED = 3;
    public static final int VALUE_MODIFIED = 4;
    private static final int KEYS_CHANGED = 4;
    private final int type;
    private final K key;
    private final V newValue;
    private final V oldValue;

    /**
     * This is a shortcut constructor for an KEYS_CHANGED event
     *
     * @param source
     */
    public MapEvent(Object source)
    {
        this(source, KEYS_CHANGED, null, null, null);
    }

    public MapEvent(Object source, int type, K key, V oldValue, V newValue)
    {
        super(source);
        this.type = type;
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public K getKey()
    {
        return key;
    }

    public int getType()
    {
        return type;
    }

    public V getOldValue()
    {
        return oldValue;
    }

    public V getNewValue()
    {
        return newValue;
    }

}
