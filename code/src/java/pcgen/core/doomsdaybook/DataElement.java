/*
 * Copyright 2003 (C) Devon Jones
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
package pcgen.core.doomsdaybook;

import java.util.List;

public interface DataElement
{

    /**
     * Get Data.
     *
     * @return ArrayList
     * @throws Exception the exception
     */
    List<DataValue> getData() throws Exception;

    /**
     * Get id
     *
     * @return id
     */
    String getId();

    /**
     * Get last data.
     *
     * @return last data
     * @throws Exception the exception
     */
    List<DataValue> getLastData() throws Exception;

    /**
     * Get title
     *
     * @return title
     */
    String getTitle();

    /**
     * Get weight
     *
     * @return weight
     */
    int getWeight();
}
