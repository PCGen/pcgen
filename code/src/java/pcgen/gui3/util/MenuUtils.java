/*
 * MenuUtils.java
 * Copyright 2016 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Aug 27, 2016, 1:26:55 PM
 */
package pcgen.gui3.util;

import java.util.List;
import java.util.function.Function;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class MenuUtils {

    public static <T> void addMenuItems(Menu menu, ObservableList<T> list, Function<T, MenuItem> mapper) {
        List<MenuItem> items = menu.getItems();
        int size = items.size();
        Bindings.bindContent(items.subList(size, size), new MappedList<MenuItem, T>(list, mapper));
    }
}
