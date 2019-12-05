/*
 * Copyright James Dempsey, 2012
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
 *
 */
package pcgen.gui2.util;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;

/**
 * The Class {@code JListEx} extends JList to provide double click events.
 *
 * @param <E> The type of the elements in this JListEx
 */
public class JListEx<E> extends JList<E>
{
    /**
     * Constant for a double click action event.
     */
    public static final int ACTION_DOUBLECLICK = 2051;

    /**
     * Create a new instance of JListEx
     */
    public JListEx()
    {
        installDoubleCLickListener();
    }

    private void installDoubleCLickListener()
    {
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getComponent().isEnabled() && e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
                {
                    Point p = e.getPoint();
                    int row = locationToIndex(p);
                    Object value = getModel().getElementAt(row);
                    fireActionEvent(JListEx.this, ACTION_DOUBLECLICK, String.valueOf(value));
                }
            }
        });
    }

    private void fireActionEvent(Object value, int id, String command)
    {
        ActionEvent e = null;
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2;i >= 0;i -= 2)
        {
            if (listeners[i] == ActionListener.class)
            {
                // Lazily create the event:
                if (e == null)
                {
                    e = new ActionEvent(value, id, command);
                }

                ((ActionListener) listeners[i + 1]).actionPerformed(e);
            }
        }
    }

    /**
     * Add a new listener to be informed of double click actions.
     *
     * @param listener The new listening class
     */
    public void addActionListener(ActionListener listener)
    {
        listenerList.add(ActionListener.class, listener);
    }

    /**
     * Remove a listener to no longer be informed of double click actions.
     *
     * @param listener The existing listening class
     */
    public void removeActionListener(ActionListener listener)
    {
        listenerList.remove(ActionListener.class, listener);
    }

}
