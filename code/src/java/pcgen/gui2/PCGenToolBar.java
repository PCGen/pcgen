/*
 * PCGenToolBar.java
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
 * Created on Aug 18, 2008, 5:12:43 PM
 */
package pcgen.gui2;

import java.awt.Dimension;
import javax.swing.JComboBox;
import javax.swing.JToolBar;
import pcgen.gui2.util.ToolBarUtilities;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public final class PCGenToolBar extends JToolBar
{

    private final PCGenFrame frame;
    private final PCGenActionMap actionMap;
    private final JComboBox sheetBox;

    public PCGenToolBar(PCGenFrame frame)
    {
        this.frame = frame;
        this.actionMap = frame.getActionMap();
        this.sheetBox = new JComboBox();
        initComponents();
    }

    private void initComponents()
    {
        setFloatable(false);
        setRollover(true);

        add(ToolBarUtilities.createToolBarButton(actionMap.get(PCGenActionMap.NEW_COMMAND)));
        add(ToolBarUtilities.createToolBarButton(actionMap.get(PCGenActionMap.OPEN_COMMAND)));
        add(ToolBarUtilities.createToolBarButton(actionMap.get(PCGenActionMap.CLOSE_COMMAND)));
        add(ToolBarUtilities.createToolBarButton(actionMap.get(PCGenActionMap.SAVE_COMMAND)));
        addSeparator();

        add(ToolBarUtilities.createToolBarButton(actionMap.get(PCGenActionMap.PRINT_COMMAND)));
        addSeparator();
        sheetBox.setMaximumSize(new Dimension(200, 22));
        //sheetBox.setModel(frame.getCharacterSheets());
        add(sheetBox);
        addSeparator();

        add(ToolBarUtilities.createToolBarButton(actionMap.get(PCGenActionMap.PREFERENCES_COMMAND)));
        addSeparator();

        add(ToolBarUtilities.createToolBarButton(actionMap.get(PCGenActionMap.HELP_CONTEXT_COMMAND)));
    }

}
