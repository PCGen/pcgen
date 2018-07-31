/*
 * Copyright 2014 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 */package pcgen.gui2.util;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JMenuItem;

/**
 * PCGMenuItem extends JMenuItem to allow us to customise it. In particular we 
 * are overiding the location of the tooltips for the menu items.
 * 
 * 
 */
@SuppressWarnings("serial")
public class PCGMenuItem extends JMenuItem
{
	public PCGMenuItem()
	{
		super();
	}

	public PCGMenuItem(Action a)
	{
		super(a);
	}

	@Override
	public Point getToolTipLocation(MouseEvent event)
	{
		Dimension size = getSize();
		double halfheight = size.getHeight() / 2;
		return new Point((int) size.getWidth(), (int) halfheight);
	}
}
