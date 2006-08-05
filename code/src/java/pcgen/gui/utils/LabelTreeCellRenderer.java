/*
 * LabelTreeCellRenderer.java
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
 * Created on February 7th, 2002.
 */
package pcgen.gui.utils;

import pcgen.util.Logging;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

/**
 *  <code>LabelTreeCellRenderer</code>
 *
 * @author     ???
 * @version    $Revision$
 */
public final class LabelTreeCellRenderer extends JLabel implements TreeCellRenderer
{
	/** Color to use for the background when selected. */
	protected static final Color SelectedBackgroundColor = Color.white;
	private static Map<String, Font> fontMap = new HashMap<String, Font>();
	private Color myColor = Color.white;

	/**
	 * This is messaged from JTree whenever it needs to get the size
	 * of the component or it wants to draw it.
	 * This attempts to set the font based on value, which will be
	 * a TreeNode.
	 * @param tree
	 * @param value
	 * @param argSelected
	 * @param expanded
	 * @param leaf
	 * @param row
	 * @param hasFocus
	 * @return Component
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean argSelected, boolean expanded,
	    boolean leaf, int row, boolean hasFocus)
	{
		String stringValue;
		Font aFont = getFont();
		String fontName = aFont.getName();
		int iSize = aFont.getSize();

		try
		{
			stringValue = tree.convertValueToText(value, argSelected, expanded, leaf, row, hasFocus);
		}
		catch (Exception exc)
		{
			stringValue = "";
			Logging.errorPrint("problem converting in treecellrenderer", exc);
		}

		if ((stringValue.length() > 1) && (stringValue.charAt(1) == '|'))
		{
			if (stringValue.charAt(0) == 'B')
			{
				stringValue = stringValue.substring(2, stringValue.length());

				Font newFont = getFontFromMap(fontName, Font.BOLD, iSize);
				setFont(newFont);
			}
			else if (stringValue.charAt(0) == 'I')
			{
				stringValue = stringValue.substring(2, stringValue.length());

				Font newFont = getFontFromMap(fontName, Font.ITALIC, iSize);
				setFont(newFont);
			}
		}
		else
		{
			Font newFont = getFontFromMap(fontName, Font.PLAIN, iSize);
			setFont(newFont);
		}

		int bi = stringValue.indexOf("|");
		int ei = stringValue.lastIndexOf("|");
		if (bi> -1 && bi != ei)
		{
			final String aString = stringValue.substring(bi+1, ei);
			myColor = new Color(Integer.parseInt(aString));
			stringValue = stringValue.substring(0,bi)+stringValue.substring(ei + 1);
			if (argSelected)
			{
				setBackground(myColor);
				setForeground(Color.white);
			}
			else
			{
				setForeground(myColor);
				setBackground(Color.white);
			}
		}
		else
		{
			if (argSelected)
			{
				setForeground(Color.white);
				setBackground(Color.blue);
			}
			else
			{
				setForeground(Color.black);
				setBackground(Color.white);
			}
		}

		setText(stringValue);

		return this;
	}

	private static Font getFontFromMap(String name, int type, int size)
	{
		String key = getFontKey(name, type, size);
		Font theFont = fontMap.get(key);

		if (theFont == null)
		{
			theFont = new Font(name, type, size);
			fontMap.put(key, theFont);
		}

		return theFont;
	}

	private static String getFontKey(String name, int type, int size)
	{
		return name + "." + type + "." + size;
	}
}
