/*
 * GenericListDataListener.java
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Jul 2, 2008, 5:27:53 PM
 */
package pcgen.gui2.util.event;

import java.util.EventListener;

/**
 *
 * @author Connor Petty &lt;cpmeister@users.sourceforge.net&gt;
 */
public interface GenericListDataListener<E> extends EventListener
{

    /** 
     * Sent after the indices in the index0,index1 
     * interval have been inserted in the data model.
     * The new interval includes both index0 and index1.
     *
     * @param e  a {@code ListDataEvent} encapsulating the
     *    event information
     */
    void intervalAdded(GenericListDataEvent<E> e);

    /**
     * Sent after the indices in the index0,index1 interval
     * have been removed from the data model.  The interval 
     * includes both index0 and index1.
     *
     * @param e  a {@code ListDataEvent} encapsulating the
     *    event information
     */
    void intervalRemoved(GenericListDataEvent<E> e);

    /** 
     * Sent when the contents of the list has changed in a way 
     * that's too complex to characterize with the previous 
     * methods. For example, this is sent when an item has been
     * replaced. Index0 and index1 bracket the change.
     *
     * @param e  a {@code ListDataEvent} encapsulating the
     *    event information
     */
    void contentsChanged(GenericListDataEvent<E> e);

}
