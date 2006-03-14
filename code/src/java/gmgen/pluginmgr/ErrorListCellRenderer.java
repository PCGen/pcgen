/*
 *  ErrorListCellRenderer.java - Used to list I/O and plugin load errors
 *  :noTabs=false:
 *
 *  Copyright (C) 2003 Devon Jones
 *  Derived from jEdit by Slava Pestov Copyright (C) 2001
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package gmgen.pluginmgr;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 *  Don't use this class, it's a special case for the plugin code
 *
 *@author     Soulcatcher
 *@since        GMGen 3.3
 */
public class ErrorListCellRenderer extends JComponent implements ListCellRenderer
{
	private Font boldFont;
	private Font plainFont;
	private FontMetrics boldFM;
	private FontMetrics plainFM;
	private String path;
	private String[] messages;

	/**
	 * Constructor
	 */
	public ErrorListCellRenderer()
	{
		plainFont = UIManager.getFont("Label.font");
		boldFont = new Font(plainFont.getName(), Font.BOLD, plainFont.getSize());
		plainFM = getFontMetrics(plainFont);
		boldFM = getFontMetrics(boldFont);

		setBorder(new EmptyBorder(2, 2, 2, 2));
	}

	/**
	 *  Gets the listCellRendererComponent attribute of the ErrorListCellRenderer
	 *  object
	 *
	 *@param  list          Description of the Parameter
	 *@param  value         Description of the Parameter
	 *@param  index         Description of the Parameter
	 *@param  isSelected    Description of the Parameter
	 *@param  cellHasFocus  Description of the Parameter
	 *@return               The listCellRendererComponent value
	 *@since        GMGen 3.3
	 */
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
	    boolean cellHasFocus)
	{
		ErrorListDialog.ErrorEntry entry = (ErrorListDialog.ErrorEntry) value;
		this.path = entry.path + ":";
		this.messages = entry.messages;

		return this;
	}

	/**
	 *  Gets the preferredSize attribute of the ErrorListCellRenderer object
	 *
	 *@return    The preferredSize value
	 *@since        GMGen 3.3
	 */
	public Dimension getPreferredSize()
	{
		int width = boldFM.stringWidth(path);
		int height = boldFM.getHeight();

		for (int i = 0; i < messages.length; i++)
		{
			width = Math.max(plainFM.stringWidth(messages[i]), width);
			height += plainFM.getHeight();
		}

		Insets insets = getBorder().getBorderInsets(this);
		width += (insets.left + insets.right);
		height += (insets.top + insets.bottom);

		return new Dimension(width, height);
	}

	/**
	 *  Description of the Method
	 *
	 *@param  g  Description of the Parameter
	 *@since        GMGen 3.3
	 */
	public void paintComponent(Graphics g)
	{
		Insets insets = getBorder().getBorderInsets(this);
		g.setFont(boldFont);
		g.drawString(path, insets.left, insets.top + boldFM.getAscent());

		int y = insets.top + boldFM.getHeight() + 2;
		g.setFont(plainFont);

		for (int i = 0; i < messages.length; i++)
		{
			g.drawString(messages[i], insets.left, y + plainFM.getAscent());
			y += plainFM.getHeight();
		}
	}
}
