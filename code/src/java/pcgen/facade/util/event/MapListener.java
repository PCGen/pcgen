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

import java.util.EventListener;

public interface MapListener<K, V> extends EventListener
{

    /**
     * Called when a key is added for the first time along
     * with some value.
     *
     * @param e
     */
    void keyAdded(MapEvent<K, V> e);

    /**
     * Called when a key is removed from the map along
     * with its value.
     *
     * @param e
     */
    void keyRemoved(MapEvent<K, V> e);

    /**
     * This indicates that a key's state has somehow changed.
     * For example if the key was a ReferenceFacade this event
     * could indicate that the reference has changed.
     *
     * @param e
     */
    void keyModified(MapEvent<K, V> e);

    /**
     * Called when the value for given key has been
     * replaced.
     *
     * @param e
     */
    void valueChanged(MapEvent<K, V> e);

    /**
     * This is called when the value for a specific key has
     * changed in some way. This differs from {@code valueChanged}
     * in that the value was not replaced but should be refreshed.
     * For example if the value was a ReferenceFacade this
     * event could indicate that its reference has changed.
     *
     * @param e
     */
    void valueModified(MapEvent<K, V> e);

    /**
     * This is called when the underlying map has undergone
     * changes that compromise all map's contents. This can
     * be a result of either a bulk add or bulk remove from
     * underlying map.
     *
     * @param e
     */
    void keysChanged(MapEvent<K, V> e);

}
