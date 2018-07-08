/*
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
 */
package pcgen.gui2.util;

import java.awt.Component;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.Icon;

public class MultiLineTextIcon implements Icon
{

	private final List<Icon> icons;
	private int width;
	private int height;

	public MultiLineTextIcon(Component c, String text)
	{
		StringTokenizer tokenizer = new StringTokenizer(text, "\n");
		this.icons = new ArrayList<>(tokenizer.countTokens());
		this.width = 0;
		this.height = 0;
		while (tokenizer.hasMoreTokens())
		{
			SimpleTextIcon icon = new SimpleTextIcon(c, tokenizer.nextToken());
			width = Math.max(width, icon.getIconWidth());
			height += icon.getIconHeight();
			icons.add(icon);
		}
	}

	public MultiLineTextIcon(Component c, List<?> lines)
	{
		this.icons = new ArrayList<>(lines.size());
		this.width = 0;
		this.height = 0;
		for (Object line : lines)
		{
			SimpleTextIcon icon = new SimpleTextIcon(c, line.toString());
			width = Math.max(width, icon.getIconWidth());
			height += icon.getIconHeight();
			icons.add(icon);
		}
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		for (Icon icon : icons)
		{
			icon.paintIcon(c, g, x, y);
			y += icon.getIconHeight();
		}
	}

	@Override
	public int getIconWidth()
	{
		return width;
	}

	@Override
	public int getIconHeight()
	{
		return height;
	}

}
