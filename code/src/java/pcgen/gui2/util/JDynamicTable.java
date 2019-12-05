/*
 * Copyright 2011 Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.gui2.util;

import java.awt.Container;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import pcgen.gui2.util.event.DynamicTableColumnModelListener;
import pcgen.gui2.util.table.DynamicTableColumnModel;
import pcgen.gui3.GuiUtility;

import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import org.controlsfx.control.action.Action;

public class JDynamicTable extends JTableEx
{

    private final DynamicTableColumnModelListener listener = new DynamicTableColumnModelListener()
    {

        @Override
        public void availableColumnAdded(TableColumnModelEvent event)
        {
            int index = event.getToIndex();
            TableColumn column = dynamicColumnModel.getAvailableColumns().get(index);
            menu.getItems().add(index, createMenuItem(column));
            cornerButton.setVisible(true);
        }

        @Override
        public void availableColumnRemove(TableColumnModelEvent event)
        {

            menu.getItems().remove(event.getFromIndex());
            if (menu.getItems().isEmpty())
            {
                cornerButton.setVisible(false);
            }
        }

    };
    private final Button cornerButton;
    private final JFXPanel wrappedCornerButton;
    private DynamicTableColumnModel dynamicColumnModel = null;
    private final ContextMenu menu = new ContextMenu();

    public JDynamicTable()
    {
        this.cornerButton = new JTableMenuButton(menu);
        this.wrappedCornerButton = GuiUtility.wrapParentAsJFXPanel(cornerButton);
    }

    @Override
    protected void configureEnclosingScrollPane()
    {
        super.configureEnclosingScrollPane();
        Container p = getParent();
        if (p instanceof JViewport)
        {
            Container gp = p.getParent();
            if (gp instanceof JScrollPane)
            {
                JScrollPane scrollPane = (JScrollPane) gp;
                // Make certain we are the viewPort's view and not, for
                // example, the rowHeaderView of the scrollPane -
                // an implementor of fixed columns might do this.
                JViewport viewport = scrollPane.getViewport();
                if (viewport == null || viewport.getView() != this)
                {
                    return;
                }
                scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                scrollPane.setCorner(
                        ScrollPaneConstants.UPPER_TRAILING_CORNER,
                        wrappedCornerButton);
            }
        }
    }

    @Override
    protected void unconfigureEnclosingScrollPane()
    {
        super.unconfigureEnclosingScrollPane();
        Container p = getParent();
        if (p instanceof JViewport)
        {
            Container gp = p.getParent();
            if (gp instanceof JScrollPane)
            {
                JScrollPane scrollPane = (JScrollPane) gp;
                // Make certain we are the viewPort's view and not, for
                // example, the rowHeaderView of the scrollPane -
                // an implementor of fixed columns might do this.
                JViewport viewport = scrollPane.getViewport();
                if (viewport == null || viewport.getView() != this)
                {
                    return;
                }
                scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, null);
            }
        }
    }

    private MenuItem createMenuItem(TableColumn column)
    {
        CheckMenuItem item = new CheckMenuItem();
        boolean visible = dynamicColumnModel.isVisible(column);
        item.setSelected(visible);
        item.setOnAction(new MenuAction(column, visible));
        return item;
    }

    @Override
    public void setColumnModel(TableColumnModel columnModel)
    {
        if (this.dynamicColumnModel != null)
        {
            this.dynamicColumnModel.removeDynamicTableColumnModelListener(listener);
            cornerButton.setVisible(false);
        }
        super.setColumnModel(columnModel);
    }

    public void setColumnModel(DynamicTableColumnModel columnModel)
    {
        if (this.dynamicColumnModel != null)
        {
            this.dynamicColumnModel.removeDynamicTableColumnModelListener(listener);
        }
        this.dynamicColumnModel = columnModel;
        columnModel.addDynamicTableColumnModelListener(listener);
        super.setColumnModel(columnModel);
        List<TableColumn> columns = columnModel.getAvailableColumns();
        menu.getItems().clear();
        if (!columns.isEmpty())
        {
            for (TableColumn column : columns)
            {
                menu.getItems().add(createMenuItem(column));
            }
            cornerButton.setVisible(true);
        } else
        {
            cornerButton.setVisible(false);
        }
    }

    private final class MenuAction extends Action
    {

        private boolean visible;
        private final TableColumn column;

        private MenuAction(TableColumn column, boolean visible)
        {
            super(column.getHeaderValue().toString());
            super.setEventHandler(this::actionPerformed);
            this.visible = visible;
            this.column = column;
        }

        public void actionPerformed(ActionEvent e)
        {
            visible = !visible;
            dynamicColumnModel.setVisible(column, visible);
        }

    }

}
