/*
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
 */
package pcgen.gui2.util;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

/**
 * This is the "button" that is displayed in the upper right corner of the
 * JTreeViewTable. This really isn't a button so much as a custom JTableHeader.
 * The reason we don't use a plain old JButton is due to how the a JButton is
 * rendered in certain Look and Feels (mainly Nimbus); the JButton sometimes has
 * round corners which look ill suited in the square corner. We use a
 * JTableHeader so that the button looks like an additional table column.
 *
 */
public class JTableMenuButton extends JTableHeader
{

	private boolean pressed = false;

	public JTableMenuButton(final JTable table, final JPopupMenu popupMenu)
	{
		getColumnModel().addColumn(new TableColumn(0));
		//without setting a table errors would be thrown during rendering
		setTable(new JTable());
		setReorderingAllowed(false);
		addMouseListener(new MouseAdapter()
		{

			@Override
			public void mousePressed(MouseEvent e)
			{
				pressed = true;
				repaint();
				Container parent = table.getParent();
				//make sure that the menu has a chance to layout its components
				//so that its width can be initialized
				popupMenu.setVisible(true);
				popupMenu.show(parent, parent.getWidth() - popupMenu.getWidth(), 0);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				pressed = false;
				repaint();
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				pressed = false;
				repaint();
			}

		});
	}

	@Override
	public void paint(Graphics g)
	{
		int size = 4;
		int width = getWidth();
		int height = getHeight();
		int x = (width - size) / 2 + 1;
		int y = (height - size) / 2;
		Color shadow = UIManager.getColor("controlShadow");
		Color darkShadow = UIManager.getColor("controlDkShadow");
		Color highlight = UIManager.getColor("controlLtHighlight");
		ArrowIcon icon;
		if (pressed)
		{
			g.setColor(shadow);
			g.fillRect(0, 0, width - 1, height - 1);
			g.setColor(darkShadow);
			g.drawRect(0, 0, width - 1, height - 1);
			icon = new ArrowIcon(SwingConstants.SOUTH, size, darkShadow, Color.BLACK, shadow);
		}
		else
		{
			super.paint(g);
			icon = new ArrowIcon(SwingConstants.SOUTH, size, shadow, darkShadow, highlight);
		}

		icon.paintIcon(this, g, x, y);
	}

}
